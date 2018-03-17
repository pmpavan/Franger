package com.frangerapp.franger.domain.chat.interactor.impl;

import android.content.Context;
import android.support.annotation.NonNull;

import com.franger.socket.SocketIOCallbacks;
import com.franger.socket.socketio.SocketIOManager;
import com.frangerapp.franger.app.util.db.AppDatabase;
import com.frangerapp.franger.data.chat.ChatApi;
import com.frangerapp.franger.domain.chat.util.ChatDataUtil;
import com.frangerapp.franger.domain.chat.interactor.ChatInteractor;
import com.frangerapp.franger.domain.chat.util.ChatDataConstants;
import com.frangerapp.franger.domain.user.model.User;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Created by pavanm on 14/03/18.
 */

public class ChatPresentationImpl implements ChatInteractor {
    private static final String TAG = ChatPresentationImpl.class.getName();

    private Context context;
    private ChatApi profileApi;
    private User user;
    private AppDatabase appDatabase;
    private SocketIOManager socketIOManager;
    private Gson gson;

    public ChatPresentationImpl(@NonNull Context context, @NonNull ChatApi profileApi, @NotNull User user, AppDatabase appDatabase, SocketIOManager socketIOManager, Gson gson) {
        this.context = context;
        this.profileApi = profileApi;
        this.user = user;
        this.appDatabase = appDatabase;
        this.socketIOManager = socketIOManager;
        this.gson = gson;
    }

    @NonNull
    @Override
    public void addEventToBeListened(String event, SocketIOCallbacks callbacks) {
        ArrayList<String> list = new ArrayList<>();
        list.add(event);
        socketIOManager.addEventsToBeListened(list, callbacks);
    }

    @Override
    public String getFeedEventName() {
        return ChatDataUtil.getFeedChannelName(user.getUserId());
    }

    @Override
    public void addFeedEvent(SocketIOCallbacks callbacks) {
        ArrayList<String> list = new ArrayList<>();
        list.add(ChatDataConstants.INITIATE_CHAT);
        socketIOManager.emitAndListenEvents(ChatDataUtil.getDomainName(), ChatDataConstants.JOIN, list, callbacks, getFeedEventName());
    }

    @Override
    public String getChatEventName(String userId, boolean isIncoming) {
        String chatChannelId;
        if (isIncoming) {
            chatChannelId = ChatDataUtil.getChatChannelName(user.getUserId(), userId, gson);
        } else {
            chatChannelId = ChatDataUtil.getChatChannelName(userId, user.getUserId(), gson);
        }
        return chatChannelId;
    }

    @Override
    public void addChatEvent(String userId, boolean isIncoming, SocketIOCallbacks callbacks) {
        ArrayList<String> list = new ArrayList<>();
        list.add(ChatDataConstants.MESSAGE);
        socketIOManager.emitAndListenEvents(ChatDataUtil.getDomainName(), ChatDataConstants.JOIN, list, callbacks, getChatEventName(userId, isIncoming));

    }

    @Override
    public void sendMessage(String userId, boolean isIncoming, String message, SocketIOCallbacks callbacks) {
        String jsonInString = ChatDataUtil.getChatMessageBody(getChatEventName(userId, isIncoming), message, gson);
        socketIOManager.emitEvent(ChatDataUtil.getDomainName(), ChatDataConstants.MESSAGE, jsonInString);

    }
}
