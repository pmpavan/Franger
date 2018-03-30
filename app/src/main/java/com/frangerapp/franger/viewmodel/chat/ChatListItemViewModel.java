package com.frangerapp.franger.viewmodel.chat;

import com.frangerapp.franger.viewmodel.user.UserBaseViewModel;

/**
 * Created by pavanm on 30/03/18.
 */

public class ChatListItemViewModel extends UserBaseViewModel {

    public enum CHAT_ITEM_TYPE {
        INCOMING(1),
        OUTGOING(2);
        public int type;

        CHAT_ITEM_TYPE(int type) {
            this.type = type;
        }
    }

    private CHAT_ITEM_TYPE type;
    private String message;

    public CHAT_ITEM_TYPE getType() {
        return type;
    }

    public void setType(CHAT_ITEM_TYPE type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
