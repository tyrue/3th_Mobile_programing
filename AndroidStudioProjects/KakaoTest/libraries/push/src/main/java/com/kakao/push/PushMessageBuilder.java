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
package com.kakao.push;

import com.kakao.util.helper.log.Logger;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 자기 자신에게 보내는 푸시 메시지를 구성하는 빌더.
 */
public class PushMessageBuilder {
    private final JSONObject messageObject = new JSONObject();
    private final GCMMessageObject forGcm;

    /**
     * 메시지 외 앱에 부가적인 정보를 전달하고자 할 때 사용한다.
     * @param customField  json 형태의 String 값.
     */
    public PushMessageBuilder(final String customField) {
        forGcm = new GCMMessageObject(customField);
    }

    /**
     * 푸시 메시지 구분자. 같은 값을 가지는 푸시 알림이 여러 개일 때 마지막 하나만 사용자 기기로 전송되도록 하기 위해 사용한다.
     * @param collapse 푸시 메시지 구분자
     * @return 계속해서 푸시 메시지를 구성할 수 있는 빌더
     */
    public PushMessageBuilder setCollapse(String collapse) {
        forGcm.collapse = collapse;
        return this;
    }

    /**
     * 사용자 기기가 idle인 경우 전송을 지연할지에 대한 설정을 한다.
     * false인 경우, 사용자 기기의 idle 상태 상관없이 즉시 푸시 알림을 전송한다.
     * true인 경우, GCM 서버 상태에 따라 어느 정도 시간이 지난 후 푸시 알림을 전송한다.
     * @param delayWhileIdle 사용자 기기가 idle인 경우 전송을 지연할지 여부
     * @return 계속해서 푸시 메시지를 구성할 수 있는 빌더
     */
    public PushMessageBuilder setDelayWhileIdle(Boolean delayWhileIdle) {
        forGcm.delayWhileIdle = delayWhileIdle;
        return this;
    }

    /**
     * 푸시 알림의 전송 실패에 대한 피드백 처리가 필요할 때 사용한다.
     * 전송 실패시 설정된 url로 사용자 id, 푸시 토큰, 전송 실패 시각(millisecond)을 POST로 전송된다.
     * @param returnUrl 푸시 전송 실패시 콜백 받을 url
     * @return 계속해서 푸시 메시지를 구성할 수 있는 빌더
     */
    public PushMessageBuilder setReturnUrl(String returnUrl) {
        forGcm.returnUrl = returnUrl;
        return this;
    }

    /**
     * 푸시 메시지를 String으로 변환.
     * @return String으로 변환된 푸시 메시지 객체.
     */
    public String toString() {
        try {
            messageObject.put(StringSet.for_gcm, forGcm.toJSONObject());
            return messageObject.toString();
        } catch (JSONException e) {
            Logger.e(e);
            return null;
        }
    }

    private static class GCMMessageObject {
        private String collapse;
        private Boolean delayWhileIdle;
        private final String customField;
        private String returnUrl;

        public GCMMessageObject(final String customField) {
            this.customField = customField;
        }

        public JSONObject toJSONObject() throws JSONException {
            JSONObject msg = new JSONObject();
            msg.put(StringSet.custom_field, customField);
            msg.put(StringSet.collapse, collapse);
            msg.put(StringSet.delay_while_idle, delayWhileIdle);
            msg.put(StringSet.return_url, returnUrl);
            return msg;
        }
    }
}
