package com.frangerapp.franger.ui.chat;

import android.databinding.ObservableArrayList;

import com.frangerapp.franger.viewmodel.chat.ChatListItemUiState;

import java.util.List;

/**
 * Created by pavanm on 31/03/18.
 */

public class ChatListUiState {

    public interface ActionClickHandler {

        void onItemClick();
    }

    private ObservableArrayList<ChatListItemUiState> items;


    public ChatListUiState() {
        items = new ObservableArrayList<>();
    }

    public ObservableArrayList<ChatListItemUiState> getItems() {
        return items;
    }

    public void update(List<ChatListItemUiState> stateList) {
        items.clear();
        items.addAll(stateList);
    }
}
