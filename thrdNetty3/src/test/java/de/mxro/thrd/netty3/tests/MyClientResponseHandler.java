package de.mxro.thrd.netty3.tests;

import java.io.ByteArrayOutputStream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpChunk;
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
public class MyClientResponseHandler extends SimpleChannelUpstreamHandler {

	ByteArrayOutputStream bos;
	private boolean chunked;

	public void processResponse() {
		System.out.println("Client Received: "+bos.toString());
	}

	@Override
	public void messageReceived(final ChannelHandlerContext ctx,
			final MessageEvent e) throws Exception {
		if (!chunked) {
			final HttpResponse response = (HttpResponse) e.getMessage();
			final boolean isChunked = response.isChunked();

			final ChannelBuffer content = response.getContent();
			bos.write(content.array());
			if (!isChunked) {
				processResponse();
			} else {
				chunked = true;
				
			}
		} else {
			final HttpChunk chunk = (HttpChunk) e.getMessage();
			bos.write(chunk.getContent().array());
			if (chunk.isLast()) {
				processResponse();
			}
		}
	}

	public MyClientResponseHandler() {
		this.bos = new ByteArrayOutputStream();
		this.chunked = false;
	}

}
