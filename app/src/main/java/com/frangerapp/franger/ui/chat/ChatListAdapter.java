package com.frangerapp.franger.ui.chat;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.frangerapp.franger.R;
import com.frangerapp.franger.databinding.IncomingChatListItemBinding;
import com.frangerapp.franger.databinding.OutgoingChatListItemBinding;
import com.frangerapp.franger.viewmodel.chat.ChatListItemViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pavanm on 18/03/18.
 */

public class ChatListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<ChatListItemViewModel> models = new ArrayList<>();

    public ChatListAdapter(List<ChatListItemViewModel> models) {
        this.models = models;
    }

    public void setModels(List<ChatListItemViewModel> models) {
        this.models = models;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ChatListItemViewModel.CHAT_ITEM_TYPE.INCOMING.type) {
            return IncomingChatViewHolder.create(LayoutInflater.from(parent.getContext()), parent);
        } else {
            return OutgoingChatViewHolder.create(LayoutInflater.from(parent.getContext()), parent);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatListItemViewModel model = models.get(position);
        if (model.getType() == ChatListItemViewModel.CHAT_ITEM_TYPE.INCOMING) {
            ((IncomingChatViewHolder) holder).bind(model);
        } else {
            ((OutgoingChatViewHolder) holder).bind(model);
        }
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    @Override
    public int getItemViewType(int position) {
        ChatListItemViewModel model = models.get(position);
        return model.getType().type;
    }

    public static class IncomingChatViewHolder extends RecyclerView.ViewHolder {
        private final IncomingChatListItemBinding binding;

        static IncomingChatViewHolder create(LayoutInflater inflater, ViewGroup parent) {
            IncomingChatListItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.incoming_chat_list_item, parent, false);
            return new IncomingChatViewHolder(binding);
        }

        IncomingChatViewHolder(IncomingChatListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ChatListItemViewModel item) {
            binding.setVm(item);
            binding.executePendingBindings();
        }
    }

    public static class OutgoingChatViewHolder extends RecyclerView.ViewHolder {
        private final OutgoingChatListItemBinding binding;

        static OutgoingChatViewHolder create(LayoutInflater inflater, ViewGroup parent) {
            OutgoingChatListItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.outgoing_chat_list_item, parent, false);
            return new OutgoingChatViewHolder(binding);
        }

        OutgoingChatViewHolder(OutgoingChatListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ChatListItemViewModel item) {
            binding.setVm(item);
            binding.executePendingBindings();
        }
    }
}
