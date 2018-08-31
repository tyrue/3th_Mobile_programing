/*
  Copyright 2016-2017 Kakao Corp.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package com.kakao.kakaolink.v2;

import com.kakao.kakaolink.v2.network.KakaoLinkTemplateResponse;
import com.kakao.kakaolink.v2.network.LinkImageDeleteRequest;
import com.kakao.kakaolink.v2.network.LinkImageScrapRequest;
import com.kakao.kakaolink.v2.network.LinkImageUploadRequest;
import com.kakao.kakaolink.v2.network.TemplateDefaultRequest;
import com.kakao.kakaolink.v2.network.TemplateScrapRequest;
import com.kakao.kakaolink.v2.network.TemplateScrapResponse;
import com.kakao.kakaolink.v2.network.TemplateValidateRequest;
import com.kakao.network.NetworkTask;
import com.kakao.network.response.ResponseBody;
import com.kakao.network.response.ResponseData;
import com.kakao.network.storage.ImageDeleteResponse;
import com.kakao.network.storage.ImageUploadResponse;
import com.kakao.util.helper.log.Logger;

import java.io.IOException;

/**
 * Template validation을 하는 API를 실행하는 클래스.
 * Created by kevin.kang on 2016. 11. 25..
 */

class KakaoLinkApi {
    private final NetworkTask networkTask;

    KakaoLinkApi(final NetworkTask networkTask) {
        this.networkTask = networkTask;
    }

    KakaoLinkTemplateResponse requestTemplateValidate(final TemplateValidateRequest request) throws IOException, ResponseBody.ResponseBodyException {
        ResponseData responseData =  networkTask.request(request);
        return new KakaoLinkTemplateResponse(getResponseBody(responseData));
    }

    TemplateScrapResponse requestTemplateScrap(final TemplateScrapRequest request) throws IOException, ResponseBody.ResponseBodyException {
        ResponseData responseData =  networkTask.request(request);
        return new TemplateScrapResponse(getResponseBody(responseData));
    }

    TemplateScrapResponse requestTemplateDefault(final TemplateDefaultRequest request) throws IOException, ResponseBody.ResponseBodyException {
        ResponseData responseData = networkTask.request(request);
        return new TemplateScrapResponse(getResponseBody(responseData));
    }
    ImageUploadResponse requestImageUpload(final LinkImageUploadRequest request) throws IOException, ResponseBody.ResponseBodyException {
        ResponseData responseData = networkTask.request(request);
        return new ImageUploadResponse(getResponseBody(responseData));
    }

    ImageUploadResponse requestImageScrap(final LinkImageScrapRequest request) throws IOException, ResponseBody.ResponseBodyException {
        ResponseData responseData = networkTask.request(request);
        return new ImageUploadResponse(getResponseBody(responseData));
    }

    ImageDeleteResponse requestImageDelete(final LinkImageDeleteRequest request) throws IOException, ResponseBody.ResponseBodyException {
        ResponseData responseData = networkTask.request(request);
        return new ImageDeleteResponse(getResponseBody(responseData));
    }

    @SuppressWarnings("WeakerAccess")
    ResponseBody getResponseBody(final ResponseData responseData) throws ResponseBody.ResponseBodyException{
        return new ResponseBody(responseData.getHttpStatusCode(), responseData.getData());
    }
}
