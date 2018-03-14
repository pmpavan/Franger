package com.frangerapp.franger.viewmodel.contact.eventbus;

import com.frangerapp.franger.viewmodel.BaseEvent;
import com.frangerapp.franger.viewmodel.contact.ContactListItemViewModel;

/**
 * Created by pavanm on 10/03/18.
 */

public class ContactEvent extends BaseEvent {
    private ContactListItemViewModel contactObj;

    public void setContactObj(ContactListItemViewModel contactObj) {
        this.contactObj = contactObj;
    }

    public ContactListItemViewModel getContactObj() {
        return contactObj;
    }
}
