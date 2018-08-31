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
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.util.SparseArray;

import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthCodeCallback;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.Session;
import com.kakao.auth.StringSet;
import com.kakao.auth.helper.StartActivityWrapper;
import com.kakao.auth.authorization.AuthorizationResult;
import com.kakao.network.ErrorResult;
import com.kakao.util.AppConfig;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author leo.shin
 */
class KakaoAuthCodeManager implements AuthCodeManager, AuthCodeListener {
    private AtomicInteger authRequestCode;

    private Context context;
    private AppConfig appConfig;
    private AuthCodeRequest currentRequest;

    private final Queue<AuthCodeService> authCodeManagers = new LinkedList<AuthCodeService>();
    private StartActivityWrapper startActivityWrapper;

    private final ISessionConfig sessionConfig;
    private AuthCodeService kakaoManager;
    private AuthCodeService storyManager;
    private AuthCodeService webManager;

    @Override
    public void requestAuthCode(AuthType authType, Activity activity, AuthCodeCallback authCodeCallback) {
        requestAuthCode(authType, new StartActivityWrapper(activity), authCodeCallback);
    }

    @Override
    public void requestAuthCode(AuthType authType, Fragment fragment, AuthCodeCallback authCodeCallback) {
        requestAuthCode(authType, new StartActivityWrapper(fragment), authCodeCallback);
    }

    @Override
    public void requestAuthCode(AuthType authType, android.support.v4.app.Fragment fragment, AuthCodeCallback authCodeCallback) {
        requestAuthCode(authType, new StartActivityWrapper(fragment), authCodeCallback);
    }

    @Override
    public void requestAuthCode(final AuthType authType, final StartActivityWrapper wrapper, AuthCodeCallback callback) {
        int requestCode;
        if ((requestCode = authRequestCode.getAndIncrement()) != 0) {
            callback.onAuthCodeFailure(new ErrorResult(new KakaoException(KakaoException.ErrorType.UNSPECIFIED_ERROR, "There is another auth code process still not finished. Please try again later.")));
            return;
        }
        AuthCodeRequest request = createAuthCodeRequest(requestCode, appConfig.getAppKey(), callback);
        startTryingAuthCodeServices(authType, request, wrapper);
    }

    @Override
    public void requestAuthCodeWithScopes(AuthType authType, StartActivityWrapper wrapper, List<String> scopes, AuthCodeCallback callback) {
        int requestCode;
        if ((requestCode = authRequestCode.getAndIncrement()) != 0) {
            callback.onAuthCodeFailure(new ErrorResult(new KakaoException(KakaoException.ErrorType.UNSPECIFIED_ERROR, "There is another auth code process still not finished. Please try again later.")));
            return;
        }
        AuthCodeRequest request = createAuthCodeRequest(requestCode, appConfig.getAppKey(), getRefreshToken(), scopes, callback);
        startTryingAuthCodeServices(authType, request, wrapper);
    }

    void startTryingAuthCodeServices(final AuthType authType, final AuthCodeRequest request, final StartActivityWrapper wrapper) {
        addToAuthCodeServicesQueue(authType);
        currentRequest = request;
        startActivityWrapper = wrapper;
        tryNextAuthCodeService(request);
    }

    void tryNextAuthCodeService(final AuthCodeRequest request) {
        AuthCodeService authCodeService;
        AuthCodeCallback callback = request.getCallback();
        // just peek here because it needs to be referenced again and removed during handleActivityResult
        while ((authCodeService = authCodeManagers.peek()) != null) {
            Logger.d("trying " + authCodeService.getClass().getSimpleName());
            if (authCodeService.requestAuthCode(request, startActivityWrapper, this)) {
                // This AuthCodeService succeeded in requesting auth code.
                // Return and wait for handleActivityResult() or onAuthCodeReceived()
                return;
            } else {
                // This AuthCodeService should be pulled out from the queue since it didn't evey try.
                authCodeManagers.poll();
            }
        }

        // handler를 끝까지 돌았는데도 authorization code를 얻지 못했으면 error
        if (callback != null) {
            onAuthCodeReceived(request.getRequestCode(), AuthorizationResult.createAuthCodeOAuthErrorResult("Failed to get Authorization Code."));
        }
    }

    KakaoAuthCodeManager(final Context context, final AppConfig appConfig, final ISessionConfig sessionConfig, final AuthCodeService kakaoManager, final AuthCodeService storyManager, final AuthCodeService webManager) {
        this.context = context;
        this.appConfig = appConfig;

        this.sessionConfig = sessionConfig;
        this.kakaoManager = kakaoManager;
        this.storyManager = storyManager;
        this.webManager = webManager;

        authRequestCode = new AtomicInteger();
    }

    private void addToAuthCodeServicesQueue(final AuthType authType) {
        AuthType type = authType == null ? AuthType.KAKAO_TALK : authType;
        switch (type) {
            case KAKAO_TALK:
            case KAKAO_TALK_EXCLUDE_NATIVE_LOGIN:
                authCodeManagers.add(kakaoManager);
                break;
            case KAKAO_STORY:
                authCodeManagers.add(storyManager);
                break;
            case KAKAO_LOGIN_ALL:
                authCodeManagers.add(kakaoManager);
                authCodeManagers.add(storyManager);
                break;
        }
        authCodeManagers.add(webManager);
    }

    public boolean handleActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (currentRequest == null) {
            Logger.w("Auth code was not requested or the request has already been processed.");
            return false;
        }
        AuthCodeService authCodeService = authCodeManagers.poll();
        if (authCodeService == null || !authCodeService.handleActivityResult(requestCode, resultCode, data, this)) {
            tryNextAuthCodeService(currentRequest);
        }
        return true;
    }

    @Override
    public boolean isTalkLoginAvailable() {
        return kakaoManager.isLoginAvailable();
    }

    @Override
    public boolean isStoryLoginAvailable() {
        return storyManager.isLoginAvailable();
    }

    String getScopesString(final List<String> requiredScopes) {
        String scopeParam = null;
        if (requiredScopes == null) {
            return null;
        }
        StringBuilder builder = null;
        for (String scope : requiredScopes) {
            if (builder != null) {
                builder.append(",");
            } else {
                builder = new StringBuilder("");
            }

            builder.append(scope);
        }

        if (builder != null) {
            scopeParam = builder.toString();
        }

        return scopeParam;
    }

    AuthCodeRequest createAuthCodeRequest(final int requestCode, final String appKey, final AuthCodeCallback callback) {
        AuthCodeRequest request = new AuthCodeRequest(appKey, StringSet.REDIRECT_URL_PREFIX + appKey + StringSet.REDIRECT_URL_POSTFIX, requestCode, callback);
        request.putExtraParam(StringSet.approval_type, sessionConfig.getApprovalType() == null ? ApprovalType.INDIVIDUAL.toString() : sessionConfig.getApprovalType().toString());
        return request;
    }

    AuthCodeRequest createAuthCodeRequest(final int requestCode, final String appKey, final String refreshToken, final List<String> scopes, final AuthCodeCallback callback) {
        AuthCodeRequest request = new AuthCodeRequest(appKey, StringSet.REDIRECT_URL_PREFIX + appKey + StringSet.REDIRECT_URL_POSTFIX, requestCode, callback);
        request.putExtraHeader(StringSet.RT, refreshToken);
        request.putExtraParam(StringSet.scope, getScopesString(scopes));
        request.putExtraParam(StringSet.approval_type, sessionConfig.getApprovalType() == null ? ApprovalType.INDIVIDUAL.toString() : sessionConfig.getApprovalType().toString());
        return request;
    }

    String getRefreshToken() {
        try {
            return Session.getCurrentSession().getTokenInfo().getRefreshToken();
        } catch (IllegalStateException|NullPointerException e) {
            return null;
        }
    }

    @Override
    public final void onAuthCodeReceived(final int requestCode, AuthorizationResult result) {
        if (currentRequest == null) {
            Logger.w("Current auth code request has already finished.");
            return;
        }
        AuthCodeCallback callback = currentRequest.getCallback();

        if (callback == null) {
            Logger.w("Callback has not been set for this auth code request. Just return.");
            return;
        }

        AuthorizationCode authCode = null;
        KakaoException exception = null;

        if (result == null) {
            exception = new KakaoException(KakaoException.ErrorType.AUTHORIZATION_FAILED, "the result of authorization code request is null.");
        } else if (result.isCanceled()) {
            exception = new KakaoException(KakaoException.ErrorType.CANCELED_OPERATION, result.getResultMessage());
        } else if (result.isAuthError() || result.isError()) {
            exception = new KakaoException(KakaoException.ErrorType.AUTHORIZATION_FAILED, result.getResultMessage());
        } else {
            final String resultRedirectURL = result.getRedirectURL();
            if (resultRedirectURL != null && resultRedirectURL.startsWith(currentRequest.getRedirectURI())) {
                authCode = AuthorizationCode.createFromRedirectedUri(result.getRedirectUri());
                // authorization code가 포함되지 않음
                if (!authCode.hasAuthorizationCode()) {
                    authCode = null;
                    exception = new KakaoException(KakaoException.ErrorType.AUTHORIZATION_FAILED, "the result of authorization code request does not have authorization code.");
                }
            } else { // 기대 했던 redirect uri 불일치
                Logger.e(resultRedirectURL);
                exception = new KakaoException(KakaoException.ErrorType.AUTHORIZATION_FAILED, "the result of authorization code request mismatched the registered redirect uri. msg = " + result.getResultMessage());
            }
        }

        if (authRequestCode.getAndSet(0) != 1) {
            Logger.w("There were more than 1 auth code request simultaneously.");
        }
        currentRequest = null;
        authCodeManagers.clear();
        if (exception != null) {
            callback.onAuthCodeFailure(new ErrorResult(exception));
            return;
        }
        callback.onAuthCodeReceived(authCode.getAuthorizationCode());
    }
}
