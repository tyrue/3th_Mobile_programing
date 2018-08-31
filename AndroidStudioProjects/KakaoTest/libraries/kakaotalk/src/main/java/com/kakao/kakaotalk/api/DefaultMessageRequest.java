package com.kakao.kakaotalk.api;

import android.net.Uri;

import com.kakao.auth.common.MessageSendable;
import com.kakao.kakaotalk.StringSet;
import com.kakao.message.template.TemplateParams;
import com.kakao.network.RequestConfiguration;
import com.kakao.network.ServerProtocol;

import java.util.Map;

/**
 * @author kevin.kang. Created on 2017. 5. 16..
 */

class DefaultMessageRequest extends DefaultTemplateRequest {
    private MessageSendable receiverInfo;

    public DefaultMessageRequest(RequestConfiguration configuration, final MessageSendable receiverInfo, TemplateParams templateParams) {
        super(configuration, templateParams);
        this.receiverInfo = receiverInfo;
    }

    @Override
    public Map<String, String> getParams() {
        Map<String, String> params = super.getParams();
        params.put(StringSet.receiver_id, receiverInfo.getTargetId());
        params.put(StringSet.receiver_id_type, receiverInfo.getType());
        return params;
    }

    @Override
    public Uri.Builder getUriBuilder() {
        Uri.Builder builder = super.getUriBuilder();
        builder.path(ServerProtocol.TALK_MESSAGE_DEFAULT_V2_PATH);
        return builder;
    }
}
