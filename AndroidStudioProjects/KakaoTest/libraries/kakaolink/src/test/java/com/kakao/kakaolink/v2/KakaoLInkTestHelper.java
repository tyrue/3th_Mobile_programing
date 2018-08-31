package com.kakao.kakaolink.v2;

import android.app.AlertDialog;
import android.content.Context;

import com.kakao.kakaolink.internal.KakaoTalkLinkProtocol;
import com.kakao.kakaolink.v2.network.TemplateValidateRequest;
import com.kakao.network.NetworkTask;
import com.kakao.network.RequestConfiguration;
import com.kakao.network.response.ResponseData;
import com.kakao.util.AppConfig;
import com.kakao.util.helper.log.Logger;

import org.json.JSONException;
import org.json.JSONObject;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.io.IOException;

/**
 * @author kevin.kang. Created on 2016. 11. 29..
 */

public class KakaoLInkTestHelper {
    public static RequestConfiguration createMockRequestConfiguration() {
        AppConfig appConfig = new AppConfig("sample_app_key", "sample_key_hash", "sample_ka_header","sample_app_ver", "sample_package_name");
        return new RequestConfiguration(appConfig, "samle_ka_header", "");
    }

    public static ResponseData createMockResponseData() {
        JSONObject object = new JSONObject();
        JSONObject templateMsg = new JSONObject();
        JSONObject warningMsg = new JSONObject();
        JSONObject argumentMsg = new JSONObject();
        try {
            templateMsg.put("key1", "value1");
            templateMsg.put("key2", "value2");
            object.put(KakaoTalkLinkProtocol.TEMPLATE_MSG, templateMsg);
            object.put(KakaoTalkLinkProtocol.WARNING_MSG, warningMsg);
            object.put(KakaoTalkLinkProtocol.ARGUMENT_MSG, argumentMsg);
        } catch (JSONException e) {
            Logger.e(e.toString());
        }
        return new ResponseData(200, object.toString().getBytes());
    }

    public static NetworkTask createMockNetworkTask() {
        NetworkTask task = Mockito.spy(new NetworkTask());
        try {
            Mockito.doReturn(createMockResponseData()).when(task).request(ArgumentMatchers.any(TemplateValidateRequest.class));
        } catch (IOException e) {
        }
        return task;
    }
}
