package com.frangerapp.franger.domain.chat.util;

/**
 * Created by pavanm on 16/03/18.
 */

public class ChatDataConstants {


    public static final String SOCKET_URL = "socket";

    public static final String INITIATE_CHAT = "initiate_chat";
    public static final String JOIN = "join";
    public static final String MESSAGE = "message";

    public enum SOCKET_EVENT_TYPE {
        FEED(1),
        MESSAGE(2);

        public int id;

        SOCKET_EVENT_TYPE(int id) {
            this.id = id;
        }
    }
}
