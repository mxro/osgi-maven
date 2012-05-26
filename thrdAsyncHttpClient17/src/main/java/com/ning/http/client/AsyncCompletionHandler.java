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
package com.ning.http.client;

/**
 * An {@link AsyncHandler} augmented with an {@link #onCompleted(Response)}
 * convenience method which gets called when the {@link Response} processing is
 * finished. This class also implement the {@link ProgressAsyncHandler}
 * callback, all doing nothing except returning
 * {@link com.ning.http.client.AsyncHandler.STATE#CONTINUE}
 * 
 * @param <T>
 *            Type of the value that will be returned by the associated
 *            {@link java.util.concurrent.Future}
 */
public abstract class AsyncCompletionHandler<T> implements AsyncHandler<T>,
		ProgressAsyncHandler<T> {

	private final Response.ResponseBuilder builder = new Response.ResponseBuilder();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public STATE onBodyPartReceived(final HttpResponseBodyPart content)
			throws Exception {
		builder.accumulate(content);
		return STATE.CONTINUE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public STATE onStatusReceived(final HttpResponseStatus status)
			throws Exception {
		builder.reset();
		builder.accumulate(status);
		return STATE.CONTINUE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public STATE onHeadersReceived(final HttpResponseHeaders headers)
			throws Exception {
		builder.accumulate(headers);
		return STATE.CONTINUE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final T onCompleted() throws Exception {
		return onCompleted(builder.build());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onThrowable(final Throwable t) {
		t.printStackTrace();
	}

	/**
	 * Invoked once the HTTP response processing is finished.
	 * <p/>
	 * <p/>
	 * Gets always invoked as last callback method.
	 * 
	 * @param response
	 *            The {@link Response}
	 * @return T Value that will be returned by the associated
	 *         {@link java.util.concurrent.Future}
	 * @throws Exception
	 *             if something wrong happens
	 */
	abstract public T onCompleted(Response response) throws Exception;

	/**
	 * Invoked when the content (a {@link java.io.File}, {@link String} or
	 * {@link java.io.FileInputStream} has been fully written on the I/O socket.
	 * 
	 * @return a {@link com.ning.http.client.AsyncHandler.STATE} telling to
	 *         CONTINUE or ABORT the current processing.
	 */
	@Override
	public STATE onHeaderWriteCompleted() {
		return STATE.CONTINUE;
	}

	/**
	 * Invoked when the content (a {@link java.io.File}, {@link String} or
	 * {@link java.io.FileInputStream} has been fully written on the I/O socket.
	 * 
	 * @return a {@link com.ning.http.client.AsyncHandler.STATE} telling to
	 *         CONTINUE or ABORT the current processing.
	 */
	@Override
	public STATE onContentWriteCompleted() {
		return STATE.CONTINUE;
	}

	/**
	 * Invoked when the I/O operation associated with the {@link Request} body
	 * as been progressed.
	 * 
	 * @param amount
	 *            The amount of bytes to transfer.
	 * @param current
	 *            The amount of bytes transferred
	 * @param total
	 *            The total number of bytes transferred
	 * @return a {@link com.ning.http.client.AsyncHandler.STATE} telling to
	 *         CONTINUE or ABORT the current processing.
	 */
	@Override
	public STATE onContentWriteProgress(final long amount, final long current,
			final long total) {
		return STATE.CONTINUE;
	}
}
