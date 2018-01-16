package com.frangerapp.network.volley;

import com.android.volley.Request;
import com.frangerapp.network.HttpClient;
import com.frangerapp.network.HttpClientException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Volley Request Future.
 * <p>
 * 1. Responsibility.
 * 1.a. A Future that represents a Volley request.
 * 1.b. Used to get the result of the VolleyRequest Synchronously.
 *
 * @param <T> The type of parsed response this future expects.
 * @author Vasanth
 * @see Future & {@link com.android.volley.toolbox.RequestFuture} for more information.
 */
public class VolleyRequestFuture<T> implements Future<T>, HttpClient.HttpResponseListener<T> {

    private Request<?> mRequest;
    private boolean mResultReceived = false;
    private T mResult;
    private HttpClientException mException;

    /**
     * Constructor.
     */
    private VolleyRequestFuture() {
    }

    public static <E> VolleyRequestFuture<E> newFuture() {
        return new VolleyRequestFuture<E>();
    }

    /**
     * Public METHODS.
     */
    public void setRequest(Request<?> request) {
        mRequest = request;
    }

    /**
     * Future METHODS.
     */
    /**
     * Attempts to cancel execution of this Request.
     *
     * @param mayInterruptIfRunning
     * @return false if the task could not be cancelled, typically because it has already completed normally; true otherwise
     */
    @Override
    public synchronized boolean cancel(boolean mayInterruptIfRunning) {
        if (mRequest == null) {
            return false;
        }

        if (!isDone()) {
            mRequest.cancel();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Waits if necessary for the computation to complete, and then retrieves its result.
     *
     * @return the computed result.
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @Override
    public T get() throws InterruptedException, ExecutionException {
        try {
            return doGet(null);
        } catch (TimeoutException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Waits if necessary for at most the given time for the computation to complete, and then retrieves its result, if available.
     *
     * @param timeout the maximum time to wait.
     * @param unit    the time unit of the timeout argument.
     * @return the computed result.
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    @Override
    public T get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return doGet(TimeUnit.MILLISECONDS.convert(timeout, unit));
    }

    /**
     * Returns true if this Request was cancelled before it completed normally.
     *
     * @return true if this Request was cancelled before it completed.
     */
    @Override
    public boolean isCancelled() {
        if (mRequest == null) {
            return false;
        }
        return mRequest.isCanceled();
    }

    /**
     * Returns true if this Request completed.
     * Completion may be due to normal termination, an exception, or cancellation -- in all of these cases, this method will return true.
     *
     * @return true if this Request completed.
     */
    @Override
    public synchronized boolean isDone() {
        return mResultReceived || mException != null || isCancelled();
    }

    /**
     * HttpClient.HttpResponseListener<T> METHODS.
     */
    @Override
    public synchronized void onSuccessResponse(T response) {
        mResultReceived = true;
        mResult = response;
        notifyAll();
    }

    @Override
    public synchronized void onErrorResponse(HttpClientException exception) {
        mException = exception;
        notifyAll();
    }

    /**
     * Private METHODS.
     */
    /**
     * Waits if necessary for at most the given time for the computation to complete, and then retrieves its result, if available..
     */
    private synchronized T doGet(Long timeoutMs)
            throws InterruptedException, ExecutionException, TimeoutException {
        if (mException != null) {
            throw new ExecutionException(mException);
        }

        if (mResultReceived) {
            return mResult;
        }

        if (timeoutMs == null) {
            wait(0);
        } else if (timeoutMs > 0) {
            wait(timeoutMs);
        }

        if (mException != null) {
            throw new ExecutionException(mException);
        }

        if (!mResultReceived) {
            throw new TimeoutException();
        }

        return mResult;
    }
}


