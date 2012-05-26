/*
 * Copyright (c) 2008-2011 by Christian Lorenz, Bjoern Kolbeck,
 *               Zuse Institute Berlin
 *
 * Licensed under the BSD License, see LICENSE file for details.
 *
 */

package de.mxro.thrd.xstreemfs.foundation.pbrpc.channels;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.security.cert.Certificate;

/**
 * A abstraction of the SocketChannel
 *
 * @author clorenz
 */
public class ChannelIO {

    protected final SocketChannel channel;

    protected Certificate[] certs;
    
    protected Object        attachment;

    public ChannelIO(SocketChannel channel) {
        this.channel = channel;
        this.certs = null;
        attachment = null;
    }

    public SelectableChannel configureBlocking(boolean block)
                    throws IOException {
            return channel.configureBlocking(block);
    }

    public boolean connect(SocketAddress remote) throws IOException {
            return this.channel.connect(remote);
    }

    public void close() throws IOException {
            channel.socket().close();
            channel.close();
    }

    public boolean isBlocking() {
            return channel.isBlocking();
    }

    public boolean isOpen() {
            return channel.isOpen();
    }

    public SelectionKey keyFor(Selector sel) {
            return channel.keyFor(sel);
    }

    public int read(ByteBuffer dst) throws IOException {
            return channel.read(dst);
    }

    public SelectionKey register(Selector sel, int ops, Object att)
                    throws ClosedChannelException {
            return channel.register(sel, ops, att);
    }

    public Socket socket() {
            return channel.socket();
    }

    public String toString() {
            return channel.toString();
    }

    public int validOps() {
            return channel.validOps();
    }

    public int write(ByteBuffer src) throws IOException {
            return channel.write(src);
    }

    public long write(ByteBuffer[] src) throws IOException {
            return channel.write(src);
    }

    public boolean finishConnect() throws IOException {
            return this.channel.finishConnect();
    }

    public boolean isConnectionPending() {
            return this.channel.isConnectionPending();
    }

    /**
     * does the handshake if needed
     * @param key
     * @return true, if handshake is completed
     * @throws IOException
     */
    public boolean doHandshake(SelectionKey key) throws IOException {
            return true;
    }

    /**
     * prepares the channel for closing
     * this can take more than 1 call
     * @param key
     * @return true, if channel is ready for closing
     * @throws IOException
     */
    public boolean shutdown(SelectionKey key) throws IOException {
        return true;
    }

    /**
     * is channel in closing-procedure?
     * @return
     */
    public boolean isShutdownInProgress() {
        return false;
    }

    /**
     * is there remaining data in channel-buffers, which must be flushed?
     * @return
     */
    public boolean isFlushed() {
        return true;
    }

    public Certificate[] getCerts() {
        return certs;
    }

    public Object getAttachment() {
        return attachment;
    }

    public void setAttachment(Object attachment) {
        this.attachment = attachment;
    }
}
