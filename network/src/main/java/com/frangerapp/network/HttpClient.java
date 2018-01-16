package com.frangerapp.network;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Http Client.
 * <p>
 * 1. Responsibility.
 * 1.a. Interface defines Common HTTP Methods.
 * 1.b. Hence we need to implement this interface using some HTTP client (like Volley, Retrofit etc) and
 * provide functionality for the HTTP methods.
 * 1.c. Inside our application we will only refer this interface for any HTTP calls there by abstracting
 * the third party HTTP client implementation.
 *
 * @author Vasanth
 */
public interface HttpClient {

    // The Default Socket timeout in milliseconds
    int DEFAULT_REQUEST_SOCKET_TIMEOUT_MS = 25 * 100 * 10;

    // The Default number of retries
    int DEFAULT_REQUEST_MAX_RETRIES = 0;

    // The Default backoff multiplier - A multiplier which is used to determine exponential time set to socket for every retry attempt.
    float DEFAULT_REQUEST_BACKOFF_MULTIPLIER = 1f;

    // Use this bodyContType If - The body of the HTTP message sent to the server is essentially one giant query string --
    // name/value pairs are separated by the ampersand (&), and names are separated from values by the equal symbal (=).
    String BODY_CONTENT_TYPE_FORM_URL_ENCODED = "application/x-www-form-urlencoded; charset=UTF-8";

    // Use this bodyContType If - The body of the HTTP message sent to the server is a json.
    String BODY_CONTENT_TYPE_JSON = "application/json; charset=utf-8";

    /**
     * HTTP Response Listener.
     * <p>
     * 1. Responsibility.
     * 1.a. Interface use to send callback once request completes.
     *
     * @param <T> Type to which response has to be parsed.
     */
    interface HttpResponseListener<T> {

        /**
         * Gets called on request success.
         *
         * @param response Response.
         */
        void onSuccessResponse(T response);

        /**
         * Gets called on request failure.
         *
         * @param exception {@link HttpClientException} provides information about the error occurred.
         */
        void onErrorResponse(HttpClientException exception);
    }

    /**
     * ASYNCHRONOUS REQUEST.
     */
    /**
     * Used to make "HTTP GET REQUEST" & get response using callback in UI Thread.
     *
     * @param identifier  Unique string to identify individual request.
     * @param headers     Request headers.
     * @param url         Request Url.
     * @param shouldCache whether or not responses to this request should be cached.
     * @param listener    Listener used to get callback on request completes.
     * @param <T>         Type to which response has to be parsed.
     */
    <T> void getRequest(final String identifier, final Map<String, String> headers, final String url, final boolean shouldCache, Type typeOfT,
                        final HttpResponseListener<T> listener);

    /**
     * Used to make "HTTP POST REQUEST" & get response using callback in UI Thread.
     *
     * @param identifier      Unique string to identify individual request.
     * @param headers         Request headers.
     * @param url             Request Url.
     * @param body            Request Body.
     * @param bodyContentType Request Body Content Type.
     * @param shouldCache     whether or not responses to this request should be cached.
     * @param listener        Listener used to get callback on request completes.
     * @param <T>             Type to which response has to be parsed.
     */
    <T> void postRequest(final String identifier, final Map<String, String> headers, final String url, final String body, final String bodyContentType,
                         final boolean shouldCache, Type typeOfT, final HttpResponseListener<T> listener);

    /**
     * Used to make "HTTP PUT REQUEST" & get response using callback in UI Thread.
     *
     * @param identifier      Unique string to identify individual request.
     * @param headers         Request headers.
     * @param url             Request Url.
     * @param body            Request Body.
     * @param bodyContentType Request Body Content Type.
     * @param shouldCache     whether or not responses to this request should be cached.
     * @param listener        Listener used to get callback on request completes.
     * @param <T>             Type to which response has to be parsed.
     */
    <T> void putRequest(final String identifier, final Map<String, String> headers, final String url, final String body, final String bodyContentType,
                        final boolean shouldCache, Type typeOfT, final HttpResponseListener<T> listener);

    /**
     * Used to make "HTTP DELETE REQUEST" & get response using callback in UI Thread.
     *
     * @param identifier  Unique string to identify individual request.
     * @param headers     Request headers.
     * @param url         Request Url.
     * @param shouldCache whether or not responses to this request should be cached.
     * @param listener    Listener used to get callback on request completes.
     * @param <T>         Type to which response has to be parsed.
     */
    <T> void deleteRequest(final String identifier, final Map<String, String> headers, final String url, final boolean shouldCache, Type typeOfT,
                           final HttpResponseListener<T> listener);

    /**
     * SYNCHRONOUS REQUEST.
     */
    /**
     * Used to make "HTTP GET REQUEST" & get response using callback in UI Thread.
     *
     * @param identifier  Unique string to identify individual request.
     * @param headers     Request headers.
     * @param url         Request Url.
     * @param shouldCache whether or not responses to this request should be cached.
     * @param <T>         Type to which response has to be parsed.
     * @throws HttpClientException request failure.
     */
    <T> T getRequestSynchronously(final String identifier, final Map<String, String> headers, final String url, final boolean shouldCache,
                                  Type typeOfT) throws HttpClientException;

    /**
     * Used to make "HTTP POST REQUEST" & get response using callback in UI Thread.
     *
     * @param identifier      Unique string to identify individual request.
     * @param headers         Request headers.
     * @param url             Request Url.
     * @param body            Request Body.
     * @param bodyContentType Request Body Content Type.
     * @param shouldCache     whether or not responses to this request should be cached.
     * @param <T>             Type to which response has to be parsed.
     * @throws HttpClientException request failure.
     */
    <T> T postRequestSynchronously(final String identifier, final Map<String, String> headers, final String url, final String body, final String bodyContentType,
                                   final boolean shouldCache, Type typeOfT) throws HttpClientException;

    /**
     * Used to make "HTTP PUT REQUEST" & get response using callback in UI Thread.
     *
     * @param identifier      Unique string to identify individual request.
     * @param headers         Request headers.
     * @param url             Request Url.
     * @param body            Request Body.
     * @param bodyContentType Request Body Content Type.
     * @param shouldCache     whether or not responses to this request should be cached.
     * @param <T>             Type to which response has to be parsed.
     * @throws HttpClientException request failure.
     */
    <T> T putRequestSynchronously(final String identifier, final Map<String, String> headers, final String url, final String body, final String bodyContentType,
                                  final boolean shouldCache, Type typeOfT) throws HttpClientException;

    /**
     * Used to make "HTTP DELETE REQUEST" & get response using callback in UI Thread.
     *
     * @param identifier  Unique string to identify individual request.
     * @param headers     Request headers.
     * @param url         Request Url.
     * @param shouldCache whether or not responses to this request should be cached.
     * @param <T>         Type to which response has to be parsed.
     * @throws HttpClientException request failure.
     */
    <T> T deleteRequestSynchronously(final String identifier, final Map<String, String> headers, final String url, final String body, final String bodyContentType,
                                     final boolean shouldCache, Type typeOfT) throws HttpClientException;

    /**
     * Used to cancel the request.
     *
     * @param identifier Unique string to identify the request & cancel it.
     */
    void cancelRequest(final String identifier);


}
