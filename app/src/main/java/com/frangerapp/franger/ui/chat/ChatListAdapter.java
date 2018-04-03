package com.frangerapp.franger.ui.chat;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.frangerapp.franger.R;
import com.frangerapp.franger.databinding.IncomingChatListItemBinding;
import com.frangerapp.franger.databinding.OutgoingChatListItemBinding;
import com.frangerapp.franger.domain.user.model.LoggedInUser;
import com.frangerapp.franger.viewmodel.chat.ChatListItemUiState;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pavanm on 18/03/18.
 */

public class ChatListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context context;
    private List<ChatListItemUiState> items = new ArrayList<>();

    private ChatListItemUiState.ChatItemClickHandler handler;

    private LoggedInUser loggedInUser;

    private int ITEM_ME = 1;
    private int ITEM_OTHER = 2;

    public ChatListAdapter(Context context, LoggedInUser loggedInUser) {
        this.context = context;
        this.loggedInUser = loggedInUser;
    }

    public void setItems(List<ChatListItemUiState> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_ME) {
            return IncomingChatViewHolder.create(LayoutInflater.from(parent.getContext()), parent);
        } else {
            return OutgoingChatViewHolder.create(LayoutInflater.from(parent.getContext()), parent);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatListItemUiState model = items.get(position);
        if (holder.getItemViewType() == ITEM_ME) {
            ((IncomingChatViewHolder) holder).bind(model);
            ((IncomingChatViewHolder) holder).binding.getRoot().setOnClickListener(v -> {
                if (handler != null)
                    handler.onItemClick(position, model);
            });
        } else {
            ((OutgoingChatViewHolder) holder).bind(model);
            ((OutgoingChatViewHolder) holder).binding.getRoot().setOnClickListener(v -> {
                if (handler != null)
                    handler.onItemClick(position, model);
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public ChatListItemUiState getItem(int position) {
        return items.get(position);
    }

    public void addAll(List<ChatListItemUiState> items) {
        this.items.addAll(items);
    }

    public void clear() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.size() == 0;
    }

    @Override
    public int getItemViewType(int position) {
        ChatListItemUiState model = items.get(position);
        if (loggedInUser.getUserId().equalsIgnoreCase(model.getUserId())) {
            return ITEM_ME;
        } else {
            return ITEM_OTHER;
        }
    }

    public ChatListItemUiState.ChatItemClickHandler getHandler() {
        return handler;
    }

    public void setHandler(ChatListItemUiState.ChatItemClickHandler handler) {
        this.handler = handler;
    }

    static class IncomingChatViewHolder extends RecyclerView.ViewHolder {
        private final IncomingChatListItemBinding binding;

        static IncomingChatViewHolder create(LayoutInflater inflater, ViewGroup parent) {
            IncomingChatListItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.incoming_chat_list_item, parent, false);
            return new IncomingChatViewHolder(binding);
        }

        IncomingChatViewHolder(IncomingChatListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ChatListItemUiState item) {
            binding.setVm(item);
            binding.executePendingBindings();
        }
    }

    static class OutgoingChatViewHolder extends RecyclerView.ViewHolder {
        private final OutgoingChatListItemBinding binding;

        static OutgoingChatViewHolder create(LayoutInflater inflater, ViewGroup parent) {
            OutgoingChatListItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.outgoing_chat_list_item, parent, false);
            return new OutgoingChatViewHolder(binding);
        }

        OutgoingChatViewHolder(OutgoingChatListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ChatListItemUiState item) {
            binding.setVm(item);
            binding.executePendingBindings();
        }
    }
}
