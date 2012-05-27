package de.mxro.thrd.mysqlconnectorj51.tests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;

public class SimpleMysqlApp {

	public static void main(final String[] args) throws Exception {

		Class.forName("com.mysql.jdbc.Driver");

		final java.sql.Connection connection = DriverManager
				.getConnection("jdbc:mysql://localhost/java?"
						+ "user=java&password=java");

		final Statement statement = connection.createStatement();

		final ResultSet resultSet = statement
				.executeQuery("select * from JAVA.TEST1");
		while (resultSet.next()) {
			System.out.println(resultSet.getString("Id"));
		}

		// -----
		// Write binary data
		final String recordId = "http://inserted.com";
		try {

			final String insertTemplate = "INSERT INTO `test1`(`Id`, `Value`) VALUES ('"
					+ recordId + "',?)";
			final java.sql.PreparedStatement insertStatement = connection
					.prepareStatement(insertTemplate);

			final byte[] data1 = "wrong test value".getBytes("UTF-8");
			final byte[] data2 = "test value".getBytes("UTF-8");

			final ByteArrayInputStream bos = new ByteArrayInputStream(data1);

			insertStatement.setBinaryStream(1, bos, data1.length);

			System.out.println("Inserted: " + insertStatement.executeUpdate()
					+ " rows with data [" + Arrays.toString(data1) + "]");

			insertStatement.close();
			bos.close();

			// -----
			// Update binary data
			final String updateTemplate = "INSERT INTO `test1`(`Id`, `Value`) VALUES (?,?) ON DUPLICATE KEY UPDATE `Value` = ?";

			final PreparedStatement updateStatement = connection
					.prepareStatement(updateTemplate);

			updateStatement.setString(1, recordId);
			final ByteArrayInputStream bos2 = new ByteArrayInputStream(data2);
			final ByteArrayInputStream bos3 = new ByteArrayInputStream(data2);
			updateStatement.setBinaryStream(2, bos2);
			updateStatement.setBinaryStream(3, bos3);

			updateStatement.executeUpdate();
			bos2.close();
			updateStatement.close();

			// ------
			// Read binary data

			final String selectTemplate = "SELECT `Id`, `Value` FROM `test1` WHERE `Id` = '"
					+ recordId + "'";

			final PreparedStatement selectStatement = connection
					.prepareStatement(selectTemplate);

			final ResultSet resultSet2 = selectStatement.executeQuery();

			while (resultSet2.next()) {
				final InputStream is = resultSet2.getBinaryStream(2);
				final byte[] readData = toByteArray(is);

				System.out.println("read data [" + Arrays.toString(readData)
						+ "]");
				System.out.println(Arrays.equals(data2, readData));
				is.close();

			}
		} finally {
			// -----
			// Delete Record

			final String deleteTemplate = "DELETE FROM `test1` WHERE `Id` = '"
					+ recordId + "'";

			final PreparedStatement deleteStatement = connection
					.prepareStatement(deleteTemplate);

			System.out.println("Deleted " + deleteStatement.executeUpdate()
					+ " rows.");
		}

	}

	public final static byte[] toByteArray(final InputStream is)
			throws IOException {

		final ByteArrayOutputStream bos = new ByteArrayOutputStream();

		int available = 0;
		while ((available = is.available()) > 0) {
			final byte[] buffer = new byte[available];
			final int readBytes = is.read(buffer);
			bos.write(buffer, 0, readBytes);
		}

		return bos.toByteArray();
	}

}
