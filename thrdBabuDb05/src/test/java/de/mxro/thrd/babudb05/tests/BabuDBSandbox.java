package de.mxro.thrd.babudb05.tests;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import junit.framework.Assert;

import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import de.mxro.thrd.babudb05.BabuDBFactory;
import de.mxro.thrd.babudb05.api.BabuDB;
import de.mxro.thrd.babudb05.api.DatabaseManager;
import de.mxro.thrd.babudb05.api.database.Database;
import de.mxro.thrd.babudb05.api.database.DatabaseInsertGroup;
import de.mxro.thrd.babudb05.api.database.DatabaseRequestResult;
import de.mxro.thrd.babudb05.api.database.ResultSet;
import de.mxro.thrd.babudb05.config.BabuDBConfig;
import de.mxro.thrd.babudb05.log.DiskLogger.SyncMode;

public class BabuDBSandbox {

	public static final File CDATA = new File("C:\\Data\\data");
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	public void test_write_big_values_in_single_opetaions() throws Exception {
		final String rootPath = CDATA.getAbsolutePath();// folder.getRoot().getAbsolutePath();
		final String dbPath = rootPath + "/db";
		final String logPath = rootPath + "/dblog";

		final BabuDB databaseSystem = BabuDBFactory
				.createBabuDB(new BabuDBConfig(dbPath, logPath, 4,
						1024 * 1024 * 16, 5 * 60, SyncMode.FSYNC, 50, 0, false,
						16, 1024 * 1024 * 512));

		final DatabaseManager dbm = databaseSystem.getDatabaseManager();
		final Database db = dbm.createDatabase("myDB", 5);

		final Object context = null;

		String bigValue = "BIG_VALUE";
		for (int i = 1; i < 100; i++) {
			bigValue = bigValue + "____Added___" + i + "_weijfoeigoreihgorig";

		}

		for (int i = 1; i <= 10000; i++) {
			db.singleInsert(2,
					("bigkey" + String.valueOf(i)).getBytes("UTF-8"),
					bigValue.getBytes("UTF-8"), context).get();
		}

		databaseSystem.getCheckpointer().checkpoint();

		databaseSystem.shutdown(true);
	}

	public void test_write_big_values_in_single_opetaions_async()
			throws Exception {
		final String rootPath = CDATA.getAbsolutePath();// folder.getRoot().getAbsolutePath();
		final String dbPath = rootPath + "/db";
		final String logPath = rootPath + "/dblog";

		final BabuDB databaseSystem = BabuDBFactory
				.createBabuDB(new BabuDBConfig(dbPath, logPath, 4,
						1024 * 1024 * 16, 5 * 60, SyncMode.FSYNC, 50, 0, false,
						16, 1024 * 1024 * 512));

		final DatabaseManager dbm = databaseSystem.getDatabaseManager();
		final Database db = dbm.createDatabase("myDB", 5);

		final Object context = null;

		String bigValue = "BIG_VALUE";
		for (int i = 1; i < 100; i++) {
			bigValue = bigValue + "____Added___" + i + "_weijfoeigoreihgorig";
		}

		final List<DatabaseRequestResult<Object>> results = new ArrayList<DatabaseRequestResult<Object>>(
				10000);
		for (int i = 1; i <= 10000; i++) {
			final DatabaseRequestResult<Object> singleInsert = db.singleInsert(
					2, ("bigkey" + String.valueOf(i)).getBytes("UTF-8"),
					bigValue.getBytes("UTF-8"), context);
			results.add(singleInsert);
		}

		Assert.assertEquals(10000, results.size());

		for (final DatabaseRequestResult<Object> result : results) {
			result.get();
		}

		databaseSystem.getCheckpointer().checkpoint();

		databaseSystem.shutdown(true);
	}

	public void test_write_and_read_big_values_in_single_opetaions_async()
			throws Exception {
		final String rootPath = CDATA.getAbsolutePath();// folder.getRoot().getAbsolutePath();
		final String dbPath = rootPath + "/db";
		final String logPath = rootPath + "/dblog";

		final BabuDB databaseSystem = BabuDBFactory
				.createBabuDB(new BabuDBConfig(dbPath, logPath, 4,
						1024 * 1024 * 16, 5 * 60, SyncMode.FSYNC, 50, 0, false,
						16, 1024 * 1024 * 512));

		final DatabaseManager dbm = databaseSystem.getDatabaseManager();
		final Database db = dbm.createDatabase("myDB", 5);

		final Object context = null;

		String bigValue = "BIG_VALUE";
		for (int i = 1; i < 100; i++) {
			bigValue = bigValue + "____Added___" + i + "_weijfoeigoreihgorig";

		}

		for (int i = 1; i <= 10000; i++) {
			db.singleInsert(2,
					("bigkey" + String.valueOf(i)).getBytes("UTF-8"),
					bigValue.getBytes("UTF-8"), context);
		}

		final List<DatabaseRequestResult<byte[]>> results = new ArrayList<DatabaseRequestResult<byte[]>>(
				10000);

		for (int i = 1; i <= 10000; i++) {
			final DatabaseRequestResult<byte[]> result = db.lookup(2,
					("bigkey" + String.valueOf(i)).getBytes("UTF-8"), context);
			// final byte[] value = result4.get();
			results.add(result);
		}

		assert results.size() == 10000;

		for (final DatabaseRequestResult<byte[]> result : results) {
			final byte[] value = result.get();
			Assert.assertEquals(bigValue, decodeBytes(value));
		}

		databaseSystem.getCheckpointer().checkpoint();

		databaseSystem.shutdown(true);
	}

	
	public void test_write_and_read_big_values_in_single_opetaions_async_with_cold_start()
			throws Exception {
		final String rootPath = CDATA.getAbsolutePath();// folder.getRoot().getAbsolutePath();
		final String dbPath = rootPath + "/db";
		final String logPath = rootPath + "/dblog";

		final Object context = null;

		String bigValue = "BIG_VALUE";
		for (int i = 1; i < 100; i++) {
			bigValue = bigValue + "____Added___" + i + "_weijfoeigoreihgorig";

		}

		// WRITE
		{
			final BabuDB databaseSystem = BabuDBFactory
					.createBabuDB(new BabuDBConfig(dbPath, logPath, 4,
							1024 * 1024 * 16, 5 * 60, SyncMode.FSYNC, 50, 0,
							false, 16, 1024 * 1024 * 512));

			final DatabaseManager dbm = databaseSystem.getDatabaseManager();
			final Database db = dbm.createDatabase("myDB", 5);

			final List<DatabaseRequestResult<Object>> results = new ArrayList<DatabaseRequestResult<Object>>(10000);
			
			for (int i = 1; i <= 10000; i++) {
				final DatabaseRequestResult<Object> singleInsert = db.singleInsert(2,
						("bigkey" + String.valueOf(i)).getBytes("UTF-8"),
						bigValue.getBytes("UTF-8"), context);
				results.add(singleInsert);
			}
			for (final DatabaseRequestResult<Object> result : results) {
				result.get();
			}
			
			databaseSystem.getCheckpointer().checkpoint();

			databaseSystem.shutdown(true);

		}
		System.out.println("all written.");
		// READ
		{

			final BabuDB databaseSystem = BabuDBFactory
					.createBabuDB(new BabuDBConfig(dbPath, logPath, 4,
							1024 * 1024 * 16, 5 * 60, SyncMode.FSYNC, 50, 0,
							false, 16, 1024 * 1024 * 512));

			final DatabaseManager dbm = databaseSystem.getDatabaseManager();
			final Database db = dbm.getDatabase("myDB");
			final List<DatabaseRequestResult<byte[]>> results = new ArrayList<DatabaseRequestResult<byte[]>>(
					10000);

			for (int i = 1; i <= 10000; i++) {
				final DatabaseRequestResult<byte[]> result = db.lookup(2,
						("bigkey" + String.valueOf(i)).getBytes("UTF-8"),
						context);
				// final byte[] value = result4.get();
				results.add(result);
			}

			assert results.size() == 10000;

			for (final DatabaseRequestResult<byte[]> result : results) {
				final byte[] value = result.get();
				Assert.assertEquals(bigValue, decodeBytes(value));
				
			}

		}

	}

	public void test_write_small_values_in_single_opetaions() throws Exception {
		final String rootPath = CDATA.getAbsolutePath();// folder.getRoot().getAbsolutePath();
		final String dbPath = rootPath + "/db";
		final String logPath = rootPath + "/dblog";

		final BabuDB databaseSystem = BabuDBFactory
				.createBabuDB(new BabuDBConfig(dbPath, logPath, 4,
						1024 * 1024 * 16, 5 * 60, SyncMode.FSYNC, 50, 0, false,
						16, 1024 * 1024 * 512));

		final DatabaseManager dbm = databaseSystem.getDatabaseManager();
		final Database db = dbm.createDatabase("myDB", 5);

		final Object context = null;

		final String smallValue = "SMALL_VALUE";
		for (int i = 1; i <= 10000; i++) {
			db.singleInsert(2, ("key" + String.valueOf(i)).getBytes("UTF-8"),
					smallValue.getBytes("UTF-8"), context).get();
		}
		System.out.println(smallValue.getBytes("UTF-8").length);

		databaseSystem.getCheckpointer().checkpoint();

		databaseSystem.shutdown(true);
	}

	public void test_write_small_values_in_single_opetaions_async()
			throws Exception {
		final String rootPath = CDATA.getAbsolutePath();// folder.getRoot().getAbsolutePath();
		final String dbPath = rootPath + "/db";
		final String logPath = rootPath + "/dblog";

		final BabuDB databaseSystem = BabuDBFactory
				.createBabuDB(new BabuDBConfig(dbPath, logPath, 4,
						1024 * 1024 * 16, 5 * 60, SyncMode.FSYNC, 50, 0, false,
						16, 1024 * 1024 * 512));

		final DatabaseManager dbm = databaseSystem.getDatabaseManager();
		final Database db = dbm.createDatabase("myDB", 5);

		final Object context = null;

		final String smallValue = "SMALL_VALUE";
		for (int i = 1; i <= 10000; i++) {
			db.singleInsert(2, ("key" + String.valueOf(i)).getBytes("UTF-8"),
					smallValue.getBytes("UTF-8"), context);
		}
		System.out.println(smallValue.getBytes("UTF-8").length);

		databaseSystem.getCheckpointer().checkpoint();

		databaseSystem.shutdown(true);
	}

	public void test_write_big_values_compressed() throws Exception {

		final String rootPath = CDATA.getAbsolutePath();// folder.getRoot().getAbsolutePath();
		final String dbPath = rootPath + "/db";
		final String logPath = rootPath + "/dblog";

		final BabuDB databaseSystem = BabuDBFactory
				.createBabuDB(new BabuDBConfig(dbPath, logPath, 4,
						1024 * 1024 * 16, 5 * 60, SyncMode.FSYNC, 50, 0, true,
						16, 1024 * 1024 * 512));

		final DatabaseManager dbm = databaseSystem.getDatabaseManager();
		final Database db = dbm.createDatabase("myDB", 5);

		final Object context = null;

		String bigValue = "BIG_VALUE";
		for (int i = 1; i < 100; i++) {
			bigValue = bigValue + "____Added___" + i + "_weijfoeigoreihgorig";

		}

		for (int i = 1; i <= 500; i++) {
			db.singleInsert(2,
					("bigkey" + String.valueOf(i)).getBytes("UTF-8"),
					bigValue.getBytes("UTF-8"), context).get();
		}

		databaseSystem.getCheckpointer().checkpoint();

		databaseSystem.shutdown(true);

	}

	public void test_write_big_values_uncompressed() throws Exception {

		final String rootPath = CDATA.getAbsolutePath();// folder.getRoot().getAbsolutePath();
		final String dbPath = rootPath + "/db";
		final String logPath = rootPath + "/dblog";

		final BabuDB databaseSystem = BabuDBFactory
				.createBabuDB(new BabuDBConfig(dbPath, logPath, 4,
						1024 * 1024 * 16, 5 * 60, SyncMode.FSYNC, 50, 0, false,
						16, 1024 * 1024 * 512));

		final DatabaseManager dbm = databaseSystem.getDatabaseManager();
		final Database db = dbm.createDatabase("myDB", 5);

		final Object context = null;

		String bigValue = "BIG_VALUE";
		for (int i = 1; i < 100; i++) {
			bigValue = bigValue + "____Added___" + i + "_weijfoeigoreihgorig";

		}

		for (int i = 1; i <= 500; i++) {
			db.singleInsert(2,
					("bigkey" + String.valueOf(i)).getBytes("UTF-8"),
					bigValue.getBytes("UTF-8"), context).get();
		}

		databaseSystem.getCheckpointer().checkpoint();

		databaseSystem.shutdown(true);

	}

	public void test_create_and_load_db() throws Exception {

		final String rootPath = CDATA.getAbsolutePath();// folder.getRoot().getAbsolutePath();
		final String dbPath = rootPath + "/db";
		final String logPath = rootPath + "/dblog";

		final BabuDB databaseSystem = BabuDBFactory
				.createBabuDB(new BabuDBConfig(dbPath, logPath, 4,
						1024 * 1024 * 16, 5 * 60, SyncMode.FSYNC, 50, 0, false,
						16, 1024 * 1024 * 512));

		final DatabaseManager dbm = databaseSystem.getDatabaseManager();
		final Database db = dbm.createDatabase("myDB", 5);

		final Object context = null;

		final DatabaseRequestResult<Object> result = db.singleInsert(2,
				"key1".getBytes("UTF-8"), "value1".getBytes("UTF-8"), context);
		result.get();

		final DatabaseInsertGroup ig = db.createInsertGroup();
		ig.addInsert(2, "key2".getBytes("UTF-8"), "value2".getBytes("UTF-8"));
		ig.addInsert(2, "key3".getBytes("UTF-8"), "value3".getBytes("UTF-8"));
		final DatabaseRequestResult<Object> result2 = db.insert(ig, context);
		result2.get();

		final DatabaseRequestResult<Object> result3 = db.singleInsert(2,
				"key1".getBytes("UTF-8"), null, context);
		result3.get();

		String bigValue = "BIG_VALUE";
		for (int i = 1; i < 100; i++) {
			bigValue = bigValue + "____Added___" + i + "_weijfoeigoreihgorig";

		}

		for (int i = 1; i <= 10000; i++) {
			db.singleInsert(2,
					("bigkey" + String.valueOf(i)).getBytes("UTF-8"),
					bigValue.getBytes("UTF-8"), context).get();
		}

		System.out.println(bigValue.getBytes("UTF-8").length * 10000);

		final DatabaseRequestResult<byte[]> result4 = db.lookup(2,
				"key2".getBytes("UTF-8"), context);
		final byte[] value = result4.get();
		final String decoded = decodeBytes(value);
		System.out.println(decoded);

		final DatabaseRequestResult<ResultSet<byte[], byte[]>> result5 = db
				.prefixLookup(2, "k".getBytes(), context);
		final Iterator<Entry<byte[], byte[]>> iterator = result5.get();
		while (iterator.hasNext()) {
			final Entry<byte[], byte[]> keyValuePair = iterator.next();
			System.out.println(decodeBytes(keyValuePair.getKey()) + ":"
					+ decodeBytes(keyValuePair.getValue()));
		}

		databaseSystem.getCheckpointer().checkpoint();

		databaseSystem.shutdown(true);

	}

	protected String decodeBytes(final byte[] value)
			throws UnsupportedEncodingException {
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bos.write(value, 0, value.length);
		final String decoded = bos.toString("UTF-8");
		return decoded;
	}

}
