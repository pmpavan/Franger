package com.frangerapp.network;

import android.content.Context;
import android.util.AttributeSet;

import com.frangerapp.network.volley.VolleyNetworkImageView;


/**
 * FD Network Image View.
 * <p>
 * 1. Responsibility.
 * 1.a. Class handles fetching an image from a URL and settings it to view.
 * <p>
 * 2. Note
 * 2.a. This class extents {@link VolleyNetworkImageView} because it provides the actual implementation.
 * 2.b. In feature if we plan to change the library (from volley to other), then just change the base class. Hence no changes needed in the client app side.
 *
 * @author Vasanth
 */
public class FDNetworkImageView extends VolleyNetworkImageView {

    /**
     * Constructors.
     */
    public FDNetworkImageView(Context context) {
        super(context);
    }

    public FDNetworkImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FDNetworkImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Sets URL of the image that should be loaded into this view.
     *
     * @param url The URL that should be loaded into this ImageView.
     */
    public void setImageUrl(final String url) {
        super.setImageUrl(url);
    }

    /**
     * Sets the default image resource ID to be used for this view until the attempt to load it
     * completes.
     */
    public void setDefaultImageResId(final int defaultImage) {
        super.setDefaultImageResId(defaultImage);
    }

    /**
     * Sets the error image resource ID to be used for this view in the event that the image
     * requested fails to load.
     */
    public void setErrorImageResId(final int errorImage) {
        super.setErrorImageResId(errorImage);
    }

    /**
     * Set listener which is used to get callback whether image was successfully loaded or not.
     */
    public void setNetworkImageViewListenerListener(final NetworkImageViewListener loadImageListener) {
        super.setNetworkImageViewListenerListener(loadImageListener);
    }

    /**
     * Used to load image from network.
     *
     * @param url               The URL that should be loaded into this ImageView.
     * @param defaultImage      The default image resource ID to be used for this view until the attempt to load it completes.
     * @param errorImage        The error image resource ID to be used for this view in the event that the image requested fails to load.
     * @param loadImageListener The listener which is used to get callback whether image was successfully loaded or not.
     */
    public void loadImage(final String url, final int defaultImage, final int errorImage, final NetworkImageViewListener loadImageListener) {
        setDefaultImageResId(defaultImage);
        setErrorImageResId(errorImage);
        setNetworkImageViewListenerListener(loadImageListener);
        setImageUrl(url);
    }

}
