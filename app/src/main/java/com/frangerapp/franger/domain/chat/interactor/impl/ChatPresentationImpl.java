package com.frangerapp.franger.domain.chat.interactor.impl;

import android.content.Context;
import android.support.annotation.NonNull;

import com.franger.mobile.logger.FRLogger;
import com.franger.socket.SocketCallbacks;
import com.franger.socket.SocketHelper;
import com.franger.socket.socketio.SocketManager;
import com.frangerapp.franger.app.util.db.AppDatabase;
import com.frangerapp.franger.data.chat.ChatApi;
import com.frangerapp.franger.domain.chat.interactor.ChatInteractor;
import com.frangerapp.franger.domain.chat.model.ChatMessage;
import com.frangerapp.franger.domain.chat.model.FeedNewMessageResponse;
import com.frangerapp.franger.domain.chat.model.MessageEvent;
import com.frangerapp.franger.domain.chat.util.ChatDataConstants;
import com.frangerapp.franger.domain.chat.util.ChatDataUtil;
import com.frangerapp.franger.domain.user.model.User;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.subjects.PublishSubject;

/**
 * Created by pavanm on 14/03/18.
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
        ArrayList<String> list = new ArrayList<>();
        list.add(ChatDataConstants.MESSAGE);
        socketManager.emitAndListenEvents(ChatDataUtil.getDomainName(), ChatDataConstants.JOIN, list, this, channelName);
        chatEventsBeingListened.add(ChatDataConstants.MESSAGE);
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
        sendMessage(getChatName(userId, isIncoming), message);
    }

    @Override
    public void sendMessage(String channel, String message) {
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
            FeedNewMessageResponse json = gson.fromJson(data.toString(), FeedNewMessageResponse.class);
            if (json != null) {
                boolean isIncoming = true;
                if (json.getChannel() != null && json.getChannel().split("_")[1].equals(user.getUserId())) {
                    isIncoming = false;
                }
                if (json.getMessageFrom() != null) {
                    for (ChatPresentationCallbacks callback :
                            callbacks) {
//                        callback.onChatInitiateEventReceived(json, json.getMessageFrom().getId(), isIncoming);
                    }
                    MessageEvent messageEvent = new MessageEvent();
                    messageEvent.setEventType(1);
                    messageEvent.setIncoming(isIncoming);
                    messageEvent.setChannel(json.getMessageFrom().getId());
                    getMessageEvent().onNext(messageEvent);

                }
            }
        } else if (event.equalsIgnoreCase(ChatDataConstants.MESSAGE)) {
            ChatMessage json = gson.fromJson(data.toString(), ChatMessage.class);
            for (ChatPresentationCallbacks callback :
                    callbacks) {
//                callback.onChatMessageEventReceived(json);
            }
            MessageEvent messageEvent = new MessageEvent();
            messageEvent.setEventType(2);
            messageEvent.setMessage(json.getMessage());
            messageEvent.setChannel(json.getChannel());
            getMessageEvent().onNext(messageEvent);
        }
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
