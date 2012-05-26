/*
 * Copyright 2010 Ning, Inc.
 *
 * Ning licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 */
package com.ning.http.client.providers.netty;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ssl.HostnameVerifier;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.ssl.SslHandler;

import com.ning.http.client.AsyncHandler;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Request;
import com.ning.http.util.AllowAllHostnameVerifier;

/**
 * Non Blocking connect.
 */
final class NettyConnectListener<T> implements ChannelFutureListener {

	private final AsyncHttpClientConfig config;
	private final NettyResponseFuture<T> future;
	private final HttpRequest nettyRequest;
	private final AtomicBoolean handshakeDone = new AtomicBoolean(false);

	private NettyConnectListener(final AsyncHttpClientConfig config,
			final NettyResponseFuture<T> future, final HttpRequest nettyRequest) {
		this.config = config;
		this.future = future;
		this.nettyRequest = nettyRequest;
	}

	public NettyResponseFuture<T> future() {
		return future;
	}

	@Override
	public final void operationComplete(final ChannelFuture f) throws Exception {
		if (f.isSuccess()) {
			final Channel channel = f.getChannel();
			channel.getPipeline().getContext(NettyAsyncHttpProvider.class)
					.setAttachment(future);
			final SslHandler sslHandler = (SslHandler) channel.getPipeline()
					.get(NettyAsyncHttpProvider.SSL_HANDLER);
			if (!handshakeDone.getAndSet(true) && (sslHandler != null)) {
				((SslHandler) channel.getPipeline().get(
						NettyAsyncHttpProvider.SSL_HANDLER)).handshake()
						.addListener(this);
				return;
			}

			final HostnameVerifier v = config.getHostnameVerifier();
			if (sslHandler != null
					&& !AllowAllHostnameVerifier.class.isAssignableFrom(v
							.getClass())) {
				// TODO: channel.getRemoteAddress()).getHostName() is very
				// expensive. Should cache the result.
				if (!v.verify(
						InetSocketAddress.class
								.cast(channel.getRemoteAddress()).getHostName(),
						sslHandler.getEngine().getSession())) {
					throw new ConnectException("HostnameVerifier exception.");
				}
			}

			future.provider().writeRequest(f.getChannel(), config, future,
					nettyRequest);
		} else {
			final Throwable cause = f.getCause();

			if (future.canRetry()
					&& cause != null
					&& (NettyAsyncHttpProvider
							.abortOnDisconnectException(cause)
							|| ClosedChannelException.class
									.isAssignableFrom(cause.getClass()) || future
							.getState() != NettyResponseFuture.STATE.NEW)) {

				if (future.provider().remotelyClosed(f.getChannel(), future)) {
					return;
				}
			}

			final boolean printCause = f.getCause() != null
					&& cause.getMessage() != null;
			final ConnectException e = new ConnectException(
					printCause ? cause.getMessage() + " to "
							+ future.getURI().toString() : future.getURI()
							.toString());
			if (cause != null) {
				e.initCause(cause);
			}
			future.abort(e);
		}
	}

	public static class Builder<T> {
		private final AsyncHttpClientConfig config;

		private final Request request;
		private final AsyncHandler<T> asyncHandler;
		private NettyResponseFuture<T> future;
		private final NettyAsyncHttpProvider provider;
		private final ChannelBuffer buffer;

		public Builder(final AsyncHttpClientConfig config,
				final Request request, final AsyncHandler<T> asyncHandler,
				final NettyAsyncHttpProvider provider,
				final ChannelBuffer buffer) {

			this.config = config;
			this.request = request;
			this.asyncHandler = asyncHandler;
			this.future = null;
			this.provider = provider;
			this.buffer = buffer;
		}

		public Builder(final AsyncHttpClientConfig config,
				final Request request, final AsyncHandler<T> asyncHandler,
				final NettyResponseFuture<T> future,
				final NettyAsyncHttpProvider provider,
				final ChannelBuffer buffer) {

			this.config = config;
			this.request = request;
			this.asyncHandler = asyncHandler;
			this.future = future;
			this.provider = provider;
			this.buffer = buffer;
		}

		public NettyConnectListener<T> build(final URI uri) throws IOException {
			final HttpRequest nettyRequest = NettyAsyncHttpProvider
					.buildRequest(config, request, uri, true, buffer);
			if (future == null) {
				future = NettyAsyncHttpProvider.newFuture(uri, request,
						asyncHandler, nettyRequest, config, provider);
			} else {
				future.setNettyRequest(nettyRequest);
				future.setRequest(request);
			}
			return new NettyConnectListener<T>(config, future, nettyRequest);
		}
	}
}
