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
package com.kakao.kakaostory.response;

import com.kakao.auth.network.response.JSONArrayResponse;
import com.kakao.network.response.ResponseBody.ResponseBodyException;
import com.kakao.network.response.ResponseBodyArray;
import com.kakao.network.response.ResponseData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author leoshin, created at 15. 7. 31..
 */
public class MultiUploadResponse extends JSONArrayResponse {
    private List<String> imageUrlList = new ArrayList<String>();

    public MultiUploadResponse(ResponseData responseData) throws ResponseBodyException, ApiResponseStatusError {
        super(responseData);
        this.imageUrlList = bodyArray.optConvertedList(ResponseBodyArray.STRING_CONVERTER, Collections.<String>emptyList());
    }

    public List<String> getImageUrlList() {
        return imageUrlList;
    }
}
