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
package com.kakao.kakaostory.request;

import com.kakao.network.ServerProtocol;
import com.kakao.auth.network.request.ApiRequest;
import com.kakao.kakaostory.StringSet;
import com.kakao.util.exception.ParameterMissingException;

import org.json.JSONArray;

import java.util.List;
import java.util.Map;

/**
 * @author leoshin, created at 15. 7. 31..
 */
public class PostPhotoRequest extends PostRequest {
    private final List<String> imageUrlList;

    public PostPhotoRequest(List<String> imageUrlList,
                            String content,
                            StoryPermission permission,
                            boolean enableShare,
                            String androidExecParam,
                            String iosExecParam,
                            String androidMarketParam,
                            String iosMarketParam) throws ParameterMissingException {
        super(content, permission, enableShare, androidExecParam, iosExecParam, androidMarketParam, iosMarketParam);
        if (imageUrlList == null || imageUrlList.size() <= 0) {
            throw new ParameterMissingException("imageUrlList is empty");
        }

        this.imageUrlList = imageUrlList;
    }

    @Override
    public String getUrl() {
        return ApiRequest.createBaseURL(ServerProtocol.API_AUTHORITY, ServerProtocol.STORY_POST_PHOTO_PATH);
    }

    @Override
    public Map<String, String> getParams() {
        Map<String, String> params = super.getParams();
        params.put(StringSet.image_url_list, new JSONArray(imageUrlList).toString());
        return params;
    }
}
