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
import java.util.ArrayList;
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

    private List<SocketIOCallbacks> socketIOCallbacks;

    private String DEBUG_TAG = "FR_LOGGER";

    private Context context;

    public Socket mSocket;

    //Thread safe hashmap, so concurrrency issues could be avoided
    private ConcurrentHashMap<String, Socket> mSocketMap = new ConcurrentHashMap<>();

    /**
     * private Constructor
     *
     * @param context
     * @param socketIOCallbacks
     */
    private SocketIOManager(Context context, String url, SocketIOCallbacks socketIOCallbacks) {
        this.context = context;
        this.url = url;
//        setCallBacks(socketIOCallbacks);
        this.mSocket = getSocket(url);
    }

    /**
     * Provides a singleton SocketIOManager instance
     *
     * @param context
     * @param url
     * @return SocketIOManager
     */
    public static SocketIOManager getInstance(Context context, String url, SocketIOCallbacks socketIOCallbacks) {
        if (socketIOManager == null) {
            socketIOManager = new SocketIOManager(context, url, socketIOCallbacks);
        }
        return socketIOManager;
    }

    /**
     * Sets the callback interface
     *
     * @param callbacks
     */
    public void setCallBacks(SocketIOCallbacks callbacks) {
        if (this.socketIOCallbacks == null) {
            this.socketIOCallbacks = new ArrayList<>();
        }
        if (!socketIOCallbacks.contains(callbacks))
            this.socketIOCallbacks.add(callbacks);
//        this.socketIOCallbacks = callbacks;
    }

    /*
     * Will create a new socket with the default options
     */
//    public String createASocket(final String url, final List<String> eventsToBeListened) {
//        return createASocket(url, eventsToBeListened, null);
//    }
    private Socket getSocket(final String url) {
        if (url == null) {
            throw new NullPointerException("URL is null");
        }

        //Setting default options
        SocketOptions opts = new SocketOptions();
        opts.setForceNew(true);
        opts.setReconnection(true);
        opts.setReconnectionAttempts(5);

        try {
            mSocket = IO.socket(url, opts);
            // Put the created socket into the hashmap
            mSocketMap.put(url, mSocket);
            Log.d(DEBUG_TAG, "Socket created with the given TAG : " + url);
            if (socketIOCallbacks != null)
                for (SocketIOCallbacks callback : socketIOCallbacks)
                    callback.onSocketCreated(url);
        } catch (URISyntaxException e) {
            /**
             * No need to check if the key is contained, the ConcurrentHashMap.remove() method
             * does nothing if the key is not found
             */
            mSocketMap.remove(url);
            Log.d(DEBUG_TAG, "Error creating socket with the given URI : " + url);
            if (socketIOCallbacks != null)
                for (SocketIOCallbacks callback : socketIOCallbacks)
                    callback.onError(url, SocketHelper.MALFORMED_URI);
        }

        return mSocket;
    }

    /**
     * Creates a new socket and returns its tag with which it can be accessed later
     *
     * @param url                the channel through which the socket must be opened
     * @param eventsToBeListened the event to which the socket must listen to
     */

//    public String createASocket(final String url, final List<String> eventsToBeListened, SocketOptions opts) {
//
//        if (url == null) {
//            throw new NullPointerException("URL is null");
//        } else if (eventsToBeListened == null) {
//            throw new NullPointerException("eventsToBeListened is null");
//        } else if (socketIOCallbacks == null) {
//            throw new NullPointerException("Please set socketIOCallbacks ( i.e socketManger.setCallBacks()) and then try creating a socket");
//        }
//
//        if (socketIOCallbacks != null)
//            for (SocketIOCallbacks callbacks : socketIOCallbacks)
//                callbacks.onConnecting(url);
//
//        // Also available is options param to create a socket
//        if (opts == null) {
//            //Setting default options
//            opts = new SocketOptions();
//            opts.setForceNew(true);
//            opts.setReconnection(true);
//            opts.setReconnectionAttempts(5);
//        }
//
//        try {
//            mSocket = IO.socket(url, opts);
//        } catch (URISyntaxException e) {
//            /**
//             * No need to check if the key is contained, the ConcurrentHashMap.remove() method
//             * does nothing if the key is not found
//             */
//            mSocketMap.remove(url);
//            Log.d(DEBUG_TAG, "Error creating socket with the given URI : " + url);
//            if (socketIOCallbacks != null)
//                for (SocketIOCallbacks callbacks : socketIOCallbacks)
//                    callbacks.onError(url, SocketHelper.MALFORMED_URI);
//        }
//
//        // Put the created socket into the hashmap
//        mSocketMap.put(url, mSocket);
//        Log.d(DEBUG_TAG, "Socket created with the given TAG : " + url);
//        if (socketIOCallbacks != null)
//            for (SocketIOCallbacks callbacks : socketIOCallbacks)
//                callbacks.onSocketCreated(url);
//
//        for (final String eventToBeListened : eventsToBeListened) {
//            mSocket.on(eventToBeListened, new Emitter.Listener() {
//                @Override
//                public void call(Object... args) {
//                    if (socketIOCallbacks != null)
//                        for (SocketIOCallbacks callbacks : socketIOCallbacks)
//                            callbacks.on(url, eventToBeListened, args);
//                }
//            });
//        }
//
//        //Finally establish connection
//        try {
//            mSocket.connect();
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (socketIOCallbacks != null)
//                for (SocketIOCallbacks callbacks : socketIOCallbacks)
//                    callbacks.onError(url, SocketHelper.UNKNOWN_ERROR_WHILE_CONNECTING);
//        }
//        // Register for required event
//        Log.d(DEBUG_TAG, "is connected : " + mSocket.connected());
//        return url;
//    }

    /**
     * @param eventsToBeListened
     * @param callbacks
     */
    public void addEventsToBeListened(final List<String> eventsToBeListened, final SocketIOCallbacks callbacks) {

        if (eventsToBeListened == null) {
            throw new NullPointerException("eventsToBeListened is null");
        } else if (callbacks == null) {
            throw new NullPointerException("socketIOCallbacks is null");
        }

        setCallBacks(callbacks);
        // Register for required event
        for (final String eventToBeListened : eventsToBeListened) {
            mSocket.on(eventToBeListened, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    if (socketIOCallbacks != null)
                        for (SocketIOCallbacks callback : socketIOCallbacks)
                            callback.on(url, eventToBeListened, args);
                }
            });
        }
        //Finally establish connection
        try {
            mSocket.connect();
        } catch (Exception e) {
            e.printStackTrace();
            if (socketIOCallbacks != null)
                for (SocketIOCallbacks callback : socketIOCallbacks)
                    callback.onError(url, SocketHelper.UNKNOWN_ERROR_WHILE_CONNECTING);
        }
    }

    /**
     * @param tag
     * @param event
     * @param eventsToBeListened
     * @param callbacks
     * @param message
     */
    public void emitAndListenEvents(String tag, String event, final List<String> eventsToBeListened, final SocketIOCallbacks callbacks, Object... message) {
        if (tag == null) {
            throw new NullPointerException("Trying to close a socket with null tag");
        }

        setCallBacks(callbacks);
        Socket mSocket = mSocketMap.get(tag);
        if (mSocket != null) {
            mSocket.connect();
            mSocket.emit(event, message);
            for (final String eventToBeListened : eventsToBeListened) {
                Log.d(DEBUG_TAG, eventToBeListened + " listening");
                mSocket.on(eventToBeListened, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Log.d(DEBUG_TAG, eventToBeListened + " " + args);
                        if (socketIOCallbacks != null)
                            for (SocketIOCallbacks callback : socketIOCallbacks)
                                callback.on(url, eventToBeListened, args);
                    }
                });
            }
        } else {
            Log.d(DEBUG_TAG, "Cannot emit. Unable to retrieve socket");
            if (socketIOCallbacks != null)
                for (SocketIOCallbacks callback : socketIOCallbacks)
                    callback.onError(tag, SocketHelper.SOCKET_NOT_FOUND);
        }
    }

    /**
     * Emits an event through the socket channel
     *
     * @param tag
     * @param event
     * @param message
     */
    public void emitEvent(String tag, String event, final SocketIOCallbacks callbacks, Object... message) {
        if (tag == null) {
            throw new NullPointerException("Trying to close a socket with null tag");
        }

        setCallBacks(callbacks);
        Socket mSocket = mSocketMap.get(tag);
        if (mSocket != null) {
            mSocket.connect();
            mSocket.emit(event, message);
        } else {
            Log.d(DEBUG_TAG, "Cannot emit. Unable to retrieve socket");
            if (socketIOCallbacks != null)
                for (SocketIOCallbacks callback : socketIOCallbacks)
                    callback.onError(tag, SocketHelper.SOCKET_NOT_FOUND);
        }
    }

    /**
     * Closes/Disconnects the socket with the given TAG
     *
     * @param TAG identifier for the socket
     */
//    public void closeASocket(String tag) {
//
//        if (tag == null) {
//            throw new NullPointerException("Trying to close a socket with null tag");
//        }
//
//        if (!mSocketMap.containsKey(tag)) {
//            if (socketIOCallbacks != null)
//                for (SocketIOCallbacks callbacks : socketIOCallbacks)
//                    callbacks.onError(tag, SocketHelper.SOCKET_NOT_FOUND);
//            Log.d(DEBUG_TAG, "Trying to close a socket that doesn't exist. TAG : " + tag);
//        } else {
//            Socket socketToBeClosed = mSocketMap.get(tag);
//            if (socketIOCallbacks != null)
//                for (SocketIOCallbacks callbacks : socketIOCallbacks)
//                    callbacks.onDisconnecting(tag);
//            socketToBeClosed.close();
//            mSocketMap.remove(tag);
//            Log.d(DEBUG_TAG, "Socket with TAG : " + tag + "has successfully been closed/destroyed");
//            if (socketIOCallbacks != null)
//                for (SocketIOCallbacks callbacks : socketIOCallbacks)
//                    callbacks.onSocketDestroyed(tag);
//        }
//    }

    /**
     * Pauses the socket from listening to events
     *
     * @param TAG identifier for the socket
     */
//    public void stopListening(String tag) {
//
//        if (tag == null) {
//            throw new NullPointerException("Trying to stop listening to a socket with null tag");
//        }
//
//        if (!mSocketMap.containsKey(tag)) {
//            if (socketIOCallbacks != null)
//                for (SocketIOCallbacks callbacks : socketIOCallbacks)
//                    callbacks.onError(tag, SocketHelper.SOCKET_NOT_FOUND);
//        } else {
//            Socket socket = mSocketMap.get(tag);
//            socket.off();
//            Log.d(DEBUG_TAG, "Socket with TAG : " + tag + "has stopped listening for events");
//        }
//    }

    /**
     * Resumes/starts to listen events
     *
     * @param tag   identifier for the socket
     * @param event the event to which the socket must listen to
     */
    public void startListening(final String tag, final String event, final SocketIOCallbacks callbacks) {

        if (tag == null) {
            throw new NullPointerException("Trying to listen a socket with null tag");
        } else if (event == null) {
            throw new NullPointerException("event is null");
        }

        setCallBacks(callbacks);

        if (!mSocketMap.containsKey(tag)) {
            if (socketIOCallbacks != null)
                for (SocketIOCallbacks callback : socketIOCallbacks)
                    callback.onError(tag, SocketHelper.SOCKET_NOT_FOUND);
            Log.d(DEBUG_TAG, "Trying to close a socket which is not found. TAG : " + tag);
        } else {
            Socket socket = mSocketMap.get(tag);
            socket.on(event, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    if (socketIOCallbacks != null)
                        for (SocketIOCallbacks callback : socketIOCallbacks)
                            callback.on(tag, event, args);
                }
            });
            if (!socket.connected()) {
                socket.connect();
            }
            Log.d(DEBUG_TAG, "Socket with TAG : " + tag + " has started listening for events");
        }
    }

    /**
     * Returns whether a socket is in connected state
     *
     * @param TAG identifier for the socket
     * @return if socket is connected
     */
//    public boolean isConnected(String tag) {
//        if (mSocketMap.containsKey(tag)) {
//            return mSocketMap.get(tag).connected();
//        }
//        Log.d(DEBUG_TAG, "Trying to close a socket which is not found. TAG : " + tag);
//        if (socketIOCallbacks != null)
//            for (SocketIOCallbacks callbacks : socketIOCallbacks)
//                callbacks.onError(tag, SocketHelper.SOCKET_NOT_FOUND);
//        return false;
//    }

    /**
     * Closes all socket connections
     * Can be used on the onDestroy method of the application class when there isn't a need to
     * keep any socket connection open in a service/background thread.
     */
//    public void clearAllSockets() {
//        for (Socket socket : mSocketMap.values()) {
//            socket.close();
//            mSocketMap.remove(mSocketMap.get(socket));
//        }
//        mSocketMap.clear();
//        Log.d(DEBUG_TAG, "All sockets (existing or newly created) are closed/destroyed");
//    }
}
