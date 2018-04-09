package com.frangerapp.franger.viewmodel.home.eventbus;

import com.frangerapp.franger.domain.chat.model.ChatContact;
import com.frangerapp.franger.viewmodel.BaseEvent;

public class OutgoingListEvent extends BaseEvent {

    private ChatContact contact;
    private boolean isIncoming;
    private String channelName;

    public ChatContact getContact() {
        return contact;
    }

    public void setContact(ChatContact contact) {
        this.contact = contact;
    }

    public boolean isIncoming() {
        return isIncoming;
    }

    public void setIncoming(boolean incoming) {
        isIncoming = incoming;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
}
