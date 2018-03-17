package com.frangerapp.franger.domain.chat.interactor;

import com.franger.socket.SocketIOCallbacks;

/**
 * Created by pavanm on 14/03/18.
 */

public interface ChatInteractor {

    void addEventToBeListened(String event, SocketIOCallbacks callbacks);

    String getFeedEventName();

    void addFeedEvent(SocketIOCallbacks callbacks);

    String getChatEventName(String userId, boolean isIncoming);

    void addChatEvent(String userId, boolean isIncoming, SocketIOCallbacks callbacks);

    void sendMessage(String userId, boolean isIncoming, String message, SocketIOCallbacks callbacks);
}
