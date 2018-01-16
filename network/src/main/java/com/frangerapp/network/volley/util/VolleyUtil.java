package com.frangerapp.network.volley.util;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.frangerapp.network.volley.ApplicationError;
import com.frangerapp.network.HttpClientException;

import java.io.UnsupportedEncodingException;

/**
 * A class contain util methods for volley network module.
 *
 * @author Vasanth
 */
public class VolleyUtil {

    /**
     * Used tp convert volley error to our application error object.
     */
    public static HttpClientException parseVolleyErrorToOurAppError(final VolleyError error) {
        HttpClientException.ErrorCode errorCode;
        String errorMessage = null;
        int httpStatusCode = -1;
        String httpResponse = null;
        if (error.networkResponse != null) {
            httpStatusCode = error.networkResponse.statusCode;
            try {
                httpResponse = new String(error.networkResponse.data, HttpHeaderParser.parseCharset(error.networkResponse.headers));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        if (error instanceof NoConnectionError) {
            errorCode = HttpClientException.ErrorCode.NO_CONNECTION_ERROR;
            errorMessage = "NoConnectionError";
        } else if (error instanceof NetworkError) {
            errorCode = HttpClientException.ErrorCode.NETWORK_ERROR;
            errorMessage = "NetworkError";
        } else if (error instanceof TimeoutError) {
            errorCode = HttpClientException.ErrorCode.TIME_OUT_ERROR;
            errorMessage = "TimeoutError";
        } else if (error instanceof ParseError) {
            errorCode = HttpClientException.ErrorCode.PARSE_ERROR;
            errorMessage = "ParseError";
        } else if (error instanceof ApplicationError) {
            errorCode = HttpClientException.ErrorCode.APPLICATION_ERROR;
            errorMessage = "ApplicationError";
        } else {
            errorCode = HttpClientException.ErrorCode.UNKNOWN_ERROR;
            errorMessage = "UnknownError";
        }
        return new HttpClientException(errorMessage, error, errorCode, httpStatusCode, httpResponse);
    }

}
