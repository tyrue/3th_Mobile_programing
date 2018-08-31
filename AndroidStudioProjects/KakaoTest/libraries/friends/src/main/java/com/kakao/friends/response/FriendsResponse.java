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
package com.kakao.friends.response;

import com.kakao.auth.network.response.JSONObjectResponse;
import com.kakao.friends.StringSet;
import com.kakao.friends.response.model.FriendInfo;
import com.kakao.network.response.ResponseBody.ResponseBodyException;
import com.kakao.network.response.ResponseData;

import java.util.Collections;
import java.util.List;

/**
 * @author leo.shin
 */
public class FriendsResponse extends JSONObjectResponse {
    final private List<FriendInfo> friendInfoList;
    final private int totalCount;
    final private String id;
    private String beforeUrl;
    private String afterUrl;

    public FriendsResponse(ResponseData responseData) throws ResponseBodyException, ApiResponseStatusError {
        super(responseData);
        this.friendInfoList = body.optConvertedList(StringSet.elements, FriendInfo.CONVERTER, Collections.<FriendInfo>emptyList());
        this.totalCount = body.optInt(StringSet.total_count, 0);
        this.beforeUrl = body.optString(StringSet.before_url, null);
        this.afterUrl = body.optString(StringSet.after_url, null);
        this.id = body.optString(StringSet.id, null);
    }

    public List<FriendInfo> getFriendInfoList() {
        return friendInfoList;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public String getBeforeUrl() {
        return beforeUrl;
    }

    public String getAfterUrl() {
        return afterUrl;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (friendInfoList != null) {
            for(FriendInfo info : friendInfoList) {
                builder.append("\n[").append(info.toString()).append("]");
            }
        }

        builder.append("totalCount : ").append(totalCount)
                .append(", beforeUrl : ").append(beforeUrl)
                .append(", afterUrl : ").append(afterUrl)
                .append(", id : ").append(id);

        return builder.toString();
    }
}
