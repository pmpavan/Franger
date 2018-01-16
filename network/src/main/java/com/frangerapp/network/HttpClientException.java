package com.frangerapp.network;

/**
 * Http Client Api Exception.
 * <p>
 * 1. Responsibility.
 * 1.a. Class gives information about the exception occurred during HTTPClientApi call.
 * <p>
 * 2. Error Code.
 * 2.a. Depending on error occurred we will get the following error codes,
 * 2.a.1. NO_CONNECTION_ERROR - Error indicating that no connection could be established when performing a request.
 * 2.a.2. NETWORK_ERROR - Error indicates that there was a network error when performing a request.
 * 2.a.3. TIME_OUT_ERROR - Error indicates that the connection or the socket timed out.
 * 2.a.4. PARSE_ERROR - Error indicates that the server's response could not be parsed.
 * 2.a.5. APPLICATION_ERROR - Error indicates that the response contains application specific error.
 * 2.a.6. UNKNOWN_ERROR - Error indicates that some random unknown error (like BadUrl etc)
 * <p>
 * 3. HTTP Status Code.
 * 3.a. getHttpStatusCode - Return's HTTP Status Code we get from the server.
 *
 * @author Vasanth
 */
public class HttpClientException extends Exception {

    /**
     * ErrorCode Enum represents the kind of exception occured.
     */
    public enum ErrorCode {
        // Error code indicates no connectivity error.
        NO_CONNECTION_ERROR,
        // Error code indicates network error.
        NETWORK_ERROR,
        // Error code indicated request time out.
        TIME_OUT_ERROR,
        // Error code indicated request could not be parsed.
        PARSE_ERROR,
        // Error code indicate that response contains application specific error.
        APPLICATION_ERROR,
        // Error code indicated some random unknown error (like BadUrl etc).
        UNKNOWN_ERROR
    }

    // errorCode.
    private ErrorCode errorCode;

    // HTTP status code.
    private int httpStatusCode;

    // Http response.
    private String httpResponse;

    public HttpClientException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates exception with the specified cause.
     *
     * @param message        error message describing a possible cause of this exception.
     * @param cause          root exception that caused this exception to be thrown.
     * @param errorCode      Error code represent the error occurred.
     * @param httpStatusCode Http status code we got from server.
     * @param httpResponse   Http raw response we got from the server.
     */
    public HttpClientException(String message, final Throwable cause, final ErrorCode errorCode, final int httpStatusCode, final String httpResponse) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatusCode = httpStatusCode;
        this.httpResponse = httpResponse;
    }

    /**
     * Getter's & Setter's.
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public String getHttpResponse() {
        return httpResponse;
    }

    public void setHttpResponse(String httpResponse) {
        this.httpResponse = httpResponse;
    }
}

