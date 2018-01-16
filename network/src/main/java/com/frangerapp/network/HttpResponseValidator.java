package com.frangerapp.network;

/**
 * HTTP Response Validator.
 * <p>
 * 1. Responsibility.
 * 1.a. A interface declares method which is responsible to validate that the response is valid or it contains any application specific errors.
 * 1.b. This interface needs to be implemented by the individual application & needs to validate the response for their application specific errors.
 * 1.c. If the implementation say's that response is valid - Then we will get the parsed response in "onSuccessResponse(T)".
 * 1.d. If the implementation say's that response is not valid (contains application error) - Then we will get the callback in "onErrorResponse()"
 * with "ErrorCode = ErrorCode.APPLICATION_ERROR" {@link HttpClientException for more information}.
 *
 * @author Vasanth
 */
public interface HttpResponseValidator {

    /**
     * Used to validate the raw http response for any application specific error response's.
     *
     * @param httpResponse Raw http response received from the server.
     * @return TRUE if the response is valid else FALSE if the response contains application specif error response.
     */
    boolean validateHttpResponseForApplicationErrors(final String httpResponse);

}
