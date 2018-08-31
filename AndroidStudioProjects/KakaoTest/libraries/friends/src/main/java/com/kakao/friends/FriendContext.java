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
package com.kakao.friends;

import com.kakao.auth.common.PageableContext;
import com.kakao.friends.request.FriendsRequest.FriendFilter;
import com.kakao.friends.request.FriendsRequest.FriendOrder;
import com.kakao.friends.request.FriendsRequest.FriendType;

public class FriendContext extends PageableContext {
    private final FriendType friendType;
    private final FriendFilter friendFilter;
    private final FriendOrder friendOrder;
    private final boolean secureResource;
    private final int offset;
    private final int limit;
    private final String order;
    private String id;

    private FriendContext(FriendType friendType, FriendFilter friendFilter, FriendOrder friendOrder, boolean secureResource, int offset, int limit, String order) {
        this.friendType = friendType;
        this.friendFilter = friendFilter;
        this.friendOrder = friendOrder;
        this.secureResource = secureResource;
        this.offset = offset;
        this.limit = limit;
        this.order = order;
    }

    /**
     * 친구의 리스트를 얻어오는데 필요한 데이터 셋을 생성한다.
     * 친구의 리스트는 limit별로 paging되어서 내려올 수 있기 때문에 생성되는 context에 다음 요청 url정보를 가지고 있게 되며,
     * next요청 url정보는 SDK에서 응답에 성공할 경우 채워준다.
     * @param friendType {@link FriendType} 친구의 타입
     * @param friendFilter {@link FriendFilter} 친구리스트 필터 방법
     * @param friendOrder {@link FriendOrder} 친구리스트 정렬 대상.
     * @param secureResource 이미지 url을 secure url로 받을 것인지 여부(http/https)
     * @param offset 가져올 친구 리스트의 시작 offset. default 0
     * @param limit 한 페이지에 가져올 친구의 수, default 100,  max 2000
     * @param order 친구리스트 정렬방법. asc, desc 중 하나의 오더로 정렬
     */
    public static FriendContext createContext(FriendType friendType, FriendFilter friendFilter, FriendOrder friendOrder, boolean secureResource, int offset, int limit, String order) {
        return new FriendContext(friendType, friendFilter, friendOrder, secureResource, offset, limit, order);
    }

    public FriendType getFriendType() {
        return friendType;
    }

    public FriendFilter getFriendFilter() {
        return friendFilter;
    }

    public FriendOrder getFriendOrder() {
        return friendOrder;
    }

    public boolean isSecureResource() {
        return secureResource;
    }

    public int getOffset() {
        return offset;
    }

    public int getLimit() {
        return limit;
    }

    public String getOrder() {
        return order;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * 응답받은 Friends Request에 대한 고유의 id값.
     * id 값을 기반으로 Friends Operation을 수행할 수 있다.
     * @return 응답받은 Friends Request에 대한 고유의 id값.
     */
    public String getId() {
        return id;
    }

}

