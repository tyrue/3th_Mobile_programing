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
package com.kakao.storage.response;

import com.kakao.auth.network.response.JSONObjectResponse;
import com.kakao.network.response.ResponseBody.ResponseBodyException;
import com.kakao.network.response.ResponseData;
import com.kakao.storage.StringSet;

/**
 * @author leoshin on 15. 9. 8.
 */
public class ImageUploadResponse extends JSONObjectResponse {
    private final String originImageUrl;
    private final String profileImageUrl;
    private final String thumbnailImageUrl;

    public ImageUploadResponse(ResponseData responseData) throws ResponseBodyException, ApiResponseStatusError {
        super(responseData);

        originImageUrl = body.optString(StringSet.origin_image, null);
        profileImageUrl = body.optString(StringSet.profile_image, null);
        thumbnailImageUrl = body.optString(StringSet.thumbnail_image, null);
    }

    public String getOriginImageUrl() {
        return originImageUrl;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getThumbnailImageUrl() {
        return thumbnailImageUrl;
    }

    @Override
    public String toString() {
        return "originImageUrl : " + originImageUrl + "\n\n" +
                ", profileImageUrl : " + profileImageUrl + "\n\n" +
                ", thumbnailImageUrl : " + thumbnailImageUrl;
    }
}
