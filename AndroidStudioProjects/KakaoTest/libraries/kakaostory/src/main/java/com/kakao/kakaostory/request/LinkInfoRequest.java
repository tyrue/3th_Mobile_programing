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
package com.kakao.kakaostory.request;

import com.kakao.network.ServerProtocol;
import com.kakao.auth.network.request.ApiRequest;
import com.kakao.kakaostory.StringSet;
import com.kakao.network.helper.QueryString;

/**
 * @author leoshin, created at 15. 8. 4..
 */
public class LinkInfoRequest extends ApiRequest {
    private final String url;
    public LinkInfoRequest(String url) {
        this.url = url;
    }
    @Override
    public String getMethod() {
        return GET;
    }

    @Override
    public String getUrl() {
        String baseUrl = createBaseURL(ServerProtocol.API_AUTHORITY, ServerProtocol.STORY_LINK_SCRAPPER_PATH);
        QueryString qs = new QueryString();
        qs.add(StringSet.url, url);
        return baseUrl + "?" + qs.toString();
    }
}
