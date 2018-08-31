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
package com.kakao.kakaostory;

import com.kakao.kakaostory.api.KakaoStoryApi;
import com.kakao.network.ServerProtocol;
import com.kakao.network.tasks.KakaoResultTask;
import com.kakao.network.tasks.KakaoTaskQueue;
import com.kakao.friends.FriendContext;
import com.kakao.friends.response.FriendsResponse;
import com.kakao.kakaostory.callback.StoryResponseCallback;
import com.kakao.kakaostory.request.PostRequest.StoryPermission;
import com.kakao.kakaostory.response.LinkInfoResponse;
import com.kakao.kakaostory.response.ProfileResponse;
import com.kakao.kakaostory.response.StoryPostResponse;
import com.kakao.kakaostory.response.model.MyStoryInfo;
import com.kakao.util.KakaoParameterException;
import com.kakao.util.KakaoParameterException.ERROR_CODE;
import com.kakao.util.helper.Utility;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 카카오스토리 API 요청을 담당한다.
 * @author MJ
 */
public class KakaoStoryService {
    private static final int MAX_POST_TEXT_LENGTH = 2048;

    /**
     * 스토리 포스팅의 종류를 나타낸다.
     */
    public enum StoryType {
        /**
         * Note 타입의 스토리로 text가 꼭 포함되어야 한다.
         */
        NOTE("NOTE", ServerProtocol.STORY_POST_NOTE_PATH),
        /**
         * Photo 타입의 스토리로, 이미지를 먼저 업로드 한후에 이미지 url을 꼭 포함되어야 한다.
         */
        PHOTO("PHOTO", ServerProtocol.STORY_POST_PHOTO_PATH),
        /**
         * Link 타입의 스토리로, url로 부터 먼저 정보를 가지고 온 후, 그 결과가 포함되어야 한다.
         */
        LINK("LINK", ServerProtocol.STORY_POST_LINK_PATH),

        NOT_SUPPORTED("NOT_SUPPORTED", null);

        private final String requestPath;
        private final String name;

        StoryType(final String name, final String requestPath) {
            this.name = name;
            this.requestPath = requestPath;
        }

        String getRequestPath() {
            return requestPath;
        }

        protected static StoryType getType(final String name){
            for(final StoryType type : StoryType.values()){
                if(type.name.equals(name))
                    return type;
            }
            return NOT_SUPPORTED;
        }
    }

    /**
     * 카카오스토리 프로필 요청
     * @param callback 프로필 요청 결과에 대한 handler
     */
    public static void requestProfile(final StoryResponseCallback<ProfileResponse> callback) {
        requestProfile(callback, false);
    }

    /**
     * 카카오스토리 프로필 요청
     * @param callback 프로필 요청 결과에 대한 handler
     * @param secureResource 이미지 url을 https로 반환할지 여부
     */
    public static void requestProfile(final StoryResponseCallback<ProfileResponse> callback, final boolean secureResource) {
        KakaoTaskQueue.getInstance().addTask(new KakaoResultTask<ProfileResponse>(callback) {
            @Override
            public ProfileResponse call() throws Exception {
                return KakaoStoryApi.requestProfile(secureResource);
            }
        });
    }

    /**
     * 지정한 id에 해당하는 카카오스토리 Activity 삭제 요청
     * @param callback 액티비티 요청 결과에 대한 handler
     * @param id 삭제하고자하는 activity id
     * @throws KakaoParameterException if parameter is invalid
     */
    public static void requestDeleteMyStory(final StoryResponseCallback<Boolean> callback, final String id) throws KakaoParameterException {
        if (id == null || id.length() < 0) {
            throw new KakaoParameterException(ERROR_CODE.CORE_PARAMETER_MISSING, "story id is empty.");
        }

        KakaoTaskQueue.getInstance().addTask(new KakaoResultTask<Boolean>(callback) {
            @Override
            public Boolean call() throws Exception {
                return KakaoStoryApi.requestDeleteMyStory(id);
            }
        });
    }

    /**
     * 카카오스토리 친구 리스트를 요청한다. Friends에 대한 접근권한이 있는 경우에만 얻어올 수 있다.
     * (제휴를 통해 권한이 부여된 특정 앱에서만 호출이 가능합니다.)
     * @param callback 친구리스트 요청 결과에 대한 callback
     * @param context 친구리스트 요청정보를 담고있는 context
     */
    public static void requestFriends(final StoryResponseCallback<FriendsResponse> callback, final FriendContext context) {
        KakaoTaskQueue.getInstance().addTask(new KakaoResultTask<FriendsResponse>(callback) {
            @Override
            public FriendsResponse call() throws Exception {
                return KakaoStoryApi.requestFriends(context);
            }
        });
    }

    /**
     * 카카오스토리에 이미지 포스팅 요청.
     * @param callback 포스팅 요청 결과에 대한 callback
     * @param fileList 요청할 이미지 경로.
     * @param content 카카오 스토리에 포스팅할 본문 내용. 2048자 제한
     */
    public static void requestPostPhoto(final StoryResponseCallback<MyStoryInfo> callback,
                                        final List<File> fileList,
                                        final String content ) {

        KakaoTaskQueue.getInstance().addTask(new KakaoResultTask<MyStoryInfo>(callback) {
            @Override
            public MyStoryInfo call() throws Exception {
                // post photo
                StoryPostResponse result = KakaoStoryApi.requestPostPhoto(fileList, content, StoryPermission.PUBLIC, true, null, null, null, null);
                return result.getMyStoryInfo();
            }
        });
    }

    /**
     * 카카오스토리에 이미지 포스팅 요청.
     * @param callback 포스팅 요청 결과에 대한 callback
     * @param fileList 요청할 이미지 경로.
     * @param content 카카오 스토리에 포스팅할 본문 내용. 2048자 제한
     * @param permission 포스팅하는 글의 공개 여부(친구/전체/나만보기)
     * @param enableShare 공개 허용 여부
     * @param androidExecParam 카카오 스토리에서 '해당 앱으로 이동' 버튼을 눌렀을 때 안드로이드 앱 실행 URL에 같이 붙여줄 파라미터. Kakao Developers_에서 안드로이드 앱 설정을 했을 경우에만 유효
     * @param iosExecParam 카카오 스토리에서 '해당 앱으로 이동' 버튼을 눌렀을 때 iOS 앱 실행 URL에 같이 붙여줄 파라미터. Kakao Developers_에서 iOS 앱 설정을 했을 경우에만 유효
     * @param androidMarketParam parameters to be passed to application when installed via google play store. Format should match URL query parameter.
     * @param iosMarketParam parameters to be passed to application when installed via app store. Format should match URL query parameter.
     * @throws KakaoParameterException if parameter is invalid
     */
    public static void requestPostPhoto(final StoryResponseCallback<MyStoryInfo> callback,
                                       final List<File> fileList,
                                       final String content,
                                       final StoryPermission permission,
                                       final boolean enableShare,
                                       final String androidExecParam,
                                       final String iosExecParam,
                                       final String androidMarketParam,
                                       final String iosMarketParam) throws KakaoParameterException {

        if (content != null && content.length() > MAX_POST_TEXT_LENGTH) {
            throw new KakaoParameterException(ERROR_CODE.CORE_PARAMETER_MISSING, "Overflow content length.");
        }

        if (fileList == null || fileList.size() < 0) {
            throw new KakaoParameterException(ERROR_CODE.CORE_PARAMETER_MISSING, "image file list is empty");
        }

        KakaoTaskQueue.getInstance().addTask(new KakaoResultTask<MyStoryInfo>(callback) {
            @Override
            public MyStoryInfo call() throws Exception {
                // post photo
                StoryPostResponse result = KakaoStoryApi.requestPostPhoto(fileList, content, permission, enableShare, androidExecParam, iosExecParam, androidMarketParam, iosMarketParam);
                return result.getMyStoryInfo();
            }
        });
    }

    /**
     * 카카오스토리에 이미지 포스팅 요청.
     * @param callback 포스팅 요청 결과에 대한 callback
     * @param fileList 요청할 이미지 경로.
     * @param content 카카오 스토리에 포스팅할 본문 내용. 2048자 제한
     * @param permission 포스팅하는 글의 공개 여부(친구/전체/나만보기)
     * @param enableShare 공개 허용 여부
     * @param androidExecParam 카카오 스토리에서 '해당 앱으로 이동' 버튼을 눌렀을 때 안드로이드 앱 실행 URL에 같이 붙여줄 파라미터. Kakao Developers_에서 안드로이드 앱 설정을 했을 경우에만 유효
     * @param iosExecParam 카카오 스토리에서 '해당 앱으로 이동' 버튼을 눌렀을 때 iOS 앱 실행 URL에 같이 붙여줄 파라미터. Kakao Developers_에서 iOS 앱 설정을 했을 경우에만 유효
     * @param androidMarketParam parameters to be passed to application when installed via google play store. Format should match URL query parameter.
     * @param iosMarketParam parameters to be passed to application when installed via app store. Format should match URL query parameter.
     * @throws KakaoParameterException if parameter is invalid
     */
    public static void requestPostPhoto(final StoryResponseCallback<MyStoryInfo> callback,
                                        final List<File> fileList,
                                        final String content,
                                        final StoryPermission permission,
                                        final boolean enableShare,
                                        final Map<String, String> androidExecParam,
                                        final Map<String, String> iosExecParam,
                                        final Map<String, String> androidMarketParam,
                                        final Map<String, String> iosMarketParam) throws KakaoParameterException {

        String androidExecParamString = Utility.buildQueryString(androidExecParam);
        String iosExecParamString = Utility.buildQueryString(iosExecParam);
        String androidMarketParamString = Utility.buildQueryString(androidMarketParam);
        String iosMarketParamsString = Utility.buildQueryString(iosMarketParam);
        requestPostPhoto(callback, fileList, content, permission, enableShare, androidExecParamString, iosExecParamString, androidMarketParamString, iosMarketParamsString);
    }

    /**
     * 카카오스토리에 포스팅 요청
     * @param callback 포스팅 요청 결과에 대한 callback
     * @param content 카카오 스토리에 포스팅할 본문 내용. 2048자 제한
     */
    public static void requestPostNote(final StoryResponseCallback<MyStoryInfo> callback,
                                       final String content) {
        KakaoTaskQueue.getInstance().addTask(new KakaoResultTask<MyStoryInfo>(callback) {
            @Override
            public MyStoryInfo call() throws Exception {
                StoryPostResponse result = KakaoStoryApi.requestPostNote(content, StoryPermission.PUBLIC, true, null, null, null, null);
                return result.getMyStoryInfo();
            }
        });
    }

    /**
     * 카카오스토리에 포스팅 요청
     * @param callback 포스팅 요청 결과에 대한 callback
     * @param content 카카오 스토리에 포스팅할 본문 내용. 2048자 제한
     * @param permission 포스팅하는 글의 공개 여부(친구/전체/나만보기)
     * @param enableShare 공개 허용 여부
     * @param androidExecParam 카카오 스토리에서 '해당 앱으로 이동' 버튼을 눌렀을 때 안드로이드 앱 실행 URL에 같이 붙여줄 파라미터. Kakao Developers_에서 안드로이드 앱 설정을 했을 경우에만 유효
     * @param iosExecParam 카카오 스토리에서 '해당 앱으로 이동' 버튼을 눌렀을 때 iOS 앱 실행 URL에 같이 붙여줄 파라미터. Kakao Developers_에서 iOS 앱 설정을 했을 경우에만 유효
     * @param androidMarketParam parameters to be passed to application when installed via google play store. Format should match URL query parameter.
     * @param iosMarketParam parameters to be passed to application when installed via app store. Format should match URL query parameter.
     * @throws KakaoParameterException if parameter is invalid
     */
    public static void requestPostNote(final StoryResponseCallback<MyStoryInfo> callback,
                                       final String content,
                                       final StoryPermission permission,
                                       final boolean enableShare,
                                       final String androidExecParam,
                                       final String iosExecParam,
                                       final String androidMarketParam,
                                       final String iosMarketParam) throws KakaoParameterException {

        if (content == null || content.length() > MAX_POST_TEXT_LENGTH) {
            throw new KakaoParameterException("Overflow content length.");
        }

        KakaoTaskQueue.getInstance().addTask(new KakaoResultTask<MyStoryInfo>(callback) {
            @Override
            public MyStoryInfo call() throws Exception {
                StoryPostResponse result = KakaoStoryApi.requestPostNote(content, permission, enableShare, androidExecParam, iosExecParam, androidMarketParam, iosMarketParam);
                return result.getMyStoryInfo();
            }
        });
    }

    /**
     * 카카오스토리에 포스팅 요청
     * @param callback 포스팅 요청 결과에 대한 callback
     * @param content 카카오 스토리에 포스팅할 본문 내용. 2048자 제한
     * @param permission 포스팅하는 글의 공개 여부(친구/전체/나만보기)
     * @param enableShare 공개 허용 여부
     * @param androidExecParam 카카오 스토리에서 '해당 앱으로 이동' 버튼을 눌렀을 때 안드로이드 앱 실행 URL에 같이 붙여줄 파라미터. Kakao Developers_에서 안드로이드 앱 설정을 했을 경우에만 유효
     * @param iosExecParam 카카오 스토리에서 '해당 앱으로 이동' 버튼을 눌렀을 때 iOS 앱 실행 URL에 같이 붙여줄 파라미터. Kakao Developers_에서 iOS 앱 설정을 했을 경우에만 유효
     * @param androidMarketParam parameters to be passed to application when installed via google play store. Format should match URL query parameter.
     * @param iosMarketParam parameters to be passed to application when installed via app store. Format should match URL query parameter.
     * @throws KakaoParameterException if parameter is invalid
     */
    public static void requestPostNote(final StoryResponseCallback<MyStoryInfo> callback,
                                       final String content,
                                       final StoryPermission permission,
                                       final boolean enableShare,
                                       final Map<String, String> androidExecParam,
                                       final Map<String, String> iosExecParam,
                                       final Map<String, String> androidMarketParam,
                                       final Map<String, String> iosMarketParam) throws KakaoParameterException {

        String androidExecParamString = Utility.buildQueryString(androidExecParam);
        String iosExecParamString = Utility.buildQueryString(iosExecParam);
        String androidMarketParamString = Utility.buildQueryString(androidMarketParam);
        String iosMarketParamsString = Utility.buildQueryString(iosMarketParam);
        requestPostNote(callback, content, permission, enableShare, androidExecParamString, iosExecParamString, androidMarketParamString, iosMarketParamsString);
    }



    /**
     * 카카오스토리에 포스팅 요청
     * @param callback 포스팅 요청 결과에 대한 callback
     * @param content 카카오 스토리에 포스팅할 본문 내용. 2048자 제한
     * @param linkUrl 스크랩 타켓 URL
     */
    public static void requestPostLink(final StoryResponseCallback<MyStoryInfo> callback,
                                       final String linkUrl,
                                       final String content) {
        KakaoTaskQueue.getInstance().addTask(new KakaoResultTask<MyStoryInfo>(callback) {
            @Override
            public MyStoryInfo call() throws Exception {
                StoryPostResponse result = KakaoStoryApi.requestPostLink(linkUrl, content, StoryPermission.PUBLIC, true, null, null, null, null);
                return result.getMyStoryInfo();
            }
        });
    }

    /**
     * 카카오스토리에 포스팅 요청
     * @param callback 포스팅 요청 결과에 대한 callback
     * @param content 카카오 스토리에 포스팅할 본문 내용. 2048자 제한
     * @param linkUrl 스크랩 타켓 URL
     * @param permission 포스팅하는 글의 공개 여부(친구/전체/나만보기)
     * @param enableShare 공개 허용 여부
     * @param androidExecParam 카카오 스토리에서 '해당 앱으로 이동' 버튼을 눌렀을 때 안드로이드 앱 실행 URL에 같이 붙여줄 파라미터. Kakao Developers_에서 안드로이드 앱 설정을 했을 경우에만 유효
     * @param iosExecParam 카카오 스토리에서 '해당 앱으로 이동' 버튼을 눌렀을 때 iOS 앱 실행 URL에 같이 붙여줄 파라미터. Kakao Developers_에서 iOS 앱 설정을 했을 경우에만 유효
     * @param androidMarketParam parameters to be passed to application when installed via google play store. Format should match URL query parameter.
     * @param iosMarketParam parameters to be passed to application when installed via app store. Format should match URL query parameter.
     * @throws KakaoParameterException if parameter is invalid
     */
    public static void requestPostLink(final StoryResponseCallback<MyStoryInfo> callback,
                                       final String linkUrl,
                                       final String content,
                                       final StoryPermission permission,
                                       final boolean enableShare,
                                       final String androidExecParam,
                                       final String iosExecParam,
                                       final String androidMarketParam,
                                       final String iosMarketParam) throws KakaoParameterException {

        if (linkUrl == null || !linkUrl.startsWith("http")) {
            throw new KakaoParameterException(ERROR_CODE.CORE_PARAMETER_MISSING, "Both url and host of KakaoStoryLinkInfo are required. linkUrl=" + linkUrl);
        }

        if (content != null && content.length() < 0) {
            throw new KakaoParameterException(ERROR_CODE.CORE_PARAMETER_MISSING, "Overflow content length.");
        }

        KakaoTaskQueue.getInstance().addTask(new KakaoResultTask<MyStoryInfo>(callback) {
            @Override
            public MyStoryInfo call() throws Exception {
                StoryPostResponse result = KakaoStoryApi.requestPostLink(linkUrl, content, permission, enableShare, androidExecParam, iosExecParam, androidMarketParam, iosMarketParam);
                return result.getMyStoryInfo();
            }
        });
    }

    /**
     * 카카오스토리에 포스팅 요청
     * @param callback 포스팅 요청 결과에 대한 callback
     * @param content 카카오 스토리에 포스팅할 본문 내용. 2048자 제한
     * @param linkUrl 스크랩 타켓 URL
     * @param permission 포스팅하는 글의 공개 여부(친구/전체/나만보기)
     * @param enableShare 공개 허용 여부
     * @param androidExecParam 카카오 스토리에서 '해당 앱으로 이동' 버튼을 눌렀을 때 안드로이드 앱 실행 URL에 같이 붙여줄 파라미터. Kakao Developers_에서 안드로이드 앱 설정을 했을 경우에만 유효
     * @param iosExecParam 카카오 스토리에서 '해당 앱으로 이동' 버튼을 눌렀을 때 iOS 앱 실행 URL에 같이 붙여줄 파라미터. Kakao Developers_에서 iOS 앱 설정을 했을 경우에만 유효
     * @param androidMarketParam parameters to be passed to application when installed via google play store. Format should match URL query parameter.
     * @param iosMarketParam parameters to be passed to application when installed via app store. Format should match URL query parameter.
     * @throws KakaoParameterException if parameter is invalid
     */
    public static void requestPostLink(final StoryResponseCallback<MyStoryInfo> callback,
                                       final String linkUrl,
                                       final String content,
                                       final StoryPermission permission,
                                       final boolean enableShare,
                                       final Map<String, String> androidExecParam,
                                       final Map<String, String> iosExecParam,
                                       final Map<String, String> androidMarketParam,
                                       final Map<String, String> iosMarketParam) throws KakaoParameterException {

        String androidExecParamString = Utility.buildQueryString(androidExecParam);
        String iosExecParamString = Utility.buildQueryString(iosExecParam);
        String androidMarketParamString = Utility.buildQueryString(androidMarketParam);
        String iosMarketParamsString = Utility.buildQueryString(iosMarketParam);
        requestPostLink(callback, linkUrl, content, permission, enableShare, androidExecParamString, iosExecParamString, androidMarketParamString, iosMarketParamsString);
    }

    /**
     * 카카오스토리에 링크 포스팅 요청전에 링크로 부터 정보를 얻어오는 과정.
     * @param callback 스크랩 요청 결과에 대한 callback
     * @param linkUrl 스크랩 타켓 URL
     */
    public static void requestGetLinkInfo(final StoryResponseCallback<LinkInfoResponse> callback, final String linkUrl) {
        KakaoTaskQueue.getInstance().addTask(new KakaoResultTask<LinkInfoResponse>(callback) {
            @Override
            public LinkInfoResponse call() throws Exception {
                return KakaoStoryApi.requestGetLinkInfo(linkUrl);
            }
        });
    }

    /**
     * 지정한 id에 해당하는 카카오스토리 Activity 요청
     * @param callback Activity 요청 결과에 대한 callback
     * @param storyId 얻어 오고자하는 activity id
     * @throws KakaoParameterException if parameter is invalid
     */
    public static void requestGetMyStory(final StoryResponseCallback<MyStoryInfo> callback, final String storyId) throws KakaoParameterException {
        if (storyId == null || storyId.length() < 0) {
            throw new KakaoParameterException(ERROR_CODE.CORE_PARAMETER_MISSING, "story id is empty.");
        }

        KakaoTaskQueue.getInstance().addTask(new KakaoResultTask<MyStoryInfo>(callback) {
            @Override
            public MyStoryInfo call() throws Exception {
                return KakaoStoryApi.requestGetMyStory(storyId).getMyStoryInfo();
            }
        });
    }

    /**
     * 현 사용자가 카카오스토리 가입자인지 확인하는 요청
     * @param callback 요청 결과에 대한 callback
     */
    public static void requestIsStoryUser(final StoryResponseCallback<Boolean> callback) {
        KakaoTaskQueue.getInstance().addTask(new KakaoResultTask<Boolean>(callback) {
            @Override
            public Boolean call() throws Exception {
                return KakaoStoryApi.requestIsStoryUser();
            }
        });
    }

    /**
     * 지정한 id로부터 최신 약 18개 카카오스토리 Activity 요청
     * @param callback Activity 요청 결과에 대한 callback
     * @param lastMyStoryId 얻고자 하는 마지막 activity id (해당 id의 activity는 결과에 포함되지 않는다.)
     */
    public static void requestGetMyStories(final StoryResponseCallback<List<MyStoryInfo>> callback, final String lastMyStoryId) {
        KakaoTaskQueue.getInstance().addTask(new KakaoResultTask<List<MyStoryInfo>>(callback) {
            @Override
            public List<MyStoryInfo> call() throws Exception {
                return KakaoStoryApi.requestGetMyStories(lastMyStoryId).getMyStoryInfoList();
            }
        });
    }
}
