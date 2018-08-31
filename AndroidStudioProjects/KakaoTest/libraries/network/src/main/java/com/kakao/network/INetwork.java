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

import java.io.IOException;

/**
 * http 연결을 할때 사용된다.
 * 사용자는 INetwork를 구현하여 원하는 http 구현체를 사용할 수 있다.
 * @author leo.shin
 */
public interface INetwork {
    /**
     * INetwork를 생성한다.
     * @param url 연결될 url.
     * @param method 연결 메소드
     * @param charset encoding 할 값.
     * @throws IOException if network error occurs
     */
    void create(String url, String method, String charset) throws IOException;

    /**
     * connection에 대한 property 설정.
     * @throws IOException if network error occurs
     */
    void configure() throws IOException;

    /**
     * http 연결을 수행한다.
     * @throws IOException if network error occurs
     */
    void connect() throws IOException;

    /**
     * 연결을 해제한다.
     */
    void disconnect();

    /**
     * http header에 대한 정보를 추가한다.
     * @param key 추가될 header의 key.
     * @param value 추가될 header의 value.
     */
    void addHeader(String key, String value);

    /**
     * http param에 대한 정보를 추가한다.
     * @param key 추가될 param의 key.
     * @param value 추가될 param의 value.
     */
    void addParam(String key, String value);

    /**
     * 연결된 reqeust에 대한 응답 stread을 끝까지 읽어야 한다.
     * @return 응답결과의 byte array.
     * @throws IOException if network error occurs
     */
    byte[] readFully() throws IOException;

    /**
     * http response code를 얻는다.
     * @return http response code
     */
    int getStatusCode();

    /**
     * file upload용 FilePart를 구현한다.
     * @param part MultiPart의 Part Object
     */
    void addPart(Part part);
}
