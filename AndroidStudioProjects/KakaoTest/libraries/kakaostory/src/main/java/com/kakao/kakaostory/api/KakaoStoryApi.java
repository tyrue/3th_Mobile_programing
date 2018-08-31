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

package com.kakao.kakaostory.api;

import com.kakao.auth.SingleNetworkTask;
import com.kakao.auth.network.response.ApiResponse.BlankApiResponse;
import com.kakao.friends.FriendContext;
import com.kakao.friends.api.FriendsApi;
import com.kakao.friends.response.FriendsResponse;
import com.kakao.kakaostory.request.CheckStoryUserRequest;
import com.kakao.kakaostory.request.DeleteMyStoryRequest;
import com.kakao.kakaostory.request.GetMyStoryListRequest;
import com.kakao.kakaostory.request.GetMyStoryRequest;
import com.kakao.kakaostory.request.LinkInfoRequest;
import com.kakao.kakaostory.request.MultiUploadRequest;
import com.kakao.kakaostory.request.PostLinkRequest;
import com.kakao.kakaostory.request.PostNoteRequest;
import com.kakao.kakaostory.request.PostPhotoRequest;
import com.kakao.kakaostory.request.PostRequest.StoryPermission;
import com.kakao.kakaostory.request.ProfileRequest;
import com.kakao.kakaostory.response.CheckStoryUserResponse;
import com.kakao.kakaostory.response.GetMyStoryListResponse;
import com.kakao.kakaostory.response.LinkInfoResponse;
import com.kakao.kakaostory.response.MultiUploadResponse;
import com.kakao.kakaostory.response.ProfileResponse;
import com.kakao.kakaostory.response.StoryPostResponse;
import com.kakao.network.response.ResponseData;

import java.io.File;
import java.util.List;

/**
 * 카카오스토리 API 요청을 담당한다.
 * @author MJ
 */
public class KakaoStoryApi {
    /**
     * 카카오스토리 프로필 요청
     */
    public static void requestProfile() throws Exception {
        requestProfile(false);
    }

    /**
     * 카카오스토리 프로필 요청
     * @param secureResource 이미지 url을 https로 반환할지 여부
     * @return ProfileResponse for current user
     */
    public static ProfileResponse requestProfile(boolean secureResource) throws Exception {
        SingleNetworkTask networkTask = new SingleNetworkTask();
        ResponseData result = networkTask.requestApi(new ProfileRequest(secureResource));
        return new ProfileResponse(result);
    }

    /**
     * 지정한 id에 해당하는 카카오스토리 Activity 삭제 요청
     * @param id 삭제하고자하는 activity id
     * @return <code>true</code> if successful
     * @throws Exception if failed
     */
    public static boolean requestDeleteMyStory(String id) throws Exception {
        SingleNetworkTask networkTask = new SingleNetworkTask();
        ResponseData result = networkTask.requestApi(new DeleteMyStoryRequest(id));
        new BlankApiResponse(result);
        return true;
    }

    /**
     * 카카오스토리 친구 리스트를 요청한다. Friends에 대한 접근권한이 있는 경우에만 얻어올 수 있다.
     * (제휴를 통해 권한이 부여된 특정 앱에서만 호출이 가능합니다.)
     * @param context {@link FriendContext} 친구리스트 요청정보를 담고있는 context
     * @return {@link FriendsResponse}
     * @throws Exception if failed
     */
    public static FriendsResponse requestFriends(final FriendContext context) throws Exception {
        return FriendsApi.requestFriends(context);
    }

    public static MultiUploadResponse requestMultiUpload(List<File> fileList) throws Exception {
        SingleNetworkTask networkTask = new SingleNetworkTask();
        ResponseData result = networkTask.requestApi(new MultiUploadRequest(fileList));
        return new MultiUploadResponse(result);
    }

    /**
     * 카카오스토리에 이미지 포스팅 요청.
     * @param fileList 요청할 이미지 경로.
     * @param content 카카오 스토리에 포스팅할 본문 내용. 2048자 제한
     * @param permission 포스팅하는 글의 공개 여부(친구/전체/나만보기)
     * @param enableShare 공개 허용 여부
     * @param androidExecParma 카카오 스토리에서 '해당 앱으로 이동' 버튼을 눌렀을 때 안드로이드 앱 실행 URL에 같이 붙여줄 파라미터. Kakao Developers_에서 안드로이드 앱 설정을 했을 경우에만 유효
     * @param iosExecParam 카카오 스토리에서 '해당 앱으로 이동' 버튼을 눌렀을 때 iOS 앱 실행 URL에 같이 붙여줄 파라미터. Kakao Developers_에서 iOS 앱 설정을 했을 경우에만 유효
     * @return StoryPostResponse
     * @throws Exception
     */
    public static StoryPostResponse requestPostPhoto(List<File> fileList,
                                        String content,
                                        StoryPermission permission,
                                        boolean enableShare,
                                        String androidExecParma,
                                        String iosExecParam,
                                        String androidMarketParam,
                                        String iosMarketParam) throws Exception {

        SingleNetworkTask networkTask = new SingleNetworkTask();

        // upload photo
        List<String> imageUrlList = requestMultiUpload(fileList).getImageUrlList();

        // post photo
        ResponseData result = networkTask.requestApi(new PostPhotoRequest(imageUrlList, content, permission, enableShare, androidExecParma, iosExecParam, androidMarketParam, iosMarketParam));
        return new StoryPostResponse(result);
    }

    /**
     * 카카오스토리에 포스팅 요청
     * @param content 카카오 스토리에 포스팅할 본문 내용. 2048자 제한
     * @param permission 포스팅하는 글의 공개 여부(친구/전체/나만보기)
     * @param enableShare 공개 허용 여부
     * @param androidExecParam 카카오 스토리에서 '해당 앱으로 이동' 버튼을 눌렀을 때 안드로이드 앱 실행 URL에 같이 붙여줄 파라미터. Kakao Developers_에서 안드로이드 앱 설정을 했을 경우에만 유효
     * @param iosExecParam 카카오 스토리에서 '해당 앱으로 이동' 버튼을 눌렀을 때 iOS 앱 실행 URL에 같이 붙여줄 파라미터. Kakao Developers_에서 iOS 앱 설정을 했을 경우에만 유효
     */
    public static StoryPostResponse requestPostNote(String content,
                                        StoryPermission permission,
                                        boolean enableShare,
                                        String androidExecParam,
                                        String iosExecParam,
                                        String androidMarketParam,
                                        String iosMarketParam) throws Exception {
        SingleNetworkTask networkTask = new SingleNetworkTask();
        ResponseData result = networkTask.requestApi(new PostNoteRequest(content, permission, enableShare, androidExecParam, iosExecParam, androidMarketParam, iosMarketParam));
        return new StoryPostResponse(result);
    }

    /**
     * 카카오스토리에 포스팅 요청
     * @param content 카카오 스토리에 포스팅할 본문 내용. 2048자 제한
     * @param linkUrl 스크랩 타켓 URL
     * @param permission 포스팅하는 글의 공개 여부(친구/전체/나만보기)
     * @param enableShare 공개 허용 여부
     * @param androidExecParma 카카오 스토리에서 '해당 앱으로 이동' 버튼을 눌렀을 때 안드로이드 앱 실행 URL에 같이 붙여줄 파라미터. Kakao Developers_에서 안드로이드 앱 설정을 했을 경우에만 유효
     * @param iosExecParam 카카오 스토리에서 '해당 앱으로 이동' 버튼을 눌렀을 때 iOS 앱 실행 URL에 같이 붙여줄 파라미터. Kakao Developers_에서 iOS 앱 설정을 했을 경우에만 유효
     */
    public static StoryPostResponse requestPostLink(String linkUrl,
                                        String content,
                                        StoryPermission permission,
                                        boolean enableShare,
                                        String androidExecParma,
                                        String iosExecParam,
                                        String androidMarketParam,
                                        String iosMarketParam) throws Exception {
        SingleNetworkTask networkTask = new SingleNetworkTask();
        ResponseData result = networkTask.requestApi(new LinkInfoRequest(linkUrl));
        result = networkTask.requestApi(new PostLinkRequest(content, new String(result.getData()), permission, enableShare, androidExecParma, iosExecParam, androidMarketParam, iosMarketParam));
        return new StoryPostResponse(result);
    }

    /**
     * 카카오스토리에 링크 포스팅 요청전에 링크로 부터 정보를 얻어오는 과정.
     * @param linkUrl 스크랩 타켓 URL
     */
    public static LinkInfoResponse requestGetLinkInfo(String linkUrl) throws Exception {
        SingleNetworkTask networkTask = new SingleNetworkTask();
        ResponseData result = networkTask.requestApi(new LinkInfoRequest(linkUrl));
        return new LinkInfoResponse(result);
    }

    /**
     * 지정한 id에 해당하는 카카오스토리 Activity 요청
     * @param storyId 얻어 오고자하는 activity id
     */
    public static StoryPostResponse requestGetMyStory(String storyId) throws Exception {
        SingleNetworkTask networkTask = new SingleNetworkTask();
        ResponseData result = networkTask.requestApi(new GetMyStoryRequest(storyId));
        return new StoryPostResponse(result);
    }

    /**
     * 현 사용자가 카카오스토리 가입자인지 확인하는 요청
     */
    public static boolean requestIsStoryUser() throws Exception {
        SingleNetworkTask networkTask = new SingleNetworkTask();
        ResponseData result = networkTask.requestApi(new CheckStoryUserRequest());
        return new CheckStoryUserResponse(result).isStoryUser();
    }

    /**
     * 지정한 id로부터 최신 약 18개 카카오스토리 Activity 요청
     * @param lastMyStoryId 얻고자 하는 마지막 activity id (해당 id의 activity는 결과에 포함되지 않는다.)
     */
    public static GetMyStoryListResponse requestGetMyStories(String lastMyStoryId) throws Exception {
        SingleNetworkTask networkTask = new SingleNetworkTask();
        ResponseData result = networkTask.requestApi(new GetMyStoryListRequest(lastMyStoryId));
        return new GetMyStoryListResponse(result);
    }
}
