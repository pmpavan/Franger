package com.frangerapp.network;

import android.graphics.Bitmap;

/**
 * FD Image Loader.
 *
 * 1. Responsibility.
 * 1.a. A interface defines the method responsible to load image from url & send a respective callback.
 *
 * @author Vasanth
 */
public interface FDImageLoader {

    /**
     * 1. Used to load image from url.
     * 2. NOTE - Make sure to call this method in UI thread.
     *
     * @param url      Url pointing to the image resource.
     * @param listener Listener used to get callback on request completes.
     */
    void loadImageRequest(final String url, final HttpClient.HttpResponseListener<Bitmap> listener);

}
