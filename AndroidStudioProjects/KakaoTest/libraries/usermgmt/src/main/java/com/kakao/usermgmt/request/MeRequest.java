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
package com.kakao.usermgmt.request;

import com.kakao.auth.network.request.ApiRequest;
import com.kakao.network.ServerProtocol;
import com.kakao.network.helper.QueryString;
import com.kakao.usermgmt.StringSet;

import org.json.JSONArray;

import java.util.List;

/**
 * @author leoshin, created at 15. 8. 6..
 */
public class MeRequest extends ApiRequest {
    private final List<String> propertyKeyList;
    private final boolean secureResource;

    public MeRequest(List<String> propertyKeyList, boolean secureResource) {
        this.propertyKeyList = propertyKeyList;
        this.secureResource = secureResource;
    }
    
    @Override
    public String getMethod() {
        return GET;
    }

    @Override
    public String getUrl() {
        String baseUrl = ApiRequest.createBaseURL(ServerProtocol.API_AUTHORITY, ServerProtocol.USER_ME_PATH);
        QueryString qs = new QueryString();
        qs.add(StringSet.secure_resource, String.valueOf(secureResource));

        if (propertyKeyList != null && propertyKeyList.size() > 0) {
            qs.add(StringSet.propertyKeys, new JSONArray(propertyKeyList).toString());
        }
        return baseUrl + "?" + qs.toString();
    }
}
