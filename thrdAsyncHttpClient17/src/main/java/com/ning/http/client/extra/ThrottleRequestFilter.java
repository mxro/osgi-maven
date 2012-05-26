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
package com.ning.http.client.extra;

import com.ning.http.client.AsyncHandler;
import com.ning.http.client.HttpResponseBodyPart;
import com.ning.http.client.HttpResponseHeaders;
import com.ning.http.client.HttpResponseStatus;
import com.ning.http.client.filter.FilterContext;
import com.ning.http.client.filter.FilterException;
import com.ning.http.client.filter.RequestFilter;


import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * A {@link com.ning.http.client.filter.RequestFilter} throttles requests and block when the number of permits is reached, waiting for
 * the response to arrives before executing the next request.
 */
public class ThrottleRequestFilter implements RequestFilter {
   
    private final int maxConnections;
    private final Semaphore available;
    private final int maxWait;

    public ThrottleRequestFilter(final int maxConnections) {
        this.maxConnections = maxConnections;
        this.maxWait = Integer.MAX_VALUE;
        available = new Semaphore(maxConnections, true);
    }

    public ThrottleRequestFilter(final int maxConnections, final int maxWait) {
        this.maxConnections = maxConnections;
        this.maxWait = maxWait;
        available = new Semaphore(maxConnections, true);
    }

    /**
     * {@inheritDoc}
     */
    /* @Override */
    @Override
	public FilterContext filter(final FilterContext ctx) throws FilterException {

        try {
           
            if (!available.tryAcquire(maxWait, TimeUnit.MILLISECONDS)) {
                throw new FilterException(
                        String.format("No slot available for processing Request %s with AsyncHandler %s",
                                ctx.getRequest(), ctx.getAsyncHandler()));
            }
            ;
        } catch (final InterruptedException e) {
            throw new FilterException(
                    String.format("Interrupted Request %s with AsyncHandler %s", ctx.getRequest(), ctx.getAsyncHandler()));
        }

        return new FilterContext.FilterContextBuilder(ctx).asyncHandler(new AsyncHandlerWrapper(ctx.getAsyncHandler())).build();
    }

    private class AsyncHandlerWrapper<T> implements AsyncHandler {

        private final AsyncHandler<T> asyncHandler;

        public AsyncHandlerWrapper(final AsyncHandler<T> asyncHandler) {
            this.asyncHandler = asyncHandler;
        }

        /**
         * {@inheritDoc}
         */
        /* @Override */
        @Override
		public void onThrowable(final Throwable t) {
            try {
                asyncHandler.onThrowable(t);
            } finally {
                available.release();
               
            }
        }

        /**
         * {@inheritDoc}
         */
        /* @Override */
        @Override
		public STATE onBodyPartReceived(final HttpResponseBodyPart bodyPart) throws Exception {
            return asyncHandler.onBodyPartReceived(bodyPart);
        }

        /**
         * {@inheritDoc}
         */
        /* @Override */
        @Override
		public STATE onStatusReceived(final HttpResponseStatus responseStatus) throws Exception {
            return asyncHandler.onStatusReceived(responseStatus);
        }

        /**
         * {@inheritDoc}
         */
        /* @Override */
        @Override
		public STATE onHeadersReceived(final HttpResponseHeaders headers) throws Exception {
            return asyncHandler.onHeadersReceived(headers);
        }

        /**
         * {@inheritDoc}
         */
        /* @Override */
        @Override
		public T onCompleted() throws Exception {
            available.release();
           
            return asyncHandler.onCompleted();
        }
    }
}
