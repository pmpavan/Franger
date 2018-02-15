package com.frangerapp.franger.data.profile.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pavan on 12/02/18.
 */

public class ContactSyncResponse {

    @SerializedName(("joined"))
    @Expose
    private List<Joined> joinedList = new ArrayList<>();

    public ContactSyncResponse() {

    }


    public List<Joined> getJoinedList() {
        return joinedList;
    }

    public void setJoinedList(List<Joined> joinedList) {
        this.joinedList = joinedList;
    }

    @Override
    public String toString() {
        return "ContactSyncResponse{" +
                "joinedList=" + joinedList +
                '}';
    }
}
