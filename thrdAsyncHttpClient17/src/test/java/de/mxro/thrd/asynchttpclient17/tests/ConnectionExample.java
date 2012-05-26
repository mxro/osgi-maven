package de.mxro.thrd.asynchttpclient17.tests;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;

public class ConnectionExample {

	public static void main(final String[] args) throws Exception {
		final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

		asyncHttpClient.prepareGet("https://www.google.co.nz/").execute(
				new AsyncCompletionHandler<Response>() {

					@Override
					public Response onCompleted(final Response response)
							throws Exception {

						// System.out.println(response.getResponseBody());
						return response;
					}

				});

	}

}
