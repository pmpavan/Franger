package com.frangerapp.franger.ui.home;

import android.databinding.ObservableArrayList;

import java.util.List;

public class IncomingListUiState {

    public interface ActionClickHandler {

        void onItemClick(IncomingListUiState chatListUiState);
    }

    private ObservableArrayList<IncomingListItemUiState> items;


    public IncomingListUiState() {
        items = new ObservableArrayList<>();
    }

    public ObservableArrayList<IncomingListItemUiState> getItems() {
        return items;
    }

    public void update(List<IncomingListItemUiState> stateList) {
        items.clear();
        items.addAll(stateList);
    }
}
