/**
 * Copyright 2014-2015 Kakao Corp.
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
package com.kakao.auth.network.request;

import android.os.Bundle;

/**
 * @author leoshin, created at 15. 7. 13..
 */
public abstract class AuthRequest {
    protected static final String POST = "POST";
    protected static final String GET = "GET";

    final private String appKey;
    final private String redirectURI;
    final private Bundle extraParams = new Bundle();
    final private Bundle extraHeaders = new Bundle();

    public AuthRequest(String appKey, String redirectURI) {
        this.appKey = appKey;
        this.redirectURI = redirectURI;
    }

    public void putExtraParam(String key, String value) {
        extraParams.putString(key, value);
    }

    public void putExtraHeader(String key, String value) {
        extraHeaders.putString(key, value);
    }

    public String getAppKey() {
        return appKey;
    }

    public String getRedirectURI() {
        return redirectURI;
    }

    public Bundle getExtraParams() {
        return extraParams;
    }

    public Bundle getExtraHeaders() {
        return extraHeaders;
    }
}
