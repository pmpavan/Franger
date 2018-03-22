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
import com.frangerapp.franger.domain.chat.util.ChatDataConstants;
import com.frangerapp.franger.domain.chat.util.ChatDataUtil;
import com.frangerapp.franger.domain.user.model.User;
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

    public interface ChatPresentationCallbacks {
        void onChatInitiateEventReceived(FeedNewMessageResponse feedNewMessageResponse, String channelName, boolean isIncoming);

        void onChatMessageEventReceived(ChatMessage chatMessage);
    }

    private Context context;
    private ChatApi chatApi;
    private User user;
    private AppDatabase appDatabase;
    private SocketManager socketManager;
    private Gson gson;

    private PublishSubject<MessageEvent> messageEvent = PublishSubject.create();

    private List<String> chatEventsBeingListened = new ArrayList<>();
    private List<String> channelsBeingListened = new ArrayList<>();

    private ArrayList<ChatPresentationCallbacks> callbacks = new ArrayList<>();


    public ChatPresentationImpl(@NonNull Context context, @NonNull ChatApi chatApi, @NotNull User user, AppDatabase appDatabase, SocketManager socketManager, Gson gson) {
        this.context = context;
        this.chatApi = chatApi;
        this.user = user;
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
        return ChatDataUtil.getFeedChannelNameJson(user.getUserId());
    }

    @Override
    public void addFeedEvent() {
        ArrayList<String> list = new ArrayList<>();
        list.add(ChatDataConstants.INITIATE_CHAT);
        FRLogger.msg("user id " + user.getUserId());
        String event = getFeedEventName();
//        socketManager.emitEvent(ChatDataUtil.getDomainName(), ChatDataConstants.JOIN, callbacks, getFeedEventName());
//        socketManager.startListening(ChatDataUtil.getDomainName(), ChatDataConstants.INITIATE_CHAT, callbacks);
        socketManager.emitAndListenEvents(ChatDataUtil.getDomainName(), ChatDataConstants.JOIN, list, this, event);
        chatEventsBeingListened.add(ChatDataConstants.INITIATE_CHAT);
        channelsBeingListened.add(event);
    }

    @Override
    public String getChatEventName(String userId, boolean isIncoming) {
        String chatChannelId;
        if (!isIncoming) {
            chatChannelId = ChatDataUtil.getChatChannelNameJson(user.getUserId(), userId, gson);
        } else {
            chatChannelId = ChatDataUtil.getChatChannelNameJson(userId, user.getUserId(), gson);
        }
        return chatChannelId;
    }

    @Override
    public void addChatEvent(String userId, boolean isIncoming) {
        String event = getChatEventName(userId, isIncoming);
        addChatEvent(event);
    }

    @Override
    public void addChatEvent(String channelName) {
        if (!chatEventsBeingListened.contains(channelName)) {
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
            chatChannelId = ChatDataUtil.getChatChannelName(user.getUserId(), userId);
        } else {
            chatChannelId = ChatDataUtil.getChatChannelName(userId, user.getUserId());
        }
        return chatChannelId;
    }

    @Override
    public void sendMessage(String userId, boolean isIncoming, String message) {
        String channelName = getChatName(userId, isIncoming);
        sendMessage(channelName, message);
        addMessageToDb(channelName, message);
    }

    private void addMessageToDb(String channelName, String msg) {
        Message message = new Message();
        if (channelName != null) {
            int indexOfOtherUser = 1;
            String[] splitChannelName = channelName.split("_");
            if (splitChannelName[1].equals(user.getUserId())) {
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

    @Override
    public void onConnecting(String TAG) {
        FRLogger.msg("onConnecting " + TAG);
    }

    @Override
    public void onSocketCreated(String TAG) {
        FRLogger.msg("onSocketCreated " + TAG);
    }

    @Override
    public void onMessage(String TAG, String message) {
        FRLogger.msg("onMessage " + TAG + ' ' + message);
    }

    @Override
    public void progressChanged(String TAG, int progress) {
        FRLogger.msg("progressChanged " + TAG + ' ' + progress);
    }

    @Override
    public void on(String TAG, String event, Object... args) {
        FRLogger.msg("on " + TAG + ' ' + event + " " + args);
        JSONObject data = (JSONObject) args[0];

        if (event.equalsIgnoreCase(ChatDataConstants.INITIATE_CHAT)) {
//            FeedNewMessageResponse feedNewMessageResponse = gson.fromJson(data.toString(), FeedNewMessageResponse.class);
//            if (feedNewMessageResponse != null) {
//            for (ChatPresentationCallbacks callback : callbacks) {
//                callback.onChatInitiateEventReceived(json, json.getMessageFrom().getId(), isIncoming);
//            }
//                broadcastFeedEvent(feedNewMessageResponse);
//
//            }
            Observable.just(data)
                    .map(JSONObject::toString)
                    .map(s -> gson.fromJson(s, FeedNewMessageResponse.class))
//                    .map(this::addMessageToDb)
                    .map(this::broadcastFeedEvent)
                    .compose(SchedulerUtils.ioToMainObservableScheduler())
                    .subscribe(feedNewMessageResponse1 -> FRLogger.msg("success"), throwable -> FRLogger.msg("failure"));
        } else if (event.equalsIgnoreCase(ChatDataConstants.MESSAGE)) {
//            ChatMessage chatMessage = gson.fromJson(data.toString(), ChatMessage.class);
//            for (ChatPresentationCallbacks callback : callbacks) {
//                callback.onChatMessageEventReceived(json);
//            }
            Observable.just(data)
                    .map(JSONObject::toString)
                    .map(s -> gson.fromJson(s, ChatMessage.class))
                    .map(this::addMessageToDb)
                    .map(this::broadcastMessageEvent)
                    .compose(SchedulerUtils.ioToMainObservableScheduler())
                    .subscribe(chatMessage -> FRLogger.msg("success"), throwable -> FRLogger.msg("failure"));

        }
    }

    private FeedNewMessageResponse broadcastFeedEvent(FeedNewMessageResponse feedNewMessageResponse) {
        boolean isIncoming = true;
        if (feedNewMessageResponse.getMessageFrom() != null) {
            if (feedNewMessageResponse.getChannel() != null && feedNewMessageResponse.getChannel().split("_")[1].equals(user.getUserId())) {
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

    private ChatMessage broadcastMessageEvent(ChatMessage chatMessage) {
        MessageEvent messageEvent = new MessageEvent();
        messageEvent.setEventType(ChatDataConstants.SOCKET_EVENT_TYPE.MESSAGE.id);
        messageEvent.setMessage(chatMessage.getMessage());
        messageEvent.setChannel(chatMessage.getChannel());
        getMessageEvent().onNext(messageEvent);
        return chatMessage;
    }

    private ChatMessage addMessageToDb(ChatMessage chatMessage) {
        addMessageToDb(chatMessage.getChannel(), chatMessage.getMessage());
        return chatMessage;
    }

    @Override
    public void onError(String TAG, SocketHelper errorCode) {
        FRLogger.msg("onError " + TAG + ' ' + errorCode.getMessage());

    }

    @Override
    public void onDisconnecting(String TAG) {
        FRLogger.msg("onDisconnecting " + TAG);

    }

    @Override
    public void onSocketDestroyed(String TAG) {
        FRLogger.msg("onSocketDestroyed " + TAG);

    }

    @Override
    public void setCallbacks(ChatPresentationCallbacks callback) {
        this.callbacks.add(callback);
    }
}
