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
package com.kakao.auth.network.response;


import com.kakao.auth.ErrorCode;
import com.kakao.auth.StringSet;
import com.kakao.network.exception.ResponseStatusError;
import com.kakao.network.response.ResponseBody;
import com.kakao.network.response.ResponseBody.ResponseBodyException;

import java.net.HttpURLConnection;


/**
 * @author leo.shin
 */
public class AuthResponse {
    protected AuthResponse() {
    }

    public AuthResponse(ResponseBody body) throws AuthResponseStatusError, ResponseBodyException {
        int statusCode = body.getStatusCode();
        if (statusCode != HttpURLConnection.HTTP_OK) {
            throw new AuthResponseStatusError(body.getString(StringSet.error), body.getString(StringSet.error_description), body);
        }
    }

    public static class AuthResponseStatusError extends ResponseStatusError {
        private static final long serialVersionUID = 3702596857996303483L;
        private final String error;
        private final String errorDescription;
        private final ResponseBody responseBody;

        public AuthResponseStatusError(String error, String errorDescription, ResponseBody responseBody) {
            super(error + " : " + errorDescription);
            this.error = error;
            this.errorDescription = errorDescription;
            this.responseBody = responseBody;
        }

        @Override
        public int getErrorCode() {
            return ErrorCode.AUTH_ERROR_CODE.getErrorCode();
        }

        public String getError() {
            return error;
        }

        public String getErrorMsg() {
            return errorDescription;
        }

        public int getHttpStatusCode() {
            return responseBody.getStatusCode();
        }

        public ResponseBody getResponseBody() {
            return responseBody;
        }
    }
}
