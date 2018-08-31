package com.kakao.network.storage;

import com.kakao.network.response.ResponseBody;
import com.kakao.util.exception.KakaoException;

import java.net.HttpURLConnection;

/**
 * @author kevin.kang. Created on 2017. 3. 20..
 */

public class ImageDeleteResponse {
    public ImageDeleteResponse(final ResponseBody responseBody) {
        if (responseBody.getStatusCode() != HttpURLConnection.HTTP_OK) {
            throw new KakaoException(KakaoException.ErrorType.ILLEGAL_ARGUMENT, responseBody.toString());
        }
    }
}
