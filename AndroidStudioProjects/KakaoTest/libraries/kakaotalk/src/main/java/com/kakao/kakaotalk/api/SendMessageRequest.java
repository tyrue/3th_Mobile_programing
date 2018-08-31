/*
  Copyright 2014-2017 Kakao Corp.

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
package com.kakao.kakaotalk.api;

import android.net.Uri;

import com.kakao.auth.common.MessageSendable;
import com.kakao.kakaotalk.StringSet;
import com.kakao.network.RequestConfiguration;
import com.kakao.network.ServerProtocol;

import java.util.Map;

/**
 * @author leoshin, created at 15. 7. 29..
 */
class SendMessageRequest extends KakaoTalkMessageRequest {

    protected final String receiverId;
    protected final String receiverIdType;

    public SendMessageRequest(final RequestConfiguration configuration, MessageSendable receiverInfo, String templateId, Map<String, String> args) {
        super(configuration, templateId, args);
        this.receiverId = receiverInfo.getTargetId();
        this.receiverIdType = receiverInfo.getType();
    }

    @Override
    public Uri.Builder getUriBuilder() {
        Uri.Builder builder = super.getUriBuilder();
        builder.path(ServerProtocol.TALK_MESSAGE_SEND_V2_PATH);
        return builder;
    }

    @Override
    public Map<String, String> getParams() {
        Map<String, String> params = super.getParams();
        params.put(StringSet.receiver_id, receiverId);
        params.put(StringSet.receiver_id_type, receiverIdType);

        return params;
    }
}
