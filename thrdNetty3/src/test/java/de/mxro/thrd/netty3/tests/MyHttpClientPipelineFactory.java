package de.mxro.thrd.netty3.tests;

import static org.jboss.netty.channel.Channels.pipeline;

import javax.net.ssl.SSLEngine;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.example.securechat.SecureChatSslContextFactory;
import org.jboss.netty.handler.codec.http.HttpClientCodec;
import org.jboss.netty.handler.codec.http.HttpContentDecompressor;
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
public class MyHttpClientPipelineFactory implements ChannelPipelineFactory {

	private final boolean ssl;

    public MyHttpClientPipelineFactory(final boolean ssl) {
        this.ssl = ssl;
    }
	
	@Override
	public ChannelPipeline getPipeline() throws Exception {
		 final ChannelPipeline pipeline = pipeline();
		if (ssl) {
            final SSLEngine engine =
                SecureChatSslContextFactory.getClientContext().createSSLEngine();
            engine.setUseClientMode(true);

            pipeline.addLast("ssl", new SslHandler(engine));
        }

        pipeline.addLast("codec", new HttpClientCodec());

        // Remove the following line if you don't want automatic content decompression.
        pipeline.addLast("inflater", new HttpContentDecompressor());

        // Uncomment the following line if you don't want to handle HttpChunks.
        //pipeline.addLast("aggregator", new HttpChunkAggregator(1048576));
        pipeline.addLast("handler", new MyClientResponseHandler());
        
        return pipeline;
	
	}

}
