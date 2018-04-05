package com.frangerapp.franger.domain.chat.interactor.impl;

import android.content.Context;
import android.support.annotation.NonNull;

import com.franger.mobile.logger.FRLogger;
import com.franger.socket.SocketCallbacks;
import com.franger.socket.SocketHelper;
import com.franger.socket.socketio.SocketManager;
import com.frangerapp.franger.app.util.db.AppDatabase;
import com.frangerapp.franger.app.util.db.entity.AnonListChannel;
import com.frangerapp.franger.app.util.db.entity.Message;
import com.frangerapp.franger.app.util.db.entity.MyListChannel;
import com.frangerapp.franger.app.util.db.entity.User;
import com.frangerapp.franger.data.chat.ChatApi;
import com.frangerapp.franger.domain.chat.interactor.ChatInteractor;
import com.frangerapp.franger.domain.chat.model.FeedNewMessageResponse;
import com.frangerapp.franger.domain.chat.model.MessageEvent;
import com.frangerapp.franger.domain.chat.model.MessageResponse;
import com.frangerapp.franger.domain.chat.util.ChatDataConstants;
import com.frangerapp.franger.domain.chat.util.ChatDataUtil;
import com.frangerapp.franger.domain.user.model.LoggedInUser;
import com.frangerapp.franger.viewmodel.common.rx.SchedulerUtils;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
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

    private HashMap<String, User> usersList = new HashMap<>();

    private Random random;

    public ChatPresentationImpl(@NonNull Context context, @NonNull ChatApi chatApi, @NotNull LoggedInUser loggedInUser, AppDatabase appDatabase, SocketManager socketManager, Gson gson, Random random) {
        this.context = context;
        this.chatApi = chatApi;
        this.loggedInUser = loggedInUser;
        this.appDatabase = appDatabase;
        this.socketManager = socketManager;
        this.gson = gson;
        this.random = random;
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
        return ChatDataUtil.getFeedChannelNameJson(loggedInUser.getUserId());
    }

    @Override
    public void addFeedEvent() {
        ArrayList<String> list = new ArrayList<>();
        list.add(ChatDataConstants.INITIATE_CHAT);
        FRLogger.msg("loggedInUser id " + loggedInUser.getUserId());
        String feedChannelName = getFeedEventName();
//        socketManager.emitEvent(ChatDataUtil.getDomainName(), ChatDataConstants.JOIN, this, getFeedEventName());
//        socketManager.startListening(ChatDataUtil.getDomainName(), ChatDataConstants.INITIATE_CHAT, this);
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
        return chatChannelId;
    }

    @Override
    public void addChatEvent(String userId, boolean isIncoming) {
        String event = getChatEventName(userId, isIncoming);
        String channelName = getChatName(userId, isIncoming);
        if (!channelsBeingListened.contains(event)) {
            ArrayList<String> list = new ArrayList<>();
            list.add(ChatDataConstants.MESSAGE);
            socketManager.emitAndListenEvents(ChatDataUtil.getDomainName(), ChatDataConstants.JOIN, list, this, event);
            chatEventsBeingListened.add(ChatDataConstants.MESSAGE);
            channelsBeingListened.add(channelName);
            FRLogger.msg("channel name " + channelName);
        }
    }

    private String getChatName(String userId, boolean isIncoming) {
        String chatChannelId;
        if (!isIncoming) {
            chatChannelId = ChatDataUtil.getChatChannelName(loggedInUser.getUserId(), userId);
        } else {
            chatChannelId = ChatDataUtil.getChatChannelName(userId, loggedInUser.getUserId());
        }
        return chatChannelId;
    }

    @Override
    public long sendMessage(String userId, boolean isIncoming, String message) {
        String channelName = getChatName(userId, isIncoming);
        if (!channelsBeingListened.contains(channelName)) {
            addChatEvent(userId, isIncoming);
        }
        sendMessage(channelName, message);
        return addMessageToDb(channelName, message);
    }

    private long addMessageToDb(@NotNull String channelName, String msg) {
        Message message = new Message();
        message.userId = loggedInUser.getUserId();
        message.message = msg;
        message.channelName = channelName;
        message.sentAt = new Date();
        return appDatabase.messageDao().addMessage(message);
    }

    private String getUserId(String channelName) {
        int indexOfOtherUser = 1;
        String[] splitChannelName = channelName.split("_");
        if (splitChannelName[1].equals(loggedInUser.getUserId())) {
            indexOfOtherUser = 2;
        }
        return splitChannelName[indexOfOtherUser];
    }

    private void sendMessage(String channel, String message) {
        String jsonInString = ChatDataUtil.getChatMessageBody(channel, message, gson);
        FRLogger.msg("message " + jsonInString);
        socketManager.emitEvent(ChatDataUtil.getDomainName(), ChatDataConstants.MESSAGE, this, jsonInString);
        chatEventsBeingListened.add(ChatDataConstants.MESSAGE);
    }

    @Override
    public Single<List<Message>> getMessages(String userId, boolean isIncoming) {
        String channelName = getChatName(userId, isIncoming);
        return getMessages(channelName);
    }

    public Single<List<Message>> getMessages(String channelName) {
        return appDatabase.messageDao().getMessages(channelName);
    }

    @Override
    public PublishSubject<MessageEvent> getMessageEvent() {
        return messageEvent;
    }

    private String getRandomName() {
        String name;
        ArrayList<String> randomNames = chatApi.getRandomNamesList();
        int index = random.nextInt(randomNames.size());
        name = randomNames.get(index);
        randomNames.remove(index);
        return name;
    }

    private Single<FeedNewMessageResponse> addChannelToDb(FeedNewMessageResponse feedNewMessageResponse) {
        return (Single<FeedNewMessageResponse>) addChannelToDb(feedNewMessageResponse, feedNewMessageResponse.getChannel(), feedNewMessageResponse.getMessageFrom().getId());
    }

    private Single<MessageResponse> addChannelToDb(MessageResponse messageResponse) {
        return (Single<MessageResponse>) addChannelToDb(messageResponse, messageResponse.getChannel(), messageResponse.getData().getFromId());
    }

    private Single addChannelToDb(Object object, String channelName, String fromId) {
        boolean isIncoming = isIncoming(channelName);
        if (isIncoming) {
            return appDatabase.myListChannelDao().getChannel(channelName)
                    .doOnError(t -> {
                        //New Chat so insert new record
                        MyListChannel myListChannel = new MyListChannel(channelName);
                        myListChannel.createdAt = new Date();
                        myListChannel.otherUserId = fromId;
                        myListChannel.unreadMsgCount = 0;
                        myListChannel.updateAt = new Date();
                        appDatabase.myListChannelDao().addChannel(myListChannel);
                    })
                    .map(myListChannel -> {
                        myListChannel.updateAt = new Date();
                        myListChannel.unreadMsgCount += 1;
                        appDatabase.myListChannelDao().updateChannel(myListChannel);
                        return Single.just(object);
                    })
                    .blockingGet();
        } else {
            return appDatabase.anonListDao().getChannel(channelName)
                    .doOnError(t -> {
                        //New Chat so insert new record
                        AnonListChannel anonListChannel = new AnonListChannel(channelName);
                        anonListChannel.createdAt = new Date();
                        anonListChannel.otherUserId = fromId;
                        anonListChannel.unreadMsgCount = 0;
                        anonListChannel.anonymisedUserName = getRandomName();
                        anonListChannel.updateAt = new Date();
                        appDatabase.anonListDao().addChannel(anonListChannel);
                    })
                    .map(anonListChannel -> {

                        anonListChannel.updateAt = new Date();
                        anonListChannel.unreadMsgCount += 1;
                        appDatabase.anonListDao().updateChannel(anonListChannel);
                        return Single.just(object);
                    })
                    .blockingGet();
        }
    }

    private MessageEvent broadcastFeedEvent(FeedNewMessageResponse feedNewMessageResponse) {
        boolean isIncoming = isIncoming(feedNewMessageResponse.getChannel());
        MessageEvent messageEvent = new MessageEvent();
        messageEvent.setEventType(ChatDataConstants.SOCKET_EVENT_TYPE.FEED.id);
        messageEvent.setIncoming(isIncoming);
        messageEvent.setChannel(feedNewMessageResponse.getMessageFrom().getId());
        messageEvent.setTimestamp(new Date());
//        getMessageEvent().onNext(messageEvent);
        return messageEvent;
    }

    private MessageEvent broadcastMessageEvent(MessageResponse chatMessage) {
        MessageEvent messageEvent = new MessageEvent();
        messageEvent.setEventType(ChatDataConstants.SOCKET_EVENT_TYPE.MESSAGE.id);
        messageEvent.setMessage(chatMessage.getMessage());
        messageEvent.setChannel(chatMessage.getChannel());
        messageEvent.setUserId(chatMessage.getData().getFromId());
        messageEvent.setTimestamp(new Date());
        messageEvent.setMessageId(chatMessage.getMessageId());
        return messageEvent;
    }

    private MessageResponse addMessageToDb(MessageResponse chatMessage) {
        long messageId = addMessageToDb(chatMessage.getChannel(), chatMessage.getMessage());
        chatMessage.setMessageId(messageId);
        return chatMessage;
    }


    private boolean isIncoming(String channelName) {
        boolean isIncoming = true;
        if (channelName != null) {
            String[] splitChannelName = channelName.split("_");
            if (splitChannelName[1].equals(loggedInUser.getUserId())) {
                isIncoming = false;
            }
        }
        return isIncoming;
    }

    private Single<MessageEvent> getUserDetails(MessageEvent data) {
        FRLogger.msg("chat message getUserDetails " + data.getUserId());
        if (!usersList.containsKey(data.getUserId())) {
            return appDatabase.userDao().getUser(data.getUserId())
                    .map(user -> {
                        usersList.put(data.getUserId(), user);
                        FRLogger.msg("user " + user);
                        data.setUser(user);
                        return data;
                    });
        } else {
            User user = usersList.get(data.getUserId());
            data.setUser(user);
            return Single.just(data);
        }
    }

    private MessageEvent postMessageEvent(MessageEvent messageEvent) {
        FRLogger.msg("chat message postMessageEvent");
        getMessageEvent().onNext(messageEvent);
        return messageEvent;
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
        Disposable disposable;
        if (event.equalsIgnoreCase(ChatDataConstants.INITIATE_CHAT)) {
            disposable = Observable.just(data)
                    .map(JSONObject::toString)
                    .map(s -> gson.fromJson(s, FeedNewMessageResponse.class))
                    .flatMapSingle(this::addChannelToDb)
                    .map(feedNewMessageResponse -> {
                        addChatEvent(feedNewMessageResponse.getMessageFrom().getId(), isIncoming(feedNewMessageResponse.getChannel()));
                        return feedNewMessageResponse;
                    })
                    .map(this::broadcastFeedEvent)
//                    .flatMapSingle(this::getUserDetails)
                    .map(this::postMessageEvent)
                    .compose(SchedulerUtils.ioToMainObservableScheduler())
                    .subscribe(feedNewMessageResponse1 -> FRLogger.msg("success"), throwable -> FRLogger.msg("failure " + throwable.getMessage()));
        } else if (event.equalsIgnoreCase(ChatDataConstants.MESSAGE)) {
            disposable = Observable.just(data)
                    .map(JSONObject::toString)
                    .map(s -> gson.fromJson(s, MessageResponse.class))
                    .flatMapSingle(this::addChannelToDb)
                    .map(this::addMessageToDb)
                    .map(this::broadcastMessageEvent)
//                    .flatMapSingle(this::getUserDetails)
                    .map(this::postMessageEvent)
                    .compose(SchedulerUtils.ioToMainObservableScheduler())
                    .subscribe(chatMessage -> FRLogger.msg("success"), throwable -> FRLogger.msg("failure " + throwable.getMessage()));
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
