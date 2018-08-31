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
package com.kakao.auth.authorization.authcode;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.kakao.auth.ApprovalType;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.StringSet;
import com.kakao.auth.authorization.AuthorizationResult;
import com.kakao.auth.helper.StartActivityWrapper;
import com.kakao.util.AppConfig;
import com.kakao.util.helper.log.Logger;
import com.kakao.util.protocol.KakaoProtocolService;
import com.kakao.util.helper.TalkProtocol;

/**
 * @author kevin.kang. Created on 2017. 5. 30..
 */

class TalkAuthCodeService implements AuthCodeService {
    @Override
    public boolean requestAuthCode(final AuthCodeRequest request, StartActivityWrapper wrapper, AuthCodeListener listener) {
        Intent intent = createLoggedInActivityIntent(request.getExtraParams());
        if (intent == null) {
            return false;
        }
        startActivityForResult(wrapper, intent, request.getRequestCode());
        return true;
    }

    @Override
    public boolean handleActivityResult(int requestCode, int resultCode, Intent data, AuthCodeListener listener) {
        AuthorizationResult result = parseAuthCodeIntent(requestCode, resultCode, data);
        if (result.isPass()) {
            return false;
        }
        listener.onAuthCodeReceived(requestCode, result);
        return true;
    }

    @Override
    public boolean isLoginAvailable() {
        return createLoggedInActivityIntent(null) != null;
    }

    TalkAuthCodeService(final Context context, final AppConfig appConfig, final ISessionConfig sessionConfig, final KakaoProtocolService protocolService) {
        this.context = context;
        this.appConfig = appConfig;
        this.redirectUriString = StringSet.REDIRECT_URL_PREFIX + appConfig.getAppKey() + StringSet.REDIRECT_URL_POSTFIX;
        this.sessionConfig = sessionConfig;
        this.protocolService = protocolService;
    }

    void startActivityForResult(final StartActivityWrapper wrapper, final Intent intent, final int requestCode) {
        if (wrapper != null) {
            wrapper.startActivityForResult(intent, requestCode);
        }
    }

    /**
     * This method parses intent directly delivered from KakaoTalk.
     *
     * @param requestCode requestCode used to start KakaoTalkActivity with startActivityForResult()
     * @param resultCode resultCode received from KakaoTalk
     * @param data Intent containing authorization resulit (Authorization code)
     * @return AuthorizationResult describing success or failure of getting authorization code
     */
    AuthorizationResult parseAuthCodeIntent(final int requestCode, final int resultCode, final Intent data) {
        AuthorizationResult outcome;

        if (data == null || resultCode == Activity.RESULT_CANCELED) {
            // This happens if the user presses 'Back'.
            outcome = AuthorizationResult.createAuthCodeCancelResult("pressed back button or cancel button during requesting auth code.");
        } else if (isCapriProtocolMatched(data)) {
            outcome = AuthorizationResult.createAuthCodeOAuthErrorResult("TalkProtocol is mismatched during requesting auth code through KakaoTalk.");
        } else if (resultCode != Activity.RESULT_OK) {
            outcome = AuthorizationResult.createAuthCodeOAuthErrorResult("got unexpected resultCode during requesting auth code. code=" + requestCode);
        } else {
            Bundle extras = data.getExtras();
            String errorType = extras.getString(EXTRA_ERROR_TYPE);
            String redirectURL = extras.getString(EXTRA_REDIRECT_URL);
            if (errorType == null && redirectURL != null && redirectURL.startsWith(redirectUriString)) {
                return AuthorizationResult.createSuccessAuthCodeResult(redirectURL);
            } else {
                String errorDes = extras.getString(EXTRA_ERROR_DESCRIPTION);
                if(errorType != null && errorType.equals(NOT_SUPPORT_ERROR)) {
                    if (errorDes != null) {
                        Logger.i(errorDes);
                    }
                    return AuthorizationResult.createAuthCodePassResult();
                }
                return AuthorizationResult.createAuthCodeOAuthErrorResult("redirectURL=" + redirectURL + ", " + errorType + " : " + errorDes);
            }
        }
        return outcome;
    }

    // This definitely belongs to TalkAuthCodeService
    protected Intent createLoggedInActivityIntent(final Bundle extras) {
        final ApprovalType approvalType = sessionConfig.getApprovalType();
        final Intent intent = createIntent(INTENT_ACTION_LOGGED_IN_ACTIVITY, appConfig.getAppKey(), redirectUriString, extras);
        return protocolService.resolveIntent(context, intent, approvalType == ApprovalType.PROJECT ? TALK_MIN_VERSION_SUPPORT_PROJEC_LOGIN : TALK_MIN_VERSION_SUPPORT_CAPRI);
    }

    protected Intent createIntent(final String action, final String appKey, final String redirectURI, final Bundle extras) {
        final Intent intent = new Intent()
                .setAction(action)
                .addCategory(Intent.CATEGORY_DEFAULT)
                .putExtra(EXTRA_PROTOCOL_VERSION, PROTOCOL_VERSION)
                .putExtra(EXTRA_APPLICATION_KEY, appKey)
                .putExtra(EXTRA_REDIRECT_URI, redirectURI)
                .putExtra(EXTRA_KA_HEADER, appConfig.getKaHeader());

        if(extras != null && !extras.isEmpty()) {
            intent.putExtra(EXTRA_EXTRAPARAMS, extras);
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        return intent;
    }

    protected boolean isCapriProtocolMatched(final Intent data) {
        int protocolVersion = data.getIntExtra(EXTRA_PROTOCOL_VERSION, 0);
        return ((PROTOCOL_VERSION == protocolVersion));
    }

    protected Context context;
    protected AppConfig appConfig;
    protected ISessionConfig sessionConfig;
    protected KakaoProtocolService protocolService;

    protected String redirectUriString;

    static final String INTENT_ACTION_LOGGED_IN_ACTIVITY = "com.kakao.talk.intent.action.CAPRI_LOGGED_IN_ACTIVITY";
    static final String EXTRA_APPLICATION_KEY = "com.kakao.sdk.talk.appKey";
    static final String EXTRA_REDIRECT_URI = "com.kakao.sdk.talk.redirectUri";
    static final String EXTRA_KA_HEADER = "com.kakao.sdk.talk.kaHeader";
    static final String EXTRA_EXTRAPARAMS = "com.kakao.sdk.talk.extraparams";
    static final String EXTRA_PROTOCOL_VERSION = "com.kakao.sdk.talk.protocol.version";
    static final int PROTOCOL_VERSION = 1;

    private static final int TALK_MIN_VERSION_SUPPORT_PROJEC_LOGIN = 178; // android talk version 4.7.5
    private static final int TALK_MIN_VERSION_SUPPORT_CAPRI = 139; // android talk version 4.2.0

    // error types
    static final String NOT_SUPPORT_ERROR = "NotSupportError"; // KakaoTalk installed but not signed up
    static final String UNKNOWN_ERROR = "UnknownError"; // No redirect url
    static final String PROTOCOL_ERROR = "ProtocolError"; // Wrong parameters provided
    static final String APPLICATION_ERROR = "ApplicationError"; // Empty redirect url
    static final String AUTH_CODE_ERROR = "AuthCodeError";
    static final String CLIENT_INFO_ERROR = "ClientInfoError"; // Could not fetch app info

    //response
    static final String EXTRA_REDIRECT_URL = "com.kakao.sdk.talk.redirectUrl";
    static final String EXTRA_ERROR_DESCRIPTION = "com.kakao.sdk.talk.error.description";
    static final String EXTRA_ERROR_TYPE = "com.kakao.sdk.talk.error.type";
}
