package com.frangerapp.franger.data.chat;

import android.content.Context;
import android.support.annotation.NonNull;

import com.franger.socket.socketio.SocketManager;
import com.frangerapp.franger.data.BaseApi;
import com.google.gson.Gson;

/**
 * Created by pavanm on 14/03/18.
 */

public class ChatApi extends BaseApi {

    private static final String TAG = "ChatApi";

    private Context context;
    private Gson gson;
    private SocketManager socketManager;

    public ChatApi(@NonNull Context context, Gson gson, SocketManager socketManager) {
        this.context = context;
        this.gson = gson;
        this.socketManager = socketManager;
    }

}
