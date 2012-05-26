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
package com.ning.http.client.providers.jdk;

import java.net.HttpURLConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import com.ning.http.client.AsyncHandler;
import com.ning.http.client.listenable.AbstractListenableFuture;

public class JDKFuture<V> extends AbstractListenableFuture<V> {

	protected Future<V> innerFuture;
	protected final AsyncHandler<V> asyncHandler;
	protected final int responseTimeoutInMs;
	protected final AtomicBoolean cancelled = new AtomicBoolean(false);
	protected final AtomicBoolean timedOut = new AtomicBoolean(false);
	protected final AtomicBoolean isDone = new AtomicBoolean(false);
	protected final AtomicReference<Throwable> exception = new AtomicReference<Throwable>();
	protected final AtomicLong touch = new AtomicLong(
			System.currentTimeMillis());
	protected final AtomicBoolean contentProcessed = new AtomicBoolean(false);
	protected final HttpURLConnection urlConnection;
	private boolean writeHeaders;
	private boolean writeBody;

	public JDKFuture(final AsyncHandler<V> asyncHandler,
			final int responseTimeoutInMs, final HttpURLConnection urlConnection) {
		this.asyncHandler = asyncHandler;
		this.responseTimeoutInMs = responseTimeoutInMs;
		this.urlConnection = urlConnection;
		writeHeaders = true;
		writeBody = true;
	}

	protected void setInnerFuture(final Future<V> innerFuture) {
		this.innerFuture = innerFuture;
	}

	@Override
	public void done(final Callable callable) {
		isDone.set(true);
		super.done();
	}

	@Override
	public void abort(final Throwable t) {
		exception.set(t);
		if (innerFuture != null) {
			innerFuture.cancel(true);
		}
		if (!timedOut.get() && !cancelled.get()) {
			try {
				asyncHandler.onThrowable(t);
			} catch (final Throwable te) {

			}
		}
		super.done();
	}

	@Override
	public void content(final V v) {
	}

	@Override
	public boolean cancel(final boolean mayInterruptIfRunning) {
		if (!cancelled.get() && innerFuture != null) {
			urlConnection.disconnect();
			try {
				asyncHandler.onThrowable(new CancellationException());
			} catch (final Throwable te) {

			}
			cancelled.set(true);
			super.done();
			return innerFuture.cancel(mayInterruptIfRunning);
		} else {
			super.done();
			return false;
		}
	}

	@Override
	public boolean isCancelled() {
		if (innerFuture != null) {
			return innerFuture.isCancelled();
		} else {
			return false;
		}
	}

	@Override
	public boolean isDone() {
		contentProcessed.set(true);
		return innerFuture.isDone();
	}

	@Override
	public V get() throws InterruptedException, ExecutionException {
		try {
			return get(responseTimeoutInMs, TimeUnit.MILLISECONDS);
		} catch (final TimeoutException e) {
			throw new ExecutionException(e);
		}
	}

	@Override
	public V get(final long timeout, final TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		V content = null;
		try {
			if (innerFuture != null) {
				content = innerFuture.get(timeout, unit);
			}
		} catch (final TimeoutException t) {
			if (!contentProcessed.get()
					&& timeout != -1
					&& ((System.currentTimeMillis() - touch.get()) <= responseTimeoutInMs)) {
				return get(timeout, unit);
			}

			if (exception.get() == null) {
				timedOut.set(true);
				throw new ExecutionException(new TimeoutException(
						String.format("No response received after %s",
								responseTimeoutInMs)));
			}
		} catch (final CancellationException ce) {
		}

		if (exception.get() != null) {
			throw new ExecutionException(exception.get());
		}
		return content;
	}

	/**
	 * Is the Future still valid
	 * 
	 * @return <code>true</code> if response has expired and should be
	 *         terminated.
	 */
	public boolean hasExpired() {
		return responseTimeoutInMs != -1
				&& ((System.currentTimeMillis() - touch.get()) > responseTimeoutInMs);
	}

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
	@Override
	public void touch() {
		touch.set(System.currentTimeMillis());
	}

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
	@Override
	public boolean getAndSetWriteHeaders(final boolean writeHeaders) {
		final boolean b = this.writeHeaders;
		this.writeHeaders = writeHeaders;
		return b;
	}

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
	@Override
	public boolean getAndSetWriteBody(final boolean writeBody) {
		final boolean b = this.writeBody;
		this.writeBody = writeBody;
		return b;
	}
}
