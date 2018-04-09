package com.frangerapp.franger.ui;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.franger.mobile.logger.FRLogger;
import com.frangerapp.franger.BR;
import com.frangerapp.franger.app.util.DateUtils;
import com.frangerapp.franger.ui.chat.ChatListAdapter;
import com.frangerapp.franger.ui.home.IncomingListAdapter;
import com.frangerapp.franger.ui.home.OutgoingListAdapter;
import com.frangerapp.franger.ui.home.OutgoingListItemUiState;
import com.frangerapp.franger.ui.util.RecyclerBindingAdapter;
import com.frangerapp.franger.ui.util.UiUtils;
import com.frangerapp.ui.CircularTextDrawableView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        void onItemClick(T item);
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
        adapter.setOnItemClickListener((item) -> {
            if (handler != null)
                handler.onItemClick(item);
        });

    }

    @BindingAdapter({"chat_items"})
    public static void setChatAdapter(RecyclerView recyclerView, ArrayList items) {
        ChatListAdapter adapter = (ChatListAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.clear();
            if (items != null)
                adapter.addAll(items);
            adapter.notifyDataSetChanged();
        }
    }


    @BindingAdapter(value = {"outgoing_items", "onItemClick"})
    public static void setOutgoingAdapter(RecyclerView recyclerView, ArrayList items, ItemClickHandler handler) {
        OutgoingListAdapter adapter = (OutgoingListAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.clear();
            if (items != null)
                adapter.addAll(items);
            adapter.setOnItemClickListener(item -> {
                if (handler != null)
                    handler.onItemClick(item);
            });
            adapter.notifyDataSetChanged();
        }
    }

    @BindingAdapter(value = {"incoming_items", "onItemClick"})
    public static void setIncomingAdapter(RecyclerView recyclerView, ArrayList items, ItemClickHandler handler) {
        IncomingListAdapter adapter = (IncomingListAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.clear();
            if (items != null)
                adapter.addAll(items);
            adapter.setOnItemClickListener((item) -> {
                if (handler != null)
                    handler.onItemClick(item);
            });
            adapter.notifyDataSetChanged();
        }
    }


    @BindingAdapter(value = {"imageUrl", "android:text", "noOfCharactersToDisplay", "imageWidth", "imageHeight", "android:textSize"}, requireAll = true)
    public static void setImageUrl(CircularTextDrawableView imageView, String imgUrl, String name, int noOfChars, float widthDp, float heightDp, float fontSize) {

        if (name != null) {

            name = name.trim();

            int color = UiUtils.getColorBase(name);

            Context context = imageView.getContext().getApplicationContext();

            String firstLetter = "";
            if (!name.isEmpty() && noOfChars > 0) {
                if (name.length() > noOfChars) {
                    firstLetter = "" + UiUtils.toCamelcase(name).substring(0, noOfChars);
                } else {
                    firstLetter = "" + name.toUpperCase(Locale.getDefault()).substring(0, 1);
                }
            }

            final String letter = firstLetter;

            final int width = (int) widthDp;
            final int height = (int) heightDp;

            imageView.setIsTextDrawable(true);
            imageView.setTextDrawable(UiUtils.getBitmapFromColor(color, width, height), letter);
            imageView.setFontSize((int) fontSize);

            if (imgUrl != null && !imgUrl.trim().isEmpty()) {
                imgUrl = imgUrl.trim();
                final Target target = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        FRLogger.msg("onBitmapLoaded");
                        imageView.setIsTextDrawable(false);
                        imageView.setImageBitmap(bitmap);
                        UiUtils.setImageInCircularView(context, bitmap, width, imageView);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        FRLogger.msg("onBitmapFailed");
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                    }
                };
                Picasso.with(context)
                        .load(imgUrl)
                        .into(target);
                imageView.setTag(target);
            }
        }
    }


    @BindingAdapter("android:text")
    public static void setDate(TextView textView, Date date) {
        textView.setText(DateUtils.getTimeAgo(date.getTime(), textView.getContext()));
    }

}
