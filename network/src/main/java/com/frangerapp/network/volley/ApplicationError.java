package com.frangerapp.network.volley;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;

/**
 * Exception class represent the application error.
 */
public class ApplicationError extends VolleyError {
    public ApplicationError() {
    }

    public ApplicationError(NetworkResponse networkResponse) {
        super(networkResponse);
    }

    public ApplicationError(Throwable cause) {
        super(cause);
    }
}
