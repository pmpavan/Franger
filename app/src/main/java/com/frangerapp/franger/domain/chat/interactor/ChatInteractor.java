package com.frangerapp.franger.domain.chat.interactor;

import com.frangerapp.franger.app.util.db.entity.Message;
import com.frangerapp.franger.app.util.db.entity.MyListChannel;
import com.frangerapp.franger.domain.chat.model.MessageEvent;
import com.frangerapp.franger.ui.home.IncomingListItemUiState;
import com.frangerapp.franger.ui.home.OutgoingListItemUiState;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by pavanm on 14/03/18.
 * <p>
 * TODO split this class into feed and chat events so we can handle them separately.
 * This gives us the advantage of not having to handle feed events in chat viewmodel
 */

public interface ChatInteractor {

    PublishSubject<MessageEvent> getMessageEvent();

    void addEventToBeListened(String event);

    String getFeedEventName();

    void addFeedEvent();

    String getChatEventName(String userId, boolean isIncoming, String message);

    void addChatEvent(String userId, boolean isIncoming,String message);

    String getChatName(String userId, boolean isIncoming);

    long sendMessage(String userId, boolean isIncoming, String message);

    Single<List<Message>> getMessages(String userId, boolean isIncoming);

    Single<List<OutgoingListItemUiState>> getMyListChannels();

    Single<List<IncomingListItemUiState>> getAnonListChannels();

    Single<List<MyListChannel>> getMyListChannelList();

    void onAppClosed();
}
