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
package com.kakao.push.response.model;

import com.kakao.network.response.ResponseBody;
import com.kakao.push.StringSet;

/**
 * @author leoshin, created at 15. 8. 10..
 */
final public class PushTokenInfo {
    private final String userId;
    private final String deviceId;
    private final String pushType;
    private final String pushToken;
    private final String createdAt;
    private final String updatedAt;

    public PushTokenInfo(ResponseBody body) {
        this.userId = body.optString(StringSet.user_id, null);
        this.deviceId = body.optString(StringSet.device_id, null);
        this.pushType = body.optString(StringSet.push_type, null);
        this.pushToken = body.optString(StringSet.push_token, null);
        this.createdAt = body.optString(StringSet.created_at, null);
        this.updatedAt = body.optString(StringSet.updated_at, null);
    }

    /**
     * 사용자의 고유 ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 기기의 고유한 ID
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * apns 혹은 gcm
     */
    public String getPushType() {
        return pushType;
    }

    /**
     * APNS, GCM으로부터 발급받은 Push Token
     */
    public String getPushToken() {
        return pushToken;
    }

    /**
     * 푸시 토큰을 처음 등록한 시각
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * 푸시 토큰을 업데이트한 시각
     */
    public String getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "PushTokenInfo{" + "userId='" + userId + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", pushType='" + pushType + '\'' +
                ", pushToken='" + pushToken + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
