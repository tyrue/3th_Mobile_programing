/**
 * Copyright 2014-2016 Kakao Corp.
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
package com.kakao.auth.authorization.accesstoken;

import android.text.TextUtils;

import com.kakao.auth.StringSet;
import com.kakao.auth.network.response.AuthResponse;
import com.kakao.network.response.ResponseBody;
import com.kakao.network.response.ResponseBody.ResponseBodyException;
import com.kakao.util.helper.Utility;
import com.kakao.util.helper.log.Logger;

import java.util.Date;

/**
 * Basic access token implementation in memory.
 *
 * refresh token에 대한 expires_at은 아직 내려오지 않는다.
 * @author MJ
 */
class AccessTokenImpl extends AuthResponse implements AccessToken {
    public static final int ACCESS_TOKEN_REQUEST = 2;
    private static final Date MAX_DATE = new Date(Long.MAX_VALUE);
    private static final Date DEFAULT_EXPIRATION_TIME = MAX_DATE;

    private String accessToken;
    private String refreshToken;
    private Date accessTokenExpiresAt;
    private Date refreshTokenExpiresAt;

    private AccessToken tokenInfo;

    public AccessTokenImpl(ResponseBody body) throws ResponseBodyException, AuthResponseStatusError {
        super(body);
        if (!body.has(StringSet.access_token)) {
            throw new ResponseBody.ResponseBodyException("No Search Element : " + StringSet.access_token);
        }
        accessToken = body.getString(StringSet.access_token);
        if (body.has(StringSet.refresh_token)) {
            refreshToken = body.getString(StringSet.refresh_token);
        }
        long expiredAt = new Date().getTime() + body.getInt(StringSet.expires_in) * 1000;
        accessTokenExpiresAt = new Date(expiredAt);
        refreshTokenExpiresAt = MAX_DATE;
    }

    public AccessTokenImpl(final String accessToken, final String refreshToken, final Date accessTokenExpiresAt, final Date refreshTokenExpiresAt) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiresAt = accessTokenExpiresAt;
        this.refreshTokenExpiresAt = refreshTokenExpiresAt;
    }

    public AccessTokenImpl(AccessToken tokenInfo) {
        this(tokenInfo.getAccessToken(), tokenInfo.getRefreshToken(), tokenInfo.accessTokenExpiresAt(), tokenInfo.refreshTokenExpiresAt());
        this.tokenInfo = tokenInfo;
    }

    public void clearAccessToken() {
        this.accessToken = null;
        this.accessTokenExpiresAt = DEFAULT_EXPIRATION_TIME;
        if (tokenInfo == null) return;
        tokenInfo.clearAccessToken();
    }

    public void clearRefreshToken() {
        this.refreshToken = null;
        this.refreshTokenExpiresAt = DEFAULT_EXPIRATION_TIME;
        if (tokenInfo == null) return;
        tokenInfo.clearRefreshToken();
    }

    @Override
    public boolean hasValidAccessToken() {
        return !Utility.isNullOrEmpty(accessToken) && !new Date().after(accessTokenExpiresAt);
    }

    @Override
    public int getRemainingExpireTime() {
        if (accessTokenExpiresAt == null || !hasValidAccessToken()) {
            return 0;
        }
        return (int) (accessTokenExpiresAt.getTime() - new Date().getTime());
    }

    // access token 갱신시에는 refresh token이 내려오지 않을 수도 있다.
    @Override
    public void updateAccessToken(final AccessToken newAccessToken){
        String newRefreshToken = newAccessToken.getRefreshToken();
        if(TextUtils.isEmpty(newRefreshToken)){
            this.accessToken = newAccessToken.getAccessToken();
            this.accessTokenExpiresAt = newAccessToken.accessTokenExpiresAt();
        } else {
            this.accessToken = newAccessToken.getAccessToken();
            this.refreshToken = newAccessToken.getRefreshToken();
            this.accessTokenExpiresAt = newAccessToken.accessTokenExpiresAt();
            this.refreshTokenExpiresAt = newAccessToken.refreshTokenExpiresAt();
        }
        if (tokenInfo == null) return;
        tokenInfo.updateAccessToken(this);
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    @Override
    public Date accessTokenExpiresAt() {
        return accessTokenExpiresAt;
    }

    @Override
    public Date refreshTokenExpiresAt() {
        return refreshTokenExpiresAt;
    }

    public boolean hasRefreshToken(){
        return !Utility.isNullOrEmpty(refreshToken);
    }

    @Deprecated
    public int getRemainedExpiresInAccessTokenTime() {
        if (accessTokenExpiresAt == null || !hasValidAccessToken()) {
            return 0;
        }

        return (int) (accessTokenExpiresAt.getTime() - new Date().getTime());
    }
}
