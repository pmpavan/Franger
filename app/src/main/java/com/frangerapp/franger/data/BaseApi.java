package com.frangerapp.franger.data;

import com.frangerapp.franger.data.common.util.DataConstants;

/**
 * Created by Pavan on 05/02/18.
 */

public class BaseApi {

    protected String getURL(String url) {
        return DataConstants.PROTOCOL +
                DataConstants.BASE_DOMAIN_URL + url;
    }
}
