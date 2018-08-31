/*
  Copyright 2017 Kakao Corp.

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
package com.kakao.kakaolink.v2.network;

import android.net.Uri;

import com.kakao.kakaolink.internal.KakaoTalkLinkProtocol;
import com.kakao.message.template.TemplateParams;
import com.kakao.network.RequestConfiguration;
import com.kakao.network.ServerProtocol;

import org.json.JSONObject;

import java.util.Map;

/**
 * @author kevin.kang
 * Created by kevin.kang on 2017. 3. 9..
 */

public class TemplateDefaultRequest extends KakaoLinkTemplateRequest {
    private final Map<String, Object> templateObject;
    private JSONObject jsonObject; // This can't be final

    public TemplateDefaultRequest(final RequestConfiguration configuration, final Map<String, Object> templateObject) {
        super(configuration);
        this.templateObject = templateObject;
        this.jsonObject = null;
    }

    public TemplateDefaultRequest(final RequestConfiguration configuration, final TemplateParams templateParams) {
        super(configuration);
        this.templateObject = null;
        this.jsonObject = templateParams.toJSONObject();
    }

    @Override
    public String getMethod() {
            return GET;
        }

    @Override
    public String getUrl() {
        Uri.Builder builder = super.getUriBuilder();
        builder.path(ServerProtocol.LINK_TEMPLATE_DEFAULT_PATH);

        JSONObject json;
        if (this.templateObject != null) {
            json = new JSONObject(this.templateObject);
        } else if (this.jsonObject != null) {
            json = jsonObject;
        } else {
            throw new IllegalArgumentException("Template object is null.");
        }
        builder.appendQueryParameter(KakaoTalkLinkProtocol.TEMPLATE_OBJECT, json.toString());
        return builder.build().toString();
    }
}
