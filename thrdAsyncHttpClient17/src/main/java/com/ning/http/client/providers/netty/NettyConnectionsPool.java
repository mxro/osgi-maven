/*
 * Copyright (c) 2010-2012 Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package com.ning.http.client.providers.netty;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jboss.netty.channel.Channel;

import com.ning.http.client.ConnectionsPool;

/**
 * A simple implementation of {@link com.ning.http.client.ConnectionsPool} based
 * on a {@link java.util.concurrent.ConcurrentHashMap}
 */
public class NettyConnectionsPool implements ConnectionsPool<String, Channel> {

	private final ConcurrentHashMap<String, ConcurrentLinkedQueue<IdleChannel>> connectionsPool = new ConcurrentHashMap<String, ConcurrentLinkedQueue<IdleChannel>>();
	private final ConcurrentHashMap<Channel, IdleChannel> channel2IdleChannel = new ConcurrentHashMap<Channel, IdleChannel>();
	private final AtomicBoolean isClosed = new AtomicBoolean(false);
	private final Timer idleConnectionDetector = new Timer(true);
	private final boolean sslConnectionPoolEnabled;
	private final int maxTotalConnections;
	private final int maxConnectionPerHost;
	private final long maxIdleTime;

	public NettyConnectionsPool(final NettyAsyncHttpProvider provider) {
		this.maxTotalConnections = provider.getConfig()
				.getMaxTotalConnections();
		this.maxConnectionPerHost = provider.getConfig()
				.getMaxConnectionPerHost();
		this.sslConnectionPoolEnabled = provider.getConfig()
				.isSslConnectionPoolEnabled();
		this.maxIdleTime = provider.getConfig()
				.getIdleConnectionInPoolTimeoutInMs();
		this.idleConnectionDetector.schedule(new IdleChannelDetector(),
				maxIdleTime, maxIdleTime);
	}

	private static class IdleChannel {
		final String uri;
		final Channel channel;
		final long start;

		IdleChannel(final String uri, final Channel channel) {
			this.uri = uri;
			this.channel = channel;
			this.start = System.currentTimeMillis();
		}

		@Override
		public boolean equals(final Object o) {
			if (this == o)
				return true;
			if (!(o instanceof IdleChannel))
				return false;

			final IdleChannel that = (IdleChannel) o;

			if (channel != null ? !channel.equals(that.channel)
					: that.channel != null)
				return false;

			return true;
		}

		@Override
		public int hashCode() {
			return channel != null ? channel.hashCode() : 0;
		}
	}

	private class IdleChannelDetector extends TimerTask {
		@Override
		public void run() {
			try {
				if (isClosed.get())
					return;

				final List<IdleChannel> channelsInTimeout = new ArrayList<IdleChannel>();
				final long currentTime = System.currentTimeMillis();

				for (final IdleChannel idleChannel : channel2IdleChannel
						.values()) {
					final long age = currentTime - idleChannel.start;
					if (age > maxIdleTime) {

						// store in an unsynchronized list to minimize the
						// impact on the ConcurrentHashMap.
						channelsInTimeout.add(idleChannel);
					}
				}
				final long endConcurrentLoop = System.currentTimeMillis();

				for (final IdleChannel idleChannel : channelsInTimeout) {
					final Object attachment = idleChannel.channel.getPipeline()
							.getContext(NettyAsyncHttpProvider.class)
							.getAttachment();
					if (attachment != null) {
						if (NettyResponseFuture.class
								.isAssignableFrom(attachment.getClass())) {
							final NettyResponseFuture<?> future = (NettyResponseFuture<?>) attachment;

							if (!future.isDone() && !future.isCancelled()) {

								continue;
							}
						}
					}

					if (remove(idleChannel)) {

						close(idleChannel.channel);
					}
				}

			} catch (final Throwable t) {

			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean offer(final String uri, final Channel channel) {
		if (isClosed.get())
			return false;

		if (!sslConnectionPoolEnabled && uri.startsWith("https")) {
			return false;
		}

		channel.getPipeline().getContext(NettyAsyncHttpProvider.class)
				.setAttachment(new NettyAsyncHttpProvider.DiscardEvent());

		ConcurrentLinkedQueue<IdleChannel> idleConnectionForHost = connectionsPool
				.get(uri);
		if (idleConnectionForHost == null) {
			final ConcurrentLinkedQueue<IdleChannel> newPool = new ConcurrentLinkedQueue<IdleChannel>();
			idleConnectionForHost = connectionsPool.putIfAbsent(uri, newPool);
			if (idleConnectionForHost == null)
				idleConnectionForHost = newPool;
		}

		boolean added;
		final int size = idleConnectionForHost.size();
		if (maxConnectionPerHost == -1 || size < maxConnectionPerHost) {
			final IdleChannel idleChannel = new IdleChannel(uri, channel);
			synchronized (idleConnectionForHost) {
				added = idleConnectionForHost.add(idleChannel);

				if (channel2IdleChannel.put(channel, idleChannel) != null) {

				}
			}
		} else {

			added = false;
		}
		return added;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Channel poll(final String uri) {
		if (!sslConnectionPoolEnabled && uri.startsWith("https")) {
			return null;
		}

		IdleChannel idleChannel = null;
		final ConcurrentLinkedQueue<IdleChannel> idleConnectionForHost = connectionsPool
				.get(uri);
		if (idleConnectionForHost != null) {
			boolean poolEmpty = false;
			while (!poolEmpty && idleChannel == null) {
				if (idleConnectionForHost.size() > 0) {
					synchronized (idleConnectionForHost) {
						idleChannel = idleConnectionForHost.poll();
						if (idleChannel != null) {
							channel2IdleChannel.remove(idleChannel.channel);
						}
					}
				}

				if (idleChannel == null) {
					poolEmpty = true;
				} else if (!idleChannel.channel.isConnected()
						|| !idleChannel.channel.isOpen()) {
					idleChannel = null;

				}
			}
		}
		return idleChannel != null ? idleChannel.channel : null;
	}

	private boolean remove(final IdleChannel pooledChannel) {
		if (pooledChannel == null || isClosed.get())
			return false;

		boolean isRemoved = false;
		final ConcurrentLinkedQueue<IdleChannel> pooledConnectionForHost = connectionsPool
				.get(pooledChannel.uri);
		if (pooledConnectionForHost != null) {
			isRemoved = pooledConnectionForHost.remove(pooledChannel);
		}
		isRemoved |= channel2IdleChannel.remove(pooledChannel.channel) != null;
		return isRemoved;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean removeAll(final Channel channel) {
		return !isClosed.get() && remove(channel2IdleChannel.get(channel));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canCacheConnection() {
		if (!isClosed.get() && maxTotalConnections != -1
				&& channel2IdleChannel.size() >= maxTotalConnections) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void destroy() {
		if (isClosed.getAndSet(true))
			return;

		// stop timer
		idleConnectionDetector.cancel();
		idleConnectionDetector.purge();

		for (final Channel channel : channel2IdleChannel.keySet()) {
			close(channel);
		}
		connectionsPool.clear();
		channel2IdleChannel.clear();
	}

	private void close(final Channel channel) {
		try {
			channel.getPipeline().getContext(NettyAsyncHttpProvider.class)
					.setAttachment(new NettyAsyncHttpProvider.DiscardEvent());
			channel.close();
		} catch (final Throwable t) {
			// noop
		}
	}

	@Override
	public final String toString() {
		return String.format("NettyConnectionPool: {pool-size: %d}",
				channel2IdleChannel.size());
	}
}
