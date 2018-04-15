package com.frangerapp.franger.data.chat.model;

import com.frangerapp.franger.R;

public class AnonymousUser {

    public String name;
    public int imageRes = R.drawable.ic_sentiment_very_dissatisfied_black_24dp;

    public AnonymousUser(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageRes() {
        return imageRes;
    }

    public void setImageRes(int imageRes) {
        this.imageRes = imageRes;
    }

    @Override
    public String toString() {
        return "AnonymousUser{" +
                "name='" + name + '\'' +
                ", imageRes=" + imageRes +
                '}';
    }
}
