package com.frangerapp.network;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Network Image View.
 * <p>
 * 1. Responsibility.
 * 1.a. Class handles fetching an image from a URL and settings it.
 * 1.b. This is the abstract class hence implementation must provide a functionality.
 *
 * @author Vasanth
 */
public abstract class NetworkImageView extends AppCompatImageView {

    /**
     * The URL of the network image to load
     */
    protected String mUrl;

    /**
     * Resource ID of the image to be used as a placeholder until the network image is loaded.
     */
    protected int mDefaultImageId;

    /**
     * Resource ID of the image to be used if the network response fails.
     */
    protected int mErrorImageId;

    /**
     * Listener used to send callback whether image was successfully loaded or not.
     */
    protected NetworkImageViewListener networkImageViewListener;

    /**
     * NetworkImageView Listener.
     * <p>
     * 1. Responsibility.
     * 1.a. Interface used to send callback whether image was successfully loaded or not.
     */
    public interface NetworkImageViewListener {

        /**
         * Gets called if image successfully loaded from network.
         */
        void onImageSuccessfullyLoaded();

        /**
         * Gets called if image failed to load from network.
         */
        void onImageFailedToLoad();
    }

    /**
     * Constructors.
     */
    public NetworkImageView(Context context) {
        super(context);
    }

    public NetworkImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NetworkImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Sets URL of the image that should be loaded into this view. Note that calling this will
     * immediately either set the cached image (if available) or the default image specified by
     * {@link NetworkImageView#setDefaultImageResId(int)} on the view.
     * <p>
     * NOTE: If applicable, {@link NetworkImageView#setDefaultImageResId(int)} and
     * {@link NetworkImageView#setErrorImageResId(int)} should be called prior to calling
     * this function.
     *
     * @param url The URL that should be loaded into this ImageView.
     */
    public void setImageUrl(String url) {
        mUrl = url;
        loadImageFromUrl();
    }

    /**
     * Sets the default image resource ID to be used for this view until the attempt to load it
     * completes.
     */
    public void setDefaultImageResId(int defaultImage) {
        mDefaultImageId = defaultImage;
    }

    /**
     * Sets the error image resource ID to be used for this view in the event that the image
     * requested fails to load.
     */
    public void setErrorImageResId(int errorImage) {
        mErrorImageId = errorImage;
    }

    /**
     * Set listener which is used to get callback whether image was successfully loaded or not.
     */
    public void setNetworkImageViewListenerListener(final NetworkImageViewListener networkImageViewListener) {
        this.networkImageViewListener = networkImageViewListener;
    }

    /**
     * Loads a image for the given url & set it to the view.
     * <p>
     * It is a abstract method hence implementation must provide functionality for it.
     */
    protected abstract void loadImageFromUrl();
}
