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
package com.kakao.kakaostory.response;

import com.kakao.auth.network.response.JSONArrayResponse;
import com.kakao.kakaostory.response.model.MyStoryInfo;
import com.kakao.network.response.ResponseBody;
import com.kakao.network.response.ResponseBody.ResponseBodyException;
import com.kakao.network.response.ResponseBodyArray;
import com.kakao.network.response.ResponseData;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.HttpURLConnection;
import java.util.List;

/**
 * @author leoshin, created at 15. 8. 4..
 */
public class GetMyStoryListResponse extends JSONArrayResponse {
    private final List<MyStoryInfo> myStoryInfoList;
    public GetMyStoryListResponse(ResponseData responseData) throws ResponseBodyException, ApiResponseStatusError {
        super(responseData);

        this.myStoryInfoList = bodyArray.getConvertedList(ARRAY_CONVERTER);
    }

    public List<MyStoryInfo> getMyStoryInfoList() {
        return myStoryInfoList;
    }

    public static final ResponseBodyArray.ArrayConverter<ResponseBody, MyStoryInfo> ARRAY_CONVERTER = new ResponseBodyArray.ArrayConverter<ResponseBody, MyStoryInfo>() {
        @Override
        public ResponseBody fromArray(JSONArray array, int i) throws ResponseBodyException {
            try {
                return new ResponseBody(HttpURLConnection.HTTP_OK, array.getJSONObject(i));
            } catch (JSONException e) {
                throw new ResponseBodyException("");
            }
        }

        @Override
        public MyStoryInfo convert(ResponseBody o) throws ResponseBodyException {
            return new MyStoryInfo(o);
        }
    };
}
