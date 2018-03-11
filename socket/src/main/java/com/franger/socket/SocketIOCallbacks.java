package com.franger.socket;

/**
 * Created by Vignesh Ramachandra.
 * All socket operation callbacks are provided by this interface
 * <p/>
 * Each socket will be associated with a TAG. Operations on it can be carried by using this
 * identifer
 */
public interface SocketIOCallbacks {

    /**
     * Initial state after socket.connect() is called. Trying to open a socket connection
     * with the given channel and port number.
     *
     * @param TAG by which the socket is identified
     */
    void onConnecting(String TAG);

    /**
     * Acknowledgement for successful establishment of the channel.
     *
     * @param TAG by which the socket is identified
     */
    void onSocketCreated(String TAG);

    /**
     * Called when a new message is received on the socket connection.
     *
     * @param TAG     by which the socket is identified
     * @param message the received message via the socket channel
     */
    void onMessage(String TAG, String message);

    /**
     * Used for large data transfer operations to track the progress.
     *
     * @param TAG      by which the socket is identified
     * @param progress the amount of data being transferred
     */
    void progressChanged(String TAG, int progress);

    /**
     * Generic event, result method for all events occuring in the socket channel.
     *
     * @param TAG   by which the socket is identified
     * @param event on occurence of event in a socket channel
     * @param args  the args for a particular event
     */
    void on(String TAG, String event, Object... args);

    /**
     * Failures/error cases handled here.
     *
     * @param TAG       by which the socket is identified
     * @param errorCode the error code
     */ 
    void onError(String TAG, SocketHelper errorCode);

    /**
     * Attempt to close the channel associated with the socket.
     *
     * @param TAG
     */
    void onDisconnecting(String TAG);

    /**
     * Callback after socket.close() is successfully executed.
     *
     * @param TAG by which the socket is identified
     */
    void onSocketDestroyed(String TAG);
}
