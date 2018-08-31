package com.kakao.kakaotalk.api;

import android.net.Uri;

import com.kakao.auth.network.request.ApiRequest;
import com.kakao.message.template.MessageTemplateProtocol;
import com.kakao.message.template.TemplateParams;
import com.kakao.network.RequestConfiguration;
import com.kakao.network.ServerProtocol;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * @author kevin.kang. Created on 2017. 5. 16..
 */

abstract class DefaultTemplateRequest extends ApiRequest {
    private JSONObject jsonObject; // This can't be final

    public DefaultTemplateRequest(final RequestConfiguration configuration, final TemplateParams templateParams) {
        super(configuration);
        this.jsonObject = templateParams.toJSONObject();
    }

    @Override
    public String getMethod() {
        return POST;
    }

    @Override
    public String getUrl() {
        return getUriBuilder().toString();
    }

    @Override
    public Map<String, String> getParams() {
        Map<String, String> params = super.getParams();
        params.put(MessageTemplateProtocol.TEMPLATE_OBJECT, jsonObject.toString());
        return params;
    }

    @Override
    public Uri.Builder getUriBuilder() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(ServerProtocol.SCHEME);
        builder.authority(ServerProtocol.API_AUTHORITY);
        return builder;
    }
}
