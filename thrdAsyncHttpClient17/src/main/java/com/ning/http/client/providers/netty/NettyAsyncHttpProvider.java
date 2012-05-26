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
 */
package com.ning.http.client.providers.netty;

import static com.ning.http.util.AsyncHttpProviderUtils.DEFAULT_CHARSET;
import static org.jboss.netty.channel.Channels.pipeline;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ssl.SSLEngine;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureProgressListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.DefaultChannelFuture;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.FileRegion;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.oio.OioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.CookieEncoder;
import org.jboss.netty.handler.codec.http.DefaultCookie;
import org.jboss.netty.handler.codec.http.DefaultHttpChunkTrailer;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpChunkTrailer;
import org.jboss.netty.handler.codec.http.HttpClientCodec;
import org.jboss.netty.handler.codec.http.HttpContentCompressor;
import org.jboss.netty.handler.codec.http.HttpContentDecompressor;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpRequestEncoder;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseDecoder;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.ssl.SslHandler;
import org.jboss.netty.handler.stream.ChunkedFile;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;

import com.ning.http.client.AsyncHandler;
import com.ning.http.client.AsyncHandler.STATE;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.AsyncHttpProvider;
import com.ning.http.client.Body;
import com.ning.http.client.BodyGenerator;
import com.ning.http.client.ConnectionsPool;
import com.ning.http.client.Cookie;
import com.ning.http.client.FluentCaseInsensitiveStringsMap;
import com.ning.http.client.HttpResponseBodyPart;
import com.ning.http.client.HttpResponseHeaders;
import com.ning.http.client.HttpResponseStatus;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.MaxRedirectException;
import com.ning.http.client.PerRequestConfig;
import com.ning.http.client.ProgressAsyncHandler;
import com.ning.http.client.ProxyServer;
import com.ning.http.client.RandomAccessBody;
import com.ning.http.client.Realm;
import com.ning.http.client.Request;
import com.ning.http.client.RequestBuilder;
import com.ning.http.client.Response;
import com.ning.http.client.filter.FilterContext;
import com.ning.http.client.filter.FilterException;
import com.ning.http.client.filter.IOExceptionFilter;
import com.ning.http.client.filter.ResponseFilter;
import com.ning.http.client.generators.InputStreamBodyGenerator;
import com.ning.http.client.listener.TransferCompletionHandler;
import com.ning.http.client.ntlm.NTLMEngine;
import com.ning.http.client.ntlm.NTLMEngineException;
import com.ning.http.client.providers.netty.spnego.SpnegoEngine;
import com.ning.http.client.websocket.WebSocketUpgradeHandler;
import com.ning.http.multipart.MultipartBody;
import com.ning.http.multipart.MultipartRequestEntity;
import com.ning.http.util.AsyncHttpProviderUtils;
import com.ning.http.util.AuthenticatorUtils;
import com.ning.http.util.CleanupChannelGroup;
import com.ning.http.util.ProxyUtils;
import com.ning.http.util.SslUtils;
import com.ning.http.util.UTF8UrlEncoder;

public class NettyAsyncHttpProvider extends SimpleChannelUpstreamHandler
		implements AsyncHttpProvider {
	private final static String WEBSOCKET_KEY = "Sec-WebSocket-Key";
	private final static String HTTP_HANDLER = "httpHandler";
	protected final static String SSL_HANDLER = "sslHandler";
	private final static String HTTPS = "https";
	private final static String HTTP = "http";
	private static final String WEBSOCKET = "ws";

	private final ClientBootstrap plainBootstrap;
	private final ClientBootstrap secureBootstrap;
	private final ClientBootstrap webSocketBootstrap;
	private final static int MAX_BUFFERED_BYTES = 8192;
	private final AsyncHttpClientConfig config;
	private final AtomicBoolean isClose = new AtomicBoolean(false);
	private final ClientSocketChannelFactory socketChannelFactory;

	private final ChannelGroup openChannels = new CleanupChannelGroup(
			"asyncHttpClient") {
		@Override
		public boolean remove(final Object o) {
			final boolean removed = super.remove(o);
			if (removed && trackConnections) {
				freeConnections.release();
			}
			return removed;
		}
	};
	private final ConnectionsPool<String, Channel> connectionsPool;
	private Semaphore freeConnections = null;
	private final NettyAsyncHttpProviderConfig asyncHttpProviderConfig;
	private boolean executeConnectAsync = true;
	public static final ThreadLocal<Boolean> IN_IO_THREAD = new ThreadLocalBoolean();
	private final boolean trackConnections;
	private final boolean useRawUrl;
	private final static NTLMEngine ntlmEngine = new NTLMEngine();
	private final static SpnegoEngine spnegoEngine = new SpnegoEngine();
	private final Protocol httpProtocol = new HttpProtocol();

	public NettyAsyncHttpProvider(final AsyncHttpClientConfig config) {

		if (config.getAsyncHttpProviderConfig() != null
				&& NettyAsyncHttpProviderConfig.class.isAssignableFrom(config
						.getAsyncHttpProviderConfig().getClass())) {
			asyncHttpProviderConfig = NettyAsyncHttpProviderConfig.class
					.cast(config.getAsyncHttpProviderConfig());
		} else {
			asyncHttpProviderConfig = new NettyAsyncHttpProviderConfig();
		}

		if (asyncHttpProviderConfig
				.getProperty(NettyAsyncHttpProviderConfig.USE_BLOCKING_IO) != null) {
			socketChannelFactory = new OioClientSocketChannelFactory(
					config.executorService());
		} else {
			ExecutorService e;
			final Object o = asyncHttpProviderConfig
					.getProperty(NettyAsyncHttpProviderConfig.BOSS_EXECUTOR_SERVICE);
			if (o != null
					&& ExecutorService.class.isAssignableFrom(o.getClass())) {
				e = ExecutorService.class.cast(o);
			} else {
				e = Executors.newCachedThreadPool();
			}
			final int numWorkers = config.getIoThreadMultiplier()
					* Runtime.getRuntime().availableProcessors();

			socketChannelFactory = new NioClientSocketChannelFactory(e,
					config.executorService(), numWorkers);
		}
		plainBootstrap = new ClientBootstrap(socketChannelFactory);
		secureBootstrap = new ClientBootstrap(socketChannelFactory);
		webSocketBootstrap = new ClientBootstrap(socketChannelFactory);
		configureNetty();

		this.config = config;

		// This is dangerous as we can't catch a wrong typed ConnectionsPool
		ConnectionsPool<String, Channel> cp = (ConnectionsPool<String, Channel>) config
				.getConnectionsPool();
		if (cp == null && config.getAllowPoolingConnection()) {
			cp = new NettyConnectionsPool(this);
		} else if (cp == null) {
			cp = new NonConnectionsPool();
		}
		this.connectionsPool = cp;

		if (config.getMaxTotalConnections() != -1) {
			trackConnections = true;
			freeConnections = new Semaphore(config.getMaxTotalConnections());
		} else {
			trackConnections = false;
		}

		useRawUrl = config.isUseRawUrl();
	}

	@Override
	public String toString() {
		return String
				.format("NettyAsyncHttpProvider:\n\t- maxConnections: %d\n\t- openChannels: %s\n\t- connectionPools: %s",
						config.getMaxTotalConnections()
								- freeConnections.availablePermits(),
						openChannels.toString(), connectionsPool.toString());
	}

	void configureNetty() {
		if (asyncHttpProviderConfig != null) {
			for (final Entry<String, Object> entry : asyncHttpProviderConfig
					.propertiesSet()) {
				plainBootstrap.setOption(entry.getKey(), entry.getValue());
			}
		}

		plainBootstrap.setPipelineFactory(new ChannelPipelineFactory() {

			/* @Override */
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				final ChannelPipeline pipeline = pipeline();

				pipeline.addLast(HTTP_HANDLER, new HttpClientCodec());

				if (config.getRequestCompressionLevel() > 0) {
					pipeline.addLast("deflater", new HttpContentCompressor(
							config.getRequestCompressionLevel()));
				}

				if (config.isCompressionEnabled()) {
					pipeline.addLast("inflater", new HttpContentDecompressor());
				}
				pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());
				pipeline.addLast("httpProcessor", NettyAsyncHttpProvider.this);
				return pipeline;
			}
		});
		DefaultChannelFuture.setUseDeadLockChecker(false);

		if (asyncHttpProviderConfig != null) {
			final Object value = asyncHttpProviderConfig
					.getProperty(NettyAsyncHttpProviderConfig.EXECUTE_ASYNC_CONNECT);
			if (value != null
					&& Boolean.class.isAssignableFrom(value.getClass())) {
				executeConnectAsync = Boolean.class.cast(value);
			} else if (asyncHttpProviderConfig
					.getProperty(NettyAsyncHttpProviderConfig.DISABLE_NESTED_REQUEST) != null) {
				DefaultChannelFuture.setUseDeadLockChecker(true);
			}
		}

		webSocketBootstrap.setPipelineFactory(new ChannelPipelineFactory() {

			/* @Override */
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				final ChannelPipeline pipeline = pipeline();
				pipeline.addLast("ws-decoder", new HttpResponseDecoder());
				pipeline.addLast("ws-encoder", new HttpRequestEncoder());
				pipeline.addLast("httpProcessor", NettyAsyncHttpProvider.this);
				return pipeline;
			}
		});
	}

	void constructSSLPipeline(final NettyConnectListener<?> cl) {

		secureBootstrap.setPipelineFactory(new ChannelPipelineFactory() {

			/* @Override */
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				final ChannelPipeline pipeline = pipeline();

				try {
					pipeline.addLast(SSL_HANDLER, new SslHandler(
							createSSLEngine()));
				} catch (final Throwable ex) {
					abort(cl.future(), ex);
				}

				pipeline.addLast(HTTP_HANDLER, new HttpClientCodec());

				if (config.isCompressionEnabled()) {
					pipeline.addLast("inflater", new HttpContentDecompressor());
				}
				pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());
				pipeline.addLast("httpProcessor", NettyAsyncHttpProvider.this);
				return pipeline;
			}
		});

		if (asyncHttpProviderConfig != null) {
			for (final Entry<String, Object> entry : asyncHttpProviderConfig
					.propertiesSet()) {
				secureBootstrap.setOption(entry.getKey(), entry.getValue());
			}
		}
	}

	private Channel lookupInCache(final URI uri) {
		final Channel channel = connectionsPool.poll(AsyncHttpProviderUtils
				.getBaseUrl(uri));

		if (channel != null) {

			try {
				// Always make sure the channel who got cached support the
				// proper protocol. It could
				// only occurs when a HttpMethod.CONNECT is used agains a proxy
				// that require upgrading from http to
				// https.
				return verifyChannelPipeline(channel, uri.getScheme());
			} catch (final Exception ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}

	private SSLEngine createSSLEngine() throws IOException,
			GeneralSecurityException {
		SSLEngine sslEngine = config.getSSLEngineFactory().newSSLEngine();
		if (sslEngine == null) {
			sslEngine = SslUtils.getSSLEngine();
		}
		return sslEngine;
	}

	private Channel verifyChannelPipeline(final Channel channel,
			final String scheme) throws IOException, GeneralSecurityException {

		if (channel.getPipeline().get(SSL_HANDLER) != null
				&& HTTP.equalsIgnoreCase(scheme)) {
			channel.getPipeline().remove(SSL_HANDLER);
		} else if (channel.getPipeline().get(HTTP_HANDLER) != null
				&& HTTP.equalsIgnoreCase(scheme)) {
			return channel;
		} else if (channel.getPipeline().get(SSL_HANDLER) == null
				&& HTTPS.equalsIgnoreCase(scheme)) {
			channel.getPipeline().addFirst(SSL_HANDLER,
					new SslHandler(createSSLEngine()));
		}
		return channel;
	}

	protected final <T> void writeRequest(final Channel channel,
			final AsyncHttpClientConfig config,
			final NettyResponseFuture<T> future, final HttpRequest nettyRequest) {
		try {
			/**
			 * If the channel is dead because it was pooled and the remote
			 * server decided to close it, we just let it go and the
			 * closeChannel do it's work.
			 */
			if (!channel.isOpen() || !channel.isConnected()) {
				return;
			}

			Body body = null;
			if (!future.getNettyRequest().getMethod()
					.equals(HttpMethod.CONNECT)) {
				final BodyGenerator bg = future.getRequest().getBodyGenerator();
				if (bg != null) {
					// Netty issue with chunking.
					if (InputStreamBodyGenerator.class.isAssignableFrom(bg
							.getClass())) {
						InputStreamBodyGenerator.class.cast(bg)
								.patchNettyChunkingIssue(true);
					}

					try {
						body = bg.createBody();
					} catch (final IOException ex) {
						throw new IllegalStateException(ex);
					}
					final long length = body.getContentLength();
					if (length >= 0) {
						nettyRequest.setHeader(
								HttpHeaders.Names.CONTENT_LENGTH, length);
					} else {
						nettyRequest.setHeader(
								HttpHeaders.Names.TRANSFER_ENCODING,
								HttpHeaders.Values.CHUNKED);
					}
				} else {
					body = null;
				}
			}

			if (TransferCompletionHandler.class.isAssignableFrom(future
					.getAsyncHandler().getClass())) {

				final FluentCaseInsensitiveStringsMap h = new FluentCaseInsensitiveStringsMap();
				for (final String s : future.getNettyRequest().getHeaderNames()) {
					for (final String header : future.getNettyRequest()
							.getHeaders(s)) {
						h.add(s, header);
					}
				}

				TransferCompletionHandler.class.cast(future.getAsyncHandler())
						.transferAdapter(
								new NettyTransferAdapter(h, nettyRequest
										.getContent(), future.getRequest()
										.getFile()));
			}

			// Leave it to true.
			if (future.getAndSetWriteHeaders(true)) {
				try {
					channel.write(nettyRequest).addListener(
							new ProgressListener(true,
									future.getAsyncHandler(), future));
				} catch (final Throwable cause) {

					try {
						channel.close();
					} catch (final RuntimeException ex) {

					}
					return;
				}
			}

			if (future.getAndSetWriteBody(true)) {
				if (!future.getNettyRequest().getMethod()
						.equals(HttpMethod.CONNECT)) {

					if (future.getRequest().getFile() != null) {
						final File file = future.getRequest().getFile();
						long fileLength = 0;
						final RandomAccessFile raf = new RandomAccessFile(file,
								"r");

						try {
							fileLength = raf.length();

							ChannelFuture writeFuture;
							if (channel.getPipeline().get(SslHandler.class) != null) {
								writeFuture = channel.write(new ChunkedFile(
										raf, 0, fileLength, 8192));
							} else {
								final FileRegion region = new OptimizedFileRegion(
										raf, 0, fileLength);
								writeFuture = channel.write(region);
							}
							writeFuture.addListener(new ProgressListener(false,
									future.getAsyncHandler(), future));
						} catch (final IOException ex) {
							if (raf != null) {
								try {
									raf.close();
								} catch (final IOException e) {
								}
							}
							throw ex;
						}
					} else if (body != null
							|| future.getRequest().getParts() != null) {
						/**
						 * TODO: AHC-78: SSL + zero copy isn't supported by the
						 * MultiPart class and pretty complex to implements.
						 */
						if (future.getRequest().getParts() != null) {
							final String boundary = future.getNettyRequest()
									.getHeader("Content-Type");
							final String length = future.getNettyRequest()
									.getHeader("Content-Length");
							body = new MultipartBody(future.getRequest()
									.getParts(), boundary, length);
						}

						ChannelFuture writeFuture;
						if (channel.getPipeline().get(SslHandler.class) == null
								&& (body instanceof RandomAccessBody)) {
							final BodyFileRegion bodyFileRegion = new BodyFileRegion(
									(RandomAccessBody) body);
							writeFuture = channel.write(bodyFileRegion);
						} else {
							final BodyChunkedInput bodyChunkedInput = new BodyChunkedInput(
									body);
							writeFuture = channel.write(bodyChunkedInput);
						}

						final Body b = body;
						writeFuture.addListener(new ProgressListener(false,
								future.getAsyncHandler(), future) {
							@Override
							public void operationComplete(final ChannelFuture cf) {
								try {
									b.close();
								} catch (final IOException e) {

								}
								super.operationComplete(cf);
							}
						});
					}
				}
			}
		} catch (final Throwable ioe) {
			try {
				channel.close();
			} catch (final RuntimeException ex) {

			}
		}

		try {
			future.touch();
			final int delay = requestTimeout(config, future.getRequest()
					.getPerRequestConfig());
			if (delay != -1 && !future.isDone() && !future.isCancelled()) {
				final ReaperFuture reaperFuture = new ReaperFuture(channel,
						future);
				final Future scheduledFuture = config.reaper()
						.scheduleAtFixedRate(reaperFuture, 0, delay,
								TimeUnit.MILLISECONDS);
				reaperFuture.setScheduledFuture(scheduledFuture);
				future.setReaperFuture(reaperFuture);
			}
		} catch (final RejectedExecutionException ex) {
			abort(future, ex);
		}

	}

	private static boolean isProxyServer(final AsyncHttpClientConfig config,
			final Request request) {
		return request.getProxyServer() != null
				|| config.getProxyServer() != null;
	}

	protected final static HttpRequest buildRequest(
			final AsyncHttpClientConfig config, final Request request,
			final URI uri, final boolean allowConnect,
			final ChannelBuffer buffer) throws IOException {

		String method = request.getMethod();
		if (allowConnect
				&& (isProxyServer(config, request) && HTTPS
						.equalsIgnoreCase(uri.getScheme()))) {
			method = HttpMethod.CONNECT.toString();
		}
		return construct(config, request, new HttpMethod(method), uri, buffer);
	}

	@SuppressWarnings("deprecation")
	private static HttpRequest construct(final AsyncHttpClientConfig config,
			final Request request, final HttpMethod m, final URI uri,
			final ChannelBuffer buffer) throws IOException {

		String host = AsyncHttpProviderUtils.getHost(uri);

		if (request.getVirtualHost() != null) {
			host = request.getVirtualHost();
		}

		HttpRequest nettyRequest;
		if (m.equals(HttpMethod.CONNECT)) {
			nettyRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_0, m,
					AsyncHttpProviderUtils.getAuthority(uri));
		} else {
			StringBuilder path = null;
			if (isProxyServer(config, request))
				path = new StringBuilder(uri.toString());
			else {
				path = new StringBuilder(uri.getRawPath());
				if (uri.getQuery() != null) {
					path.append("?").append(uri.getRawQuery());
				}
			}
			nettyRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, m,
					path.toString());
		}
		final boolean webSocket = uri.getScheme().equalsIgnoreCase(WEBSOCKET);
		if (webSocket) {
			nettyRequest.addHeader(HttpHeaders.Names.UPGRADE,
					HttpHeaders.Values.WEBSOCKET);
			nettyRequest.addHeader(HttpHeaders.Names.CONNECTION,
					HttpHeaders.Values.UPGRADE);
			nettyRequest.addHeader("Sec-WebSocket-Origin",
					"http://" + uri.getHost());
			nettyRequest.addHeader(WEBSOCKET_KEY, WebSocketUtil.getKey());
			nettyRequest.addHeader("Sec-WebSocket-Version", "8");
		}

		if (host != null) {
			if (uri.getPort() == -1) {
				nettyRequest.setHeader(HttpHeaders.Names.HOST, host);
			} else if (request.getVirtualHost() != null) {
				nettyRequest.setHeader(HttpHeaders.Names.HOST, host);
			} else {
				nettyRequest.setHeader(HttpHeaders.Names.HOST,
						host + ":" + uri.getPort());
			}
		} else {
			host = "127.0.0.1";
		}

		if (!m.equals(HttpMethod.CONNECT)) {
			final FluentCaseInsensitiveStringsMap h = request.getHeaders();
			if (h != null) {
				for (final String name : h.keySet()) {
					if (!"host".equalsIgnoreCase(name)) {
						for (final String value : h.get(name)) {
							nettyRequest.addHeader(name, value);
						}
					}
				}
			}

			if (config.isCompressionEnabled()) {
				nettyRequest.setHeader(HttpHeaders.Names.ACCEPT_ENCODING,
						HttpHeaders.Values.GZIP);
			}
		} else {
			final List<String> auth = request.getHeaders().get(
					HttpHeaders.Names.PROXY_AUTHORIZATION);
			if (auth != null && auth.size() > 0
					&& auth.get(0).startsWith("NTLM")) {
				nettyRequest.addHeader(HttpHeaders.Names.PROXY_AUTHORIZATION,
						auth.get(0));
			}
		}
		final ProxyServer proxyServer = request.getProxyServer() != null ? request
				.getProxyServer() : config.getProxyServer();
		final Realm realm = request.getRealm() != null ? request.getRealm()
				: config.getRealm();

		if (realm != null && realm.getUsePreemptiveAuth()) {

			String domain = realm.getNtlmDomain();
			if (proxyServer != null && proxyServer.getNtlmDomain() != null) {
				domain = proxyServer.getNtlmDomain();
			}

			final String authHost = realm.getNtlmHost();
			if (proxyServer != null && proxyServer.getHost() != null) {
				host = proxyServer.getHost();
			}

			switch (realm.getAuthScheme()) {
			case BASIC:
				nettyRequest.setHeader(HttpHeaders.Names.AUTHORIZATION,
						AuthenticatorUtils.computeBasicAuthentication(realm));
				break;
			case DIGEST:
				if (realm.getNonce() != null && !realm.getNonce().equals("")) {
					try {
						nettyRequest.setHeader(HttpHeaders.Names.AUTHORIZATION,
								AuthenticatorUtils
										.computeDigestAuthentication(realm));
					} catch (final NoSuchAlgorithmException e) {
						throw new SecurityException(e);
					}
				}
				break;
			case NTLM:
				try {
					nettyRequest.setHeader(HttpHeaders.Names.AUTHORIZATION,
							ntlmEngine.generateType1Msg("NTLM " + domain,
									authHost));
				} catch (final NTLMEngineException e) {
					final IOException ie = new IOException();
					ie.initCause(e);
					throw ie;
				}
				break;
			case KERBEROS:
			case SPNEGO:
				String challengeHeader = null;
				final String server = proxyServer == null ? host : proxyServer
						.getHost();
				try {
					challengeHeader = spnegoEngine.generateToken(server);
				} catch (final Throwable e) {
					final IOException ie = new IOException();
					ie.initCause(e);
					throw ie;
				}
				nettyRequest.setHeader(HttpHeaders.Names.AUTHORIZATION,
						"Negotiate " + challengeHeader);
				break;
			case NONE:
				break;
			default:
				throw new IllegalStateException(String.format(
						"Invalid Authentication %s", realm.toString()));
			}
		}

		if (!webSocket
				&& !request.getHeaders().containsKey(
						HttpHeaders.Names.CONNECTION)) {
			nettyRequest.setHeader(HttpHeaders.Names.CONNECTION, "keep-alive");
		}

		final boolean avoidProxy = ProxyUtils.avoidProxy(proxyServer, request);
		if (!avoidProxy) {
			if (!request.getHeaders().containsKey("Proxy-Connection")) {
				nettyRequest.setHeader("Proxy-Connection", "keep-alive");
			}

			if (proxyServer.getPrincipal() != null) {
				if (proxyServer.getNtlmDomain() != null
						&& proxyServer.getNtlmDomain().length() > 0) {

					final List<String> auth = request.getHeaders().get(
							HttpHeaders.Names.PROXY_AUTHORIZATION);
					if (!(auth != null && auth.size() > 0 && auth.get(0)
							.startsWith("NTLM"))) {
						try {
							final String msg = ntlmEngine.generateType1Msg(
									proxyServer.getNtlmDomain(),
									proxyServer.getHost());
							nettyRequest.setHeader(
									HttpHeaders.Names.PROXY_AUTHORIZATION,
									"NTLM " + msg);
						} catch (final NTLMEngineException e) {
							final IOException ie = new IOException();
							ie.initCause(e);
							throw ie;
						}
					}
				} else {
					nettyRequest.setHeader(
							HttpHeaders.Names.PROXY_AUTHORIZATION,
							AuthenticatorUtils
									.computeBasicAuthentication(proxyServer));
				}
			}
		}

		// Add default accept headers.
		if (request.getHeaders().getFirstValue("Accept") == null) {
			nettyRequest.setHeader(HttpHeaders.Names.ACCEPT, "*/*");
		}

		if (request.getHeaders().getFirstValue("User-Agent") != null) {
			nettyRequest.setHeader("User-Agent", request.getHeaders()
					.getFirstValue("User-Agent"));
		} else if (config.getUserAgent() != null) {
			nettyRequest.setHeader("User-Agent", config.getUserAgent());
		} else {
			nettyRequest.setHeader("User-Agent", AsyncHttpProviderUtils
					.constructUserAgent(NettyAsyncHttpProvider.class));
		}

		if (!m.equals(HttpMethod.CONNECT)) {
			if (request.getCookies() != null && !request.getCookies().isEmpty()) {
				final CookieEncoder httpCookieEncoder = new CookieEncoder(false);
				final Iterator<Cookie> ic = request.getCookies().iterator();
				Cookie c;
				org.jboss.netty.handler.codec.http.Cookie cookie;
				while (ic.hasNext()) {
					c = ic.next();
					cookie = new DefaultCookie(c.getName(), c.getValue());
					cookie.setPath(c.getPath());
					cookie.setMaxAge(c.getMaxAge());
					cookie.setDomain(c.getDomain());
					httpCookieEncoder.addCookie(cookie);
				}
				nettyRequest.setHeader(HttpHeaders.Names.COOKIE,
						httpCookieEncoder.encode());
			}

			final String reqType = request.getMethod();
			if (!"GET".equals(reqType) && !"HEAD".equals(reqType)
					&& !"OPTION".equals(reqType) && !"TRACE".equals(reqType)) {

				final String bodyCharset = request.getBodyEncoding() == null ? DEFAULT_CHARSET
						: request.getBodyEncoding();

				// We already have processed the body.
				if (buffer != null && buffer.writerIndex() != 0) {
					nettyRequest.setHeader(HttpHeaders.Names.CONTENT_LENGTH,
							buffer.writerIndex());
					nettyRequest.setContent(buffer);
				} else if (request.getByteData() != null) {
					nettyRequest.setHeader(HttpHeaders.Names.CONTENT_LENGTH,
							String.valueOf(request.getByteData().length));
					nettyRequest.setContent(ChannelBuffers
							.wrappedBuffer(request.getByteData()));
				} else if (request.getStringData() != null) {
					nettyRequest.setHeader(
							HttpHeaders.Names.CONTENT_LENGTH,
							String.valueOf(request.getStringData().getBytes(
									bodyCharset).length));
					nettyRequest.setContent(ChannelBuffers
							.wrappedBuffer(request.getStringData().getBytes(
									bodyCharset)));
				} else if (request.getStreamData() != null) {
					final int[] lengthWrapper = new int[1];
					final byte[] bytes = AsyncHttpProviderUtils.readFully(
							request.getStreamData(), lengthWrapper);
					final int length = lengthWrapper[0];
					nettyRequest.setHeader(HttpHeaders.Names.CONTENT_LENGTH,
							String.valueOf(length));
					nettyRequest.setContent(ChannelBuffers.wrappedBuffer(bytes,
							0, length));
				} else if (request.getParams() != null
						&& !request.getParams().isEmpty()) {
					final StringBuilder sb = new StringBuilder();
					for (final Entry<String, List<String>> paramEntry : request
							.getParams()) {
						final String key = paramEntry.getKey();
						for (final String value : paramEntry.getValue()) {
							if (sb.length() > 0) {
								sb.append("&");
							}
							UTF8UrlEncoder.appendEncoded(sb, key);
							sb.append("=");
							UTF8UrlEncoder.appendEncoded(sb, value);
						}
					}
					nettyRequest.setHeader(HttpHeaders.Names.CONTENT_LENGTH,
							String.valueOf(sb.length()));
					nettyRequest.setContent(ChannelBuffers.wrappedBuffer(sb
							.toString().getBytes(bodyCharset)));

					if (!request.getHeaders().containsKey(
							HttpHeaders.Names.CONTENT_TYPE)) {
						nettyRequest.setHeader(HttpHeaders.Names.CONTENT_TYPE,
								"application/x-www-form-urlencoded");
					}

				} else if (request.getParts() != null) {
					int lenght = computeAndSetContentLength(request,
							nettyRequest);

					if (lenght == -1) {
						lenght = MAX_BUFFERED_BYTES;
					}

					final MultipartRequestEntity mre = AsyncHttpProviderUtils
							.createMultipartRequestEntity(request.getParts(),
									request.getParams());

					nettyRequest.setHeader(HttpHeaders.Names.CONTENT_TYPE,
							mre.getContentType());
					nettyRequest.setHeader(HttpHeaders.Names.CONTENT_LENGTH,
							String.valueOf(mre.getContentLength()));

					/**
					 * TODO: AHC-78: SSL + zero copy isn't supported by the
					 * MultiPart class and pretty complex to implements.
					 */
					if (uri.toString().startsWith(HTTPS)) {
						final ChannelBuffer b = ChannelBuffers
								.dynamicBuffer(lenght);
						mre.writeRequest(new ChannelBufferOutputStream(b));
						nettyRequest.setContent(b);
					}
				} else if (request.getEntityWriter() != null) {
					int lenght = computeAndSetContentLength(request,
							nettyRequest);

					if (lenght == -1) {
						lenght = MAX_BUFFERED_BYTES;
					}

					final ChannelBuffer b = ChannelBuffers
							.dynamicBuffer(lenght);
					request.getEntityWriter().writeEntity(
							new ChannelBufferOutputStream(b));
					nettyRequest.setHeader(HttpHeaders.Names.CONTENT_LENGTH,
							b.writerIndex());
					nettyRequest.setContent(b);
				} else if (request.getFile() != null) {
					final File file = request.getFile();
					if (!file.isFile()) {
						throw new IOException(String.format(
								"File %s is not a file or doesn't exist",
								file.getAbsolutePath()));
					}
					nettyRequest.setHeader(HttpHeaders.Names.CONTENT_LENGTH,
							file.length());
				}
			}
		}
		return nettyRequest;
	}

	@Override
	public void close() {
		isClose.set(true);
		try {
			connectionsPool.destroy();
			openChannels.close();

			for (final Channel channel : openChannels) {
				final ChannelHandlerContext ctx = channel.getPipeline()
						.getContext(NettyAsyncHttpProvider.class);
				if (ctx.getAttachment() instanceof NettyResponseFuture<?>) {
					final NettyResponseFuture<?> future = (NettyResponseFuture<?>) ctx
							.getAttachment();
					future.setReaperFuture(null);
				}
			}

			config.executorService().shutdown();
			config.reaper().shutdown();
			socketChannelFactory.releaseExternalResources();
			plainBootstrap.releaseExternalResources();
			secureBootstrap.releaseExternalResources();
			webSocketBootstrap.releaseExternalResources();
		} catch (final Throwable t) {

		}
	}

	/* @Override */

	@Override
	public Response prepareResponse(final HttpResponseStatus status,
			final HttpResponseHeaders headers,
			final Collection<HttpResponseBodyPart> bodyParts) {
		return new NettyResponse(status, headers, bodyParts);
	}

	/* @Override */

	@Override
	public <T> ListenableFuture<T> execute(final Request request,
			final AsyncHandler<T> asyncHandler) throws IOException {
		return doConnect(request, asyncHandler, null, true,
				executeConnectAsync, false);
	}

	private <T> void execute(final Request request,
			final NettyResponseFuture<T> f, final boolean useCache,
			final boolean asyncConnect) throws IOException {
		doConnect(request, f.getAsyncHandler(), f, useCache, asyncConnect,
				false);
	}

	private <T> void execute(final Request request,
			final NettyResponseFuture<T> f, final boolean useCache,
			final boolean asyncConnect, final boolean reclaimCache)
			throws IOException {
		doConnect(request, f.getAsyncHandler(), f, useCache, asyncConnect,
				reclaimCache);
	}

	private <T> ListenableFuture<T> doConnect(final Request request,
			final AsyncHandler<T> asyncHandler, NettyResponseFuture<T> f,
			final boolean useCache, final boolean asyncConnect,
			final boolean reclaimCache) throws IOException {

		if (isClose.get()) {
			throw new IOException("Closed");
		}

		if (request.getUrl().startsWith(WEBSOCKET)
				&& !validateWebSocketRequest(request, asyncHandler)) {
			throw new IOException("WebSocket method must be a GET");
		}

		final ProxyServer proxyServer = request.getProxyServer() != null ? request
				.getProxyServer() : config.getProxyServer();
		String requestUrl;
		if (useRawUrl) {
			requestUrl = request.getRawUrl();
		} else {
			requestUrl = request.getUrl();
		}
		final URI uri = AsyncHttpProviderUtils.createUri(requestUrl);
		Channel channel = null;

		if (useCache) {
			if (f != null && f.reuseChannel() && f.channel() != null) {
				channel = f.channel();
			} else {
				channel = lookupInCache(uri);
			}
		}

		ChannelBuffer bufferedBytes = null;
		if (f != null
				&& f.getRequest().getFile() == null
				&& !f.getNettyRequest().getMethod().getName()
						.equals(HttpMethod.CONNECT.getName())) {
			bufferedBytes = f.getNettyRequest().getContent();
		}

		final boolean useSSl = uri.getScheme().compareToIgnoreCase(HTTPS) == 0
				&& proxyServer == null;
		if (channel != null && channel.isOpen() && channel.isConnected()) {
			HttpRequest nettyRequest = buildRequest(config, request, uri,
					f == null ? false : f.isConnectAllowed(), bufferedBytes);

			if (f == null) {
				f = newFuture(uri, request, asyncHandler, nettyRequest, config,
						this);
			} else {
				nettyRequest = buildRequest(config, request, uri,
						f.isConnectAllowed(), bufferedBytes);
				f.setNettyRequest(nettyRequest);
			}
			f.setState(NettyResponseFuture.STATE.POOLED);
			f.attachChannel(channel, false);

			channel.getPipeline().getContext(NettyAsyncHttpProvider.class)
					.setAttachment(f);

			try {
				writeRequest(channel, config, f, nettyRequest);
			} catch (final Exception ex) {

				if (useSSl && ex.getMessage() != null
						&& ex.getMessage().contains("SSLEngine")) {

					f = null;
				} else {
					try {
						asyncHandler.onThrowable(ex);
					} catch (final Throwable t) {

					}
					final IOException ioe = new IOException(ex.getMessage());
					ioe.initCause(ex);
					throw ioe;
				}
			}
			return f;
		}

		// Do not throw an exception when we need an extra connection for a
		// redirect.
		if (!reclaimCache && !connectionsPool.canCacheConnection()) {
			final IOException ex = new IOException(String.format(
					"Too many connections %s", config.getMaxTotalConnections()));
			try {
				asyncHandler.onThrowable(ex);
			} catch (final Throwable t) {

			}
			throw ex;
		}

		boolean acquiredConnection = false;

		if (trackConnections) {
			if (!reclaimCache) {
				if (!freeConnections.tryAcquire()) {
					final IOException ex = new IOException(String.format(
							"Too many connections %s",
							config.getMaxTotalConnections()));
					try {
						asyncHandler.onThrowable(ex);
					} catch (final Throwable t) {

					}
					throw ex;
				} else {
					acquiredConnection = true;
				}
			}
		}

		final NettyConnectListener<T> c = new NettyConnectListener.Builder<T>(
				config, request, asyncHandler, f, this, bufferedBytes)
				.build(uri);
		final boolean avoidProxy = ProxyUtils.avoidProxy(proxyServer,
				uri.getHost());

		if (useSSl) {
			constructSSLPipeline(c);
		}

		ChannelFuture channelFuture;
		final ClientBootstrap bootstrap = request.getUrl()
				.startsWith(WEBSOCKET) ? webSocketBootstrap
				: (useSSl ? secureBootstrap : plainBootstrap);
		bootstrap.setOption("connectTimeoutMillis",
				config.getConnectionTimeoutInMs());

		// Do no enable this with win.
		if (System.getProperty("os.name").toLowerCase().indexOf("win") == -1) {
			bootstrap.setOption("reuseAddress", asyncHttpProviderConfig
					.getProperty(NettyAsyncHttpProviderConfig.REUSE_ADDRESS));
		}

		try {
			if (request.getInetAddress() != null) {
				channelFuture = bootstrap
						.connect(new InetSocketAddress(
								request.getInetAddress(),
								AsyncHttpProviderUtils.getPort(uri)));
			} else if (proxyServer == null || avoidProxy) {
				channelFuture = bootstrap.connect(new InetSocketAddress(
						AsyncHttpProviderUtils.getHost(uri),
						AsyncHttpProviderUtils.getPort(uri)));
			} else {
				channelFuture = bootstrap.connect(new InetSocketAddress(
						proxyServer.getHost(), proxyServer.getPort()));
			}
		} catch (final Throwable t) {
			if (acquiredConnection) {
				freeConnections.release();
			}
			abort(c.future(), t.getCause() == null ? t : t.getCause());
			return c.future();
		}

		boolean directInvokation = true;
		if (IN_IO_THREAD.get() && DefaultChannelFuture.isUseDeadLockChecker()) {
			directInvokation = false;
		}

		if (directInvokation && !asyncConnect && request.getFile() == null) {
			final int timeOut = config.getConnectionTimeoutInMs() > 0 ? config
					.getConnectionTimeoutInMs() : Integer.MAX_VALUE;
			if (!channelFuture.awaitUninterruptibly(timeOut,
					TimeUnit.MILLISECONDS)) {
				if (acquiredConnection) {
					freeConnections.release();
				}
				channelFuture.cancel();
				abort(c.future(),
						new ConnectException(String.format(
								"Connect operation to %s timeout %s", uri,
								timeOut)));
			}

			try {
				c.operationComplete(channelFuture);
			} catch (final Exception e) {
				if (acquiredConnection) {
					freeConnections.release();
				}
				final IOException ioe = new IOException(e.getMessage());
				ioe.initCause(e);
				try {
					asyncHandler.onThrowable(ioe);
				} catch (final Throwable t) {

				}
				throw ioe;
			}
		} else {
			channelFuture.addListener(c);
		}

		if (!c.future().isCancelled() || !c.future().isDone()) {
			openChannels.add(channelFuture.getChannel());
			c.future().attachChannel(channelFuture.getChannel(), false);
		}
		return c.future();
	}

	protected static int requestTimeout(final AsyncHttpClientConfig config,
			final PerRequestConfig perRequestConfig) {
		int result;
		if (perRequestConfig != null) {
			final int prRequestTimeout = perRequestConfig
					.getRequestTimeoutInMs();
			result = (prRequestTimeout != 0 ? prRequestTimeout : config
					.getRequestTimeoutInMs());
		} else {
			result = config.getRequestTimeoutInMs();
		}
		return result;
	}

	private void closeChannel(final ChannelHandlerContext ctx) {
		connectionsPool.removeAll(ctx.getChannel());
		finishChannel(ctx);
	}

	private void finishChannel(final ChannelHandlerContext ctx) {
		ctx.setAttachment(new DiscardEvent());

		// The channel may have already been removed if a timeout occurred, and
		// this method may be called just after.
		if (ctx.getChannel() == null) {
			return;
		}

		try {
			ctx.getChannel().close();
		} catch (final Throwable t) {

		}

		if (ctx.getChannel() != null) {
			openChannels.remove(ctx.getChannel());
		}

	}

	@Override
	public void messageReceived(final ChannelHandlerContext ctx,
			final MessageEvent e) throws Exception {
		// call super to reset the read timeout
		super.messageReceived(ctx, e);
		IN_IO_THREAD.set(Boolean.TRUE);
		if (ctx.getAttachment() == null) {

		}

		if (ctx.getAttachment() instanceof DiscardEvent) {
			return;
		} else if (ctx.getAttachment() instanceof AsyncCallable) {
			if (e.getMessage() instanceof HttpChunk) {
				final HttpChunk chunk = (HttpChunk) e.getMessage();
				if (chunk.isLast()) {
					final AsyncCallable ac = (AsyncCallable) ctx
							.getAttachment();
					ac.call();
				} else {
					return;
				}
			} else {
				final AsyncCallable ac = (AsyncCallable) ctx.getAttachment();
				ac.call();
			}
			ctx.setAttachment(new DiscardEvent());
			return;
		} else if (!(ctx.getAttachment() instanceof NettyResponseFuture<?>)) {
			try {
				ctx.getChannel().close();
			} catch (final Throwable t) {

			}
			return;
		}

		final Protocol p = httpProtocol;
		p.handle(ctx, e);
	}

	private Realm kerberosChallenge(final List<String> proxyAuth,
			final Request request, final ProxyServer proxyServer,
			final FluentCaseInsensitiveStringsMap headers, final Realm realm,
			final NettyResponseFuture<?> future) throws NTLMEngineException {

		final URI uri = URI.create(request.getUrl());
		final String host = request.getVirtualHost() == null ? AsyncHttpProviderUtils
				.getHost(uri) : request.getVirtualHost();
		final String server = proxyServer == null ? host : proxyServer
				.getHost();
		try {
			final String challengeHeader = spnegoEngine.generateToken(server);
			headers.remove(HttpHeaders.Names.AUTHORIZATION);
			headers.add(HttpHeaders.Names.AUTHORIZATION, "Negotiate "
					+ challengeHeader);

			Realm.RealmBuilder realmBuilder;
			if (realm != null) {
				realmBuilder = new Realm.RealmBuilder().clone(realm);
			} else {
				realmBuilder = new Realm.RealmBuilder();
			}
			return realmBuilder.setUri(uri.getPath())
					.setMethodName(request.getMethod())
					.setScheme(Realm.AuthScheme.KERBEROS).build();
		} catch (final Throwable throwable) {
			if (proxyAuth.contains("NTLM")) {
				return ntlmChallenge(proxyAuth, request, proxyServer, headers,
						realm, future);
			}
			abort(future, throwable);
			return null;
		}
	}

	private Realm ntlmChallenge(final List<String> wwwAuth,
			final Request request, final ProxyServer proxyServer,
			final FluentCaseInsensitiveStringsMap headers, final Realm realm,
			final NettyResponseFuture<?> future) throws NTLMEngineException {

		final boolean useRealm = (proxyServer == null && realm != null);

		final String ntlmDomain = useRealm ? realm.getNtlmDomain()
				: proxyServer.getNtlmDomain();
		final String ntlmHost = useRealm ? realm.getNtlmHost() : proxyServer
				.getHost();
		final String principal = useRealm ? realm.getPrincipal() : proxyServer
				.getPrincipal();
		final String password = useRealm ? realm.getPassword() : proxyServer
				.getPassword();

		Realm newRealm;
		if (realm != null && !realm.isNtlmMessageType2Received()) {
			final String challengeHeader = ntlmEngine.generateType1Msg(
					ntlmDomain, ntlmHost);

			headers.add(HttpHeaders.Names.AUTHORIZATION, "NTLM "
					+ challengeHeader);
			newRealm = new Realm.RealmBuilder().clone(realm)
					.setScheme(realm.getAuthScheme())
					.setUri(URI.create(request.getUrl()).getPath())
					.setMethodName(request.getMethod())
					.setNtlmMessageType2Received(true).build();
			future.getAndSetAuth(false);
		} else {
			headers.remove(HttpHeaders.Names.AUTHORIZATION);

			if (wwwAuth.get(0).startsWith("NTLM ")) {
				final String serverChallenge = wwwAuth.get(0).trim()
						.substring("NTLM ".length());
				final String challengeHeader = ntlmEngine.generateType3Msg(
						principal, password, ntlmDomain, ntlmHost,
						serverChallenge);

				headers.add(HttpHeaders.Names.AUTHORIZATION, "NTLM "
						+ challengeHeader);
			}

			Realm.RealmBuilder realmBuilder;
			Realm.AuthScheme authScheme;
			if (realm != null) {
				realmBuilder = new Realm.RealmBuilder().clone(realm);
				authScheme = realm.getAuthScheme();
			} else {
				realmBuilder = new Realm.RealmBuilder();
				authScheme = Realm.AuthScheme.NTLM;
			}
			newRealm = realmBuilder.setScheme(authScheme)
					.setUri(URI.create(request.getUrl()).getPath())
					.setMethodName(request.getMethod()).build();
		}

		return newRealm;
	}

	private Realm ntlmProxyChallenge(final List<String> wwwAuth,
			final Request request, final ProxyServer proxyServer,
			final FluentCaseInsensitiveStringsMap headers, final Realm realm,
			final NettyResponseFuture<?> future) throws NTLMEngineException {
		future.getAndSetAuth(false);
		headers.remove(HttpHeaders.Names.PROXY_AUTHORIZATION);

		if (wwwAuth.get(0).startsWith("NTLM ")) {
			final String serverChallenge = wwwAuth.get(0).trim()
					.substring("NTLM ".length());
			final String challengeHeader = ntlmEngine.generateType3Msg(
					proxyServer.getPrincipal(), proxyServer.getPassword(),
					proxyServer.getNtlmDomain(), proxyServer.getHost(),
					serverChallenge);
			headers.add(HttpHeaders.Names.PROXY_AUTHORIZATION, "NTLM "
					+ challengeHeader);
		}
		Realm newRealm;
		Realm.RealmBuilder realmBuilder;
		if (realm != null) {
			realmBuilder = new Realm.RealmBuilder().clone(realm);
		} else {
			realmBuilder = new Realm.RealmBuilder();
		}
		newRealm = realmBuilder
				// .setScheme(realm.getAuthScheme())
				.setUri(URI.create(request.getUrl()).getPath())
				.setMethodName(request.getMethod()).build();

		return newRealm;
	}

	private void drainChannel(final ChannelHandlerContext ctx,
			final NettyResponseFuture<?> future, final boolean keepAlive,
			final URI uri) {
		ctx.setAttachment(new AsyncCallable(future) {
			@Override
			public Object call() throws Exception {
				if (keepAlive
						&& ctx.getChannel().isReadable()
						&& connectionsPool.offer(
								AsyncHttpProviderUtils.getBaseUrl(uri),
								ctx.getChannel())) {
					return null;
				}

				finishChannel(ctx);
				return null;
			}

			@Override
			public String toString() {
				return String.format("Draining task for channel %s",
						ctx.getChannel());
			}
		});
	}

	private FilterContext handleIoException(FilterContext fc,
			final NettyResponseFuture<?> future) {
		for (final IOExceptionFilter asyncFilter : config
				.getIOExceptionFilters()) {
			try {
				fc = asyncFilter.filter(fc);
				if (fc == null) {
					throw new NullPointerException("FilterContext is null");
				}
			} catch (final FilterException efe) {
				abort(future, efe);
			}
		}
		return fc;
	}

	private void replayRequest(final NettyResponseFuture<?> future,
			final FilterContext fc, final HttpResponse response,
			final ChannelHandlerContext ctx) throws IOException {
		final Request newRequest = fc.getRequest();
		future.setAsyncHandler(fc.getAsyncHandler());
		future.setState(NettyResponseFuture.STATE.NEW);
		future.touch();

		drainChannel(ctx, future, future.getKeepAlive(), future.getURI());
		nextRequest(newRequest, future);
		return;
	}

	private List<String> getAuthorizationToken(
			final List<Entry<String, String>> list, final String headerAuth) {
		final ArrayList<String> l = new ArrayList<String>();
		for (final Entry<String, String> e : list) {
			if (e.getKey().equalsIgnoreCase(headerAuth)) {
				l.add(e.getValue().trim());
			}
		}
		return l;
	}

	private void nextRequest(final Request request,
			final NettyResponseFuture<?> future) throws IOException {
		nextRequest(request, future, true);
	}

	private void nextRequest(final Request request,
			final NettyResponseFuture<?> future, final boolean useCache)
			throws IOException {
		execute(request, future, useCache, true, true);
	}

	private void abort(final NettyResponseFuture<?> future, final Throwable t) {
		final Channel channel = future.channel();
		if (channel != null && openChannels.contains(channel)) {
			closeChannel(channel.getPipeline().getContext(
					NettyAsyncHttpProvider.class));
			openChannels.remove(channel);
		}

		if (!future.isCancelled() && !future.isDone()) {

		}

		future.abort(t);
	}

	private void upgradeProtocol(final ChannelPipeline p, final String scheme)
			throws IOException, GeneralSecurityException {
		if (p.get(HTTP_HANDLER) != null) {
			p.remove(HTTP_HANDLER);
		}

		if (scheme.startsWith(HTTPS)) {
			if (p.get(SSL_HANDLER) == null) {
				p.addFirst(HTTP_HANDLER, new HttpClientCodec());
				p.addFirst(SSL_HANDLER, new SslHandler(createSSLEngine()));
			} else {
				p.addAfter(SSL_HANDLER, HTTP_HANDLER, new HttpClientCodec());
			}

		} else {
			p.addFirst(HTTP_HANDLER, new HttpClientCodec());
		}
	}

	@Override
	public void channelClosed(final ChannelHandlerContext ctx,
			final ChannelStateEvent e) throws Exception {

		if (isClose.get()) {
			return;
		}

		connectionsPool.removeAll(ctx.getChannel());
		try {
			super.channelClosed(ctx, e);
		} catch (final Exception ex) {

		}

		if (ctx.getAttachment() instanceof AsyncCallable) {
			final AsyncCallable ac = (AsyncCallable) ctx.getAttachment();
			ctx.setAttachment(ac.future());
			ac.call();
			return;
		}

		if (ctx.getAttachment() instanceof NettyResponseFuture<?>) {
			final NettyResponseFuture<?> future = (NettyResponseFuture<?>) ctx
					.getAttachment();
			future.touch();

			if (config.getIOExceptionFilters().size() > 0) {
				FilterContext<?> fc = new FilterContext.FilterContextBuilder()
						.asyncHandler(future.getAsyncHandler())
						.request(future.getRequest())
						.ioException(new IOException("Channel Closed")).build();
				fc = handleIoException(fc, future);

				if (fc.replayRequest() && !future.cannotBeReplay()) {
					replayRequest(future, fc, null, ctx);
					return;
				}
			}

			final Protocol p = httpProtocol;
			p.onClose(ctx, e);

			if (future != null && !future.isDone() && !future.isCancelled()) {
				if (!remotelyClosed(ctx.getChannel(), future)) {
					abort(future,
							new IOException("Remotely Closed "
									+ ctx.getChannel()));
				}
			} else {
				closeChannel(ctx);
			}
		}
	}

	protected boolean remotelyClosed(final Channel channel,
			NettyResponseFuture<?> future) {

		if (isClose.get()) {
			return false;
		}

		connectionsPool.removeAll(channel);

		if (future == null
				&& channel.getPipeline()
						.getContext(NettyAsyncHttpProvider.class)
						.getAttachment() != null
				&& NettyResponseFuture.class.isAssignableFrom(channel
						.getPipeline().getContext(NettyAsyncHttpProvider.class)
						.getAttachment().getClass())) {
			future = (NettyResponseFuture<?>) channel.getPipeline()
					.getContext(NettyAsyncHttpProvider.class).getAttachment();
		}

		if (future == null || future.cannotBeReplay()) {

			return false;
		}

		future.setState(NettyResponseFuture.STATE.RECONNECTED);

		try {
			nextRequest(future.getRequest(), future);
			return true;
		} catch (final IOException iox) {
			future.setState(NettyResponseFuture.STATE.CLOSED);
			future.abort(iox);

		}
		return false;
	}

	private void markAsDone(final NettyResponseFuture<?> future,
			final ChannelHandlerContext ctx) throws MalformedURLException {
		// We need to make sure everything is OK before adding the connection
		// back to the pool.
		try {
			future.done(null);
		} catch (final Throwable t) {
			// Never propagate exception once we know we are done.

		}

		if (!future.getKeepAlive() || !ctx.getChannel().isReadable()) {
			closeChannel(ctx);
		}
	}

	private void finishUpdate(final NettyResponseFuture<?> future,
			final ChannelHandlerContext ctx, final boolean lastValidChunk)
			throws IOException {
		if (lastValidChunk && future.getKeepAlive()) {
			drainChannel(ctx, future, future.getKeepAlive(), future.getURI());
		} else {
			if (future.getKeepAlive()
					&& ctx.getChannel().isReadable()
					&& connectionsPool.offer(
							AsyncHttpProviderUtils.getBaseUrl(future.getURI()),
							ctx.getChannel())) {
				markAsDone(future, ctx);
				return;
			}
			finishChannel(ctx);
		}
		markAsDone(future, ctx);
	}

	@SuppressWarnings("unchecked")
	private final boolean updateStatusAndInterrupt(final AsyncHandler handler,
			final HttpResponseStatus c) throws Exception {
		return handler.onStatusReceived(c) != STATE.CONTINUE;
	}

	@SuppressWarnings("unchecked")
	private final boolean updateHeadersAndInterrupt(final AsyncHandler handler,
			final HttpResponseHeaders c) throws Exception {
		return handler.onHeadersReceived(c) != STATE.CONTINUE;
	}

	@SuppressWarnings("unchecked")
	private final boolean updateBodyAndInterrupt(
			final NettyResponseFuture<?> future, final AsyncHandler handler,
			final HttpResponseBodyPart c) throws Exception {
		final boolean state = handler.onBodyPartReceived(c) != STATE.CONTINUE;
		if (c.closeUnderlyingConnection()) {
			future.setKeepAlive(false);
		}
		return state;
	}

	// Simple marker for stopping publishing bytes.

	final static class DiscardEvent {
	}

	@Override
	public void exceptionCaught(final ChannelHandlerContext ctx,
			final ExceptionEvent e) throws Exception {
		final Channel channel = e.getChannel();
		Throwable cause = e.getCause();
		NettyResponseFuture<?> future = null;

		try {

			if (cause != null
					&& ClosedChannelException.class.isAssignableFrom(cause
							.getClass())) {
				return;
			}

			if (ctx.getAttachment() instanceof NettyResponseFuture<?>) {
				future = (NettyResponseFuture<?>) ctx.getAttachment();
				future.attachChannel(null, false);
				future.touch();

				if (IOException.class.isAssignableFrom(cause.getClass())) {

					if (config.getIOExceptionFilters().size() > 0) {
						FilterContext<?> fc = new FilterContext.FilterContextBuilder()
								.asyncHandler(future.getAsyncHandler())
								.request(future.getRequest())
								.ioException(new IOException("Channel Closed"))
								.build();
						fc = handleIoException(fc, future);

						if (fc.replayRequest()) {
							replayRequest(future, fc, null, ctx);
							return;
						}
					} else {
						// Close the channel so the recovering can occurs.
						try {
							ctx.getChannel().close();
						} catch (final Throwable t) {
							; // Swallow.
						}
						return;
					}
				}

				if (abortOnReadCloseException(cause)
						|| abortOnWriteCloseException(cause)) {

					return;
				}
			} else if (ctx.getAttachment() instanceof AsyncCallable) {
				future = ((AsyncCallable) ctx.getAttachment()).future();
			}
		} catch (final Throwable t) {
			cause = t;
		}

		if (future != null) {
			try {

				abort(future, cause);
			} catch (final Throwable t) {

			}
		}

		final Protocol p = httpProtocol;
		p.onError(ctx, e);

		closeChannel(ctx);
		ctx.sendUpstream(e);
	}

	protected static boolean abortOnConnectCloseException(final Throwable cause) {
		try {
			for (final StackTraceElement element : cause.getStackTrace()) {
				if (element.getClassName().equals(
						"sun.nio.ch.SocketChannelImpl")
						&& element.getMethodName().equals("checkConnect")) {
					return true;
				}
			}

			if (cause.getCause() != null) {
				return abortOnConnectCloseException(cause.getCause());
			}

		} catch (final Throwable t) {
		}
		return false;
	}

	protected static boolean abortOnDisconnectException(final Throwable cause) {
		try {
			for (final StackTraceElement element : cause.getStackTrace()) {
				if (element.getClassName().equals(
						"org.jboss.netty.handler.ssl.SslHandler")
						&& element.getMethodName()
								.equals("channelDisconnected")) {
					return true;
				}
			}

			if (cause.getCause() != null) {
				return abortOnConnectCloseException(cause.getCause());
			}

		} catch (final Throwable t) {
		}
		return false;
	}

	protected static boolean abortOnReadCloseException(final Throwable cause) {

		for (final StackTraceElement element : cause.getStackTrace()) {
			if (element.getClassName().equals("sun.nio.ch.SocketDispatcher")
					&& element.getMethodName().equals("read")) {
				return true;
			}
		}

		if (cause.getCause() != null) {
			return abortOnReadCloseException(cause.getCause());
		}

		return false;
	}

	protected static boolean abortOnWriteCloseException(final Throwable cause) {

		for (final StackTraceElement element : cause.getStackTrace()) {
			if (element.getClassName().equals("sun.nio.ch.SocketDispatcher")
					&& element.getMethodName().equals("write")) {
				return true;
			}
		}

		if (cause.getCause() != null) {
			return abortOnReadCloseException(cause.getCause());
		}

		return false;
	}

	private final static int computeAndSetContentLength(final Request request,
			final HttpRequest r) {
		int length = (int) request.getContentLength();
		if (length == -1
				&& r.getHeader(HttpHeaders.Names.CONTENT_LENGTH) != null) {
			length = Integer.valueOf(r
					.getHeader(HttpHeaders.Names.CONTENT_LENGTH));
		}

		if (length >= 0) {
			r.setHeader(HttpHeaders.Names.CONTENT_LENGTH,
					String.valueOf(length));
		}
		return length;
	}

	public static <T> NettyResponseFuture<T> newFuture(final URI uri,
			final Request request, final AsyncHandler<T> asyncHandler,
			final HttpRequest nettyRequest, final AsyncHttpClientConfig config,
			final NettyAsyncHttpProvider provider) {

		final NettyResponseFuture<T> f = new NettyResponseFuture<T>(uri,
				request, asyncHandler, nettyRequest, requestTimeout(config,
						request.getPerRequestConfig()),
				config.getIdleConnectionTimeoutInMs(), provider);

		if (request.getHeaders().getFirstValue("Expect") != null
				&& request.getHeaders().getFirstValue("Expect")
						.equalsIgnoreCase("100-Continue")) {
			f.getAndSetWriteBody(false);
		}
		return f;
	}

	private class ProgressListener implements ChannelFutureProgressListener {

		private final boolean notifyHeaders;
		private final AsyncHandler asyncHandler;
		private final NettyResponseFuture<?> future;

		public ProgressListener(final boolean notifyHeaders,
				final AsyncHandler asyncHandler,
				final NettyResponseFuture<?> future) {
			this.notifyHeaders = notifyHeaders;
			this.asyncHandler = asyncHandler;
			this.future = future;
		}

		@Override
		public void operationComplete(final ChannelFuture cf) {
			// The write operation failed. If the channel was cached, it means
			// it got asynchronously closed.
			// Let's retry a second time.
			final Throwable cause = cf.getCause();
			if (cause != null
					&& future.getState() != NettyResponseFuture.STATE.NEW) {

				if (IllegalStateException.class.isAssignableFrom(cause
						.getClass())) {

					try {
						cf.getChannel().close();
					} catch (final RuntimeException ex) {

					}
					return;
				}

				if (ClosedChannelException.class.isAssignableFrom(cause
						.getClass())
						|| abortOnReadCloseException(cause)
						|| abortOnWriteCloseException(cause)) {

					try {
						cf.getChannel().close();
					} catch (final RuntimeException ex) {

					}
					return;
				} else {
					future.abort(cause);
				}
				return;
			}
			future.touch();

			/**
			 * We need to make sure we aren't in the middle of an authorization
			 * process before publishing events as we will re-publish again the
			 * same event after the authorization, causing unpredictable
			 * behavior.
			 */
			final Realm realm = future.getRequest().getRealm() != null ? future
					.getRequest().getRealm() : NettyAsyncHttpProvider.this
					.getConfig().getRealm();
			final boolean startPublishing = future.isInAuth() || realm == null
					|| realm.getUsePreemptiveAuth() == true;

			if (startPublishing
					&& ProgressAsyncHandler.class.isAssignableFrom(asyncHandler
							.getClass())) {
				if (notifyHeaders) {
					ProgressAsyncHandler.class.cast(asyncHandler)
							.onHeaderWriteCompleted();
				} else {
					ProgressAsyncHandler.class.cast(asyncHandler)
							.onContentWriteCompleted();
				}
			}
		}

		@Override
		public void operationProgressed(final ChannelFuture cf,
				final long amount, final long current, final long total) {
			future.touch();
			if (ProgressAsyncHandler.class.isAssignableFrom(asyncHandler
					.getClass())) {
				ProgressAsyncHandler.class.cast(asyncHandler)
						.onContentWriteProgress(amount, current, total);
			}
		}
	}

	/**
	 * Because some implementation of the ThreadSchedulingService do not clean
	 * up cancel task until they try to run them, we wrap the task with the
	 * future so the when the NettyResponseFuture cancel the reaper future this
	 * wrapper will release the references to the channel and the
	 * nettyResponseFuture immediately. Otherwise, the memory referenced this
	 * way will only be released after the request timeout period which can be
	 * arbitrary long.
	 */
	private final class ReaperFuture implements Future, Runnable {
		private Future scheduledFuture;
		private Channel channel;
		private NettyResponseFuture<?> nettyResponseFuture;

		public ReaperFuture(final Channel channel,
				final NettyResponseFuture<?> nettyResponseFuture) {
			this.channel = channel;
			this.nettyResponseFuture = nettyResponseFuture;
		}

		public void setScheduledFuture(final Future scheduledFuture) {
			this.scheduledFuture = scheduledFuture;
		}

		/**
		 * @Override
		 */
		@Override
		public synchronized boolean cancel(final boolean mayInterruptIfRunning) {
			// cleanup references to allow gc to reclaim memory independently
			// of this Future lifecycle
			this.channel = null;
			this.nettyResponseFuture = null;
			return this.scheduledFuture.cancel(mayInterruptIfRunning);
		}

		/**
		 * @Override
		 */
		@Override
		public Object get() throws InterruptedException, ExecutionException {
			return this.scheduledFuture.get();
		}

		/**
		 * @Override
		 */
		@Override
		public Object get(final long timeout, final TimeUnit unit)
				throws InterruptedException, ExecutionException,
				TimeoutException {
			return this.scheduledFuture.get(timeout, unit);
		}

		/**
		 * @Override
		 */
		@Override
		public boolean isCancelled() {
			return this.scheduledFuture.isCancelled();
		}

		/**
		 * @Override
		 */
		@Override
		public boolean isDone() {
			return this.scheduledFuture.isDone();
		}

		/**
		 * @Override
		 */
		@Override
		public synchronized void run() {
			if (isClose.get()) {
				cancel(true);
				return;
			}

			if (this.nettyResponseFuture != null
					&& this.nettyResponseFuture.hasExpired()
					&& !this.nettyResponseFuture.isDone()
					&& !this.nettyResponseFuture.isCancelled()) {

				int requestTimeout = config.getRequestTimeoutInMs();
				final PerRequestConfig p = this.nettyResponseFuture
						.getRequest().getPerRequestConfig();
				if (p != null && p.getRequestTimeoutInMs() != -1) {
					requestTimeout = p.getRequestTimeoutInMs();
				}

				abort(this.nettyResponseFuture,
						new TimeoutException(String
								.format("No response received after %s",
										requestTimeout)));

				this.nettyResponseFuture = null;
				this.channel = null;
			}

			if (this.nettyResponseFuture == null
					|| this.nettyResponseFuture.isDone()
					|| this.nettyResponseFuture.isCancelled()) {
				cancel(true);
			}
		}
	}

	private abstract class AsyncCallable implements Callable<Object> {

		private final NettyResponseFuture<?> future;

		public AsyncCallable(final NettyResponseFuture<?> future) {
			this.future = future;
		}

		@Override
		abstract public Object call() throws Exception;

		public NettyResponseFuture<?> future() {
			return future;
		}
	}

	public static class ThreadLocalBoolean extends ThreadLocal<Boolean> {

		private final boolean defaultValue;

		public ThreadLocalBoolean() {
			this(false);
		}

		public ThreadLocalBoolean(final boolean defaultValue) {
			this.defaultValue = defaultValue;
		}

		@Override
		protected Boolean initialValue() {
			return defaultValue ? Boolean.TRUE : Boolean.FALSE;
		}
	}

	public static class OptimizedFileRegion implements FileRegion {

		private final FileChannel file;
		private final RandomAccessFile raf;
		private final long position;
		private final long count;
		private long byteWritten;

		public OptimizedFileRegion(final RandomAccessFile raf,
				final long position, final long count) {
			this.raf = raf;
			this.file = raf.getChannel();
			this.position = position;
			this.count = count;
		}

		@Override
		public long getPosition() {
			return position;
		}

		@Override
		public long getCount() {
			return count;
		}

		@Override
		public long transferTo(final WritableByteChannel target,
				final long position) throws IOException {
			final long count = this.count - position;
			if (count < 0 || position < 0) {
				throw new IllegalArgumentException("position out of range: "
						+ position + " (expected: 0 - " + (this.count - 1)
						+ ")");
			}
			if (count == 0) {
				return 0L;
			}

			final long bw = file.transferTo(this.position + position, count,
					target);
			byteWritten += bw;
			if (byteWritten == raf.length()) {
				releaseExternalResources();
			}
			return bw;
		}

		@Override
		public void releaseExternalResources() {
			try {
				file.close();
			} catch (final IOException e) {

			}

			try {
				raf.close();
			} catch (final IOException e) {

			}
		}
	}

	private static class NettyTransferAdapter extends
			TransferCompletionHandler.TransferAdapter {

		private final ChannelBuffer content;
		private final FileInputStream file;
		private int byteRead = 0;

		public NettyTransferAdapter(
				final FluentCaseInsensitiveStringsMap headers,
				final ChannelBuffer content, final File file)
				throws IOException {
			super(headers);
			this.content = content;
			if (file != null) {
				this.file = new FileInputStream(file);
			} else {
				this.file = null;
			}
		}

		@Override
		public void getBytes(final byte[] bytes) {
			if (content.writableBytes() != 0) {
				content.getBytes(byteRead, bytes);
				byteRead += bytes.length;
			} else if (file != null) {
				try {
					byteRead += file.read(bytes);
				} catch (final IOException e) {

				}
			}
		}
	}

	protected AsyncHttpClientConfig getConfig() {
		return config;
	}

	private static class NonConnectionsPool implements
			ConnectionsPool<String, Channel> {

		@Override
		public boolean offer(final String uri, final Channel connection) {
			return false;
		}

		@Override
		public Channel poll(final String uri) {
			return null;
		}

		@Override
		public boolean removeAll(final Channel connection) {
			return false;
		}

		@Override
		public boolean canCacheConnection() {
			return true;
		}

		@Override
		public void destroy() {
		}
	}

	private static final boolean validateWebSocketRequest(
			final Request request, final AsyncHandler<?> asyncHandler) {
		if (request.getMethod() != "GET"
				|| !WebSocketUpgradeHandler.class.isAssignableFrom(asyncHandler
						.getClass())) {
			return false;
		}
		return true;
	}

	private final class HttpProtocol implements Protocol {
		@Override
		public void handle(final ChannelHandlerContext ctx, final MessageEvent e)
				throws Exception {
			final NettyResponseFuture<?> future = (NettyResponseFuture<?>) ctx
					.getAttachment();
			future.touch();

			// The connect timeout occured.
			if (future.isCancelled() || future.isDone()) {
				finishChannel(ctx);
				return;
			}

			final HttpRequest nettyRequest = future.getNettyRequest();
			AsyncHandler handler = future.getAsyncHandler();
			final Request request = future.getRequest();
			HttpResponse response = null;
			try {
				if (e.getMessage() instanceof HttpResponse) {
					response = (HttpResponse) e.getMessage();

					// Required if there is some trailing headers.
					future.setHttpResponse(response);

					final int statusCode = response.getStatus().getCode();

					final String ka = response
							.getHeader(HttpHeaders.Names.CONNECTION);
					future.setKeepAlive(ka == null
							|| ka.toLowerCase().equals("keep-alive"));

					final List<String> wwwAuth = getAuthorizationToken(
							response.getHeaders(),
							HttpHeaders.Names.WWW_AUTHENTICATE);
					final Realm realm = request.getRealm() != null ? request
							.getRealm() : config.getRealm();

					final HttpResponseStatus status = new ResponseStatus(
							future.getURI(), response,
							NettyAsyncHttpProvider.this);
					final HttpResponseHeaders responseHeaders = new ResponseHeaders(
							future.getURI(), response,
							NettyAsyncHttpProvider.this);
					FilterContext fc = new FilterContext.FilterContextBuilder()
							.asyncHandler(handler).request(request)
							.responseStatus(status)
							.responseHeaders(responseHeaders).build();

					for (final ResponseFilter asyncFilter : config
							.getResponseFilters()) {
						try {
							fc = asyncFilter.filter(fc);
							if (fc == null) {
								throw new NullPointerException(
										"FilterContext is null");
							}
						} catch (final FilterException efe) {
							abort(future, efe);
						}
					}

					// The handler may have been wrapped.
					handler = fc.getAsyncHandler();
					future.setAsyncHandler(handler);

					// The request has changed
					if (fc.replayRequest()) {
						replayRequest(future, fc, response, ctx);
						return;
					}

					Realm newRealm = null;
					final ProxyServer proxyServer = request.getProxyServer() != null ? request
							.getProxyServer() : config.getProxyServer();
					final FluentCaseInsensitiveStringsMap headers = request
							.getHeaders();
					final RequestBuilder builder = new RequestBuilder(
							future.getRequest());

					// if (realm != null &&
					// !future.getURI().getPath().equalsIgnoreCase(realm.getUri()))
					// {
					// builder.setUrl(future.getURI().toString());
					// }

					if (statusCode == 401 && wwwAuth.size() > 0
							&& !future.getAndSetAuth(true)) {

						future.setState(NettyResponseFuture.STATE.NEW);
						// NTLM
						if (!wwwAuth.contains("Kerberos")
								&& (wwwAuth.contains("NTLM") || (wwwAuth
										.contains("Negotiate")))) {
							newRealm = ntlmChallenge(wwwAuth, request,
									proxyServer, headers, realm, future);
							// SPNEGO KERBEROS
						} else if (wwwAuth.contains("Negotiate")) {
							newRealm = kerberosChallenge(wwwAuth, request,
									proxyServer, headers, realm, future);
							if (newRealm == null)
								return;
						} else {
							Realm.RealmBuilder realmBuilder;
							if (realm != null) {
								realmBuilder = new Realm.RealmBuilder().clone(
										realm).setScheme(realm.getAuthScheme());
							} else {
								realmBuilder = new Realm.RealmBuilder();
							}
							newRealm = realmBuilder
									.setUri(URI.create(request.getUrl())
											.getPath())
									.setMethodName(request.getMethod())
									.setUsePreemptiveAuth(true)
									.parseWWWAuthenticateHeader(wwwAuth.get(0))
									.build();
						}

						final Realm nr = new Realm.RealmBuilder()
								.clone(newRealm).setUri(request.getUrl())
								.build();

						final AsyncCallable ac = new AsyncCallable(future) {
							@Override
							public Object call() throws Exception {
								drainChannel(ctx, future,
										future.getKeepAlive(), future.getURI());
								nextRequest(builder.setHeaders(headers)
										.setRealm(nr).build(), future);
								return null;
							}
						};

						if (future.getKeepAlive() && response.isChunked()) {
							// We must make sure there is no bytes left before
							// executing the next request.
							ctx.setAttachment(ac);
						} else {
							ac.call();
						}
						return;
					}

					if (statusCode == 100) {
						future.getAndSetWriteHeaders(false);
						future.getAndSetWriteBody(true);
						writeRequest(ctx.getChannel(), config, future,
								nettyRequest);
						return;
					}

					final List<String> proxyAuth = getAuthorizationToken(
							response.getHeaders(),
							HttpHeaders.Names.PROXY_AUTHENTICATE);
					if (statusCode == 407 && proxyAuth.size() > 0
							&& !future.getAndSetAuth(true)) {

						future.setState(NettyResponseFuture.STATE.NEW);

						if (!proxyAuth.contains("Kerberos")
								&& (proxyAuth.get(0).contains("NTLM") || (proxyAuth
										.contains("Negotiate")))) {
							newRealm = ntlmProxyChallenge(proxyAuth, request,
									proxyServer, headers, realm, future);
							// SPNEGO KERBEROS
						} else if (proxyAuth.contains("Negotiate")) {
							newRealm = kerberosChallenge(proxyAuth, request,
									proxyServer, headers, realm, future);
							if (newRealm == null)
								return;
						} else {
							newRealm = future.getRequest().getRealm();
						}

						final Request req = builder.setHeaders(headers)
								.setRealm(newRealm).build();
						future.setReuseChannel(true);
						future.setConnectAllowed(true);
						nextRequest(req, future);
						return;
					}

					if (future.getNettyRequest().getMethod()
							.equals(HttpMethod.CONNECT)
							&& statusCode == 200) {

						if (future.getKeepAlive()) {
							future.attachChannel(ctx.getChannel(), true);
						}

						try {

							upgradeProtocol(ctx.getChannel().getPipeline(),
									request.getUrl());
						} catch (final Throwable ex) {
							abort(future, ex);
						}
						final Request req = builder.build();
						future.setReuseChannel(true);
						future.setConnectAllowed(false);
						nextRequest(req, future);
						return;
					}

					final boolean redirectEnabled = request
							.isRedirectOverrideSet() ? request
							.isRedirectEnabled() : config.isRedirectEnabled();
					if (redirectEnabled
							&& (statusCode == 302 || statusCode == 301
									|| statusCode == 303 || statusCode == 307)) {

						if (future.incrementAndGetCurrentRedirectCount() < config
								.getMaxRedirects()) {
							// We must allow 401 handling again.
							future.getAndSetAuth(false);

							final String location = response
									.getHeader(HttpHeaders.Names.LOCATION);
							final URI uri = AsyncHttpProviderUtils
									.getRedirectUri(future.getURI(), location);
							final boolean stripQueryString = config
									.isRemoveQueryParamOnRedirect();
							if (!uri.toString().equalsIgnoreCase(
									future.getURI().toString())) {
								final RequestBuilder nBuilder = stripQueryString ? new RequestBuilder(
										future.getRequest())
										.setQueryParameters(null)
										: new RequestBuilder(
												future.getRequest());

								if (!(statusCode < 302 || statusCode > 303)
										&& !(statusCode == 302 && config
												.isStrict302Handling())) {
									nBuilder.setMethod("GET");
								}
								final URI initialConnectionUri = future
										.getURI();
								final boolean initialConnectionKeepAlive = future
										.getKeepAlive();
								future.setURI(uri);
								final String newUrl = uri.toString();

								for (final String cookieStr : future
										.getHttpResponse().getHeaders(
												HttpHeaders.Names.SET_COOKIE)) {
									final Cookie c = AsyncHttpProviderUtils
											.parseCookie(cookieStr);
									nBuilder.addOrReplaceCookie(c);
								}

								for (final String cookieStr : future
										.getHttpResponse().getHeaders(
												HttpHeaders.Names.SET_COOKIE2)) {
									final Cookie c = AsyncHttpProviderUtils
											.parseCookie(cookieStr);
									nBuilder.addOrReplaceCookie(c);
								}

								final AsyncCallable ac = new AsyncCallable(
										future) {
									@Override
									public Object call() throws Exception {
										if (initialConnectionKeepAlive
												&& ctx.getChannel()
														.isReadable()
												&& connectionsPool
														.offer(AsyncHttpProviderUtils
																.getBaseUrl(initialConnectionUri),
																ctx.getChannel())) {
											return null;
										}
										finishChannel(ctx);
										return null;
									}
								};

								if (response.isChunked()) {
									// We must make sure there is no bytes left
									// before executing the next request.
									ctx.setAttachment(ac);
								} else {
									ac.call();
								}
								nextRequest(nBuilder.setUrl(newUrl).build(),
										future);
								return;
							}
						} else {
							throw new MaxRedirectException(
									"Maximum redirect reached: "
											+ config.getMaxRedirects());
						}
					}

					if (!future.getAndSetStatusReceived(true)
							&& updateStatusAndInterrupt(handler, status)) {
						finishUpdate(future, ctx, response.isChunked());
						return;
					} else if (updateHeadersAndInterrupt(handler,
							responseHeaders)) {
						finishUpdate(future, ctx, response.isChunked());
						return;
					} else if (!response.isChunked()) {
						if (response.getContent().readableBytes() != 0) {
							updateBodyAndInterrupt(future, handler,
									new ResponseBodyPart(future.getURI(),
											response,
											NettyAsyncHttpProvider.this, true));
						}
						finishUpdate(future, ctx, false);
						return;
					}

					if (nettyRequest.getMethod().equals(HttpMethod.HEAD)) {
						updateBodyAndInterrupt(future, handler,
								new ResponseBodyPart(future.getURI(), response,
										NettyAsyncHttpProvider.this, true));
						markAsDone(future, ctx);
						drainChannel(ctx, future, future.getKeepAlive(),
								future.getURI());
					}

				} else if (e.getMessage() instanceof HttpChunk) {
					final HttpChunk chunk = (HttpChunk) e.getMessage();

					if (handler != null) {
						if (chunk.isLast()
								|| updateBodyAndInterrupt(future, handler,
										new ResponseBodyPart(future.getURI(),
												null,
												NettyAsyncHttpProvider.this,
												chunk, chunk.isLast()))) {
							if (chunk instanceof DefaultHttpChunkTrailer) {
								updateHeadersAndInterrupt(handler,
										new ResponseHeaders(future.getURI(),
												future.getHttpResponse(),
												NettyAsyncHttpProvider.this,
												(HttpChunkTrailer) chunk));
							}
							finishUpdate(future, ctx, !chunk.isLast());
						}
					}
				}
			} catch (final Exception t) {
				if (IOException.class.isAssignableFrom(t.getClass())
						&& config.getIOExceptionFilters().size() > 0) {
					FilterContext<?> fc = new FilterContext.FilterContextBuilder()
							.asyncHandler(future.getAsyncHandler())
							.request(future.getRequest())
							.ioException(IOException.class.cast(t)).build();
					fc = handleIoException(fc, future);

					if (fc.replayRequest()) {
						replayRequest(future, fc, response, ctx);
						return;
					}
				}

				try {
					abort(future, t);
				} finally {
					finishUpdate(future, ctx, false);
					throw t;
				}
			}
		}

		@Override
		public void onError(final ChannelHandlerContext ctx,
				final ExceptionEvent e) {
		}

		@Override
		public void onClose(final ChannelHandlerContext ctx,
				final ChannelStateEvent e) {
		}
	}

}
