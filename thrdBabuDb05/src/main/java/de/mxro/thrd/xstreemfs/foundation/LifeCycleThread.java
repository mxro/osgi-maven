/*
 * Copyright (c) 2008-2010 by Jan Stender,
 *               Zuse Institute Berlin
 *
 * Licensed under the BSD License, see LICENSE file for details.
 *
 */

package de.mxro.thrd.xstreemfs.foundation;

import de.mxro.thrd.xstreemfs.foundation.logging.Logging;
import de.mxro.thrd.xstreemfs.foundation.logging.Logging.Category;

/**
 * A base class for threads representing a life cycle. It offers methods for
 * blocking other threads until a certain life cycle event has occured. It
 * currently supports two life cycle-related events: startup and shutdown.
 * 
 * @author stender
 * 
 */
public class LifeCycleThread extends Thread {
    
    private final Object      startLock;
    
    private final Object      stopLock;
    
    private boolean           started;
    
    private boolean           stopped;
    
    private Exception         exc;
    
    private LifeCycleListener listener;
    
    public LifeCycleThread(String name) {
        super(name);
        startLock = new Object();
        stopLock = new Object();
    }
    
    /**
     * This method should be invoked by subclasses when the startup procedure
     * has been completed.
     */
    protected void notifyStarted() {
        
        if (Logging.isInfo())
            Logging.logMessage(Logging.LEVEL_INFO, Category.lifecycle, this, "Thread %s started", Thread
                    .currentThread().getName());
        
        synchronized (startLock) {
            started = true;
            startLock.notifyAll();
            if (listener != null)
                listener.startupPerformed();
        }
    }
    
    /**
     * This method should be invoked by subclasses when the shutdown procedure
     * has been completed.
     */
    protected void notifyStopped() {
        
        if (Logging.isInfo())
            Logging.logMessage(Logging.LEVEL_INFO, Category.lifecycle, this, "Thread %s terminated", Thread
                    .currentThread().getName());
        
        synchronized (stopLock) {
            stopped = true;
            stopLock.notifyAll();
            if (listener != null)
                listener.shutdownPerformed();
        }
    }
    
    /**
     * This method should be invoked by subclasses when the thread has crashed.
     */
    protected void notifyCrashed(Exception exc) {
        
        Logging.logMessage(Logging.LEVEL_CRIT, this, "service ***CRASHED***, shutting down");
        Logging.logError(Logging.LEVEL_CRIT, this, exc);
        
        synchronized (startLock) {
            this.exc = exc;
            started = true;
            startLock.notifyAll();
        }
        
        synchronized (stopLock) {
            this.exc = exc;
            stopped = true;
            stopLock.notifyAll();
        }
        
        if (listener != null)
            listener.crashPerformed(exc);
    }
    
    /**
     * Synchronously waits for a notification indicating that the startup
     * procedure has been completed.
     * 
     * @throws Exception
     *             if an error occured during the startup procedure
     */
    public void waitForStartup() throws Exception {
        synchronized (startLock) {
            
            while (!started)
                startLock.wait();
            
            if (exc != null)
                throw exc;
        }
    }
    
    /**
     * Synchronously waits for a notification indicating that the shutdown
     * procedure has been completed.
     * 
     * @throws Exception
     *             if an error occured during the shutdown procedure
     */
    public void waitForShutdown() throws Exception {
        synchronized (stopLock) {
            
            if (!started)
                return;
            while (!stopped)
                stopLock.wait();
            
            if (exc != null)
                throw exc;
        }
    }
    
    /**
     * Terminates the thread. This method should be overridden in subclasses.
     * @throws Exception if an error occurred
     */
    public void shutdown() throws Exception {
    }
    
    /**
     * Sets a listener waiting for life cycle events.
     * 
     * @param listener
     *            the listener
     */
    public void setLifeCycleListener(LifeCycleListener listener) {
        this.listener = listener;
    }
    
}
