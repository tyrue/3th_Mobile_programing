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
package com.kakao.storage;

import com.kakao.auth.ApiResponseCallback;
import com.kakao.network.tasks.KakaoResultTask;
import com.kakao.network.tasks.KakaoTaskQueue;
import com.kakao.storage.api.StorageApi;
import com.kakao.storage.response.ImageUploadResponse;

import java.io.File;

/**
 * @author leoshin on 15. 9. 8.
 */
public class StorageService {
    /**
     * 이미지를 업로드 한다.
     * 5M 이하의 이미지를 업로드 할 수 있다.
     * (제휴를 통해 권한이 부여된 특정 앱에서만 호출이 가능합니다.)
     * @param callback 요청 결과에 대한 callback
     * @param imageFile 업로드할 이미지 파일
     */
    @Deprecated
    public static void requestImageUpload(final ApiResponseCallback<ImageUploadResponse> callback, final File imageFile) {
        requestImageUpload(callback, imageFile, false);
    }

    /**
     * 이미지를 업로드 한다.
     * 5M 이하의 이미지를 업로드 할 수 있다.
     * (제휴를 통해 권한이 부여된 특정 앱에서만 호출이 가능합니다.)
     * @param callback 요청 결과에 대한 callback
     * @param imageFile 업로드할 이미지 파일
     * @param secureResource 이미지 url을 https로 반환할지 여부.
     */
    @Deprecated
    public static void requestImageUpload(final ApiResponseCallback<ImageUploadResponse> callback, final File imageFile, final boolean secureResource) {
        KakaoTaskQueue.getInstance().addTask(new KakaoResultTask<ImageUploadResponse>(callback) {
            @Override
            public ImageUploadResponse call() throws Exception {
                return StorageApi.requestImageUpload(imageFile, secureResource);
            }
        });
    }
}
