package com.franger.socket.socketio;

import android.content.Context;
import android.util.Log;

import com.franger.socket.SocketHelper;
import com.franger.socket.SocketIOCallbacks;
import com.franger.socket.SocketOptions;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Vignesh Ramachandra.
 * Wrapper for Socket.io for handling all socket related operations.
 * Makes use of the SocketIOCallbacks interface to deliver callbacks.
 */
public class SocketIOManager {

    private static SocketIOManager socketIOManager;
    private String url;

    private SocketIOCallbacks socketIOCallbacks;

    private String DEBUG_TAG = "SocketIOManager";

    private Context context;

    private Socket mSocket;

    //Thread safe hashmap, so concurrrency issues could be avoided
    private ConcurrentHashMap<String, Socket> mSocketMap = new ConcurrentHashMap<>();

    /**
     * private Constructor
     *
     * @param context
     */
    private SocketIOManager(Context context, String url) {
        this.context = context;
        this.url = url;
        this.mSocket = getSocket(url, null);
    }

    /**
     * Provides a singleton SocketIOManager instance
     *
     * @param context
     * @return SocketIOManager
     */
    public static SocketIOManager getInstance(Context context, String url) {
        if (socketIOManager == null) {
            socketIOManager = new SocketIOManager(context, url);
        }
        return socketIOManager;
    }

    /**
     * Sets the callback interface
     *
     * @param socketIOCallbacks
     */
    public void setCallBacks(SocketIOCallbacks socketIOCallbacks) {
        this.socketIOCallbacks = socketIOCallbacks;
    }

    /*
     * Will create a new socket with the default options
     */
    public String createASocket(final String url, final List<String> eventsToBeListened) {
        return createASocket(url, eventsToBeListened, null);
    }

    public Socket getSocket(final String url, SocketOptions opts) {
        if (url == null) {
            throw new NullPointerException("URL is null");
        }

        // Also available is options param to create a socket
        if (opts == null) {
            //Setting default options
            opts = new SocketOptions();
            opts.setForceNew(true);
            opts.setReconnection(true);
            opts.setReconnectionAttempts(5);
        }

        try {
            mSocket = IO.socket(url, opts);
            // Put the created socket into the hashmap
            mSocketMap.put(url, mSocket);
            Log.d(DEBUG_TAG, "Socket created with the given TAG : " + url);
            if (socketIOCallbacks != null)
                socketIOCallbacks.onSocketCreated(url);
        } catch (URISyntaxException e) {
            /**
             * No need to check if the key is contained, the ConcurrentHashMap.remove() method
             * does nothing if the key is not found
             */
            mSocketMap.remove(url);
            Log.d(DEBUG_TAG, "Error creating socket with the given URI : " + url);
            socketIOCallbacks.onError(url, SocketHelper.MALFORMED_URI);
        }
        return mSocket;
    }

    /**
     * Creates a new socket and returns its tag with which it can be accessed later
     *
     * @param url                the channel through which the socket must be opened
     * @param eventsToBeListened the event to which the socket must listen to
     */

    public String createASocket(final String url, final List<String> eventsToBeListened, SocketOptions opts) {

        if (url == null) {
            throw new NullPointerException("URL is null");
        } else if (eventsToBeListened == null) {
            throw new NullPointerException("eventsToBeListened is null");
        } else if (socketIOCallbacks == null) {
            throw new NullPointerException("Please set socketIOCallbacks ( i.e socketManger.setCallBacks()) and then try creating a socket");
        }


        socketIOCallbacks.onConnecting(url);

        // Also available is options param to create a socket
        if (opts == null) {
            //Setting default options
            opts = new SocketOptions();
            opts.setForceNew(true);
            opts.setReconnection(true);
            opts.setReconnectionAttempts(5);
        }

        try {
            mSocket = IO.socket(url, opts);
        } catch (URISyntaxException e) {
            /**
             * No need to check if the key is contained, the ConcurrentHashMap.remove() method
             * does nothing if the key is not found
             */
            mSocketMap.remove(url);
            Log.d(DEBUG_TAG, "Error creating socket with the given URI : " + url);
            socketIOCallbacks.onError(url, SocketHelper.MALFORMED_URI);
        }

        // Put the created socket into the hashmap
        mSocketMap.put(url, mSocket);
        Log.d(DEBUG_TAG, "Socket created with the given TAG : " + url);
        socketIOCallbacks.onSocketCreated(url);

        // Register for required event
        for (final String eventToBeListened : eventsToBeListened) {
            mSocket.on(eventToBeListened, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    socketIOCallbacks.on(url, eventToBeListened, args);
                }
            });
        }

        //Finally establish connection
        try {
            mSocket.connect();
        } catch (Exception e) {
            e.printStackTrace();
            socketIOCallbacks.onError(url, SocketHelper.UNKNOWN_ERROR_WHILE_CONNECTING);
        }
        Log.d(DEBUG_TAG, "is connected : " + mSocket.connected());
        return url;
    }

    public void addEventsToBeListened(final List<String> eventsToBeListened, final SocketIOCallbacks callbacks) {

        if (eventsToBeListened == null) {
            throw new NullPointerException("eventsToBeListened is null");
        } else if (socketIOCallbacks == null) {
            throw new NullPointerException("socketIOCallbacks is null");
        }

        //Finally establish connection
        try {
            mSocket.connect();
        } catch (Exception e) {
            e.printStackTrace();
            callbacks.onError(url, SocketHelper.UNKNOWN_ERROR_WHILE_CONNECTING);
        }
        // Register for required event
        for (final String eventToBeListened : eventsToBeListened) {
            mSocket.on(eventToBeListened, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    callbacks.on(url, eventToBeListened, args);
                }
            });
        }
    }

    public void emitAndListenEvents(String tag, String event, final List<String> eventsToBeListened, final SocketIOCallbacks callbacks, Object... message) {
        if (tag == null) {
            throw new NullPointerException("Trying to close a socket with null tag");
        }

        Socket mSocket = mSocketMap.get(tag);
        if (mSocket != null) {
            mSocket.connect();
            mSocket.emit(event, message);
            for (final String eventToBeListened : eventsToBeListened) {
                mSocket.on(eventToBeListened, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        callbacks.on(url, eventToBeListened, args);
                    }
                });
            }
        } else {
            Log.d(DEBUG_TAG, "Cannot emit. Unable to retrieve socket");
//            if (callbacks != null)
                callbacks.onError(tag, SocketHelper.SOCKET_NOT_FOUND);
        }
    }

    /**
     * Emits an event through the socket channel
     *
     * @param tag
     * @param event
     * @param message
     */
    public void emitEvent(String tag, String event, Object... message) {
        if (tag == null) {
            throw new NullPointerException("Trying to close a socket with null tag");
        }

        Socket mSocket = mSocketMap.get(tag);
        if (mSocket != null) {
            mSocket.connect();
            mSocket.emit(event, message);
        } else {
            Log.d(DEBUG_TAG, "Cannot emit. Unable to retrieve socket");
            socketIOCallbacks.onError(tag, SocketHelper.SOCKET_NOT_FOUND);
        }
    }

    /**
     * Closes/Disconnects the socket with the given TAG
     *
     * @param TAG identifier for the socket
     */
    public void closeASocket(String tag) {

        if (tag == null) {
            throw new NullPointerException("Trying to close a socket with null tag");
        }

        if (!mSocketMap.containsKey(tag)) {
            socketIOCallbacks.onError(tag, SocketHelper.SOCKET_NOT_FOUND);
            Log.d(DEBUG_TAG, "Trying to close a socket that doesn't exist. TAG : " + tag);
        } else {
            Socket socketToBeClosed = mSocketMap.get(tag);
            socketIOCallbacks.onDisconnecting(tag);
            socketToBeClosed.close();
            mSocketMap.remove(tag);
            Log.d(DEBUG_TAG, "Socket with TAG : " + tag + "has successfully been closed/destroyed");
            socketIOCallbacks.onSocketDestroyed(tag);
        }
    }

    /**
     * Pauses the socket from listening to events
     *
     * @param TAG identifier for the socket
     */
    public void stopListening(String tag) {

        if (tag == null) {
            throw new NullPointerException("Trying to stop listening to a socket with null tag");
        }

        if (!mSocketMap.containsKey(tag)) {
            socketIOCallbacks.onError(tag, SocketHelper.SOCKET_NOT_FOUND);
        } else {
            Socket socket = mSocketMap.get(tag);
            socket.off();
            Log.d(DEBUG_TAG, "Socket with TAG : " + tag + "has stopped listening for events");
        }
    }

    /**
     * Resumes/starts to listen events
     *
     * @param TAG   identifier for the socket
     * @param event the event to which the socket must listen to
     */
    public void startListening(final String tag, final String event) {

        if (tag == null) {
            throw new NullPointerException("Trying to listen a socket with null tag");
        } else if (event == null) {
            throw new NullPointerException("event is null");
        }

        if (!mSocketMap.containsKey(tag)) {
            socketIOCallbacks.onError(tag, SocketHelper.SOCKET_NOT_FOUND);
            Log.d(DEBUG_TAG, "Trying to close a socket which is not found. TAG : " + tag);
        } else {
            Socket socket = mSocketMap.get(tag);
            socket.on(event, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    socketIOCallbacks.on(tag, event, args);
                }
            });
            if (!socket.connected()) {
                socket.connect();
            }
            Log.d(DEBUG_TAG, "Socket with TAG : " + tag + "has started listening for events");
        }
    }

    /**
     * Returns whether a socket is in connected state
     *
     * @param TAG identifier for the socket
     * @return if socket is connected
     */
    public boolean isConnected(String tag) {
        if (mSocketMap.containsKey(tag)) {
            return mSocketMap.get(tag).connected();
        }
        Log.d(DEBUG_TAG, "Trying to close a socket which is not found. TAG : " + tag);
        socketIOCallbacks.onError(tag, SocketHelper.SOCKET_NOT_FOUND);
        return false;
    }

    /**
     * Closes all socket connections
     * Can be used on the onDestroy method of the application class when there isn't a need to
     * keep any socket connection open in a service/background thread.
     */
    public void clearAllSockets() {
        for (Socket socket : mSocketMap.values()) {
            socket.close();
            mSocketMap.remove(mSocketMap.get(socket));
        }
        mSocketMap.clear();
        Log.d(DEBUG_TAG, "All sockets (existing or newly created) are closed/destroyed");
    }
}
