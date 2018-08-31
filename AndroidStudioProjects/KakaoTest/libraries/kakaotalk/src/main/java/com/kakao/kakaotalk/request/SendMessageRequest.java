/*
  Copyright 2014-2015 Kakao Corp.

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
package com.kakao.kakaotalk.request;

import com.kakao.auth.common.MessageSendable;
import com.kakao.network.ServerProtocol;
import com.kakao.auth.network.request.ApiRequest;
import com.kakao.kakaotalk.StringSet;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author leoshin, created at 15. 7. 29..
 */
@Deprecated
public class SendMessageRequest extends ApiRequest {
    @Override
    public String getMethod() {
        return POST;
    }

    @Override
    public String getUrl() {
        return ApiRequest.createBaseURL(ServerProtocol.API_AUTHORITY, ServerProtocol.TALK_MESSAGE_SEND);
    }

    @Override
    public Map<String, String> getParams() {
        Map<String, String> params = new HashMap<String, String>();
        params.put(StringSet.receiver_id, receiverId);
        params.put(StringSet.receiver_id_type, receiverIdType);
        params.put(StringSet.template_id, templateId);

        if (args != null && args.length() > 0) {
            params.put(StringSet.args, args.toString());
        }
        return params;
    }

    private final String receiverId;
    private final String receiverIdType;
    private final String templateId;
    private final JSONObject args;

    public SendMessageRequest(MessageSendable receiverInfo, String templateId, Map<String, String> args) {
        this.receiverId = receiverInfo.getTargetId();
        this.receiverIdType = receiverInfo.getType();
        this.templateId = templateId;
        this.args = args != null ? new JSONObject(args) : null;
    }
}
