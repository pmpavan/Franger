package com.frangerapp.network.volley;

import android.content.Context;

import com.android.volley.Request;
import com.frangerapp.network.HttpClient;
import com.frangerapp.network.HttpClientException;
import com.frangerapp.network.HttpResponseValidator;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Volley Http Client Implementation.
 * <p>
 * 1. Responsibility.
 * 1.a. {@link HttpClient} implementation using Volley Library.
 *
 * @author Vasanth
 * @TODO - Cancel Synchronously Request done using RequestFuture.
 */
public class VolleyHttpClientImpl implements HttpClient {

    private VolleySingleton volleySingleton;
    private Gson jsonParser;
    private HttpResponseValidator httpResponseValidator;

    /**
     * Constructor.
     *
     * @param appContext Application Context.
     * @param jsonParser Json Parser used to parse the response.
     */
    public VolleyHttpClientImpl(final Context appContext, final Gson jsonParser, final HttpResponseValidator httpResponseValidator) {
        volleySingleton = VolleySingleton.getInstance(appContext);
        this.jsonParser = jsonParser;
        this.httpResponseValidator = httpResponseValidator;
    }

    /**
     * ASYNCHRONOUS REQUEST.
     */
    /**
     * HTTP GET REQUEST.
     */
    @Override
    public <T> void getRequest(String identifier, Map<String, String> headers, String url, final boolean shouldCache, Type typeOfT,
                               HttpResponseListener<T> listener) {
        VolleyRequest<T> volleyRequest = new VolleyRequest<T>(identifier, Request.Method.GET, headers, url, shouldCache, typeOfT, jsonParser,
                httpResponseValidator, listener);
        volleySingleton.addToRequestQueue(volleyRequest);
    }

    /**
     * HTTP POST REQUEST.
     */
    @Override
    public <T> void postRequest(String identifier, Map<String, String> headers, String url, String body, String bodyContentType,
                                final boolean shouldCache, Type typeOfT, HttpResponseListener<T> listener) {
        VolleyRequest<T> volleyRequest = new VolleyRequest<T>(identifier, Request.Method.POST, headers, url, body, bodyContentType,
                shouldCache, typeOfT, jsonParser, httpResponseValidator, listener);
        volleySingleton.addToRequestQueue(volleyRequest);
    }

    /**
     * HTTP PUT REQUEST.
     */
    @Override
    public <T> void putRequest(String identifier, Map<String, String> headers, String url, String body, String bodyContentType,
                               final boolean shouldCache, Type typeOfT, HttpResponseListener<T> listener) {
        VolleyRequest<T> volleyRequest = new VolleyRequest<T>(identifier, Request.Method.PUT, headers, url, body, bodyContentType,
                shouldCache, typeOfT, jsonParser, httpResponseValidator, listener);
        volleySingleton.addToRequestQueue(volleyRequest);
    }

    /**
     * HTTP DELETE REQUEST
     */
    @Override
    public <T> void deleteRequest(String identifier, Map<String, String> headers, String url, final boolean shouldCache, Type typeOfT,
                                  HttpResponseListener<T> listener) {
        VolleyRequest<T> volleyRequest = new VolleyRequest<T>(identifier, Request.Method.DELETE, headers, url, shouldCache, typeOfT, jsonParser,
                httpResponseValidator, listener);
        volleySingleton.addToRequestQueue(volleyRequest);
    }

    /**
     * SYNCHRONOUS REQUEST.
     */
    /**
     * HTTP GET REQUEST.
     */
    @Override
    public <T> T getRequestSynchronously(String identifier, Map<String, String> headers, String url, boolean shouldCache,
                                         Type typeOfT) throws HttpClientException {
        VolleyRequestFuture<T> volleyRequestFuture = VolleyRequestFuture.newFuture();
        VolleyRequest<T> volleyRequest = new VolleyRequest<T>(identifier, Request.Method.GET, headers, url, shouldCache, typeOfT, jsonParser,
                httpResponseValidator, volleyRequestFuture);
        volleyRequestFuture.setRequest(volleySingleton.addToRequestQueue(volleyRequest));
        return getResponseFromFuture(volleyRequestFuture);
    }

    /**
     * HTTP POST REQUEST.
     */
    @Override
    public <T> T postRequestSynchronously(String identifier, Map<String, String> headers, String url, String body, String bodyContentType,
                                          boolean shouldCache, Type typeOfT) throws HttpClientException {
        VolleyRequestFuture<T> volleyRequestFuture = VolleyRequestFuture.newFuture();
        VolleyRequest<T> volleyRequest = new VolleyRequest<T>(identifier, Request.Method.POST, headers, url, body, bodyContentType,
                shouldCache, typeOfT, jsonParser, httpResponseValidator, volleyRequestFuture);
        volleyRequestFuture.setRequest(volleySingleton.addToRequestQueue(volleyRequest));
        return getResponseFromFuture(volleyRequestFuture);
    }

    /**
     * HTTP PUT REQUEST.
     */
    @Override
    public <T> T putRequestSynchronously(String identifier, Map<String, String> headers, String url, String body, String bodyContentType,
                                         boolean shouldCache, Type typeOfT) throws HttpClientException {
        VolleyRequestFuture<T> volleyRequestFuture = VolleyRequestFuture.newFuture();
        VolleyRequest<T> volleyRequest = new VolleyRequest<T>(identifier, Request.Method.PUT, headers, url, body, bodyContentType,
                shouldCache, typeOfT, jsonParser, httpResponseValidator, volleyRequestFuture);
        volleyRequestFuture.setRequest(volleySingleton.addToRequestQueue(volleyRequest));
        return getResponseFromFuture(volleyRequestFuture);
    }

    /**
     * HTTP DELETE REQUEST
     */
    @Override
    public <T> T deleteRequestSynchronously(String identifier, Map<String, String> headers, String url, String body, String bodyContentType,
                                            boolean shouldCache, Type typeOfT) throws HttpClientException {
        VolleyRequestFuture<T> volleyRequestFuture = VolleyRequestFuture.newFuture();
        VolleyRequest<T> volleyRequest = new VolleyRequest<T>(identifier, Request.Method.DELETE, headers, url, body, bodyContentType,
                shouldCache, typeOfT, jsonParser, httpResponseValidator, volleyRequestFuture);
        volleyRequestFuture.setRequest(volleySingleton.addToRequestQueue(volleyRequest));
        return getResponseFromFuture(volleyRequestFuture);
    }

    /**
     * CANCEL REQUEST.
     */
    @Override
    public void cancelRequest(String identifier) {
        volleySingleton.getRequestQueue().cancelAll(identifier);
    }

    /**
     * Private METHODS.
     */
    /**
     * Used to get Response object for  Future.
     *
     * @return Response Object.
     * @throws HttpClientException on Request Failure.
     */
    private <T> T getResponseFromFuture(final VolleyRequestFuture<T> volleyRequestFuture) throws HttpClientException {
        T t = null;
        try {
            t = volleyRequestFuture.get();
        } catch (ExecutionException e) {
            if (e.getCause() instanceof HttpClientException) {
                throw ((HttpClientException) e.getCause());
            } else {
                throw new HttpClientException(e.getMessage(), e, HttpClientException.ErrorCode.UNKNOWN_ERROR, -1, "");
            }
        } catch (Exception exp) {
            throw new HttpClientException(exp.getMessage(), exp, HttpClientException.ErrorCode.UNKNOWN_ERROR, -1, "");
        }
        return t;
    }
}
