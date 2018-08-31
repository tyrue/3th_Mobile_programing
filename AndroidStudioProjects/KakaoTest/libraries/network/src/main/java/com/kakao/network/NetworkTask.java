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
package com.kakao.network;

import com.kakao.network.multipart.Part;
import com.kakao.network.response.ResponseData;
import com.kakao.util.helper.log.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Http요청을 수행하는 class.
 * INetwork를 구현한 class를 직접 사용할 수도 있다.
 * @author leoshin on 15. 9. 18.
 */
public class NetworkTask {

    final private INetwork network;

    public NetworkTask() {
        this.network = new KakaoNetworkImpl();
    }

    public NetworkTask(INetwork network) {
        this.network = network;
    }

    /**
     * http 요청.
     * @param request http 요청할 대상에 대한 정보.
     * @return first is http statusCode, seconde is response body.
     * @throws IOException if network request fails
     */
    public ResponseData request(IRequest request) throws IOException {
        Logger.d(request.getUrl());

        try {
            network.create(request.getUrl(), request.getMethod(), request.getBodyEncoding());
            // add header
            Map<String, String> header = request.getHeaders();
            Logger.d(header.toString());

            for (String key : header.keySet()) {
                if (key.equalsIgnoreCase("Expect")) {
                    throw new IllegalStateException("Expect: 100-Continue not supported");
                }
                network.addHeader(key, header.get(key));
            }

            // add params
            Map<String, String> params = request.getParams();
            for (String key : params.keySet()) {
                network.addParam(key, params.get(key));
            }

            // add file list
            List<Part> partList = request.getMultiPartList();
            for (Part part : partList) {
                network.addPart(part);
            }

            network.configure();
            network.connect();

            int statusCode = network.getStatusCode();
            Logger.d("++ httpStatus : [%s]", statusCode);
            return new ResponseData(statusCode, network.readFully());
        } finally {
            network.disconnect();
        }
    }
}
