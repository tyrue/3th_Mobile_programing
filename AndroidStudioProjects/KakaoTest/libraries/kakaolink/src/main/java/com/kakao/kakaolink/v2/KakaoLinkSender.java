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
import com.kakao.kakaolink.v2.network.TemplateScrapResponse;
import com.kakao.kakaolink.v2.network.TemplateValidateRequest;
import com.kakao.kakaolink.v2.network.KakaoLinkTemplateResponse;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.network.storage.ImageDeleteResponse;
import com.kakao.network.storage.ImageUploadResponse;
import com.kakao.network.tasks.KakaoResultTask;
import com.kakao.network.tasks.KakaoTaskQueue;
import com.kakao.util.helper.log.Logger;
import com.kakao.util.protocol.KakaoProtocolService;
import com.kakao.util.exception.KakaoException;

import org.json.JSONObject;

/**
 * 실제로 Template validate을 하고 Intent를 생성하여 카카오링크 v2 메시지를 보내는 클래스.
 *
 * @author kevin.kang
 * Created by kevin.kang on 2016. 11. 28..
 */

class KakaoLinkSender {
    private final KakaoTaskQueue taskQueue;
    private final KakaoLinkApi linkApi;
    private final KakaoProtocolService protocolService;

    KakaoLinkSender(final KakaoTaskQueue taskQueue, final KakaoLinkApi linkApi, final KakaoProtocolService protocolService) {
        this.taskQueue = taskQueue;
        this.linkApi = linkApi;
        this.protocolService = protocolService;
    }

    void send(final Context context, final KakaoLinkTemplateRequest request, final ResponseCallback<KakaoLinkResponse> callback) {
        taskQueue.addTask(getKakaoLinkResultTask(context, request, callback));
    }

    void uploadImage(final LinkImageUploadRequest request, final ResponseCallback<ImageUploadResponse> callback) {
        taskQueue.addTask(getImageUploadResultTask(request, callback));
    }

    void uploadImageAfterScrap(final LinkImageScrapRequest request, final ResponseCallback<ImageUploadResponse> callback) {
        taskQueue.addTask(getImageScrapResultTask(request, callback));
    }

    void deleteImage(final LinkImageDeleteRequest request, final ResponseCallback<ImageDeleteResponse> callback) {
        taskQueue.addTask(getImageDeleteResultTask(request, callback));
    }

    @SuppressWarnings("WeakerAccess")
    KakaoResultTask<KakaoLinkResponse> getKakaoLinkResultTask(final Context context, final KakaoLinkTemplateRequest request, final ResponseCallback<KakaoLinkResponse> callback) {
        return new KakaoResultTask<KakaoLinkResponse>(callback) {
            @Override
            public KakaoLinkResponse call() throws Exception {
                KakaoLinkTemplateResponse response;
                if (request instanceof TemplateValidateRequest) {
                    response = linkApi.requestTemplateValidate((TemplateValidateRequest)request);
                } else if (request instanceof TemplateDefaultRequest) {
                    response = linkApi.requestTemplateDefault((TemplateDefaultRequest)request);
                } else {
                    response = linkApi.requestTemplateScrap((TemplateScrapRequest)request);
                }

                Intent intent = createKakaoLinkIntent(context, request, response);
                Intent resolvedIntent = protocolService.resolveIntent(context, intent, TALK_MIN_VERSION_SUPPORT_LINK_40);
                context.startActivity(resolvedIntent);
                return new KakaoLinkResponse(response.getTemplateMsg(), response.getWarningMsg(), response.getArgumentMsg());
            }
        };
    }

    @SuppressWarnings("WeakerAccess")
    KakaoResultTask<ImageUploadResponse> getImageUploadResultTask(final LinkImageUploadRequest request, final ResponseCallback<ImageUploadResponse> callback) {
        return new KakaoResultTask<ImageUploadResponse>(callback) {
            @Override
            public ImageUploadResponse call() throws Exception {
                return linkApi.requestImageUpload(request);
            }
        };
    }

    KakaoResultTask<ImageUploadResponse> getImageScrapResultTask(final LinkImageScrapRequest request, final ResponseCallback<ImageUploadResponse> callback) {
        return new KakaoResultTask<ImageUploadResponse>(callback) {
            @Override
            public ImageUploadResponse call() throws Exception {
                return linkApi.requestImageScrap(request);
            }
        };
    }

    KakaoResultTask<ImageDeleteResponse> getImageDeleteResultTask(final LinkImageDeleteRequest request, final ResponseCallback<ImageDeleteResponse> callback) {
        return new KakaoResultTask<ImageDeleteResponse>(callback) {
            @Override
            public ImageDeleteResponse call() throws Exception {
                return linkApi.requestImageDelete(request);
            }
        };
    }

    Intent createKakaoLinkIntent(final Context context, final KakaoLinkTemplateRequest request, final KakaoLinkTemplateResponse response) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(KakaoTalkLinkProtocol.LINK_SCHEME).authority(KakaoTalkLinkProtocol.LINK_AUTHORITY);
        builder.appendQueryParameter(KakaoTalkLinkProtocol.LINKVER, KakaoTalkLinkProtocol.LINK_VERSION_40);
        builder.appendQueryParameter(KakaoTalkLinkProtocol.APP_KEY, request.getAppKey());
        builder.appendQueryParameter(KakaoTalkLinkProtocol.APP_VER, request.getAppVer());


        String templateId;
        String templateArgsString = null;

        if (response instanceof TemplateScrapResponse) {
            templateId = ((TemplateScrapResponse) response).getTemplateId();
        } else {
            templateId = request.getTemplateId();
        }
        if (templateId != null) {
            builder.appendQueryParameter(KakaoTalkLinkProtocol.TEMPLATE_ID, templateId);
        }

        if (response instanceof TemplateScrapResponse) {
            JSONObject templateArgs = ((TemplateScrapResponse) response).getTemplateArgs();
            if (templateArgs != null) {
                templateArgsString = templateArgs.toString();
            }
        } else  {
            templateArgsString = request.getTemplateArgsString();
        }
        if (templateArgsString != null) {
            builder.appendQueryParameter(KakaoTalkLinkProtocol.TEMPLATE_ARGS, templateArgsString);
        }

        String extras = request.getExtras();
        if (extras != null) {
            builder.appendQueryParameter(KakaoTalkLinkProtocol.EXTRAS, extras);
        }
        builder.appendQueryParameter(KakaoTalkLinkProtocol.TEMPLATE_JSON, response.getTemplateMsg().toString());
        Uri uri = builder.build();
        if (uri.toString().length() > KakaoTalkLinkProtocol.LINK_URI_MAX_SIZE) {
            throw new KakaoException(KakaoException.ErrorType.URI_LENGTH_EXCEEDED, context.getString(R.string.com_kakao_alert_uri_too_long));
        }
        Intent intent = new Intent(Intent.ACTION_SEND, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    private static final int TALK_MIN_VERSION_SUPPORT_LINK_40 = 1400255; // 6.0.0
}