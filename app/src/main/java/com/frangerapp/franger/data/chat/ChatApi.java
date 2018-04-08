package com.frangerapp.franger.data.chat;

import android.content.Context;
import android.support.annotation.NonNull;

import com.franger.socket.socketio.SocketManager;
import com.frangerapp.franger.app.ChatStore;
import com.frangerapp.franger.data.BaseApi;
import com.frangerapp.franger.data.chat.model.AnonymousUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pavanm on 14/03/18.
 */

public class ChatApi extends BaseApi {

    private static final String TAG = "ChatApi";

    private Context context;
    private Gson gson;
    private SocketManager socketManager;
    private ChatStore chatStore;

    public ChatApi(@NonNull Context context, Gson gson, SocketManager socketManager, ChatStore chatStore) {
        this.context = context;
        this.gson = gson;
        this.socketManager = socketManager;
        this.chatStore = chatStore;
    }


    public ArrayList<AnonymousUser> getRandomNamesList() {
        String json = chatStore.getRandomNames(context, "");
        ArrayList<AnonymousUser> randomNames = new ArrayList<>();
        if (json.isEmpty()) {
            randomNames.add(new AnonymousUser("Superman"));
            randomNames.add(new AnonymousUser("Spiderman"));
            randomNames.add(new AnonymousUser("Green Lantern"));
            randomNames.add(new AnonymousUser("Nityananda"));
            randomNames.add(new AnonymousUser("Yogi ji"));
            randomNames.add(new AnonymousUser("Hardik"));
            randomNames.add(new AnonymousUser("Dhoni"));
            json = gson.toJson(randomNames);
            chatStore.storeRandomNames(context, json);
        } else {
            randomNames = gson.fromJson(json, new TypeToken<List<String>>() {
            }.getType());
        }
        return randomNames;
    }
}
