package com.frangerapp.franger.ui;

import android.databinding.BindingAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.frangerapp.franger.BR;
import com.frangerapp.franger.ui.chat.ChatListAdapter;
import com.frangerapp.franger.ui.util.RecyclerBindingAdapter;
import com.frangerapp.franger.viewmodel.chat.ChatListItemUiState;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pavan on 21/01/18.
 */

public class BaseBindingAdapters {

    @BindingAdapter("android:visibility")
    public static void setVisibility(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("android:visibility")
    public static void setVisibility(View view, int viewVisibility) {
        view.setVisibility(viewVisibility);
    }

    @BindingAdapter("android:onClick")
    public static void setClickListener(View view, ClickHandler handler) {
        view.setOnClickListener(view1 -> handler.onClick());
    }

    public interface ClickHandler {
        void onClick();
    }

    public interface ItemClickHandler<T> {
        void onItemClick(int position, T item);
    }

    @BindingAdapter("ellipsize")
    public static void setEllipsize(TextView textView, TextUtils.TruncateAt truncateAt) {
        textView.setEllipsize(truncateAt);
    }

    @BindingAdapter(value = {"adapter", "listitem", "onItemClick"})
    public static <T> void setAdapter(RecyclerView recyclerView, List<T> listItemViewModels, int layoutId, ItemClickHandler handler) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        if (listItemViewModels == null) {
            listItemViewModels = new ArrayList<>();
        }
        RecyclerBindingAdapter<T> adapter = new RecyclerBindingAdapter<T>(layoutId, BR.vm, listItemViewModels);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((position, item) -> {
            if (handler != null)
                handler.onItemClick(position, item);
        });

    }

    @BindingAdapter({"chat_items"})
    public static void setAdapter(RecyclerView recyclerView, ArrayList items) {
        ChatListAdapter adapter = (ChatListAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.clear();
            if (items != null)
                adapter.addAll(items);
            adapter.notifyDataSetChanged();
        }
    }

}
