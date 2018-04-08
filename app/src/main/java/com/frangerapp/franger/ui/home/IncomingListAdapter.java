package com.frangerapp.franger.ui.home;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.frangerapp.franger.R;
import com.frangerapp.franger.databinding.IncomingChannelListItemBinding;
import com.frangerapp.franger.domain.user.model.LoggedInUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class IncomingListAdapter extends RecyclerView.Adapter<IncomingListAdapter.IncomingChannelViewHolder> {

    private Context context;
    private List<IncomingListItemUiState> items = new ArrayList<>();

    private IncomingListItemUiState.IncomingGroupItemClickHandler handler;

    private LoggedInUser loggedInUser;

    private int ITEM_CHANNEL = 1;

    public IncomingListAdapter(Context context, LoggedInUser loggedInUser) {
        this.context = context;
        this.loggedInUser = loggedInUser;
    }

    @NonNull
    @Override
    public IncomingChannelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return IncomingChannelViewHolder.create(LayoutInflater.from(parent.getContext()), parent);
    }

    @Override
    public void onBindViewHolder(@NonNull IncomingChannelViewHolder holder, int position) {
        IncomingListItemUiState model = items.get(position);
        holder.bind(model);
        holder.binding.getRoot().setOnClickListener(v -> {
            if (handler != null)
                handler.onItemClick(model);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return ITEM_CHANNEL;
    }

    public IncomingListItemUiState getItem(int position) {
        return items.get(position);
    }

    public void addAll(List<IncomingListItemUiState> items) {
        this.items.addAll(items);
    }

    public void clear() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.size() == 0;
    }

    public void setHandler(@NotNull IncomingListItemUiState.IncomingGroupItemClickHandler handler) {
        this.handler = handler;
    }


    static class IncomingChannelViewHolder extends RecyclerView.ViewHolder {
        private final IncomingChannelListItemBinding binding;

        static IncomingChannelViewHolder create(LayoutInflater inflater, ViewGroup parent) {
            IncomingChannelListItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.incoming_channel_list_item, parent, false);
            return new IncomingChannelViewHolder(binding);
        }

        IncomingChannelViewHolder(IncomingChannelListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(IncomingListItemUiState item) {
            binding.setVm(item);
            binding.executePendingBindings();
        }
    }
}
