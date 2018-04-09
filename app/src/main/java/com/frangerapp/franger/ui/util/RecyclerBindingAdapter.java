package com.frangerapp.franger.ui.util;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.frangerapp.franger.R;
import com.frangerapp.franger.databinding.RecyclerViewLoadingLayoutBinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Pavan on 21/01/18.
 * <p>
 * Base adapter for RecyclerView
 * <p>
 * This adapter can be used in cases like
 * <p>
 * 1) A simple list with only one type of items.
 * 2) A simple list with loading states(NONE, LOADING, ALL_RESULTS_LOADED, UNKNOWN_ERROR)
 */

public class RecyclerBindingAdapter<T> extends RecyclerView.Adapter<RecyclerBindingAdapter.BindingHolder> {

    private int holderLayout, variableId;
    private List<T> items = new ArrayList<>();
    private OnItemClickListener<T> onItemClickListener;

    public static final int ITEM_TYPE_ITEM = 1;
    public static final int ITEM_TYPE_LOAD_MORE = 2;

    private Handler handler = new Handler();

    private LoadMoreState loadMoreState;

    public enum LoadMoreState {
        NONE, LOADING, ALL_RESULTS_LOADED, UNKNOWN_ERROR
    }

    public RecyclerBindingAdapter(int holderLayout, int variableId, List<T> items) {
        this.holderLayout = holderLayout;
        this.variableId = variableId;
        this.items = items;
        this.loadMoreState = LoadMoreState.NONE;
    }

    @Override
    public RecyclerBindingAdapter.BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BindingHolder viewHolder = null;
        if (viewType == ITEM_TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(holderLayout, parent, false);
            viewHolder = new BindingHolder(v);
        } else if (viewType == ITEM_TYPE_LOAD_MORE) {
            viewHolder = new LoadMoreViewHolder(LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.recycler_view_loading_layout, parent, false));
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerBindingAdapter.BindingHolder holder, int position) {
        if (holder.getItemViewType() == ITEM_TYPE_ITEM) {
            final T item = items.get(position);
            holder.getBinding().getRoot().setOnClickListener(v -> {
                if (onItemClickListener != null)
                    onItemClickListener.onItemClick(item);
            });
            holder.getBinding().setVariable(variableId, item);
        } else if (holder.getItemViewType() == ITEM_TYPE_LOAD_MORE) {
            LoadMoreViewHolder loadMoreViewHolder = (LoadMoreViewHolder) holder;
            loadMoreViewHolder.bindViewContent(loadMoreState);
        }
    }

    @Override
    public int getItemCount() {
        int itemCount = items.size();
        // If we have loadMoreState then add it.
        if (loadMoreState != LoadMoreState.NONE) {
            itemCount += 1;
        }
        return itemCount;
    }

    /**
     * Sets the LODE_MORE state.
     */
    public void setLoadMoreState(LoadMoreState loadMoreState) {
        this.loadMoreState = loadMoreState;
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                notifyDataSetChanged();
//            }
//        });
    }

    @Override
    public int getItemViewType(int position) {
        int itemViewType;
        // If position is more than ticket size - then it is the load more item.
        if (position < items.size()) {
            itemViewType = ITEM_TYPE_ITEM;
        } else {
            itemViewType = ITEM_TYPE_LOAD_MORE;
        }
        return itemViewType;
    }

    /**
     * Get all items in the adapter.
     */
    public List<T> getAllItems() {
        return items;
    }

    /**
     * Gets the item at the given position.
     * If the item at the position is LOAD_MORE the it return NULL.
     */
    @Nullable
    public T getItem(int position) {
        T item = null;
        if (getItemViewType(position) == ITEM_TYPE_ITEM) {
            item = items.get(position);
        }
        return item;
    }

    /**
     * Adds the item at the end of the list.
     */
    public void add(@Nullable T item) {
        items.add(item);
        notifyItemInserted(items.size());
    }

    /**
     * Adds the specified mItems at the end of the list.
     */
    public void addAll(@NonNull Collection<? extends T> items) {
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    /**
     * Inserts the specified item at the specified index in the list.
     */
    public void insert(@Nullable T item, int index) {
        this.items.add(index, item);
        notifyItemInserted(index);
    }

    /**
     * Removes the specified item from the list.
     */
    public void remove(@Nullable T item) {
        int index = this.items.indexOf(item);
        if (index != -1) {
            this.items.remove(item);
            notifyItemRemoved(index);
        }
    }

    /**
     * Removes the item at the specified index in the list.
     */
    public void remove(int index) {
        this.items.remove(index);
        notifyItemRemoved(index);
    }

    /**
     * Remove all elements from the list.
     */
    public void clear() {
        this.items.clear();
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public interface OnItemClickListener<T> {
        void onItemClick(T item);
    }

    public static class BindingHolder extends RecyclerView.ViewHolder {
        private ViewDataBinding binding;

        public BindingHolder(View v) {
            super(v);
            binding = DataBindingUtil.bind(v);
            binding.executePendingBindings();
        }

        public ViewDataBinding getBinding() {
            return binding;
        }
    }

    // VIEW HOLDER.
    private class LoadMoreViewHolder extends BindingHolder {

        private RecyclerViewLoadingLayoutBinding binding;

        LoadMoreViewHolder(final View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            itemView.setOnClickListener(v -> {
                // dummy on click.. will prevent onitemclick from being called
            });
        }

        void bindViewContent(@NonNull LoadMoreState loadMoreState) {
            switch (loadMoreState) {
                case NONE:
                    binding.loading.setVisibility(View.GONE);
                    binding.allResultsLoaded.setVisibility(View.GONE);
                    binding.message.setText("");
                    break;

                case LOADING:
                    binding.loading.setVisibility(View.VISIBLE);
                    binding.allResultsLoaded.setVisibility(View.GONE);
                    binding.message.setText("");
                    break;

                case ALL_RESULTS_LOADED:
                    binding.loading.setVisibility(View.GONE);
                    binding.allResultsLoaded.setVisibility(View.VISIBLE);
                    binding.message.setText("");
                    break;

                case UNKNOWN_ERROR:
                    binding.loading.setVisibility(View.GONE);
                    binding.allResultsLoaded.setVisibility(View.VISIBLE);
                    binding.message.setText(binding.message.getContext().getString(R.string.sorry_error_occurred));
            }
            binding.executePendingBindings();
        }
    }

}
