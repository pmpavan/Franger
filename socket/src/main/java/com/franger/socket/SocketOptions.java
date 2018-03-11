package com.franger.socket;

import com.github.nkzawa.socketio.client.IO;

/**
 * Created by Vignesh Ramachandra.
 * <p/>
 * Wrapper to set options to the Socket.io client
 */
public class SocketOptions extends IO.Options {

    /**
     * Creates a Socket options instance with default set of options
     */
    public SocketOptions() {
        // if you don't want to reuse a cached socket instance when the query parameter changes,
        // you should use the forceNew option
        this.setForceNew(true);
        this.setReconnection(true);
        this.setReconnectionAttempts(10);
        this.setReconnectionDelay(1500);
    }

    public void setForceNew(boolean forceNew) {
        super.forceNew = forceNew;
    }

    public void setReconnection(boolean reconnection) {
        super.reconnection = reconnection;
    }

    public void setReconnectionAttempts(int reconnectionAttempts) {
        super.reconnectionAttempts = reconnectionAttempts;
    }

    public void setReconnectionDelay(long reconnectionDelay) {
        super.reconnectionDelay = reconnectionDelay;
    }
}
