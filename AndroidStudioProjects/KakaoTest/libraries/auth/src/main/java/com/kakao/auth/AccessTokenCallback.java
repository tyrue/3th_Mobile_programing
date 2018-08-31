/*
  Copyright 2017 Kakao Corp.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package com.kakao.auth;

import com.kakao.auth.authorization.accesstoken.AccessToken;
import com.kakao.auth.authorization.accesstoken.AccessTokenListener;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.network.exception.ResponseStatusError;
import com.kakao.util.exception.KakaoException;

import java.net.HttpURLConnection;

/**
 * Success/failure callback for getting access token.
 *
 * @author kevin.kang. Created on 2017. 4. 28..
 */

public abstract class AccessTokenCallback extends ResponseCallback<AccessToken> implements AccessTokenListener {
    @Override
    public final void onFailure(ErrorResult errorResult) {
        Exception exception = errorResult.getException();
        if (exception != null && exception instanceof ResponseStatusError) {
            ResponseStatusError e = (ResponseStatusError) exception;
            switch (e.getHttpStatusCode()) {
                case HttpURLConnection.HTTP_BAD_REQUEST:
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                    exception = new KakaoException(KakaoException.ErrorType.AUTHORIZATION_FAILED, e.getErrorMsg());
                    break;
                default:
                    exception = new KakaoException(KakaoException.ErrorType.UNSPECIFIED_ERROR, e.getErrorMsg());
                    break;
            }
        }
        onAccessTokenFailure(new ErrorResult(exception));
    }

    /**
     *
     * @param accessToken AccessToken instance
     */
    @Override
    public final void onSuccess(AccessToken accessToken) {
        if (accessToken.hasValidAccessToken()) {
            onAccessTokenReceived(accessToken);
        } else {
            Exception exception = new KakaoException(KakaoException.ErrorType.AUTHORIZATION_FAILED, "the result of access token request is invalid access token.");
            onAccessTokenFailure(new ErrorResult(exception));
        }
    }
}
