package com.kakao.network;

import android.content.Context;
import android.text.TextUtils;

import com.kakao.util.AppConfig;
import com.kakao.util.helper.CommonProtocol;
import com.kakao.util.helper.SystemInfo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * API request를 보내기 위해 필요한 다양한 값들을 관리하는 클래스.
 * Created by kevin.kang on 2016. 11. 29..
 */

public class RequestConfiguration {
    private AppConfig appConfig;
    private String kaHeader;
    private String extras;

    private static RequestConfiguration configuration;

    public RequestConfiguration(AppConfig appConfig, String kaHeader, String extras) {
        this.appConfig = appConfig;
        this.kaHeader = kaHeader;
        if (TextUtils.isEmpty(this.kaHeader)) {
            throw new IllegalStateException("KA Header is null.");
        }
        this.extras = extras;

    }

    public static RequestConfiguration createRequestConfiguration(final Context context) {
        if (configuration != null)
            return configuration;

        AppConfig appConfig = AppConfig.getInstance(context);
        SystemInfo.initialize(context);
        String kaHeader = SystemInfo.getKAHeader();

        String extrasString;
        JSONObject extras = new JSONObject();
        try {
            extras.put(CommonProtocol.APP_PACKAGE, context.getPackageName());
            extras.put(CommonProtocol.KA_HEADER_KEY, kaHeader);
            extras.put(CommonProtocol.APP_KEY_HASH, appConfig.getKeyHash());
            extrasString = extras.toString();
        } catch (JSONException e) {
            throw new IllegalArgumentException("JSON parsing error. Malformed parameters were provided. Detailed error message: " + e.toString());
        }

        configuration = new RequestConfiguration(appConfig, kaHeader, extrasString);
        return configuration;
    }


    public String getAppKey() {
        return appConfig.getAppKey();
    }
    public String getKeyHash() {
        return appConfig.getKeyHash();
    }
    public String getAppVer() {
        return appConfig.getAppVer();
    }
    public String getPackageName() {
        return appConfig.getPackageName();
    }
    public String getKaHeader() {
        return kaHeader;
    }
    public String getExtras() {
        return extras;
    }
}
