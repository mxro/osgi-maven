package de.mxro.thrd.netty4.tests;

import org.junit.Test;

import de.mxro.thrd.netty4.tests.http.HttpHelloWorldServer;

public class TestNetty4 {

	@Test
	public void test_netty4_http_server() {
		
		final HttpHelloWorldServer server = new HttpHelloWorldServer(18912);
		
		
		new Thread() {

			@Override
			public void run() {
				server.run();
			}
			
		}.start();
		
	}
	
}
