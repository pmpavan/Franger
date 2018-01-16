package com.frangerapp.network.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.frangerapp.network.HttpClient;
import com.frangerapp.network.HttpResponseValidator;
import com.frangerapp.network.volley.util.VolleyUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Volley Request.
 * <p>
 * 1. Responsibility.
 * 1.a. Class responsible for retrieving the response body at a given URL.
 * 1.b. Parse's the responseBody to the given Type (T).
 *
 * @author Vasanth
 */
class VolleyRequest<T> extends Request<T> {

    private static final String TAG = "VolleyRequest";
    private Map<String, String> mHeaders;
    private String mBody;
    private String mBodyContentType;
    private Type typeOfT;
    private Gson jsonParser;
    private HttpResponseValidator httpResponseValidator;
    private HttpClient.HttpResponseListener<T> listener;

    /**
     * Constructor.
     *
     * @param identifier            Unique string to identify individual request.
     * @param method                HTTP Request Method.
     * @param headers               Request Headers.
     * @param url                   Request Url.
     * @param shouldCache           whether or not responses to this request should be cached.
     * @param typeOfT               the type of T.
     * @param jsonParser            JsonParser used to parse the response.
     * @param httpResponseValidator Http Response Validator.
     * @param listener              Listener used to get callback on request completes.
     */
    VolleyRequest(final String identifier, final int method, final Map<String, String> headers, final String url,
                  final boolean shouldCache, final Type typeOfT, final Gson jsonParser, final HttpResponseValidator httpResponseValidator,
                  final HttpClient.HttpResponseListener<T> listener) {
        super(method, url, new ResponseErrorListener<T>(listener));

        setTag(identifier);
        setShouldCache(shouldCache);
        setRetryPolicy(new DefaultRetryPolicy(HttpClient.DEFAULT_REQUEST_SOCKET_TIMEOUT_MS, HttpClient.DEFAULT_REQUEST_MAX_RETRIES, HttpClient.DEFAULT_REQUEST_BACKOFF_MULTIPLIER));

        this.mHeaders = headers;
        this.typeOfT = typeOfT;
        this.jsonParser = jsonParser;
        this.httpResponseValidator = httpResponseValidator;
        this.listener = listener;

//        FSLogger.logInformation(TAG, "Url : " + url);
//        for (Map.Entry<String, String> entry : headers.entrySet()) {
//            FSLogger.logInformation(TAG, entry.getKey() + ":" + entry.getValue());
//        }
    }

    /**
     * Constructor.
     *
     * @param identifier            Unique string to identify individual request.
     * @param method                HTTP Request Method.
     * @param headers               Request Headers.
     * @param url                   Request Url.
     * @param body                  Request Body.
     * @param bodyContentType       Request Body Content Type.
     * @param shouldCache           whether or not responses to this request should be cached.
     * @param typeOfT               the type of T.
     * @param jsonParser            JsonParser used to parse the response.
     * @param httpResponseValidator Http Response Validator.
     * @param listener              Listener used to get callback on request completes.
     */
    VolleyRequest(final String identifier, final int method, final Map<String, String> headers, final String url, final String body,
                  final String bodyContentType, final boolean shouldCache, final Type typeOfT, final Gson jsonParser,
                  final HttpResponseValidator httpResponseValidator, final HttpClient.HttpResponseListener<T> listener) {
        this(identifier, method, headers, url, shouldCache, typeOfT, jsonParser, httpResponseValidator, listener);

        this.mBody = body;
        this.mBodyContentType = bodyContentType;

//        FSLogger.logInformation(TAG, "Body : " + body);
//        FSLogger.logInformation(TAG, "BodyContentType : " + bodyContentType);
    }


    /**
     * Request METHODS.
     */
    /**
     * Returns a list of extra HTTP headers to go along with this request.
     *
     * @return Headers.
     * @throws AuthFailureError
     */
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return (mHeaders != null) ? mHeaders : super.getHeaders();
    }

    /**
     * Used to get request body.
     *
     * @return Request body.
     * @throws AuthFailureError
     */
    @Override
    public byte[] getBody() throws AuthFailureError {
        byte[] body = null;
        // String Request Body.
        if (mBody != null) {
            body = mBody.getBytes();
        } else {
            body = super.getBody();
        }
        return body;
    }

    /**
     * Used to get request body content type.
     *
     * @return Request body content type.
     */
    @Override
    public String getBodyContentType() {
        return (mBodyContentType != null) ? mBodyContentType : super.getBodyContentType();
    }

    /**
     * Used to parse the networkResponse to javaObject.
     *
     * @param networkResponse Response from the network
     * @return The parsed response, or error in the case of an error
     */
    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse networkResponse) {
        Response<T> response;
        try {
            String json = new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers));
//            FSLogger.logInformation(TAG, "Success Response : " + json);
//            FSLogger.logInformation(TAG, "Response Code : " + networkResponse.statusCode);

            if (httpResponseValidator.validateHttpResponseForApplicationErrors(json)) {
                T t = null;
                if (typeOfT instanceof Class<?>) {
                    if (((Class<?>) typeOfT).getName().equals(String.class.getName())) {
                        t = (T) json;
                    } else {
                        t = jsonParser.fromJson(json, (Class<T>) typeOfT);
                    }
                } else {
                    t = jsonParser.fromJson(json, typeOfT);
                }
                response = Response.success(t, HttpHeaderParser.parseCacheHeaders(networkResponse));
            } else {
                response = Response.error(new ApplicationError(networkResponse));
            }
        } catch (UnsupportedEncodingException e) {
            response = Response.error(new ParseError(e));
        } catch (JsonSyntaxException je) {
            response = Response.error(new ParseError(je));
        } catch (Exception exp) {
            response = Response.error(new VolleyError(exp));
        }
        return response;
    }

    /**
     * Used to deliver the response to client listener.
     *
     * @param response The parsed response returned by parseNetworkResponse(NetworkResponse)
     */
    @Override
    protected void deliverResponse(T response) {
        listener.onSuccessResponse(response);
    }

    /**
     * Response Error Listener.
     * <p>
     * 1. Responsibility.
     * 1.a. Listener gets notified on failure of request.
     */
    private static class ResponseErrorListener<T> implements Response.ErrorListener {

        private HttpClient.HttpResponseListener<T> mListener;

        /**
         * Constructor.
         *
         * @param listener Listener used to send callback on request failure.
         */
        ResponseErrorListener(final HttpClient.HttpResponseListener<T> listener) {
            this.mListener = listener;
        }

        /**
         * Gets called on error response.
         *
         * @param error Error.
         */
        @Override
        public void onErrorResponse(final VolleyError error) {
//            FSLogger.logInformation(TAG, "Error Response : " + error.getMessage());

            mListener.onErrorResponse(VolleyUtil.parseVolleyErrorToOurAppError(error));
        }
    }
}
