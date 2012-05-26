/*
 * Copyright (c) 2008-2010 by Jan Stender, Bjoern Kolbeck,
 *               Zuse Institute Berlin
 *
 * Licensed under the BSD License, see LICENSE file for details.
 *
 */

package de.mxro.thrd.xstreemfs.foundation;

/**
 * This class is meant to maintain version numbers for different components used
 * in XtreemFS, in order to be able to detect possible incompatibilities between
 * different versions.
 * 
 * When a new version of the protocol, database, etc. has been implemented, the
 * corresponding version number should be replaced. XtreemFS will rely on this
 * class to find out what the current version numbers are.
 * 
 */
public class VersionManagement {
    
    public static final String RELEASE_VERSION   = "1.3.1 (Tasty Tartlet)";
    
    private static final long  mrcDataVersion    = 10;
    
    private static final long  osdDataVersion    = 1;
    
    private static final long  foundationVersion = 2;
    
    public static long getMrcDataVersion() {
        return mrcDataVersion;
    }
    
    public static long getOsdDataVersion() {
        return osdDataVersion;
    }
    
    public static long getFoundationVersion() {
        return foundationVersion;
    }
    
}
