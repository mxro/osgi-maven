package de.mxro.thrd.netty3.tests;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

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
public class MyServerRequestHandler extends SimpleChannelUpstreamHandler {

	private final ByteArrayOutputStream receivedData;
	private boolean chunked;

	@Override
	public void messageReceived(final ChannelHandlerContext ctx,
			final MessageEvent e) throws Exception {

		if (!chunked) {
			final HttpRequest request = (HttpRequest) e.getMessage();

			final ChannelBuffer buffer = request.getContent();
			receivedData.write(buffer.array());
			//System.out.println("received "+buffer.array() );
			//System.out.println(buffer.array().length);
			if (!request.isChunked()) {
				processRequest(e);
			} else {
				chunked = true;

			}
			//final boolean keepAlive = isKeepAlive(request);
		} else {
			final HttpChunk chunk = (HttpChunk) e.getMessage();
			final ChannelBuffer buffer = chunk.getContent();
			receivedData.write(buffer.array());
			if (chunk.isLast()) {
				processRequest(e);
			}
		}

	}

	public void processRequest(final MessageEvent e) {
		try {
			System.out.println("Server received: " + receivedData.toString("UTF-8"));
		} catch (final UnsupportedEncodingException e1) {
			throw new RuntimeException(e1);
		}
		sendResponse(e);
	}

	public void sendResponse(final MessageEvent e) {
		// Build the response object.
		final HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);

		final ByteArrayOutputStream bos = receivedData;
		try {
			bos.write(" successfully received by server".getBytes("UTF-8"));
		} catch (final UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (final IOException e1) {
			e1.printStackTrace();
		}
		final ChannelBuffer buffer = ChannelBuffers.wrappedBuffer(bos
				.toByteArray());
		response.setContent(buffer);
		// response.setContent(arg0)
		// response.setContent(ChannelBuffers.copiedBuffer(buf.toString(),
		// CharsetUtil.UTF_8));
		response.setHeader(CONTENT_TYPE, "application/octet-stream");

		final ChannelFuture future = e.getChannel().write(response);

		// Close the non-keep-alive connection after the write operation is
		// done.
		// if (!keepAlive) {
		future.addListener(ChannelFutureListener.CLOSE);
		// }
	}

	public MyServerRequestHandler() {
		receivedData = new ByteArrayOutputStream();
		chunked = false;
	}

}
