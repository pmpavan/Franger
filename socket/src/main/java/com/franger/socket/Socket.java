package com.franger.socket;

import java.util.List;

/**
 * Created by Pavan on 14/11/17.
 */

public interface Socket {

    String createASocket(String url, List<String> eventsToBeListened, SocketOptions opts);

    void emitEvent(String tag, String event, Object... message);

    void closeASocket(String tag);

    void stopListening(String tag);

    void startListening(final String tag, final String event);

    boolean isConnected(String tag);

    void clearAllSockets();

    void setCallBacks(SocketIOCallbacks socketIOCallbacks);
}
