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
import com.frangerapp.franger.domain.chat.model.MessageResponseWrapper;
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
        if (userId != null && !channelsBeingListened.contains(channelName)) {
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
    public long sendMessage(String receivingUserId, boolean isIncoming, String message) {

        //TODO add connect to feed if not connected code
        String channelName = getChatName(receivingUserId, isIncoming);
        if (!channelsBeingListened.contains(channelName)) {
            addChatEvent(receivingUserId, isIncoming, message);
        } else {
            sendMessage(channelName, message);
        }
        handleSentMessageInDb(channelName, receivingUserId, message);
        return addMessageToDb(channelName, message, loggedInUser.getUserId());
    }

    private MessageEvent broadcastSentMessageEvent(String channelName, String message, String receivingUserId, AnonymousUser anonymousUser) {
        boolean isIncoming = isIncoming(channelName);
        MessageEvent messageEvent = new MessageEvent();
        messageEvent.setEventType(ChatDataConstants.SOCKET_EVENT_TYPE.MESSAGE.id);
        messageEvent.setIncoming(isIncoming);
        messageEvent.setChannel(channelName);
        messageEvent.setTimestamp(new Date());
        messageEvent.setMessage(message);
        messageEvent.setUserId(receivingUserId);
        messageEvent.setSentMessage(true);
        messageEvent.setAnonymisedUserImg(anonymousUser.imageRes);
        messageEvent.setAnonymisedUserName(anonymousUser.name);
        return messageEvent;
    }

    private void handleSentMessageInDb(String channelName, String userId, String message) {
        Disposable disposable = addChannelToDb(channelName, userId, message)
                .map(messageResponseWrapper -> broadcastSentMessageEvent(messageResponseWrapper.getChannel(), messageResponseWrapper.getMessage(), messageResponseWrapper.getFromId(), messageResponseWrapper.getAnonymousUser()))
                .flatMap(this::getUserDetails)
                .map(this::postMessageEvent)
                .toObservable()
                .compose(SchedulerUtils.ioToMainObservableScheduler())
                .subscribe(messageEvent -> FRLogger.msg("success " + messageEvent), throwable -> FRLogger.msg("failure add to channel " + throwable.getMessage()));
        disposables.add(disposable);

    }

    private Single<MessageResponseWrapper> addChannelToDb(String channelName, String fromId, String message) {
        MessageResponseWrapper wrapper = MessageResponseWrapper.builder()
                .setChannel(channelName)
                .setFromId(fromId)
                .setMessage(message);
        return (Single<MessageResponseWrapper>) addChannelToDb(wrapper, channelName, fromId, message, true);
    }

    private long addMessageToDb(@NotNull String channelName, String msg, String fromId) {
        Message message = new Message();
        message.userId = fromId;
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

    private Single<MessageResponseWrapper> addChannelToDb(FeedNewMessageResponse feedNewMessageResponse) {
        MessageResponseWrapper wrapper = MessageResponseWrapper.builder()
                .setChannel(feedNewMessageResponse.getChannel())
                .setFromId(feedNewMessageResponse.getMessageFrom().getId())
                .setMessage(feedNewMessageResponse.getMessage());
        return (Single<MessageResponseWrapper>) addChannelToDb(wrapper, feedNewMessageResponse.getChannel(), feedNewMessageResponse.getMessageFrom().getId(), feedNewMessageResponse.getMessage(), false)
                .blockingGet();
    }

    private Single<MessageResponseWrapper> addChannelToDb(MessageResponse messageResponse) {
        MessageResponseWrapper wrapper = MessageResponseWrapper.builder()
                .setChannel(messageResponse.getChannel())
                .setFromId(messageResponse.getData().getFromId())
                .setMessage(messageResponse.getMessage());
        return (Single<MessageResponseWrapper>) addChannelToDb(wrapper, messageResponse.getChannel(), messageResponse.getData().getFromId(), messageResponse.getMessage(), false)
                .blockingGet();
    }

    private Single addChannelToDb(MessageResponseWrapper wrapper, String channelName, String otherUserId, String message, boolean isSentMessage) {
        boolean isIncoming = isIncoming(channelName);
        String id = otherUserId;
        if (otherUserId != null && !otherUserId.equals(loggedInUser.getUserId())) {
            id = getUserId(channelName);
        }
        FRLogger.msg("addChannelToDb " + channelName + " " + otherUserId + " " + message);
        if (!isIncoming) {
            String userId = id;
            return appDatabase.myListChannelDao().getChannel(channelName)
                    .map(myListChannel -> {
                        myListChannel.updateAt = new Date();
                        if (isSentMessage) {
                            myListChannel.unreadMsgCount = 0;
                        } else {
                            myListChannel.unreadMsgCount += 1;
                        }
                        myListChannel.message = message;
                        appDatabase.myListChannelDao().updateChannel(myListChannel);
                        return Single.just(wrapper);
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
                        return Single.just(wrapper);
                    });
        } else {
            String userId = id;
            return appDatabase.anonListDao().getChannel(channelName)
                    .map(anonListChannel -> {
                        anonListChannel.updateAt = new Date();
                        if (isSentMessage) {
                            anonListChannel.unreadMsgCount = 0;
                        } else {
                            anonListChannel.unreadMsgCount += 1;
                        }
                        anonListChannel.message = message;
                        appDatabase.anonListDao().updateChannel(anonListChannel);
                        return Single.just(wrapper);
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
                        wrapper.setAnonymousUser(anonymousUser);
                        return Single.just(wrapper);
                    });
        }
    }

    private MessageEvent broadcastMessageEvent(MessageResponseWrapper chatMessage, ChatDataConstants.SOCKET_EVENT_TYPE type) {
        MessageEvent messageEvent = new MessageEvent();
        messageEvent.setEventType(type.id);
        messageEvent.setMessage(chatMessage.getMessage());
        messageEvent.setChannel(chatMessage.getChannel());
        messageEvent.setUserId(chatMessage.getFromId());
        messageEvent.setTimestamp(new Date());
        messageEvent.setMessageId(chatMessage.getMessageId());
        FRLogger.msg("message Event MessageResponse " + messageEvent);
        return messageEvent;
    }

    private MessageResponseWrapper addMessageToDb(MessageResponseWrapper chatMessage) {
        long messageId = addMessageToDb(chatMessage.getChannel(), chatMessage.getMessage(), chatMessage.getFromId());
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
        String userId = data.getUserId();
        if (!usersList.containsKey(userId)) {
            return appDatabase.userDao().getUser(userId)
                    .map(user -> {
                        usersList.put(userId, user);
                        FRLogger.msg("user " + user);
                        data.setUser(user);
                        return data;
                    });
        } else {
            User user = usersList.get(userId);
            data.setUser(user);
            return Single.just(data);
        }
    }


    //TODO move this to ViewModel
    @Override
    public Single<List<IncomingListItemUiState>> getAnonListChannels() {
        return appDatabase.anonListDao().getAllChannels()
                .toObservable()
                .concatMapIterable(myListChannels -> myListChannels)
                .concatMap(myListChannel -> {
                    IncomingListItemUiState incomingListItemUiState = new IncomingListItemUiState();
                    incomingListItemUiState.setLastMessage(myListChannel.message);
                    incomingListItemUiState.setAnonymisedUserName(myListChannel.anonymisedUserName);
                    incomingListItemUiState.setAnonymisedUserImg(myListChannel.anonymisedUserImg);
                    incomingListItemUiState.setUserId(myListChannel.otherUserId);
                    incomingListItemUiState.setTimeStamp(myListChannel.updateAt);
                    incomingListItemUiState.setUnreadCount(myListChannel.unreadMsgCount);
                    incomingListItemUiState.setChannelName(myListChannel.channelName);
                    incomingListItemUiState.setImageUrl("");
                    return Observable.just(incomingListItemUiState);
                })
//                .flatMapSingle(incomingListItemUiState -> {
//                    addChatEvent(incomingListItemUiState.userId, isIncoming(incomingListItemUiState.channelName), null);
//                    return Single.just(incomingListItemUiState);
//                })
                .flatMapSingle(this::getUserDetails)
                .toList();
    }

    //TODO move this to ViewModel
    @Override
    public Single<List<OutgoingListItemUiState>> getMyListChannels() {
        return appDatabase.myListChannelDao().getAllChannels()
                .toObservable()
                .concatMapIterable(myListChannels -> myListChannels)
                .concatMap(myListChannel -> {
                    OutgoingListItemUiState outgoingListItemUiState = new OutgoingListItemUiState();
                    outgoingListItemUiState.setLastMessage(myListChannel.message);
                    outgoingListItemUiState.setUserId(myListChannel.otherUserId);
                    outgoingListItemUiState.setTimeStamp(myListChannel.updateAt);
                    outgoingListItemUiState.setUnreadCount(myListChannel.unreadMsgCount);
                    outgoingListItemUiState.setChannelName(myListChannel.channelName);
                    outgoingListItemUiState.setImageUrl("");
                    return Observable.just(outgoingListItemUiState);
                })
//                .flatMapSingle(outgoingListItemUiState -> {
//                    addChatEvent(outgoingListItemUiState.userId, isIncoming(outgoingListItemUiState.channelName), null);
//                    return Single.just(outgoingListItemUiState);
//                })
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

    private Single<IncomingListItemUiState> getUserDetails(IncomingListItemUiState incomingListItemUiState) {
        FRLogger.msg("IncomingListItemUiState getUserDetails " + incomingListItemUiState.getUserId());
        String userId = incomingListItemUiState.getUserId();
        if (userId == null) {
            return Single.just(incomingListItemUiState);
        } else if (userId.equals(loggedInUser.getUserId())) {
            User user = new User();
            user.userId = loggedInUser.getUserId();
            user.phoneNumber = loggedInUser.getCountryCode() + "-" + loggedInUser.getPhoneNumber();
            user.cleanedPhoneNumber = loggedInUser.getPhoneNumber();
            user.displayName = loggedInUser.getUserName();
            incomingListItemUiState.setUser(user);
            FRLogger.msg("getUserDetails " + user);
            return Single.just(incomingListItemUiState);
        } else if (!usersList.containsKey(userId)) {
            return appDatabase.userDao().getUser(userId)
                    .map(user -> {
                        usersList.put(userId, user);
                        FRLogger.msg("user " + user);
                        incomingListItemUiState.setUser(user);
                        FRLogger.msg("getUserDetails " + user);
                        return incomingListItemUiState;
                    })
                    .onErrorReturn(t -> {
                        FRLogger.msg("getUserDetails is null");
                        return incomingListItemUiState;
                    });
        } else {
            User user = usersList.get(userId);
            incomingListItemUiState.setUser(user);
            FRLogger.msg("getUserDetails " + user);
            return Single.just(incomingListItemUiState);
        }
    }

    private Single<OutgoingListItemUiState> getUserDetails(OutgoingListItemUiState data) {
        FRLogger.msg("OutgoingListItemUiState getUserDetails " + data.getUserId());
        String userId = data.getUserId();
        if (userId == null) {
            return Single.just(data);
        } else if (userId.equals(loggedInUser.getUserId())) {
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

    @Override
    public Single<AnonListChannel> blockChannel(String channelName, boolean isChannelBlocked) {
        return appDatabase.anonListDao().getChannel(channelName)
                .map(anonListChannel -> {
                    anonListChannel.isUserBlocked = isChannelBlocked;
                    appDatabase.anonListDao().updateChannel(anonListChannel);
                    return anonListChannel;
                })
                .onErrorReturn(t -> {
                    //New Chat so insert new record
                    FRLogger.msg("Anon New Chat so insert new record");
                    String userId = getUserId(channelName);
                    AnonListChannel anonListChannel = new AnonListChannel(channelName);
                    anonListChannel.createdAt = new Date();
                    anonListChannel.otherUserId = userId;
                    anonListChannel.unreadMsgCount = 0;
                    anonListChannel.isUserBlocked = isChannelBlocked;
                    AnonymousUser anonymousUser = getRandomName();
                    anonListChannel.anonymisedUserName = anonymousUser.name;
                    anonListChannel.anonymisedUserImg = anonymousUser.imageRes;
                    anonListChannel.message = "";
                    anonListChannel.updateAt = new Date();
                    appDatabase.anonListDao().addChannel(anonListChannel);
                    return anonListChannel;
                });

    }

    @Override
    public Single<MyListChannel> muteChannel(String channelName, boolean isChannelMuted) {
        return appDatabase.myListChannelDao().getChannel(channelName)
                .map(myListChannel -> {
                    myListChannel.isUserMuted = isChannelMuted;
                    appDatabase.myListChannelDao().updateChannel(myListChannel);
                    return myListChannel;
                })
                .onErrorReturn(t -> {
                    //New Chat so insert new record
                    FRLogger.msg("My New Chat so insert new record");
                    String userId = getUserId(channelName);
                    MyListChannel myListChannel = new MyListChannel(channelName);
                    myListChannel.createdAt = new Date();
                    myListChannel.otherUserId = userId;
                    myListChannel.unreadMsgCount = 0;
                    myListChannel.updateAt = new Date();
                    myListChannel.isUserMuted = isChannelMuted;
                    myListChannel.message = "";
                    appDatabase.myListChannelDao().addChannel(myListChannel);
                    return myListChannel;
                });
//                .toObservable()
//                .compose(SchedulerUtils.ioToMainObservableScheduler())
//                .subscribe(object -> FRLogger.msg("success " + object), throwable -> FRLogger.msg("failure mute to channel " + throwable.getMessage()));
    }

    @Override
    public void clearIncomingChannelChat(String channelName) {
        appDatabase.messageDao().removeChannelMessages(channelName);
        Disposable disposable = appDatabase.anonListDao().getChannel(channelName)
                .map(anonListChannel -> {
                    anonListChannel.message = "";
                    appDatabase.anonListDao().updateChannel(anonListChannel);
                    return anonListChannel;
                })
                .toObservable()
                .compose(SchedulerUtils.ioToMainObservableScheduler())
                .subscribe(object -> FRLogger.msg("success " + object), throwable -> FRLogger.msg("failure clear to channel " + throwable.getMessage()));
        disposables.add(disposable);
    }

    @Override
    public void clearOutgoingChannelChat(String channelName) {
        appDatabase.messageDao().removeChannelMessages(channelName);
        Disposable disposable = appDatabase.myListChannelDao().getChannel(channelName)
                .map(myListChannel -> {
                    myListChannel.message = "";
                    appDatabase.myListChannelDao().updateChannel(myListChannel);
                    return myListChannel;
                })
                .toObservable()
                .compose(SchedulerUtils.ioToMainObservableScheduler())
                .subscribe(object -> FRLogger.msg("success " + object), throwable -> FRLogger.msg("failure clear to channel " + throwable.getMessage()));
        disposables.add(disposable);
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
                        addChatEvent(feedNewMessageResponse.getFromId(), isIncoming(feedNewMessageResponse.getChannel()), null);
                        return feedNewMessageResponse;
                    })
                    .map(this::addMessageToDb)
                    .map(messageResponseWrapper -> broadcastMessageEvent(messageResponseWrapper, ChatDataConstants.SOCKET_EVENT_TYPE.FEED))
                    .flatMapSingle(this::getUserDetails)
                    .map(this::postMessageEvent)
                    .compose(SchedulerUtils.ioToMainObservableScheduler())
                    .subscribe(feedNewMessageResponse1 -> FRLogger.msg("success feed " + feedNewMessageResponse1), throwable -> FRLogger.msg("failure " + throwable.getMessage()));
            disposables.add(disposable);
        } else if (event.equalsIgnoreCase(ChatDataConstants.MESSAGE)) {
            Disposable disposable;
            disposable = Observable.just(data)
                    .map(JSONObject::toString)
                    .map(s -> gson.fromJson(s, MessageResponse.class))
                    .flatMapSingle(this::addChannelToDb)
                    .map(this::addMessageToDb)
                    .map(messageResponseWrapper -> broadcastMessageEvent(messageResponseWrapper, ChatDataConstants.SOCKET_EVENT_TYPE.MESSAGE))
                    .flatMapSingle(this::getUserDetails)
                    .map(this::postMessageEvent)
                    .compose(SchedulerUtils.ioToMainObservableScheduler())
                    .subscribe(chatMessage -> FRLogger.msg("success chat " + chatMessage), throwable -> FRLogger.msg("failure " + throwable.getMessage()));
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
        if (!disposables.isDisposed())
            disposables.dispose();
    }


}
