/*
 * Copyright (c) 2010, Jan Stender, Bjoern Kolbeck, Mikael Hoegqvist,
 *                     Felix Hupfeld, Zuse Institute Berlin
 * 
 * Licensed under the BSD License, see LICENSE file for details.
 * 
 */
package de.mxro.thrd.babudb05.sandbox;

import java.io.IOException;

import de.mxro.thrd.babudb05.BabuDBFactory;
import de.mxro.thrd.babudb05.api.BabuDB;
import de.mxro.thrd.babudb05.api.database.Database;
import de.mxro.thrd.babudb05.api.database.DatabaseInsertGroup;
import de.mxro.thrd.babudb05.api.exception.BabuDBException;
import de.mxro.thrd.babudb05.config.BabuDBConfig;
import de.mxro.thrd.babudb05.log.DiskLogger.SyncMode;

/**
 * 
 * @author bjko
 */
public class HugeDBTest {
    
    public static final String dbname = "testdb";
        
    private BabuDB             database;
    
    public HugeDBTest(String basedir) throws IOException, BabuDBException {
        
        // checkpoint every 1m and check every 1 min
        database = BabuDBFactory.createBabuDB(new BabuDBConfig(basedir, basedir, 0, 1024 * 128, 60 * 1,
            SyncMode.ASYNC, 0, 0, false, 16, 1024 * 1024 * 512));
        
        database.getDatabaseManager().createDatabase(dbname, 5);
    }
    
    public void startTest() throws Exception {
        
        final Database db = database.getDatabaseManager().getDatabase(dbname);
        long dbSize = 0;
        boolean[] contents = new boolean[10000];
        
        for (;;) {
            
            final int numInGroup = (int) Math.round(Math.random() * 9) + 1;
            final DatabaseInsertGroup ig = db.createInsertGroup();
            for (int i = 0; i < numInGroup; i++) {
                final byte[] key = generateData(30);
                final byte[] value = generateData(10000);
                ig.addInsert((int) (Math.random() * 5), key, value);
                dbSize += key.length + value.length;
            }
            
            db.insert(ig, null).get();
            
            int block = (int) (dbSize / (1024 * 1204 * 50));
            if (block > 0 && !contents[block]) {
                contents[block] = true;
                System.out.println(dbSize / (1024 * 1204) + " MB");
                Thread.sleep(8000 * block);
            }
            
        }
        
    }
        
    public void shutdown() throws Exception {
        database.getCheckpointer().checkpoint();
        database.shutdown();
    }
    
    public static void main(String[] args) throws Exception {
        HugeDBTest test = new HugeDBTest("/scratch/disk1/dbtest");
        test.startTest();
    }
    
    private static byte[] generateData(int size) {
        byte[] bytes = new byte[size];
        for(int i = 0; i < bytes.length; i++)
            bytes[i] = (byte) (Math.random() * 256);
        return bytes;
    }

}
