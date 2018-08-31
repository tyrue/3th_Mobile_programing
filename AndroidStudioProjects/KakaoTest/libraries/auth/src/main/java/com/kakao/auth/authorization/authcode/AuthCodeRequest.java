/*
  Copyright 2014-2017 Kakao Corp.

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
package com.kakao.auth.authorization.authcode;

import com.kakao.auth.AuthCodeCallback;
import com.kakao.auth.network.request.AuthRequest;

/**
 * @author leoshin, created at 15. 7. 13..
 */
public class AuthCodeRequest extends AuthRequest {
    public enum Command {
        /**
         * / talk에 login되어 있는 계정이 있는 경우
         */
        LOGGED_IN_TALK,

        /**
         * story에 login되어 있는 계정이 있는 경우
         */
        LOGGED_IN_STORY,

        /**
         * talk이 install되어 있지 않는 경우
         */
        WEBVIEW_AUTH
    }

    final private AuthCodeCallback callback;

    private Integer requestCode;

    public AuthCodeRequest(String appKey, String redirectURI, Integer requestCode, final AuthCodeCallback callback) {
        super(appKey, redirectURI);
        this.callback = callback;
        this.requestCode = requestCode;
    }

    public AuthCodeCallback getCallback() {
        return callback;
    }

    public Integer getRequestCode() {
        return requestCode;
    }
}
