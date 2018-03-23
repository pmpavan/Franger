package com.frangerapp.franger.domain.chat.interactor;

import com.frangerapp.franger.domain.chat.model.MessageEvent;

import java.util.List;

import io.reactivex.subjects.PublishSubject;

/**
 * Created by pavanm on 14/03/18.
 *
 * TODO split this class into feed and chat events so we can handle them separately.
 * This gives us teh advantage of not having to handle feed events in chat viewmodel
 */

public interface ChatInteractor {

    PublishSubject<MessageEvent> getMessageEvent();

    void addEventToBeListened(String event);

    String getFeedEventName();

    void addFeedEvent();

    String getChatEventName(String userId, boolean isIncoming);

    void addChatEvent(String userId, boolean isIncoming);

    void addChatEvent(String channelName);

    void sendMessage(String userId, boolean isIncoming, String message);

    List<String> getChatEventsBeingListened();
}
