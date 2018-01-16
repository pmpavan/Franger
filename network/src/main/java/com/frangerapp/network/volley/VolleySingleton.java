package com.frangerapp.network.volley;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.frangerapp.network.TLSSocketFactory;

import java.net.CookieHandler;
import java.net.CookieManager;

/**
 * Volley Singleton.
 * <p>
 * 1. Responsibility.
 * 1.a. Singleton class used to provide single instance volley components.
 * 1.b. Provides "Request Queue" & allows as to add "Request" to the queue.
 *
 * @author Vasanth
 */
class VolleySingleton {

    private static VolleySingleton mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    /**
     * Constructor.
     *
     * @param appContext Application Context.
     */
    private VolleySingleton(final Context appContext) {

        /**
         * Enable TLS v1.2 support.
         * 1. For API V16 - v19 - TLS v1.2 is not enabled by default hence enable it.
         * 2. For API v20 & above - It is enabled by default hence we don't need to do any thing.
         */
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) && (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT)) {
            HttpStack stack = null;
            try {
                stack = new HurlStack(null, new TLSSocketFactory());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (stack == null) {
                stack = new HurlStack();
            }
            // getApplicationContext() is key, it keeps you from leaking the Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(appContext.getApplicationContext(), stack);
        } else {
            mRequestQueue = Volley.newRequestQueue(appContext.getApplicationContext());
        }

        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> cache = new LruCache<>(20);

            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });

    }

    /**
     * Used to singleton instance of VolleySingleton.
     *
     * @param context Context.
     * @return Singleton instance of VolleySingleton.
     */
    static synchronized VolleySingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleySingleton(context);
        }
        return mInstance;
    }

    /**
     * Used to get volley request queue.
     *
     * @return Volley request queue.
     */
    RequestQueue getRequestQueue() {
        /**
         * 1. Initialize CookieManager for each request - such that we don't accept any cookie from server.
         * 2. For REST API we don't need any cookie's.
         */
        CookieHandler.setDefault(new CookieManager());

        return mRequestQueue;
    }

    /**
     * Used to add the request to the request queue.
     *
     * @param request Request to be added to the queue.
     */
    <T> Request<T> addToRequestQueue(Request<T> request) {
        return getRequestQueue().add(request);
    }

    /**
     * Used to get Image Loader.
     *
     * @return Image Loader.
     */
    ImageLoader getImageLoader() {
        return mImageLoader;
    }

}
