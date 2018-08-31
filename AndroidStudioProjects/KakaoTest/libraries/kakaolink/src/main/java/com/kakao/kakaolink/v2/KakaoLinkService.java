/*
  Copyright 2016-2017 Kakao Corp.

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
package com.kakao.kakaolink.v2;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.kakao.kakaolink.R;
import com.kakao.kakaolink.internal.KakaoTalkLinkProtocol;
import com.kakao.kakaolink.v2.network.KakaoLinkTemplateRequest;
import com.kakao.kakaolink.v2.network.LinkImageDeleteRequest;
import com.kakao.kakaolink.v2.network.LinkImageScrapRequest;
import com.kakao.kakaolink.v2.network.LinkImageUploadRequest;
import com.kakao.kakaolink.v2.network.TemplateDefaultRequest;
import com.kakao.kakaolink.v2.network.TemplateScrapRequest;
import com.kakao.kakaolink.v2.network.TemplateValidateRequest;
import com.kakao.message.template.TemplateParams;
import com.kakao.network.ErrorResult;
import com.kakao.network.NetworkTask;
import com.kakao.network.RequestConfiguration;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.network.storage.ImageDeleteResponse;
import com.kakao.network.storage.ImageUploadResponse;
import com.kakao.network.tasks.KakaoTaskQueue;
import com.kakao.util.protocol.KakaoProtocolService;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.CommonProtocol;
import com.kakao.util.helper.log.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Map;

/**
 * Class for sending KakaoTalk messages using KakaoLink v2 API.
 * @author kevin.kang
 * Created by kevin.kang on 2016. 11. 25..
 */

public class KakaoLinkService {
    private static KakaoLinkService instance;

    private KakaoProtocolService protocolService;

    KakaoLinkService(KakaoProtocolService service) {
        protocolService = service;
    }

    /**
     * Returns a singleton instance for KakaoLink v2 API.
     * @return a singleton instance of KakaoLinkService class
     */
    public static KakaoLinkService getInstance() {
        if (instance == null) {
            synchronized (KakaoLinkService.class) {
                if (instance == null) {
                    instance = new KakaoLinkService(KakaoProtocolService.Factory.getInstance());
                }
            }
        }
        return instance;
    }

    /**
     * Send KakaoLink v2 message with custom templates. Template id and arguments should be provided.
     * @param context Context to start an activity for KakaoLink
     * @param templateId id of the custom template created in developer website
     * @param templateArgs template arguments to fill in the custom template
     * @param callback success/failure callback that will contain detailed warnings or error messages
     * @throws IllegalStateException if kakao app key or android key hash is not set
     */
    public void sendCustom(final Context context, final String templateId, final Map<String, String> templateArgs, final ResponseCallback<KakaoLinkResponse> callback) {
        // App key, key hash, KA header 등을 준비한다.
        final RequestConfiguration configuration = getRequestConfiguration(context);
        // 카카오링크 4.0을 실행시킬 수 있는 카카오톡 버전(6.0) 인지 체크한다.
        if (!isKakaoLinkV2Available(context)) {
            if (callback != null) {
                callback.onFailure(getKakaoTalkNotInstalledErrorResult(context));
            }
            return;
        }
        final TemplateValidateRequest request = getTemplateValidateRequest(configuration, templateId, templateArgs);
        send(context, request, callback);
    }

    /**
     * Send KakaoLink v2 message with default templates.
     * @param context Context to start an activity for KakaoLink
     * @param templateObject Map object containing key-value pairs of template arguments
     * @param callback success/failure callback that will contain detailed warnings or error messages
     * @throws IllegalStateException if kakao app key or android key hash is not set
     */
    @SuppressWarnings("unused")
    public void sendDefault(final Context context, final Map<String, Object> templateObject, final ResponseCallback<KakaoLinkResponse> callback) {
        final RequestConfiguration configuration = getRequestConfiguration(context);
        if (!isKakaoLinkV2Available(context)) {
            if (callback != null) {
                callback.onFailure(getKakaoTalkNotInstalledErrorResult(context));
            }
            return;
        }
        final TemplateDefaultRequest request = getTemplateDefaultRequest(configuration, templateObject);
        send(context, request, callback);
    }

    /**
     * Send KakaoLink v2 message with default templates.
     * @param context Context to start an activity for KakaoLink
     * @param params TemplateParams object containing template arguments bulit with its builder
     * @param callback success/failure callback that will contain detailed warnings or error messages
     * @throws IllegalStateException if kakao app key or android key hash is not set
     */
    public void sendDefault(final Context context, final TemplateParams params, final ResponseCallback<KakaoLinkResponse> callback) {
        final RequestConfiguration configuration = getRequestConfiguration(context);
        if (!isKakaoLinkV2Available(context)) {
            if (callback != null) {
                callback.onFailure(getKakaoTalkNotInstalledErrorResult(context));
            }
            return;
        }
        final TemplateDefaultRequest request = getTemplateDefaultRequest(configuration, params);
        send(context, request, callback);
    }

    /**
     * Send a URL scrap message with custom template and template arguments.
     * @param context Context to start an activity for KakaoLink
     * @param url URL to be scrapped
     * @param templateId id of the custom template created in developer website
     * @param templateArgs template arguments to fill in the custom template
     * @param callback success/failure callback that will be passed detailed warnings or error messages
     * @throws IllegalStateException if kakao app key or android key hash is not set
     */
    @SuppressWarnings("WeakerAccess")
    public void sendScrap(final Context context, final String url, final String templateId, final Map<String, String> templateArgs, final ResponseCallback<KakaoLinkResponse> callback) {
        final RequestConfiguration configuration = getRequestConfiguration(context);
        if (!isKakaoLinkV2Available(context)) {
            if (callback != null) {
                callback.onFailure(getKakaoTalkNotInstalledErrorResult(context));
            }
            return;
        }
        final TemplateScrapRequest request = getTemplateScrapRequest(configuration, url, templateId, templateArgs);
        send(context, request, callback);
    }

    /**
     * Send a URL scrap message with default scrap template.
     * @param context Context to start an activity for KakaoLink
     * @param url URL to be scrapped
     * @param callback success/failure callback that will be passed detailed warnings or error messages
     * @throws IllegalStateException if kakao app key or android key hash is not set
     */
    public void sendScrap(final Context context, final String url, final ResponseCallback<KakaoLinkResponse> callback) {
        sendScrap(context, url, null, null, callback);
    }

    /**
     * Upload image to Kakao storage server to be used in KakaoLink message.
     * @param context Context to start an activity for KakaoLink
     * @param secureResource true if https is needed for image url, false if http is sufficient
     * @param imageFile Image file
     * @param callback success/failure callback that will be passed detailed warnings or error messages
     */
    public void uploadImage(final Context context, final Boolean secureResource, final File imageFile, final ResponseCallback<ImageUploadResponse> callback) {
        try {
            final KakaoLinkSender sender = getKakaoLinkSender();
            final RequestConfiguration configuration = getRequestConfiguration(context);
            final LinkImageUploadRequest request = getImageUploadRequest(configuration, secureResource, imageFile);
            sender.uploadImage(request, callback);
        } catch (Exception e) {
            if (callback != null) {
                callback.onFailure(new ErrorResult(e));
            }
        }
    }

    /**
     * Upload image with the given URL to Kakao storage server.
     * @param context Context to start an activity for KakaoLink
     * @param secureResource true if https is needed for image url, false if http is sufficient
     * @param imageUrl URL of image to be scrapped
     * @param callback success/failure callback that will be passed detailed warnings or error messages
     */
    public void scrapImage(final Context context, final Boolean secureResource, final String imageUrl, final ResponseCallback<ImageUploadResponse> callback) {
        try {
            final KakaoLinkSender sender = getKakaoLinkSender();
            final RequestConfiguration configuration = getRequestConfiguration(context);
            final LinkImageScrapRequest request = getImageScrapRequest(configuration, secureResource, imageUrl);
            sender.uploadImageAfterScrap(request, callback);
        } catch (Exception e) {
            if (callback != null) {
                callback.onFailure(new ErrorResult(e));
            }
        }
    }

    /**
     * Delete the image with the given URL from Kakao storage server.
     * @param context Context to start an activity for KakaoLink
     * @param imageUrl true if https is needed for image url, false if http is sufficient
     * @param callback success/failure callback that will be passed detailed warnings or error messages
     */
    public void deleteImageWithUrl(final Context context, final String imageUrl, final ResponseCallback<ImageDeleteResponse> callback) {
        try {
            final KakaoLinkSender sender = getKakaoLinkSender();
            final RequestConfiguration configuration = getRequestConfiguration(context);
            final LinkImageDeleteRequest request = getImageDeleteRequest(configuration, imageUrl, null);
            sender.deleteImage(request, callback);
        } catch (Exception e) {
            if (callback != null) {
                callback.onFailure(new ErrorResult(e));
            }
        }
    }

    /**
     * Delete the image with the given token from Kakao storage server.
     * @param context Context to start an activity for KakaoLink
     * @param imageToken Token of image to be deleted
     * @param callback success/failure callback that will be passed detailed warnings or error messages
     */
    @SuppressWarnings("unused")
    public void deleteImageWithToken(final Context context, final String imageToken, final ResponseCallback<ImageDeleteResponse> callback) {
        try {
            final KakaoLinkSender sender = getKakaoLinkSender();
            final RequestConfiguration configuration = getRequestConfiguration(context);
            final LinkImageDeleteRequest request = getImageDeleteRequest(configuration, null, imageToken);
            sender.deleteImage(request, callback);
        } catch (Exception e) {
            if (callback != null) {
                callback.onFailure(new ErrorResult(e));
            }
        }
    }

    /**
     * Internal send method that will call Kakao API server to validate user's KakaoLink 4.0 requests.
     * @param context Context to start an acitivty for KakaoLink
     * @param request KakaoLinkTemplateRequest object for default/scrap/custom validation requests
     * @param callback success/failure callback that will be passed detailed warnings or error messages
     */
    private void send(final Context context, KakaoLinkTemplateRequest request, final ResponseCallback<KakaoLinkResponse> callback) {
        try {
            final KakaoLinkSender sender = getKakaoLinkSender();
            sender.send(context, request, callback);
        } catch (Exception e) {
            if (callback != null) {
                callback.onFailure(new ErrorResult(e));
            }
        }
    }

    private ErrorResult getKakaoTalkNotInstalledErrorResult(final Context context) {
        return new ErrorResult(new KakaoException(KakaoException.ErrorType.KAKAOTALK_NOT_INSTALLED, context.getString(R.string.com_kakao_alert_install_kakaotalk)));
    }

    // Below are getters that wrap around constructors for mocking in tests.
    KakaoLinkSender getKakaoLinkSender() {
       return new KakaoLinkSender(getTaskQueue(), getKakaoLinkApi(getNetworkTask()), protocolService);
    }

    @SuppressWarnings("WeakerAccess")
    TemplateValidateRequest getTemplateValidateRequest(final RequestConfiguration configuration, final String templateId, final Map<String, String> templateArgs) {
        return new TemplateValidateRequest(configuration, templateId, templateArgs);
    }

    @SuppressWarnings("WeakerAccess")
    TemplateDefaultRequest getTemplateDefaultRequest(final RequestConfiguration configuration, final Map<String, Object> templateObject) {
        return new TemplateDefaultRequest(configuration, templateObject);
    }

    @SuppressWarnings("WeakerAccess")
    TemplateDefaultRequest getTemplateDefaultRequest(final RequestConfiguration configuration, final TemplateParams templateParams) {
        return new TemplateDefaultRequest(configuration, templateParams);
    }

    @SuppressWarnings("WeakerAccess")
    TemplateScrapRequest getTemplateScrapRequest(final RequestConfiguration configuration, final String url, final String templateId, final Map<String, String> templateArgs) {
        return new TemplateScrapRequest(configuration, url, templateId, templateArgs);
    }

    @SuppressWarnings("WeakerAccess")
    LinkImageUploadRequest getImageUploadRequest(final RequestConfiguration configuration, final Boolean secureResource, final File imageFile) {
        return new LinkImageUploadRequest(configuration, secureResource, imageFile);
    }

    @SuppressWarnings("WeakerAccess")
    LinkImageScrapRequest getImageScrapRequest(final RequestConfiguration configuration, final Boolean secureResource, final String imageUrl) {
        return new LinkImageScrapRequest(configuration, imageUrl, secureResource);
    }

    @SuppressWarnings("WeakerAccess")
    LinkImageDeleteRequest getImageDeleteRequest(final RequestConfiguration configuration, final String imageUrl, final String imageToken) {
        return new LinkImageDeleteRequest(configuration, imageUrl, imageToken);
    }

    NetworkTask getNetworkTask() {
        return new NetworkTask();
    }

    KakaoTaskQueue getTaskQueue() {
        return KakaoTaskQueue.getInstance();
    }

    KakaoLinkApi getKakaoLinkApi(final NetworkTask networkTask) {
        return new KakaoLinkApi(networkTask);
    }

    RequestConfiguration getRequestConfiguration(final Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context should not be null.");
        }
        return RequestConfiguration.createRequestConfiguration(context);
    }

    /**
     * Checks whether KakaoLink v2 messages can be sent with the installed KakoaTalk
     * @param context Context instance to be used in resolving intent
     * @return true if KakaoLink v2 can be sent with the installed KakaoTalk
     */
    public boolean isKakaoLinkV2Available(final Context context) {
        Uri uri = new Uri.Builder().scheme(KakaoTalkLinkProtocol.LINK_SCHEME).authority(KakaoTalkLinkProtocol.LINK_AUTHORITY).build();
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        return protocolService.resolveIntent(context, intent, KakaoTalkLinkProtocol.TALK_MIN_VERSION_SUPPORT_LINK_V2) != null;
    }

    String getReferrer(final RequestConfiguration configuration) {
        JSONObject json = new JSONObject();
        try {
            json.put(CommonProtocol.KA_HEADER_KEY, configuration.getKaHeader());
            json.put(KakaoTalkLinkProtocol.APP_KEY, configuration.getAppKey());
            json.put(KakaoTalkLinkProtocol.APP_VER, configuration.getAppVer());
            json.put(KakaoTalkLinkProtocol.APP_PACKAGE, configuration.getPackageName());
        } catch (JSONException e) {
            Logger.w(e);
            return "";
        }
        return json.toString();
    }


    /**
     * Returns an intent to start KakaoTalk install page of Google play store.
     *
     * @param context Context to get app information (package name, app key, key hash, and KA header) from.
     * @return Intent to start KakaoTalk install page of Google Play Store.
     */
    @SuppressWarnings("unused")
    public Intent getKakaoTalkInstallIntent(final Context context) {
        Intent marketIntent;
        try {
            marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(KakaoTalkLinkProtocol.TALK_MARKET_URL_PREFIX + getReferrer(getRequestConfiguration(context))));
            marketIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (ActivityNotFoundException e) {
            marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(KakaoTalkLinkProtocol.TALK_MARKET_URL_PREFIX_2 + getReferrer(getRequestConfiguration(context))));
            marketIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return marketIntent;
    }
}
