/*
 * Copyright (c) 2008, Jan Stender, Bjoern Kolbeck, Mikael Hoegqvist,
 *                     Felix Hupfeld, Zuse Institute Berlin
 * 
 * Licensed under the BSD License, see LICENSE file for details.
 * 
 */

package de.mxro.thrd.babudb05.index.reader;

import java.nio.ByteBuffer;


import de.mxro.thrd.babudb05.api.index.ByteRangeComparator;
import de.mxro.thrd.babudb05.index.ByteRange;
import de.mxro.thrd.xstreemfs.foundation.buffer.BufferPool;
import de.mxro.thrd.xstreemfs.foundation.buffer.ReusableBuffer;
import de.mxro.thrd.xstreemfs.foundation.util.OutputUtils;

public class FixedLenMiniPage extends MiniPage {
    
    private final int entrySize;
    
    private final int limit;
    
    public FixedLenMiniPage(int entrySize, int numEntries, ByteBuffer buf, int offset, int limit,
        ByteRangeComparator comp) {
        
        super(numEntries, buf, offset, comp);
        
        this.entrySize = entrySize;
        this.limit = limit;
    }
    
    public ByteRange getEntry(int n) {
        assert (offset < buf.limit()) : "offset == " + offset + ", buf.limit == " + buf.limit()
            + ", entrySize == " + entrySize + ", n == " + n;
        return new ByteRange(buf, offset + n * entrySize, offset + (n + 1) * entrySize - 1);
    }
    
    public String toString() {
        
        buf.position(offset);
        buf.limit(limit);
        ReusableBuffer newBuf = BufferPool.allocate(limit - buf.position());
        newBuf.put(buf);
        String result = OutputUtils.byteArrayToFormattedHexString(newBuf.array());
        BufferPool.free(newBuf);
        buf.clear();
        
        return result;
    }
}
