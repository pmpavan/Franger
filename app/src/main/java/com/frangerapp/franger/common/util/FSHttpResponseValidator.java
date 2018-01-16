package com.frangerapp.franger.common.util;

import com.frangerapp.network.HttpResponseValidator;

/**
 * Created by Pavan on 16/01/18.
 */

public class FSHttpResponseValidator implements HttpResponseValidator {
    @Override
    public boolean validateHttpResponseForApplicationErrors(String httpResponse) {
        return true;
    }
}
