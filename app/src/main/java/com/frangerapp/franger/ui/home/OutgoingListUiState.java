package com.frangerapp.franger.ui.home;

import android.databinding.ObservableArrayList;

import java.util.List;

public class OutgoingListUiState {

    public interface ActionClickHandler {

        void onItemClick(OutgoingListUiState chatListUiState);
    }

    private ObservableArrayList<OutgoingListItemUiState> items;


    public OutgoingListUiState() {
        items = new ObservableArrayList<>();
    }

    public ObservableArrayList<OutgoingListItemUiState> getItems() {
        return items;
    }

    public void update(List<OutgoingListItemUiState> stateList) {
        items.clear();
        items.addAll(stateList);
    }
}
