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
import com.kakao.network.response.ResponseData;

import java.net.HttpURLConnection;

/**
 * @author leo.shin
 */
public class ApiResponse {
    private ApiResponse() {
    }

    protected ApiResponse(ResponseData responseData) throws ResponseBodyException, ApiResponseStatusError {
        if (responseData.getHttpStatusCode() != HttpURLConnection.HTTP_OK) {
            ResponseBody errResponseBody = new ResponseBody(responseData.getHttpStatusCode(), responseData.getData());
            throw new ApiResponseStatusError(errResponseBody.getInt(StringSet.code),  errResponseBody.optString(StringSet.msg, ""), responseData.getHttpStatusCode());
        }
    }

    public static class InsufficientScopeException extends ApiResponseStatusError {
        public InsufficientScopeException(ResponseBody body) throws ResponseBodyException {
            this(body.getInt(StringSet.code), body.optString(StringSet.msg, ""), body.getStatusCode());
        }

        public InsufficientScopeException(final String errorMsg) {
            super(ErrorCode.INVALID_SCOPE_CODE.getErrorCode(), errorMsg, HttpURLConnection.HTTP_FORBIDDEN);
        }

        public InsufficientScopeException(int errorCode, String errorMsg, int httpStatusCode) {
            super(errorCode, errorMsg, httpStatusCode);
        }
    }

    public static class SessionClosedException extends ApiResponseStatusError {
        public SessionClosedException(String errorMsg) {
            this(ErrorCode.INVALID_TOKEN_CODE.getErrorCode(), errorMsg, HttpURLConnection.HTTP_UNAUTHORIZED);
        }

        public SessionClosedException(int errorCode, String errorMsg, int httpStatusCode) {
            super(errorCode, errorMsg, httpStatusCode);
        }
    }

    public static class ApiResponseStatusError extends ResponseStatusError {
        private static final long serialVersionUID = 3702596857996303483L;
        private final int errorCode;
        private final String errorMsg;
        private final int httpStatusCode;

        public ApiResponseStatusError(int errorCode, String errorMsg, int httpStatusCode) {
            super(errorMsg);
            this.errorCode = errorCode;
            this.errorMsg = errorMsg;
            this.httpStatusCode = httpStatusCode;
        }

        public int getErrorCode() {
            return errorCode;
        }

        public String getErrorMsg() {
            return errorMsg;
        }

        public int getHttpStatusCode() {
            return httpStatusCode;
        }
    }

    public static final class BlankApiResponse extends ApiResponse {
        public BlankApiResponse(ResponseData responseData) throws ResponseBodyException, ApiResponseStatusError {
            if (responseData.getHttpStatusCode() != HttpURLConnection.HTTP_OK) {
                ResponseBody errResponseBody = new ResponseBody(responseData.getHttpStatusCode(), responseData.getData());
                throw new ApiResponseStatusError(errResponseBody.getInt(StringSet.code), errResponseBody.optString(StringSet.msg, ""), responseData.getHttpStatusCode());
            }
        }
    }
}
