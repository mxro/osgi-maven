/*
 * Copyright (c) 2009 - 2011, Jan Stender, Bjoern Kolbeck, Mikael Hoegqvist,
 *                     Felix Hupfeld, Felix Langner, Zuse Institute Berlin
 * 
 * Licensed under the BSD License, see LICENSE file for details.
 * 
 */
package de.mxro.thrd.babudb05.snapshots;

import de.mxro.thrd.babudb05.api.database.ResultSet;
import de.mxro.thrd.babudb05.api.exception.BabuDBException;

public interface BabuDBView {
    
    public byte[] directLookup(int indexId, byte[] key) throws BabuDBException;
    
    public ResultSet<byte[], byte[]> directPrefixLookup(int indexId, byte[] key, boolean ascending)
        throws BabuDBException;
    
    public ResultSet<byte[], byte[]> directRangeLookup(int indexId, byte[] from, byte[] to,
        boolean ascending) throws BabuDBException;
    
    public void shutdown() throws BabuDBException;
    
}
