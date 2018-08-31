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
package com.kakao.friends.api;

import com.kakao.auth.SingleNetworkTask;
import com.kakao.friends.FriendContext;
import com.kakao.friends.FriendOperationContext;
import com.kakao.friends.request.FriendsOperationRequest;
import com.kakao.friends.request.FriendsRequest;
import com.kakao.friends.response.FriendsResponse;
import com.kakao.network.response.ResponseData;

/**
 * 친구 요청정보를 담고있는 context를 받아 친구정보를 요청한다.
 * context는 paging되는 next url정보를 가지고 있을 수 있으며, next url에 대해선 SDK가 채워주게된다.
 * @author leoshin, created at 15. 8. 5..
 */
public class FriendsApi {
    /**
     * 친구의 리스트를 얻어온다.
     * (제휴를 통해 권한이 부여된 특정 앱에서만 호출이 가능합니다.)
     * @param context {@link FriendContext} 친구리스트 요청정보를 담고있는 context
     * @return FriendsResponse containing list of friends
     * @throws Exception 요청 실패에 대한 exception
     */
    public static FriendsResponse requestFriends(FriendContext context) throws Exception {
        SingleNetworkTask networkTask = new SingleNetworkTask();
        ResponseData result = networkTask.requestApi(new FriendsRequest(context));
        FriendsResponse response = new FriendsResponse(result);

        context.setBeforeUrl(response.getBeforeUrl());
        context.setAfterUrl(response.getAfterUrl());
        context.setId(response.getId());

        return response;
    }

    /**
     * 친구 정보요청을 통해 얻은 데이터를 토대로 Operation을 수행할 수 있다.
     * (제휴를 통해 권한이 부여된 특정 앱에서만 호출이 가능합니다.)
     * @param context Operation에 필요한 데이터를 담은 Context.
     * @return FriendsResponse containing lsit of friends
     * @throws Exception 요청 실패에 대한 exception
     */
    public static FriendsResponse requestFriendsOperation(FriendOperationContext context) throws Exception {
        SingleNetworkTask networkTask = new SingleNetworkTask();
        ResponseData result = networkTask.requestApi(new FriendsOperationRequest(context));
        FriendsResponse response = new FriendsResponse(result);
        context.setBeforeUrl(response.getBeforeUrl());
        context.setAfterUrl(response.getAfterUrl());
        return response;
    }
}
