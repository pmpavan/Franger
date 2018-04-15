package com.frangerapp.franger.ui.home;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import java.util.List;

public class IncomingChannelListDiffUtil extends DiffUtil.Callback {

    List<IncomingListItemUiState> oldPersons;
    List<IncomingListItemUiState> newPersons;

    public IncomingChannelListDiffUtil(List<IncomingListItemUiState> newPersons, List<IncomingListItemUiState> oldPersons) {
        this.newPersons = newPersons;
        this.oldPersons = oldPersons;
    }


    @Override
    public int getOldListSize() {
        return oldPersons.size();
    }

    @Override
    public int getNewListSize() {
        return newPersons.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldPersons.get(oldItemPosition).channelName.equals(newPersons.get(newItemPosition).channelName);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldPersons.get(oldItemPosition).equals(newPersons.get(newItemPosition));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        //you can return particular field for changed item.
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
