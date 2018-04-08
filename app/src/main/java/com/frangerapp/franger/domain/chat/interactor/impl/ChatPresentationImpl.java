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
import com.frangerapp.franger.data.chat.model.AnonymousUser;
import com.frangerapp.franger.domain.chat.interactor.ChatInteractor;
import com.frangerapp.franger.domain.chat.model.FeedNewMessageResponse;
import com.frangerapp.franger.domain.chat.model.MessageEvent;
import com.frangerapp.franger.domain.chat.model.MessageResponse;
import com.frangerapp.franger.domain.chat.util.ChatDataConstants;
import com.frangerapp.franger.domain.chat.util.ChatDataUtil;
import com.frangerapp.franger.domain.user.model.LoggedInUser;
import com.frangerapp.franger.ui.home.IncomingListItemUiState;
import com.frangerapp.franger.ui.home.OutgoingListItemUiState;
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
import io.reactivex.disposables.CompositeDisposable;
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

    private CompositeDisposable disposables = new CompositeDisposable();

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
    public String getChatEventName(String userId, boolean isIncoming, String message) {
        String chatChannelId;
        if (!isIncoming) {
            chatChannelId = ChatDataUtil.getChatChannelNameJson(loggedInUser.getUserId(), userId, gson, message);
        } else {
            chatChannelId = ChatDataUtil.getChatChannelNameJson(userId, loggedInUser.getUserId(), gson, message);
        }
        return chatChannelId;
    }

    @Override
    public void addChatEvent(String userId, boolean isIncoming, String message) {
        String event = getChatEventName(userId, isIncoming, message);
        String channelName = getChatName(userId, isIncoming);
        if (!channelsBeingListened.contains(event)) {
            ArrayList<String> list = new ArrayList<>();
            list.add(ChatDataConstants.MESSAGE);
            socketManager.emitAndListenEvents(ChatDataUtil.getDomainName(), ChatDataConstants.JOIN, list, this, event);
            chatEventsBeingListened.add(ChatDataConstants.MESSAGE);
            channelsBeingListened.add(channelName);
            FRLogger.msg("channel name being listened " + channelName);
        }
    }

    @Override
    public String getChatName(String userId, boolean isIncoming) {
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
            addChatEvent(userId, isIncoming, message);
        } else {
            sendMessage(channelName, message);
        }
        addChannelToDb(channelName, userId, message)
                .toObservable()
                .compose(SchedulerUtils.ioToMainObservableScheduler())
                .subscribe(object -> FRLogger.msg("success " + object), throwable -> FRLogger.msg("failure add to channel " + throwable.getMessage()));
        return addMessageToDb(channelName, message);
    }

    private Single<Object> addChannelToDb(String channelName, String fromId, String message) {
        return (Single<Object>) addChannelToDb(new Object(), channelName, fromId, message)
                .blockingGet();
    }

    private long addMessageToDb(@NotNull String channelName, String msg) {
        Message message = new Message();
        message.userId = loggedInUser.getUserId();
        message.message = msg;
        message.channelName = channelName;
        message.sentAt = new Date();
        message.isMessageRead = true;
        FRLogger.msg("message obj " + message);
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

    private Single<List<Message>> getMessages(String channelName) {
        return appDatabase.messageDao().getMessages(channelName);
    }

    @Override
    public PublishSubject<MessageEvent> getMessageEvent() {
        return messageEvent;
    }

    private AnonymousUser getRandomName() {
        AnonymousUser anonymousUser;
        ArrayList<AnonymousUser> randomNames = chatApi.getRandomNamesList();
        int index = random.nextInt(randomNames.size());
        anonymousUser = randomNames.get(index);
        randomNames.remove(index);
        return anonymousUser;
    }

    private Single<FeedNewMessageResponse> addChannelToDb(FeedNewMessageResponse feedNewMessageResponse) {
        return (Single<FeedNewMessageResponse>) addChannelToDb(feedNewMessageResponse, feedNewMessageResponse.getChannel(), feedNewMessageResponse.getMessageFrom().getId(), feedNewMessageResponse.getMessage())
                .blockingGet();
    }

    private Single<MessageResponse> addChannelToDb(MessageResponse messageResponse) {
        return (Single<MessageResponse>) addChannelToDb(messageResponse, messageResponse.getChannel(), messageResponse.getData().getFromId(), messageResponse.getMessage())
                .blockingGet();
    }

    private Single addChannelToDb(Object object, String channelName, String otherUserId, String message) {
        boolean isIncoming = isIncoming(channelName);
        String id = otherUserId;
        if (!otherUserId.equals(loggedInUser.getUserId())) {
            id = getUserId(channelName);
        }
        if (!isIncoming) {
            String userId = id;
            return appDatabase.myListChannelDao().getChannel(channelName)
                    .map(myListChannel -> {
                        myListChannel.updateAt = new Date();
//                        if (otherUserId.equals(loggedInUser.getUserId())) {
//                            myListChannel.unreadMsgCount = 0;
//                        } else {
                        myListChannel.unreadMsgCount += 1;
//                        }
                        myListChannel.message = message;
                        appDatabase.myListChannelDao().updateChannel(myListChannel);
                        return Single.just(object);
                    })
                    .onErrorReturn(t -> {
                        //New Chat so insert new record
                        FRLogger.msg("My New Chat so insert new record");
                        MyListChannel myListChannel = new MyListChannel(channelName);
                        myListChannel.createdAt = new Date();
                        myListChannel.otherUserId = userId;
                        myListChannel.unreadMsgCount = 0;
                        myListChannel.updateAt = new Date();
                        myListChannel.message = message;
                        appDatabase.myListChannelDao().addChannel(myListChannel);
                        return Single.just(object);
                    });
        } else {
            String userId = id;
            return appDatabase.anonListDao().getChannel(channelName)
                    .map(anonListChannel -> {
                        anonListChannel.updateAt = new Date();
                        anonListChannel.unreadMsgCount += 1;
                        anonListChannel.message = message;
                        appDatabase.anonListDao().updateChannel(anonListChannel);
                        return Single.just(object);
                    })
                    .onErrorReturn(t -> {
                        //New Chat so insert new record
                        FRLogger.msg("Anon New Chat so insert new record");
                        AnonListChannel anonListChannel = new AnonListChannel(channelName);
                        anonListChannel.createdAt = new Date();
                        anonListChannel.otherUserId = userId;
                        anonListChannel.unreadMsgCount = 0;
                        AnonymousUser anonymousUser = getRandomName();
                        anonListChannel.anonymisedUserName = anonymousUser.name;
                        anonListChannel.anonymisedUserImg = anonymousUser.imageRes;
                        anonListChannel.message = message;
                        anonListChannel.updateAt = new Date();
                        appDatabase.anonListDao().addChannel(anonListChannel);
                        return Single.just(object);
                    });
        }
    }

    private MessageEvent broadcastFeedEvent(FeedNewMessageResponse feedNewMessageResponse) {
        boolean isIncoming = isIncoming(feedNewMessageResponse.getChannel());
        MessageEvent messageEvent = new MessageEvent();
        messageEvent.setEventType(ChatDataConstants.SOCKET_EVENT_TYPE.FEED.id);
        messageEvent.setIncoming(isIncoming);
        messageEvent.setChannel(feedNewMessageResponse.getMessageFrom().getId());
        messageEvent.setTimestamp(new Date());
        messageEvent.setMessage(feedNewMessageResponse.getMessage());
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

//    private Single<MessageEvent> getUserDetails(MessageEvent data) {
//        FRLogger.msg("chat message getUserDetails " + data.getUserId());
//        String userId = data.getUserId();
//        if (!usersList.containsKey(userId)) {
//            return appDatabase.userDao().getUser(userId)
//                    .map(user -> {
//                        usersList.put(userId, user);
//                        FRLogger.msg("user " + user);
//                        data.setUser(user);
//                        return data;
//                    });
//        } else {
//            User user = usersList.get(userId);
//            data.setUser(user);
//            return Single.just(data);
//        }
//    }


    //TODO move this to ViewModel
    @Override
    public Single<List<IncomingListItemUiState>> getAnonListChannels() {
        return appDatabase.anonListDao().getAllChannels()
                .toObservable()
                .flatMapIterable(myListChannels -> myListChannels)
                .flatMap(myListChannel -> {
                    IncomingListItemUiState incomingListItemUiState = new IncomingListItemUiState();
                    incomingListItemUiState.setLastMessage(myListChannel.message);
                    incomingListItemUiState.setAnonymisedUserName(myListChannel.anonymisedUserName);
                    incomingListItemUiState.setAnonymisedUserImg(myListChannel.anonymisedUserImg);
                    incomingListItemUiState.setUserId(myListChannel.otherUserId);
                    incomingListItemUiState.setTimeStamp(myListChannel.updateAt);
                    incomingListItemUiState.setChannelName(myListChannel.channelName);
                    incomingListItemUiState.setImageUrl("");
                    return Observable.just(incomingListItemUiState);
                })
                .flatMapSingle(incomingListItemUiState -> {
//                    addChatEvent(incomingListItemUiState.userId, isIncoming(incomingListItemUiState.channelName), null);
                    return Single.just(incomingListItemUiState);
                })
                .flatMapSingle(this::getUserDetails)
                .toList();
    }

    //TODO move this to ViewModel
    @Override
    public Single<List<OutgoingListItemUiState>> getMyListChannels() {
        return appDatabase.myListChannelDao().getAllChannels()
                .toObservable()
                .flatMapIterable(myListChannels -> myListChannels)
                .flatMap(myListChannel -> {
                    OutgoingListItemUiState outgoingListItemUiState = new OutgoingListItemUiState();
                    outgoingListItemUiState.setLastMessage(myListChannel.message);
                    outgoingListItemUiState.setUserId(myListChannel.otherUserId);
                    outgoingListItemUiState.setTimeStamp(myListChannel.updateAt);
                    outgoingListItemUiState.setChannelName(myListChannel.channelName);
                    outgoingListItemUiState.setImageUrl("");
                    return Observable.just(outgoingListItemUiState);
                })
                .flatMapSingle(outgoingListItemUiState -> {
//                    addChatEvent(outgoingListItemUiState.userId, isIncoming(outgoingListItemUiState.channelName), null);
                    return Single.just(outgoingListItemUiState);
                })
                .flatMapSingle(this::getUserDetails)
                .toList();
    }

//    private Single<OutgoingListItemUiState> getMessage(OutgoingListItemUiState outgoingListItemUiState) {
//        long messageId = outgoingListItemUiState.messageId;
//        return appDatabase.messageDao().getMessage(messageId)
//                .flatMap(message -> {
//                    outgoingListItemUiState.setLastMessage(message.message);
//                    return Single.just(outgoingListItemUiState);
//                });
//    }

    private Single<IncomingListItemUiState> getUserDetails(IncomingListItemUiState data) {
        FRLogger.msg("OutgoingListItemUiState getUserDetails " + data.getUserId());
        String userId = data.getUserId();
        if (userId.equals(loggedInUser.getUserId())) {
            User user = new User();
            FRLogger.msg("user ID " + loggedInUser.getUserId());
            user.userId = loggedInUser.getUserId();
            user.phoneNumber = loggedInUser.getCountryCode() + "-" + loggedInUser.getPhoneNumber();
            user.cleanedPhoneNumber = loggedInUser.getPhoneNumber();
            user.displayName = loggedInUser.getUserName();
            data.setUser(user);
            return Single.just(data);
        } else if (!usersList.containsKey(userId)) {
            return appDatabase.userDao().getUser(userId)
                    .map(user -> {
                        usersList.put(userId, user);
                        FRLogger.msg("user " + user);
                        data.setUser(user);
                        return data;
                    })
                    .onErrorReturn(t -> data);
        } else {
            User user = usersList.get(userId);
            data.setUser(user);
            return Single.just(data);
        }
    }

    private Single<OutgoingListItemUiState> getUserDetails(OutgoingListItemUiState data) {
        FRLogger.msg("OutgoingListItemUiState getUserDetails " + data.getUserId());
        String userId = data.getUserId();
        if (userId.equals(loggedInUser.getUserId())) {
            User user = new User();
            FRLogger.msg("user ID " + loggedInUser.getUserId());
            user.userId = loggedInUser.getUserId();
            user.phoneNumber = loggedInUser.getCountryCode() + "-" + loggedInUser.getPhoneNumber();
            user.cleanedPhoneNumber = loggedInUser.getPhoneNumber();
            user.displayName = loggedInUser.getUserName();
            data.setUser(user);
            return Single.just(data);
        } else if (!usersList.containsKey(userId)) {
            return appDatabase.userDao().getUser(userId)
                    .map(user -> {
                        usersList.put(userId, user);
                        FRLogger.msg("user " + user);
                        data.setUser(user);
                        return data;
                    })
                    .onErrorReturn(t -> data);
        } else {
            User user = usersList.get(userId);
            data.setUser(user);
            return Single.just(data);
        }
    }

    @Override
    public Single<List<MyListChannel>> getMyListChannelList() {
        return appDatabase.myListChannelDao().getAllChannels();
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
        if (event.equalsIgnoreCase(ChatDataConstants.INITIATE_CHAT)) {
            Disposable disposable;
            disposable = Observable.just(data)
                    .map(JSONObject::toString)
                    .map(s -> gson.fromJson(s, FeedNewMessageResponse.class))
                    .flatMapSingle(this::addChannelToDb)
                    .map(feedNewMessageResponse -> {
                        addChatEvent(feedNewMessageResponse.getMessageFrom().getId(), isIncoming(feedNewMessageResponse.getChannel()), null);
                        return feedNewMessageResponse;
                    })
                    .map(this::broadcastFeedEvent)
//                    .flatMapSingle(this::getUserDetails)
                    .map(this::postMessageEvent)
                    .compose(SchedulerUtils.ioToMainObservableScheduler())
                    .subscribe(feedNewMessageResponse1 -> FRLogger.msg("success " + feedNewMessageResponse1), throwable -> FRLogger.msg("failure " + throwable.getMessage()));
            disposables.add(disposable);
        } else if (event.equalsIgnoreCase(ChatDataConstants.MESSAGE)) {
            Disposable disposable;
            disposable = Observable.just(data)
                    .map(JSONObject::toString)
                    .map(s -> gson.fromJson(s, MessageResponse.class))
                    .flatMapSingle(this::addChannelToDb)
                    .map(this::addMessageToDb)
                    .map(this::broadcastMessageEvent)
//                    .flatMapSingle(this::getUserDetails)
                    .map(this::postMessageEvent)
                    .compose(SchedulerUtils.ioToMainObservableScheduler())
                    .subscribe(chatMessage -> FRLogger.msg("success " + chatMessage), throwable -> FRLogger.msg("failure " + throwable.getMessage()));
            disposables.add(disposable);
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

    @Override
    public void onAppClosed() {
//        socketManager.clearAllSockets();

    }
}
