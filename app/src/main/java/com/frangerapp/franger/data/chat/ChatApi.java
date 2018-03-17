package com.frangerapp.franger.data.chat;

import android.content.Context;
import android.support.annotation.NonNull;

import com.franger.socket.SocketIOCallbacks;
import com.franger.socket.socketio.SocketIOManager;
import com.frangerapp.franger.data.BaseApi;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by pavanm on 14/03/18.
 */

public class ChatApi extends BaseApi {

    private static final String TAG = "ChatApi";

    private Context context;
    private Gson gson;
    private SocketIOManager socketIOManager;

    public ChatApi(@NonNull Context context, Gson gson, SocketIOManager socketIOManager) {
        this.context = context;
        this.gson = gson;
        this.socketIOManager = socketIOManager;
    }

}
