package com.frangerapp.network.volley;

import android.content.Context;
import android.graphics.Bitmap;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.frangerapp.network.FDImageLoader;
import com.frangerapp.network.HttpClient;
import com.frangerapp.network.volley.util.VolleyUtil;

/**
 * {@link FDImageLoader} implementation using volley.
 *
 * @author Vasanth
 */
public class VolleyImageLoader implements FDImageLoader {

    private VolleySingleton volleySingleton;

    public VolleyImageLoader(final Context appContext) {
        volleySingleton = VolleySingleton.getInstance(appContext);
    }

    @Override
    public void loadImageRequest(String url, final HttpClient.HttpResponseListener<Bitmap> listener) {
        volleySingleton.getImageLoader().get(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                // If image is not in cache, the we will get callback immediately with bitmap = null && inImmediate = true, which we will ignore.
                if (!isImmediate || response.getBitmap() != null) {
                    if (listener != null) {
                        listener.onSuccessResponse(response.getBitmap());
                    }
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                if (listener != null) {
                    listener.onErrorResponse(VolleyUtil.parseVolleyErrorToOurAppError(error));
                }
            }
        });
    }
}
