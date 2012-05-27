package de.mxro.thrd.javaxmail14.tests;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMailApp {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {

		final Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
		props.put("mail.debug", "false");
		props.put("mail.debug.auth", "false");

		// final MailHandler h = new MailHandler(props);
		// h.setLevel(Level.OFF);

		final Session session = Session.getDefaultInstance(props,
				new javax.mail.Authenticator() {
					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication("max@onedb.de", "");
					}
				});

		try {

			final Message message = new MimeMessage(session);

			message.setFrom(new InternetAddress("max@onedb.de"));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse("max@onedb.de"));
			message.setRecipients(Message.RecipientType.BCC,
					InternetAddress.parse("max@onedb.de"));
			message.setSubject("Your onedb Api Key");
			message.setText("Dear User,"
					+ "\n\nThank you for getting an API key for onedb.de.\n\n"
					+ "    Your key is: [ergjoerijwefjoiwef] (excluding [])\n\n"
					+ "To get started, check out:\n\n"
					+ "   http://missinglinkblog.com/2012/04/28/onedb-getting-started\n\n"
					+ "Max");

			Transport.send(message);

			System.out.println("Done");

		} catch (final MessagingException e) {
			throw new RuntimeException(e);
		}
	}

}
