package com.frangerapp.franger.domain.chat.interactor.impl;

import android.content.Context;
import android.support.annotation.NonNull;

import com.franger.mobile.logger.FRLogger;
import com.franger.socket.SocketCallbacks;
import com.franger.socket.SocketHelper;
import com.franger.socket.socketio.SocketManager;
import com.frangerapp.franger.app.util.db.AppDatabase;
import com.frangerapp.franger.app.util.db.entity.Message;
import com.frangerapp.franger.data.chat.ChatApi;
import com.frangerapp.franger.domain.chat.interactor.ChatInteractor;
import com.frangerapp.franger.domain.chat.model.ChatMessage;
import com.frangerapp.franger.domain.chat.model.FeedNewMessageResponse;
import com.frangerapp.franger.domain.chat.model.MessageEvent;
import com.frangerapp.franger.domain.chat.model.MessageResponse;
import com.frangerapp.franger.domain.chat.model.MessageResponseData;
import com.frangerapp.franger.domain.chat.util.ChatDataConstants;
import com.frangerapp.franger.domain.chat.util.ChatDataUtil;
import com.frangerapp.franger.domain.user.model.LoggedInUser;
import com.frangerapp.franger.viewmodel.common.rx.SchedulerUtils;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by pavanm on 14/03/18.
 */

/**
 * TODO split classes into feed and message chat classes
 */
public class ChatPresentationImpl implements ChatInteractor, SocketCallbacks {
    private static final String TAG = ChatPresentationImpl.class.getName();

    private Context context;
    private ChatApi chatApi;
    private LoggedInUser loggedInUser;
    private AppDatabase appDatabase;
    private SocketManager socketManager;
    private Gson gson;

    private PublishSubject<MessageEvent> messageEvent = PublishSubject.create();

    private List<String> chatEventsBeingListened = new ArrayList<>();
    private List<String> channelsBeingListened = new ArrayList<>();


    public ChatPresentationImpl(@NonNull Context context, @NonNull ChatApi chatApi, @NotNull LoggedInUser loggedInUser, AppDatabase appDatabase, SocketManager socketManager, Gson gson) {
        this.context = context;
        this.chatApi = chatApi;
        this.loggedInUser = loggedInUser;
        this.appDatabase = appDatabase;
        this.socketManager = socketManager;
        this.gson = gson;
    }

    @NonNull
    @Override
    public void addEventToBeListened(String event) {
        ArrayList<String> list = new ArrayList<>();
        list.add(event);
        socketManager.addEventsToBeListened(list, this);
        chatEventsBeingListened.add(event);
    }

    @Override
    public String getFeedEventName() {
//        return ChatDataUtil.getFeedChannelNameJson(loggedInUser.getUserId());
        return ChatDataUtil.getFeedChannelNameJson("2");
    }

    @Override
    public void addFeedEvent() {
        ArrayList<String> list = new ArrayList<>();
        list.add(ChatDataConstants.INITIATE_CHAT);
        FRLogger.msg("loggedInUser id " + loggedInUser.getUserId());
        String feedChannelName = getFeedEventName();
//        socketManager.emitEvent(ChatDataUtil.getDomainName(), ChatDataConstants.JOIN, callbacks, getFeedEventName());
//        socketManager.startListening(ChatDataUtil.getDomainName(), ChatDataConstants.INITIATE_CHAT, callbacks);
        socketManager.emitAndListenEvents(ChatDataUtil.getDomainName(), ChatDataConstants.JOIN, list, this, feedChannelName);
        chatEventsBeingListened.add(ChatDataConstants.INITIATE_CHAT);
        channelsBeingListened.add(feedChannelName);
    }

    @Override
    public String getChatEventName(String userId, boolean isIncoming) {
        String chatChannelId;
        if (!isIncoming) {
            chatChannelId = ChatDataUtil.getChatChannelNameJson(loggedInUser.getUserId(), userId, gson);
        } else {
            chatChannelId = ChatDataUtil.getChatChannelNameJson(userId, loggedInUser.getUserId(), gson);
        }
        chatChannelId = ChatDataUtil.getChatChannelNameJson("1", "2", gson);
        return chatChannelId;
    }

    @Override
    public void addChatEvent(String userId, boolean isIncoming) {
        String event = getChatEventName(userId, isIncoming);
        addChatEvent(event);
    }

    @Override
    public void addChatEvent(String channelName) {
        if (!channelsBeingListened.contains(channelName)) {
            ArrayList<String> list = new ArrayList<>();
            list.add(ChatDataConstants.MESSAGE);
            socketManager.emitAndListenEvents(ChatDataUtil.getDomainName(), ChatDataConstants.JOIN, list, this, channelName);
            chatEventsBeingListened.add(ChatDataConstants.MESSAGE);
            channelsBeingListened.add(channelName);
        }
    }

    private String getChatName(String userId, boolean isIncoming) {
        String chatChannelId;
        if (!isIncoming) {
            chatChannelId = ChatDataUtil.getChatChannelName(loggedInUser.getUserId(), userId);
        } else {
            chatChannelId = ChatDataUtil.getChatChannelName(userId, loggedInUser.getUserId());
        }
//        return chatChannelId;
        return ChatDataUtil.getChatChannelName("1", "2");
    }

    @Override
    public void sendMessage(String userId, boolean isIncoming, String message) {
        String channelName = getChatName(userId, isIncoming);
        if (!channelsBeingListened.contains(channelName)) {
            addChatEvent(userId, isIncoming);
        }
        sendMessage(channelName, message);
        addMessageToDb(channelName, message);
    }

    private void addMessageToDb(String channelName, String msg) {
        Message message = new Message();
        if (channelName != null) {
            int indexOfOtherUser = 1;
            String[] splitChannelName = channelName.split("_");
            if (splitChannelName[1].equals(loggedInUser.getUserId())) {
                indexOfOtherUser = 2;
            }
            message.userId = splitChannelName[indexOfOtherUser];
        }
        message.message = msg;
        message.sentAt = new Date();
        appDatabase.messageDao().addMessage(message);
    }

    private void sendMessage(String channel, String message) {
        String jsonInString = ChatDataUtil.getChatMessageBody(channel, message, gson);
        FRLogger.msg("message " + jsonInString);
        socketManager.emitEvent(ChatDataUtil.getDomainName(), ChatDataConstants.MESSAGE, this, jsonInString);
        chatEventsBeingListened.add(ChatDataConstants.MESSAGE);
    }

    @Override
    public List<String> getChatEventsBeingListened() {
        return chatEventsBeingListened;
    }

    @Override
    public PublishSubject<MessageEvent> getMessageEvent() {
        return messageEvent;
    }


    private FeedNewMessageResponse addChannelToDb(FeedNewMessageResponse feedNewMessageResponse) {

        return feedNewMessageResponse;
    }

    private FeedNewMessageResponse broadcastFeedEvent(FeedNewMessageResponse feedNewMessageResponse) {
        boolean isIncoming = true;
        if (feedNewMessageResponse.getMessageFrom() != null) {
            if (feedNewMessageResponse.getChannel() != null && feedNewMessageResponse.getChannel().split("_")[1].equals(loggedInUser.getUserId())) {
                isIncoming = false;
            }
        }
        MessageEvent messageEvent = new MessageEvent();
        messageEvent.setEventType(ChatDataConstants.SOCKET_EVENT_TYPE.FEED.id);
        messageEvent.setIncoming(isIncoming);
        messageEvent.setChannel(feedNewMessageResponse.getMessageFrom().getId());
        getMessageEvent().onNext(messageEvent);
        return feedNewMessageResponse;
    }

    private MessageResponse broadcastMessageEvent(MessageResponse chatMessage) {
        MessageEvent messageEvent = new MessageEvent();
        messageEvent.setEventType(ChatDataConstants.SOCKET_EVENT_TYPE.MESSAGE.id);
        messageEvent.setMessage(chatMessage.getMessage());
        messageEvent.setChannel(chatMessage.getChannel());
        messageEvent.setUserId(chatMessage.getData().getFromId());
        getMessageEvent().onNext(messageEvent);
        return chatMessage;
    }

    private MessageResponse addMessageToDb(MessageResponse chatMessage) {
        FRLogger.msg("chat message " + chatMessage.getChannel() + " " + chatMessage.getMessage());
        addMessageToDb(chatMessage.getChannel(), chatMessage.getMessage());
        return chatMessage;
    }

    /**
     * Socket callbacks
     */
    @Override
    public void onConnecting(String TAG) {
        FRLogger.msg("ChatPresentationImpl onConnecting " + TAG);
    }

    @Override
    public void onSocketCreated(String TAG) {
        FRLogger.msg("ChatPresentationImpl onSocketCreated " + TAG);
    }

    @Override
    public void onMessage(String TAG, String message) {
        FRLogger.msg("ChatPresentationImpl onMessage " + TAG + ' ' + message);
    }

    @Override
    public void progressChanged(String TAG, int progress) {
        FRLogger.msg("ChatPresentationImpl progressChanged " + TAG + ' ' + progress);
    }

    @Override
    public void on(String TAG, String event, Object... args) {
        JSONObject data = (JSONObject) args[0];
        FRLogger.msg("ChatPresentationImpl on " + TAG + ' ' + event + " " + data.toString());

        if (event.equalsIgnoreCase(ChatDataConstants.INITIATE_CHAT)) {
            Observable.just(data)
                    .map(JSONObject::toString)
                    .map(s -> gson.fromJson(s, FeedNewMessageResponse.class))
                    .map(this::addChannelToDb)
                    .map(this::broadcastFeedEvent)
                    .compose(SchedulerUtils.ioToMainObservableScheduler())
                    .subscribe(feedNewMessageResponse1 -> FRLogger.msg("success"), throwable -> FRLogger.msg("failure"));
        } else if (event.equalsIgnoreCase(ChatDataConstants.MESSAGE)) {
            Observable.just(data)
                    .map(JSONObject::toString)
                    .map(s -> gson.fromJson(s, MessageResponse.class))
                    .map(this::addMessageToDb)
                    .map(this::broadcastMessageEvent)
                    .compose(SchedulerUtils.ioToMainObservableScheduler())
                    .subscribe(chatMessage -> FRLogger.msg("success"), throwable -> FRLogger.msg("failure"));
        }
    }

    @Override
    public void onError(String TAG, SocketHelper errorCode) {
        FRLogger.msg("ChatPresentationImpl onError " + TAG + ' ' + errorCode.getMessage());
    }

    @Override
    public void onDisconnecting(String TAG) {
        FRLogger.msg("ChatPresentationImpl onDisconnecting " + TAG);

    }

    @Override
    public void onSocketDestroyed(String TAG) {
        FRLogger.msg("ChatPresentationImpl onSocketDestroyed " + TAG);

    }

}
