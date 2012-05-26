package de.mxro.thrd.netty3.tests;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executors;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.junit.Test;

/**
 * All examples based on the <a href=
 * "http://netty.io/docs/3.2.6.Final/xref/org/jboss/netty/example/http/snoop/package-summary.html"
 * >Netty Http server Examples</a>
 * <br /> 
 * 
 * 
 * @author <a href="http://www.mxro.de/">Max Rohde</a>
 * 
 */
public class TestNettyExploration {

	public Channel startServer(final int port, final boolean useSsl) {
		// Configure the server.
		final ServerBootstrap bootstrap = new ServerBootstrap(
				new NioServerSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));

		// Set up the event pipeline factory.
		bootstrap.setPipelineFactory(new MyServerPipelineFactory(useSsl));

		// Bind and start to accept incoming connections.
		final Channel server = bootstrap.bind(new InetSocketAddress(port));

		System.out.println("Server started on port " + port);
		return server;

	}

	public void startClient(final URI uri) throws UnsupportedEncodingException {
		final String scheme = uri.getScheme() == null ? "http" : uri
				.getScheme();
		final String host = uri.getHost() == null ? "localhost" : uri.getHost();

		final int port = uri.getPort();
		/*
		 * if (port == -1) { if (scheme.equalsIgnoreCase("http")) { port = 80; }
		 * else if (scheme.equalsIgnoreCase("https")) { port = 443; } }
		 */

		final boolean ssl = scheme.equalsIgnoreCase("https");
		final ClientBootstrap bootstrap = new ClientBootstrap(
				new NioClientSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));

		bootstrap.setPipelineFactory(new MyHttpClientPipelineFactory(ssl));

		// Start the connection attempt.
		final ChannelFuture future = bootstrap.connect(new InetSocketAddress(
				host, port));

		// Wait until the connection attempt succeeds or fails.
		final Channel channel = future.awaitUninterruptibly().getChannel();
		if (!future.isSuccess()) {
			bootstrap.releaseExternalResources();

			throw new RuntimeException(future.getCause());

		}
		System.out.println("Client started successfully");
		final byte[] payload = "sent to server".getBytes("UTF-8");
		final HttpRequest request = new DefaultHttpRequest(
				HttpVersion.HTTP_1_1, HttpMethod.POST, uri.toASCIIString());
		request.setHeader(HttpHeaders.Names.HOST, host);
		request.setHeader(HttpHeaders.Names.CONNECTION,
				HttpHeaders.Values.CLOSE);
		request.setHeader(HttpHeaders.Names.ACCEPT_ENCODING,
				HttpHeaders.Values.GZIP);
		request.setHeader(HttpHeaders.Names.CONTENT_LENGTH, payload.length);
		final ChannelBuffer buffer = ChannelBuffers.wrappedBuffer(payload);
		request.setContent(buffer);

		// Send the HTTP request.
		channel.write(request);

		// Wait for the server to close the connection.
		channel.getCloseFuture().awaitUninterruptibly();

		// Shut down executor threads to exit.
		bootstrap.releaseExternalResources();

	}

	@Test
	public void test_netty_http_server() throws Exception {
		// without encryption
		{
			final Channel server = startServer(8080, false);
			startClient(URI.create("http://localhost:8080"));
			server.unbind().awaitUninterruptibly();
		}

		// with encryption
		{
			final Channel server = startServer(8080, true);
			startClient(URI.create("https://localhost:8080"));
			server.unbind().awaitUninterruptibly();
		}

		// with built-in Java client
		{
			final Channel server = startServer(8080, false);
			final URI uri = URI.create("http://localhost:8080");
			final URLConnection connection = uri.toURL().openConnection();
			connection.setDoOutput(true);
			final OutputStreamWriter out = new OutputStreamWriter(
					connection.getOutputStream());
			out.write("Send from Java client");
			out.close();

			final BufferedReader in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));

			System.out.println("Java client received: " + in.readLine());
			in.close();
			server.unbind().awaitUninterruptibly();
		}

		// with built-in Java client SSL
		{
			final Channel server = startServer(8080, true);
			disableSSLCertificateChecking();
			final URI uri = URI.create("https://localhost:8080");
			final URLConnection connection = uri.toURL().openConnection();
			connection.setDoOutput(true);
			final OutputStreamWriter out = new OutputStreamWriter(
					connection.getOutputStream());
			out.write("Send from Java client");
			out.close();

			final BufferedReader in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));

			System.out.println("Java client received: " + in.readLine());
			in.close();
			server.unbind().awaitUninterruptibly();
		}

	}

	/**
	 * Disables the SSL certificate checking for new instances of
	 * {@link HttpsURLConnection} This has been created to aid testing on a
	 * local box, not for use on production.
	 * 
	 * @see Based on
	 *      http://blerg.net/Ignore-certificate-for-HttpURLConnection-in-Android
	 */
	private static void disableSSLCertificateChecking() {
		final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			@Override
			public void checkServerTrusted(
					final java.security.cert.X509Certificate[] certs,
					final String authType)
					throws java.security.cert.CertificateException {
				return;
			}

			@Override
			public void checkClientTrusted(
					final java.security.cert.X509Certificate[] certs,
					final String authType)
					throws java.security.cert.CertificateException {
				return;
			}
		} };

		try {
			final SSLContext sc = SSLContext.getInstance("TLS");

			sc.init(null, trustAllCerts, new java.security.SecureRandom());

			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection
					.setDefaultHostnameVerifier(new HostnameVerifier() {

						@Override
						public boolean verify(final String arg0,
								final SSLSession arg1) {

							return true;
						}

					});
		} catch (final KeyManagementException e) {
			e.printStackTrace();
		} catch (final NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

}
