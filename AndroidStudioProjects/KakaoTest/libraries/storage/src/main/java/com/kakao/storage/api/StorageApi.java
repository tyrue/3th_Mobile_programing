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
package com.kakao.storage.api;

import com.kakao.auth.SingleNetworkTask;
import com.kakao.network.response.ResponseData;
import com.kakao.storage.request.ImageUploadRequest;
import com.kakao.storage.response.ImageUploadResponse;

import java.io.File;

/**
 * @author leoshin on 15. 9. 8.
 */
public class StorageApi {
    /**
     * 이미지를 업로드 한다.
     * 5M 이하의 이미지를 업로드 할 수 있다.
     * (제휴를 통해 권한이 부여된 특정 앱에서만 호출이 가능합니다.)
     * @param imageFile 업로드할 이미지 파일
     * @param secureResource 이미지 url을 https로 반환할지 여부.
     * @return 업로드한 이미지의 사이즈별 정보를 담은 response 객체.
     * @throws Exception 실패하였을경우의 정보를 담고있는 Exception
     */
    public static ImageUploadResponse requestImageUpload(File imageFile, boolean secureResource) throws Exception {
        SingleNetworkTask networkTask = new SingleNetworkTask();
        ResponseData result = networkTask.requestApi(new ImageUploadRequest(imageFile, secureResource));
        return new ImageUploadResponse(result);
    }
}
