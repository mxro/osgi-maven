package de.mxro.thrd.netty3.tests;

import static org.jboss.netty.channel.Channels.pipeline;

import javax.net.ssl.SSLEngine;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.example.securechat.SecureChatSslContextFactory;
import org.jboss.netty.handler.codec.http.HttpContentCompressor;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.ssl.SslHandler;

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
public class MyServerPipelineFactory implements ChannelPipelineFactory {

	protected final boolean useSsl;

	@Override
	public ChannelPipeline getPipeline() throws Exception {
		// Create a default pipeline implementation.
		final ChannelPipeline pipeline = pipeline();

		// Uncomment the following line if you want HTTPS
		if (useSsl) {	
			final SSLEngine engine = SecureChatSslContextFactory
					.getServerContext().createSSLEngine();
			
			engine.setUseClientMode(false);
			pipeline.addLast("ssl", new SslHandler(engine));
		}

		pipeline.addLast("decoder", new HttpRequestDecoder());
		// Uncomment the following line if you don't want to handle HttpChunks.
		// pipeline.addLast("aggregator", new HttpChunkAggregator(1048576));
		pipeline.addLast("encoder", new HttpResponseEncoder());
		// Remove the following line if you don't want automatic content
		// compression.
		pipeline.addLast("deflater", new HttpContentCompressor());
		pipeline.addLast("handler", new MyServerRequestHandler());
		return pipeline;
	}

	public MyServerPipelineFactory(final boolean useSsl) {
		super();
		this.useSsl = useSsl;
	}

}
