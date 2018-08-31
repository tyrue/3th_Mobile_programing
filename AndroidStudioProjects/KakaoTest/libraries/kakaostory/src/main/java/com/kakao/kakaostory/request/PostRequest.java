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

import com.kakao.auth.network.request.ApiRequest;
import com.kakao.kakaostory.StringSet;
import com.kakao.util.exception.ParameterMissingException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author leoshin, created at 15. 7. 31..
 */
public abstract class PostRequest extends ApiRequest {
    /**
     * 공개 범위 값
     */
    public enum StoryPermission {
        /**
         * 알수 없는 공개 범위
         */
        UNKNOWN("UNKNOWN", "UNKNOWN"),
        /**
         * 전체공개
         */
        PUBLIC("PUBLIC", "A"),
        /**
         * 친구공개
         */
        FRIEND("FRIEND", "F"),
        /**
         * 나만보기
         */
        ONLY_ME("ONLY_ME", "M");


        final String name;
        final String value;
        StoryPermission(final String name, final String value) {
            this.name = name;
            this.value = value;
        }

        public static StoryPermission getPermission(final String name){
            for(StoryPermission permission : StoryPermission.values()){
                if(permission.name.equals(name))
                    return permission;
            }
            return UNKNOWN;
        }
    }

    protected final String content;
    protected final StoryPermission permission;
    protected final boolean enableShare;
    protected final String iosExecParam;
    protected final String androidExecParam;
    protected final String iosMarketParam;
    protected final String androidMarketParam;

    public PostRequest(String content,
                       StoryPermission permission,
                       boolean enableShare,
                       String androidExecParam,
                       String iosExecParam,
                       String androidMarketParam,
                       String iosMarketParam) throws ParameterMissingException {
        if (permission == null || permission == StoryPermission.UNKNOWN) {
            throw new ParameterMissingException("invalid StoryPermission : " + permission);
        }

        this.content = content;
        this.permission = permission;
        this.enableShare = enableShare;
        this.androidExecParam = androidExecParam;
        this.iosExecParam = iosExecParam;
        this.iosMarketParam = iosMarketParam;
        this.androidMarketParam = androidMarketParam;
    }

    @Override
    public String getMethod() {
        return POST;
    }

    @Override
    public Map<String, String> getParams() {
        Map<String, String> params = new HashMap<String, String>();

        if (content != null && content.length() > 0) {
            params.put(StringSet.content, content);
        }
        params.put(StringSet.permission, permission.value);
        params.put(StringSet.enable_share, String.valueOf(enableShare));
        if (androidExecParam != null && androidExecParam.length() > 0) {
            params.put(StringSet.android_exec_param, androidExecParam);
        }
        if (iosExecParam != null && iosExecParam.length() > 0) {
            params.put(StringSet.ios_exec_param, iosExecParam);
        }
        if (androidMarketParam != null && androidMarketParam.length() > 0) {
            params.put(StringSet.android_market_param, androidMarketParam);
        }
        if (iosMarketParam != null && iosMarketParam.length() > 0) {
            params.put(StringSet.ios_market_param, iosMarketParam);
        }

        return params;
    }
}
