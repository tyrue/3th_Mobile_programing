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
package com.kakao.push.response;

import com.kakao.auth.network.response.JSONArrayResponse;
import com.kakao.network.response.ResponseBody;
import com.kakao.network.response.ResponseBody.ResponseBodyException;
import com.kakao.network.response.ResponseBodyArray;
import com.kakao.network.response.ResponseData;
import com.kakao.push.response.model.PushTokenInfo;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.HttpURLConnection;
import java.util.List;

/**
 * @author leoshin, created at 15. 8. 10..
 */
public class GetPushTokenResponse extends JSONArrayResponse {
    private final List<PushTokenInfo> pushTokenInfoList;

    public GetPushTokenResponse(ResponseData responseData) throws ResponseBodyException, ApiResponseStatusError {
        super(responseData);

        this.pushTokenInfoList = bodyArray.getConvertedList(ARRAY_CONVERTER);
    }

    public List<PushTokenInfo> getPushTokenInfoList() {
        return pushTokenInfoList;
    }

    public static final ResponseBodyArray.ArrayConverter<ResponseBody, PushTokenInfo> ARRAY_CONVERTER = new ResponseBodyArray.ArrayConverter<ResponseBody, PushTokenInfo>() {
        @Override
        public ResponseBody fromArray(JSONArray array, int i) throws ResponseBodyException {
            try {
                return new ResponseBody(HttpURLConnection.HTTP_OK, array.getJSONObject(i));
            } catch (JSONException e) {
                throw new ResponseBodyException("");
            }
        }

        @Override
        public PushTokenInfo convert(ResponseBody o) throws ResponseBodyException {
            return new PushTokenInfo(o);
        }
    };
}
