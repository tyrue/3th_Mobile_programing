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
package com.kakao.kakaotalk;

import com.kakao.auth.common.PageableContext;

/**
 * 카카오톡의 채팅방을 얻어오는 API에서 사용된다.
 * 한번에 얻어갈 수 있는 채팅방의 수가 제한적이기때문에 현재 요청했던 상태를 관리해주는 용도로 사용된다.
 * (제휴를 통해 권한이 부여된 특정 앱에서만 호출이 가능합니다.)
 * @author leoshin on 15. 9. 4.
 */
public class ChatListContext extends PageableContext {
    // 가져올 챗방 리스트의 시작 offset
    private final int fromId;

    // 한 페이지에 가져올 챗방의 수. maximum 30개까지 가져올 수 있음
    private final int limit;

    // 챗방리스트 정렬방법. asc, desc 중 하나의 오더로 정렬
    private final String order;

    private final ChatFilterBuilder filterBuilder;

    private ChatListContext(ChatFilterBuilder filterBuilder, int fromId, int limit, String order) {
        this.filterBuilder = filterBuilder;
        this.fromId = fromId;
        this.limit = limit;
        this.order = order;
    }

    public static ChatListContext createContext(final ChatFilterBuilder filterBuilder, final int fromId, final int limit, final String order) {
        return new ChatListContext(filterBuilder, fromId, limit, order);
    }

    /**
     * 가져올 챗방 리스트의 시작 offset
     * @return 가져올 챗방 리스트의 시작 offset
     */
    public int getFromId() {
        return fromId;
    }

    /**
     *  한 페이지에 가져올 챗방의 수. maximum 30개까지 가져올 수 있음
     * @return  한 페이지에 가져올 챗방의 수. maximum 30개까지 가져올 수 있음
     */
    public int getLimit() {
        return limit;
    }

    /**
     * 챗방리스트 정렬방법. asc, desc 중 하나의 오더로 정렬
     * @return 챗방리스트 정렬방법. asc, desc 중 하나의 오더로 정렬
     */
    public String getOrder() {
        return order;
    }

    /**
     * 채팅방 리스트 filter.
     * @return 채팅방 리스트 filter.
     */
    public String getFilterString() {
        return filterBuilder.build();
    }
}
