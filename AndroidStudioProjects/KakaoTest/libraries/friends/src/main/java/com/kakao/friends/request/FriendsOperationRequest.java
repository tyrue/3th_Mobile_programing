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
package com.kakao.friends.request;

import com.kakao.auth.network.request.ApiRequest;
import com.kakao.friends.FriendOperationContext;
import com.kakao.friends.StringSet;
import com.kakao.network.ServerProtocol;
import com.kakao.network.helper.QueryString;

/**
 * @author leo.shin
 */
public class FriendsOperationRequest extends ApiRequest {
    public enum Operation {
        UNDEFINED("undefined", -1),
        INTERSECTION("i", 0),
        UNION("u", 1),
        SUBTRACTION("s", 2);

        final private String name;
        final private int value;
        Operation(String name, int value) {
            this.name = name;
            this.value = value;
        }
    }

    private final String firstId;
    private final String secondId;
    private final Operation operation;
    private final boolean secureResource;
    private final int offset;
    private final int limit;
    private final String order;
    private final String url;

    public FriendsOperationRequest(FriendOperationContext context) {
        this.firstId = context.getFirstId();
        this.secondId = context.getSecondId();
        this.operation = context.getOperation();
        this.secureResource = context.isSecureResource();
        this.offset = context.getOffset();
        this.limit = context.getLimit();
        this.order = context.getOrder();
        this.url = context.getAfterUrl();
    }

    @Override
    public String getMethod() {
        return GET;
    }

    @Override
    public String getUrl() {
        if (url != null && url.length() > 0) {
            return url;
        }
        String baseUrl = ApiRequest.createBaseURL(ServerProtocol.API_AUTHORITY, ServerProtocol.GET_FRIENDS_OPERATION_PATH);
        QueryString qs = new QueryString();
        qs.add(StringSet.first_id, firstId);
        qs.add(StringSet.second_id, secondId);
        qs.add(StringSet.operator, operation.name);
        qs.add(StringSet.secure_resource, String.valueOf(secureResource));
        qs.add(StringSet.offset, String.valueOf(offset));
        qs.add(StringSet.limit, String.valueOf(limit));
        qs.add(StringSet.order, order);
        return baseUrl + "?" + qs.toString();
    }
}
