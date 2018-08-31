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
package com.kakao.auth;

/**
 * @author leo.shin
 */
public class StringSet {
    public static final String rule_id = "rule_id";
    public static final String code = "code";
    public static final String status = "status";
    public static final String msg = "msg";
    public static final String error = "error";
    public static final String error_description = "error_description";
    public static final String required_scopes = "required_scopes";
    public static final String allowed_scopes = "allowed_scopes";
    public static final String api_type = "api_type";
    public static final String RT = "RT";
    public static final String scope = "scope";

    //oauth param
    public static final String client_id = "client_id";
    public static final String client_secret = "client_secret";
    public static final String redirect_uri = "redirect_uri";
    public static final String response_type = "response_type";
    public static final String grant_type = "grant_type";
    public static final String access_token = "access_token";
    public static final String refresh_token = "refresh_token";
    public static final String expires_in = "expires_in";
    public static final String authorization_code = "authorization_code";
    public static final String android_key_hash = "android_key_hash";
    public static final String approval_type = "approval_type";

    public static final String file = "file";
    public static final String expiresInMillis = "expiresInMillis";
    public static final String id = "id";

    public static final String api = "api";
    public static final String token_type = "token_type";
    public static final String return_url = "return_url";

    public static final String ageauth_level = "ageauth_level";
    public static final String age_limit = "age_limit";
    public static final String access_denied = "access_denied";

    public static final String is_western_age = "is_western_age";
    public static final String skip_term = "skip_term";
    public static final String auth_from = "auth_from";
    public static final String update = "update";

    public static final String property_keys = "property_keys";

    public static final String REDIRECT_URL_PREFIX = "kakao";
    public static final String REDIRECT_URL_POSTFIX = "://oauth";
    public static final String AGEAUTH_REDIRECT_URL_POSTFIX = "://ageauth";
}
