/*
 * Copyright (c) 2008-2011 by Christian Lorenz, Bjoern Kolbeck,
 *               Zuse Institute Berlin
 *
 * Licensed under the BSD License, see LICENSE file for details.
 *
 */

package de.mxro.thrd.xstreemfs.foundation.pbrpc.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;

import de.mxro.thrd.xstreemfs.foundation.SSLOptions;
import de.mxro.thrd.xstreemfs.foundation.buffer.BufferPool;
import de.mxro.thrd.xstreemfs.foundation.buffer.ReusableBuffer;
import de.mxro.thrd.xstreemfs.foundation.logging.Logging;
import de.mxro.thrd.xstreemfs.foundation.logging.Logging.Category;
import de.mxro.thrd.xstreemfs.foundation.util.OutputUtils;

/**
 * A secure abstraction of the SocketChannel (by using SSL)
 * 
 * @author clorenz
 */
public class SSLChannelIO extends ChannelIO {
    
    /**
     * Number of Threads in the ThreadPool which will be used for the
     * time-consuming tasks
     */
    // public static int EXECUTOR_THREADS = 4;
    /**
     * used SSLEngine for this channel
     */
    protected final SSLEngine sslEngine;
    
    /**
     * contains the read data encrypted by ssl
     */
    protected ReusableBuffer  inNetBuffer, inReadBuffer;
    
    /**
     * contains the written data encrypted by ssl
     */
    protected ReusableBuffer  outNetBuffer;
    
    /**
     * an empty buffer for e.g. handshaking and shutdown; it will never contain
     * data
     */
    protected ReusableBuffer  dummyBuffer;
    
    /**
     * the last SSLEngine-status
     */
    protected HandshakeStatus handshakeStatus;
    
    protected boolean         handshakeComplete;
    
    protected int             keyOpsBeforeHandshake                  = -1;
    
    /**
     * true, if shutdown was called at least one time
     */
    protected boolean         shutdownInProgress;
    
    /**
     * cipher suites without symmetric encryption, wich are supported by the
     * SSLEngine in Java6
     */
    protected static String[] supportedCipherSuitesWithoutEncryption = null;
    
    /**
     * SSL-Channel is used by client
     */
    protected boolean         clientMode;
    
    /**
     * for asynchronious execution of time-consuming tasks only one executor for
     * ALL SSLChannelIOs
     */
    // private static ExecutorService executor = null;
    private boolean           closed                                 = false;
    
    private boolean           shutdownComplete                       = false;
    
    /**
     * creates a SSLChannelIO
     * 
     * @param channel
     *            channel, which should be protected by SSL
     * @param sslOptions
     *            the Options for the SSL-Connection
     * @param clientMode
     *            true, if you are a client; false, if you are a server
     * @throws SSLException
     */
    public SSLChannelIO(SocketChannel channel, SSLOptions sslOptions, boolean clientMode) throws SSLException {
        super(channel);
        // initialize SSLEngine for a server
        sslEngine = sslOptions.getSSLContext().createSSLEngine();
        sslEngine.setUseClientMode(clientMode);
        sslEngine.setNeedClientAuth(true);
        
        if (clientMode) {
            // the first call for a client is wrap()
            sslEngine.beginHandshake();
            handshakeStatus = HandshakeStatus.NEED_WRAP;
        } else {
            // the first call for a server is unwrap()
            sslEngine.beginHandshake();
            handshakeStatus = HandshakeStatus.NEED_UNWRAP;
        }
        
        handshakeComplete = false;
        shutdownInProgress = false;
        
        int netBufSize = sslEngine.getSession().getPacketBufferSize();
        inNetBuffer = BufferPool.allocate(netBufSize);
        inReadBuffer = BufferPool.allocate(sslEngine.getSession().getApplicationBufferSize() * 2);
        outNetBuffer = BufferPool.allocate(netBufSize);
        dummyBuffer = BufferPool.allocate(netBufSize);
        
        sslEngine.setEnabledProtocols(sslEngine.getSupportedProtocols());
        if (sslOptions.isAuthenticationWithoutEncryption()) { // only
            // authentication
            // without
            // protecting
            // data?
            // enable only cipher suites without encryption
            
            if (supportedCipherSuitesWithoutEncryption == null) { // runs only
                // first time
                // a
                // SSLChannelIO
                // without Encryption is created
                // find all supported cipher suites without symmetric encryption
                ArrayList<String> cipherSuites = new ArrayList<String>();
                for (String cipherSuite : sslEngine.getSupportedCipherSuites()) {
                    if (cipherSuite.contains("WITH_NULL")) {
                        cipherSuites.add(cipherSuite);
                    }
                }
                supportedCipherSuitesWithoutEncryption = new String[cipherSuites.size()];
                supportedCipherSuitesWithoutEncryption = cipherSuites
                        .toArray(supportedCipherSuitesWithoutEncryption);
            }
            sslEngine.setEnabledCipherSuites(supportedCipherSuitesWithoutEncryption);
        } else // enable all supported cipher suites
        {
            sslEngine.setEnabledCipherSuites(sslEngine.getSupportedCipherSuites());
        }
        
        // only initialize the first time an SSLChannelIO is created
        /*
         * if(executor==null) executor =
         * Executors.newFixedThreadPool(EXECUTOR_THREADS);
         */
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int read(ByteBuffer dst) throws IOException {
        int returnValue = 0;
        if (!shutdownInProgress) {
            if (handshakeComplete) {
                if (inReadBuffer.remaining() == inReadBuffer.capacity()) {
                    if (channel.read(inNetBuffer.getBuffer()) == -1) {
                        return -1;
                    }
                    inNetBuffer.flip(); // ready for being read
                    inDataAvail: while (inNetBuffer.hasRemaining()) {
                        SSLEngineResult result = sslEngine.unwrap(inNetBuffer.getBuffer(), inReadBuffer
                                .getBuffer());
                        
                        switch (result.getStatus()) {
                        case OK: {
                            // returnValue += result.bytesProduced();
                            // FIXME: if client does't close the connection
                            // after receiving close_notify =>
                            // decomment it
                            if (sslEngine.isInboundDone()) // received
                            // close_notify
                            {
                                close();
                            }
                            break;
                        }
                        case BUFFER_UNDERFLOW: {
                            // needed more data in inNetBuffer, maybe nexttime
                            // inNetBuffer.compact();
                            break inDataAvail;
                            // return returnValue;
                        }
                        case BUFFER_OVERFLOW: {
                            // needed more space in dst
                            throw new IOException(
                                "BufferOverflow in the SSLEngine: Destination-Buffer is too small.");
                        }
                        case CLOSED: {
                            throw new IOException("The SSLEngine is already closed.");
                        }
                        default: {
                            throw new IOException("The SSLEngine is in an undefined state.");
                        }
                        }
                    }
                    inNetBuffer.compact(); // ready for reading from channel
                }
                inReadBuffer.flip();
                if (dst.remaining() >= inReadBuffer.remaining()) {
                    returnValue += inReadBuffer.remaining();
                    dst.put(inReadBuffer.getBuffer());
                } else {
                    while (inReadBuffer.hasRemaining() && dst.hasRemaining()) {
                        dst.put(inReadBuffer.get());
                        returnValue++;
                    }
                }
                inReadBuffer.compact();
            }
        }
        return returnValue;
    }
    
    /**
     * {@inheritDoc} warning: maybe more bytes would be consumed from src-buffer
     * than will be written to channel (returned value)
     */
    @Override
    public int write(ByteBuffer src) throws IOException {
        int returnValue = 0;
        if (!shutdownInProgress) {
            if (handshakeComplete) {
                SSLEngineResult result = sslEngine.wrap(src, outNetBuffer.getBuffer());
                outNetBuffer.flip(); // ready for writing to channel
                
                switch (result.getStatus()) {
                case OK: {
                    tryFlush();
                    
                    break;
                }
                case BUFFER_OVERFLOW: {
                    // needed more space in outNetBuffer
                    // two reasons for overflow:
                    // 1. buffer is too small
                    // 2. buffer is nearly full
                    tryFlush();
                    /*
                     * throw new IOException(
                     * "BufferOverflow in SSLEngine. Buffer for SSLEngine-generated data is too small."
                     * );
                     */
                    break;
                }
                case CLOSED: {
                    throw new IOException("The SSLEngine is already closed.");
                }
                default: {
                    throw new IOException("The SSLEngine is in a curiuos state.");
                }
                }
                returnValue = result.bytesConsumed();
            }
        }
        return returnValue;
    }
    
    /**
     * {@inheritDoc}
     */
    /**
     * {@inheritDoc} warning: maybe more bytes would be consumed from src-buffer
     * than will be written to channel (returned value)
     */
    @Override
    public long write(ByteBuffer[] src) throws IOException {
        int returnValue = 0;
        if (!shutdownInProgress) {
            if (handshakeComplete) {
                SSLEngineResult result = sslEngine.wrap(src, outNetBuffer.getBuffer());
                outNetBuffer.flip(); // ready for writing to channel
                
                switch (result.getStatus()) {
                case OK: {
                    tryFlush();
                    
                    break;
                }
                case BUFFER_OVERFLOW: {
                    // needed more space in outNetBuffer
                    // two reasons for overflow:
                    // 1. buffer is too small
                    // 2. buffer is nearly full
                    tryFlush();
                    /*
                     * throw new IOException(
                     * "BufferOverflow in SSLEngine. Buffer for SSLEngine-generated data is too small."
                     * );
                     */
                    break;
                }
                case CLOSED: {
                    throw new IOException("The SSLEngine is already closed.");
                }
                default: {
                    throw new IOException("The SSLEngine is in a curiuos state.");
                }
                }
                returnValue = result.bytesConsumed();
            }
        }
        return returnValue;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isShutdownInProgress() {
        return this.shutdownInProgress;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shutdown(SelectionKey key) throws IOException {
        if (!handshakeComplete) { // no SSL connection is established => simple
            // close
            shutdownInProgress = true;
            return true;
        }
        
        if (shutdownComplete) {
            return shutdownComplete;
        }
        
        if (!shutdownInProgress) { // initiate shutdown
        
            if (Logging.isDebug())
                Logging.logMessage(Logging.LEVEL_DEBUG, Category.net, this,
                    "shutdown SSL connection of %s:%d", channel.socket().getInetAddress().toString(), channel
                            .socket().getPort());
            
            sslEngine.closeOutbound();
            shutdownInProgress = true;
            key.interestOps(key.interestOps() & ~SelectionKey.OP_READ); // don't
            // wait
            // for
            // the
            // close_notify-reply
        }
        
        outNetBuffer.flip(); // ready for writing to channel
        if (tryFlush() && sslEngine.isOutboundDone()) { // shutdown complete
            key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
            shutdownComplete = true;
        }
        
        if (!sslEngine.isOutboundDone()) {
            // Get close message
            SSLEngineResult result = sslEngine.wrap(dummyBuffer.getBuffer(), outNetBuffer.getBuffer());
            outNetBuffer.flip(); // ready for writing to channel
            switch (result.getStatus()) {
            case OK: {
                throw new IOException("This should not happen.");
            }
            case BUFFER_OVERFLOW: {
                // needed more space in outNetBuffer
                // two reasons for overflow:
                // 1. buffer is too small
                // 2. buffer is nearly full
                tryFlush();
                key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
                /*
                 * throw new IOException(
                 * "BufferOverflow in SSLEngine. Buffer for SSLEngine-generated data is too small."
                 * );
                 */
                break;
            }
            case CLOSED: {
                if (tryFlush() && sslEngine.isOutboundDone()) {
                    key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
                    shutdownComplete = true;
                } else {
                    key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
                }
                break;
            }
            default: {
                throw new IOException("The SSLEngine is in a curiuos state.");
            }
            }
        }
        return shutdownComplete;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        try {
            super.close();
            try {
                sslEngine.closeInbound();
                sslEngine.closeOutbound();
            } catch (SSLException e) {
                // ignore it
            }
            // free buffers
            BufferPool.free(inNetBuffer);
            inNetBuffer = null;
            BufferPool.free(inReadBuffer);
            inReadBuffer = null;
            BufferPool.free(outNetBuffer);
            BufferPool.free(dummyBuffer);
            shutdownInProgress = true;
            closed = true;
        } catch (Throwable th) {
            System.out.println("CANNOT CLOSE DUE TO: " + th);
            throw new IOException(th);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void finalize() {
        if (inNetBuffer != null) {
            Logging.logMessage(Logging.LEVEL_ERROR, Category.misc, this, "buffers not freed!");
            BufferPool.free(inNetBuffer);
            inNetBuffer = null;
            BufferPool.free(outNetBuffer);
            BufferPool.free(dummyBuffer);
        }
        if (!closed) {
            System.out.println("CONNECTION WAS NOT CLOSED PROPERLY: " + this);
        }
    }
    
    /**
     * Writes the outNetBuffer-data to the channel. After write, the buffer is
     * empty or ready for add new data
     * 
     * @return true, if write was successful; false, if buffer is not empty
     * @throws IOException
     */
    protected boolean tryFlush() throws IOException {
        // if (outNetBuffer.hasRemaining()) { // flush the buffer
        channel.write(outNetBuffer.getBuffer());
        if (outNetBuffer.hasRemaining()) {
            outNetBuffer.compact();
            return false;
        } else {
            outNetBuffer.compact();
        }
        // }
        return true;
    }
    
    /**
     * {@inheritDoc} warning: the function manipulates the SelectionKey Ops, so
     * don't do anything in your programm beetween first call of this function
     * until the function returns true
     */
    @Override
    public boolean doHandshake(SelectionKey key) throws IOException {
        if (handshakeComplete || shutdownInProgress) { // quick return
            return handshakeComplete;
        }
        
        if (keyOpsBeforeHandshake == -1) {
            keyOpsBeforeHandshake = key.interestOps();
            key.interestOps(key.interestOps() & ~SelectionKey.OP_READ & ~SelectionKey.OP_WRITE);
        }
        
        if (!handshakeComplete) {
            // Logging.logMessage(Logging.LEVEL_DEBUG, this,
            // "SSL-handshake next step: "+handshakeStatus);
            
            SSLEngineResult result;
            switch (handshakeStatus) {
            case NEED_UNWRAP: {
                key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
                if (channel.read(inNetBuffer.getBuffer()) == -1) {
                    throw new IOException("End of stream has reached.");
                }
                
                boolean underflow = false;
                do { // read all read data in buffer
                    // Logging.logMessage(Logging.LEVEL_DEBUG, this,
                    // "SSL-handshake doing: unwrap");
                    inNetBuffer.flip(); // ready for being read
                    result = sslEngine.unwrap(inNetBuffer.getBuffer(), dummyBuffer.getBuffer());
                    inNetBuffer.compact(); // ready for reading from channel
                    
                    handshakeStatus = result.getHandshakeStatus();
                    switch (result.getStatus()) {
                    case OK: {
                        analyseHandshakeStatus(key, handshakeStatus);
                        break;
                    }
                    case BUFFER_UNDERFLOW: {
                        // needed more data in inNetBuffer, maybe nexttime
                        underflow = true;
                        key.interestOps(key.interestOps() | SelectionKey.OP_READ);
                        break;
                    }
                    case CLOSED: {
                        throw new IOException("The SSLEngine is already closed.");
                    }
                    default: {
                        throw new IOException("The SSLEngine is in a curiuos state.");
                    }
                    }
                } while (bufferRemaining(inNetBuffer) != 0 && handshakeStatus == HandshakeStatus.NEED_UNWRAP
                    && !underflow);
                break;
            }
            case NEED_WRAP: {
                key.interestOps(key.interestOps() & ~SelectionKey.OP_READ);
                // Logging.logMessage(Logging.LEVEL_DEBUG, this,
                // "SSL-handshake doing: wrap");
                
                result = sslEngine.wrap(dummyBuffer.getBuffer(), outNetBuffer.getBuffer());
                outNetBuffer.flip(); // ready for writing to channel
                
                handshakeStatus = result.getHandshakeStatus();
                switch (result.getStatus()) {
                case OK: {
                    tryFlush();
                    
                    analyseHandshakeStatus(key, handshakeStatus);
                    break;
                }
                case BUFFER_OVERFLOW: {
                    // needed more space in outNetBuffer
                    // two reasons for overflow:
                    // 1. buffer is too small
                    // 2. buffer is nearly full
                    tryFlush();
                    key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
                    /*
                     * throw new IOException(
                     * "BufferOverflow in SSLEngine. Buffer for SSLEngine-generated data is too small."
                     * );
                     */
                    break;
                }
                case CLOSED: {
                    throw new IOException("The SSLEngine is already closed.");
                }
                default: {
                    throw new IOException("The SSLEngine is in a curiuos state.");
                }
                }
                break;
            }
            case FINISHED: {
                outNetBuffer.flip(); // ready for writing to channel
                if (tryFlush()) {
                    handshakeFinished(key);
                } else {
                    key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
                }
                break;
            }
            case NEED_TASK: {
                key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
                doTasks(key);
                break;
            }
            case NOT_HANDSHAKING: {
                // TODO: Exception or maybe handshakeComplete = true?
                throw new IOException("The SSLEngine is not handshaking.");
            }
            default: {
                throw new IOException("The SSLEngine is in a curiuos handshake-state.");
            }
            }
        }
        return handshakeComplete;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFlushed() {
        return bufferRemaining(outNetBuffer) == 0;
    }
    
    /**
     * finishing operations for handshake
     * 
     * @param key
     */
    protected void handshakeFinished(SelectionKey key) {
        
        if (Logging.isDebug())
            Logging.logMessage(Logging.LEVEL_DEBUG, Category.net, this, "SSL-handshake for %s:%d finished",
                channel.socket().getInetAddress().toString(), channel.socket().getPort());
        
        // all handshake-data processed and sent
        handshakeComplete = true;
        inNetBuffer.clear();
        outNetBuffer.clear();
        key.interestOps(keyOpsBeforeHandshake);
        try {
            this.certs = sslEngine.getSession().getPeerCertificates();
        } catch (SSLPeerUnverifiedException ex) {
            Logging.logMessage(Logging.LEVEL_ERROR, Category.auth, this, OutputUtils.stackTraceToString(ex));
            this.certs = null;
        }
        
    }
    
    /**
     * 
     * @param key
     * @param handshakeStatus
     * @throws IOException
     */
    protected void analyseHandshakeStatus(SelectionKey key, HandshakeStatus handshakeStatus) throws IOException {
        switch (handshakeStatus) {
        case NEED_UNWRAP: {
            key.interestOps(key.interestOps() | SelectionKey.OP_READ & ~SelectionKey.OP_WRITE);
            break;
        }
        case NEED_WRAP: {
            key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
            break;
        }
        case NEED_TASK: {
            key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
            doTasks(key);
            break;
        }
        case FINISHED: {
            outNetBuffer.flip(); // ready for writing to channel
            if (tryFlush()) {
                handshakeFinished(key);
            } else {
                key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
            }
            break;
        }
        case NOT_HANDSHAKING: {
            // TODO: Exception or maybe handshakeComplete = true?
            throw new IOException("The SSLEngine is not handshaking.");
        }
        default: {
            throw new IOException("The SSLEngine is in a curiuos handshake-state.");
        }
        }
    }
    
    /**
     * checks the remaining data of the buffer (only for internal buffers)
     * 
     * @param buffer
     * @return
     */
    private int bufferRemaining(ReusableBuffer buffer) {
        buffer.flip(); // ready for being read
        int tmp = buffer.remaining();
        buffer.compact(); // ready for being read
        return tmp;
    }
    
    /**
     * runs the time-consuming tasks
     * 
     * @param key
     * @throws IOException
     */
    protected void doTasks(final SelectionKey key) {
        // Logging.logMessage(Logging.LEVEL_DEBUG, this,
        // "SSL-handshake doing: doing task");
        
        final int tmp = key.interestOps();
        // clear all interests, so no one other than this thread can modify the
        // selector
        key.interestOps(0);
        
        /*
         * executor.execute(new Runnable(){ public void run() {
         */
        // TODO: running in a different thread
        Runnable run;
        while ((run = sslEngine.getDelegatedTask()) != null) {
            run.run();
        }
        
        switch (handshakeStatus = sslEngine.getHandshakeStatus()) {
        case NEED_WRAP: {
            key.interestOps(tmp | SelectionKey.OP_WRITE);
            break;
        }
        case NEED_UNWRAP: {
            // need to read from channel
            key.interestOps(tmp | SelectionKey.OP_READ);
            break;
        }
        case FINISHED: {
            // should not happen
            handshakeFinished(key);
            break;
        }
        case NEED_TASK: {
            // should not happen
            doTasks(key);
            break;
        }
        case NOT_HANDSHAKING: {
            // should not happen
            Logging.logMessage(Logging.LEVEL_ERROR, Category.auth, this,
                "Exception in worker-thread: The SSLEngine is not handshaking.");
            break;
        }
        default: {
            Logging.logMessage(Logging.LEVEL_ERROR, Category.auth, this,
                "Exception in worker-thread: The SSLEngine is in a curiuos handshake-state.");
            assert (false);
            // throw new
            // IOException("The SSLEngine is in a curiuos handshake-state.");
        }
        }
        
        /*
         * key.selector().wakeup(); } });
         */
    }
}
