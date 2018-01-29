package com.frangerapp.franger.data.common.util;

import com.frangerapp.network.HttpResponseValidator;

/**
 * Created by Pavan on 16/01/18.
 */

public class FRHttpResponseValidator implements HttpResponseValidator {
    @Override
    public boolean validateHttpResponseForApplicationErrors(String httpResponse) {
        return true;
    }
}
