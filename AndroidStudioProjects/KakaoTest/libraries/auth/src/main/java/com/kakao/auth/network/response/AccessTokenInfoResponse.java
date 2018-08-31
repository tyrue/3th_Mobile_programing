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
package com.kakao.auth.network.response;

import com.kakao.auth.StringSet;
import com.kakao.network.response.ResponseBody.ResponseBodyException;
import com.kakao.network.response.ResponseData;

/**
 * @author leoshin, created at 15. 8. 10..
 */
public class AccessTokenInfoResponse extends JSONObjectResponse {
    private final long userId;
    private final long expiresInMillis;

    public AccessTokenInfoResponse(ResponseData responseData) throws ResponseBodyException, ApiResponseStatusError {
        super(responseData);

        userId = body.optLong(StringSet.id, 0);
        expiresInMillis = body.optLong(StringSet.expiresInMillis, 0);
    }

    public long getUserId() {
        return userId;
    }

    public long getExpiresInMillis() {
        return expiresInMillis;
    }

    @Override
    public String toString() {
        return "AccessTokenInfoResponse{" + "userId=" + userId +
                ", expiresInMillis=" + expiresInMillis +
                '}';
    }
}
