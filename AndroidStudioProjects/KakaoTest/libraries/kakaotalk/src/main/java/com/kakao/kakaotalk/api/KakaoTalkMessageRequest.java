package com.kakao.kakaotalk.api;

import android.net.Uri;

import com.kakao.auth.network.request.ApiRequest;
import com.kakao.kakaotalk.StringSet;
import com.kakao.network.RequestConfiguration;
import com.kakao.network.ServerProtocol;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kevin.kang. Created on 2017. 4. 26..
 */

public abstract class KakaoTalkMessageRequest extends ApiRequest {

    protected final String templateId;
    protected final Map<String, String> templateArgs;

    public KakaoTalkMessageRequest(final RequestConfiguration configuration, String templateId, Map<String, String> args) {
        super(configuration);
        this.templateId = templateId;
        this.templateArgs = args;
    }

    @Override
    public String getMethod() {
        return POST;
    }

    @Override
    public String getUrl() {
        return getUriBuilder().build().toString();
    }

    @Override
    public Uri.Builder getUriBuilder() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(ServerProtocol.SCHEME);
        builder.authority(ServerProtocol.API_AUTHORITY);
        return builder;
    }

    @Override
    public Map<String, String> getParams() {
        Map<String, String> params = new HashMap<String, String>();
        params.put(StringSet.template_id, templateId);

        if (templateArgs != null && !templateArgs.isEmpty()) {
            params.put(StringSet.template_args, new JSONObject(templateArgs).toString());
        }
        return params;
    }
}
