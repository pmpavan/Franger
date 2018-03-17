package com.frangerapp.franger.domain.chat.util;

import com.frangerapp.franger.domain.chat.model.ChatBody;
import com.frangerapp.franger.domain.chat.model.ChatData;
import com.frangerapp.franger.data.common.util.DataConstants;
import com.frangerapp.franger.domain.chat.model.ChatMessage;
import com.google.gson.Gson;

/**
 * Created by pavanm on 14/03/18.
 */

public class ChatDataUtil {

    public static String getFeedChannelName(String userId) {
        ChatBody chatBody = new ChatBody();
        ChatData data = new ChatData();

        chatBody.setName("feed_" + userId);
        chatBody.setType("feed");
        data.setUserId(userId);
        chatBody.setData(data);

        Gson gson = new Gson();
        return gson.toJson(chatBody, ChatBody.class);
    }

    public static String getDomainName() {
        return DataConstants.PROTOCOL + DataConstants.BASE_DOMAIN_URL + "/" + ChatDataConstants.SOCKET_URL;
    }

    public static String getChatChannelName(String fromUserId, String toUserId,Gson gson) {
        ChatBody chatBody = new ChatBody();
        ChatData data = new ChatData();

        chatBody.setName("chat_" + fromUserId + "_" + toUserId);
        chatBody.setType("chat");
        data.setFromId(fromUserId);
        data.setToId(toUserId);
        chatBody.setData(data);

        return gson.toJson(chatBody, ChatBody.class);
    }

    public static String getChatMessageBody(String chatChannelId, String message, Gson gson) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChannel(chatChannelId);
        chatMessage.setMessage(message);
        return gson.toJson(message);
    }
}
