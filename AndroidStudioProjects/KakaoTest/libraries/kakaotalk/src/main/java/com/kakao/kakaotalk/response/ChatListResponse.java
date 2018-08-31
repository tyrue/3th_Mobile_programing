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
package com.kakao.kakaotalk.response;

import com.kakao.auth.network.response.JSONObjectResponse;
import com.kakao.friends.StringSet;
import com.kakao.kakaotalk.response.model.ChatInfo;
import com.kakao.network.response.ResponseBody.ResponseBodyException;
import com.kakao.network.response.ResponseData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author leo.shin
 * Created by leoshin on 15. 8. 25..
 */
public class ChatListResponse extends JSONObjectResponse {
    final private List<ChatInfo> chatInfoList;
    final private int totalCount;
    private String beforeUrl;
    private String afterUrl;

    public ChatListResponse(ResponseData responseData) throws ResponseBodyException, ApiResponseStatusError {
        super(responseData);
        this.chatInfoList = body.optConvertedList(StringSet.elements, ChatInfo.CONVERTER, new ArrayList<ChatInfo>());
        this.totalCount = body.optInt(StringSet.total_count, 0);
        this.beforeUrl = body.optString(StringSet.before_url, null);
        this.afterUrl = body.optString(StringSet.after_url, null);
    }

    /**
     * paging된 chat list정보를 모은다.
     * @param response 기존에 paging되어 받아온 response에 merge시킬 ChatList 정보.
     */
    public void merge(ChatListResponse response) {
        this.chatInfoList.addAll(response.getChatInfoList());
        this.beforeUrl = response.getBeforeUrl();
        this.afterUrl = response.getAfterUrl();
    }

    public List<ChatInfo> getChatInfoList() {
        return chatInfoList;
    }

    public String getBeforeUrl() {
        return beforeUrl;
    }

    public String getAfterUrl() {
        return afterUrl;
    }

    public int getTotalCount() {
        return totalCount;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (chatInfoList != null) {
            for(ChatInfo info : chatInfoList) {
                builder.append("\n[").append(info.toString()).append("]");
            }
        }

        builder.append("totalCount : ").append(totalCount)
                .append(", beforeUrl : ").append(beforeUrl)
                .append(", afterUrl : ").append(afterUrl);

        return builder.toString();
    }
}
