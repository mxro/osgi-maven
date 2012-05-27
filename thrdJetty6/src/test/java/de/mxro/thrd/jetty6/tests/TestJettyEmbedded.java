package de.mxro.thrd.jetty6.tests;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.AbstractHandler;

public class TestJettyEmbedded {

	@Test
	public void test_start_embedded_jetty() throws Exception {
		final Handler handler = new AbstractHandler() {
			@Override
			public void handle(final String target,
					final HttpServletRequest request,
					final HttpServletResponse response, final int dispatch)
					throws IOException, ServletException {
				response.setContentType("text/html");
				response.setStatus(HttpServletResponse.SC_OK);
				response.getWriter().println("<h1>Hello</h1>");
				((Request) request).setHandled(true);
			}
		};

		final Server server = new Server(8080);
		server.setHandler(handler);

		// server.start();

		// Thread.sleep(50000);
	}

}
