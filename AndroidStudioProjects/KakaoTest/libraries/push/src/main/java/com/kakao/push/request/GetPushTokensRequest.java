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
package com.kakao.push.request;

import com.kakao.network.ServerProtocol;
import com.kakao.auth.network.request.ApiRequest;

/**
 * @author leoshin, created at 15. 8. 10..
 */
public class GetPushTokensRequest extends ApiRequest {
    @Override
    public String getMethod() {
        return GET;
    }

    @Override
    public String getUrl() {
        return createBaseURL(ServerProtocol.API_AUTHORITY, ServerProtocol.PUSH_TOKENS_PATH);
    }
}
