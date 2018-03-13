package com.frangerapp.franger.data.util;

import com.frangerapp.franger.data.common.util.DataConstants;
import com.frangerapp.franger.domain.user.model.socket.ChatBody;
import com.frangerapp.franger.domain.user.model.socket.Data;
import com.google.gson.Gson;

/**
 * Created by pavanm on 11/03/18.
 */

public class ChatUtils {

    public static String getFeedChannelName(String userId) {
        ChatBody chatBody = new ChatBody();
        Data data = new Data();

        chatBody.setName("feed_" + userId);
        chatBody.setType("feed");
        data.setUserId(userId);
        chatBody.setData(data);

        Gson gson = new Gson();
        return gson.toJson(chatBody, ChatBody.class);
    }

    public static String getDomainName() {
        return DataConstants.DOMAIN_URL + DataConstants.SOCKET_URL;
    }

    private static String getChatChannelName(String fromUserId, String toUserId) {
        ChatBody chatBody = new ChatBody();
        Data data = new Data();

        chatBody.setName("chat_" + fromUserId + "_" + toUserId);
        chatBody.setType("chat");
        data.setFromId(fromUserId);
        data.setToId(toUserId);
        chatBody.setData(data);

        Gson gson = new Gson();

        return gson.toJson(chatBody, ChatBody.class);
    }

}