package com.frangerapp.franger.ui.home;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.frangerapp.franger.R;
import com.frangerapp.franger.databinding.MyListChannelListItemBinding;
import com.frangerapp.franger.domain.user.model.LoggedInUser;
import com.frangerapp.franger.ui.util.RecyclerBindingAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class OutgoingListAdapter extends RecyclerView.Adapter<OutgoingListAdapter.MyListChannelViewHolder> {

    private Context context;
    private List<OutgoingListItemUiState> items = new ArrayList<>();

    public OutgoingListItemUiState.OutgoingGroupItemClickHandler handler;

    private LoggedInUser loggedInUser;

    private int ITEM_CHANNEL = 1;

//    private RecyclerBindingAdapter.OnItemClickListener<OutgoingListItemUiState> onItemClickListener;

    public OutgoingListAdapter(Context context, LoggedInUser loggedInUser) {
        this.context = context;
        this.loggedInUser = loggedInUser;
    }

    @NonNull
    @Override
    public MyListChannelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return MyListChannelViewHolder.create(LayoutInflater.from(parent.getContext()), parent);
    }

    @Override
    public void onBindViewHolder(@NonNull MyListChannelViewHolder holder, int position) {
        OutgoingListItemUiState model = items.get(position);
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

    public OutgoingListItemUiState getItem(int position) {
        return items.get(position);
    }

    public void addAll(List<OutgoingListItemUiState> items) {
        this.items.addAll(items);
    }

    public void clear() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.size() == 0;
    }

    public void updateList(ArrayList<OutgoingListItemUiState> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new OutgoingChannelListDiffUtil(this.items, newList));
        diffResult.dispatchUpdatesTo(this);
        items.addAll(newList);
    }

    public void setHandler(@NotNull OutgoingListItemUiState.OutgoingGroupItemClickHandler handler) {
        this.handler = handler;
    }

//    public void setOnItemClickListener(RecyclerBindingAdapter.OnItemClickListener<OutgoingListItemUiState> onItemClickListener) {
//        this.onItemClickListener = onItemClickListener;
//    }


    static class MyListChannelViewHolder extends RecyclerView.ViewHolder {
        private final MyListChannelListItemBinding binding;

        static MyListChannelViewHolder create(LayoutInflater inflater, ViewGroup parent) {
            MyListChannelListItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.my_list_channel_list_item, parent, false);
            return new MyListChannelViewHolder(binding);
        }

        MyListChannelViewHolder(MyListChannelListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(OutgoingListItemUiState item) {
            binding.setVm(item);
            binding.executePendingBindings();
        }
    }
}
