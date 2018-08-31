/**
 * Copyright 2014-2016 Kakao Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kakao.auth.api;


import android.content.Context;

import com.kakao.auth.SingleNetworkTask;
import com.kakao.auth.authorization.accesstoken.AccessToken;
import com.kakao.auth.authorization.accesstoken.AccessTokenRequest;
import com.kakao.auth.network.request.AccessTokenInfoRequest;
import com.kakao.auth.network.response.AccessTokenInfoResponse;
import com.kakao.network.response.ResponseBody;
import com.kakao.network.response.ResponseData;

/**
 * Bloking으로 동작하며, 인증관련 내부 API콜을 한다.
 * @author leoshin
 */
public class AuthApi {

    private static AuthApi instance;

    public static AuthApi getInstance() {
        if (instance == null) {
            synchronized (AuthApi.class) {
                if (instance == null) {
                    instance = new AuthApi();
                }
            }
        }
        return instance;
    }

    public AccessToken requestAccessToken(Context context, String appKey, String authCode, String refreshToken, String clientSecret, String approvalType) throws Exception {
        SingleNetworkTask networkTask = new SingleNetworkTask();
        ResponseBody result = networkTask.requestAuth(new AccessTokenRequest(context, appKey, authCode, refreshToken, clientSecret, approvalType));
       return AccessToken.Factory.createFromResponse(result);
    }

    public AccessTokenInfoResponse requestAccessTokenInfo() throws Exception {
        SingleNetworkTask networkTask = new SingleNetworkTask();
        ResponseData result = networkTask.requestApi(new AccessTokenInfoRequest());
        return new AccessTokenInfoResponse(result);
    }
}
