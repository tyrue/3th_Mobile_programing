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

import java.util.List;
import java.util.Map;

/**
 * Http 요청에 필요한 정보를 얻기위해 제공된다.
 * @author leoshin, created at 15. 7. 14..
 */
public interface IRequest {
    /**
     * http 통신방법.
     * @return httpMethod. GET, POST, PUT, DELETE 등등.
     */
    String getMethod();

    /**
     * 요청할 target url.
     * @return 요청할 target url.
     */
    String getUrl();

    /**
     * http요청에 필요한 params.
     * @return http요청에 필요한 params.
     */
    Map<String, String> getParams();

    /**
     * http요청에 필요한 headers.
     * @return http요청에 필요한 headers.
     */
    Map<String, String> getHeaders();

    /**
     * MultiPart에 대한 정보
     * MultiPart로 요청을 보낼때 값을 채워주면 된다.
     * @return MultiPart에 대한 정보
     */
    List<Part> getMultiPartList();

    /**
     * param의 encording정보.
     * @return param의 encording정보. default "UTF-8"
     */
    String getBodyEncoding();
}
