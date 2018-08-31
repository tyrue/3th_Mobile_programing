package com.kakao.util;

import android.content.Context;
import android.text.TextUtils;

import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.CommonProtocol;
import com.kakao.util.helper.SystemInfo;
import com.kakao.util.helper.Utility;

/**
 * Class used to hold app-specific configurations.
 * @author kevin.kang. Created on 2017. 5. 10..
 */

public class AppConfig {
    protected String appKey;
    protected String keyHash;
    protected String kaHeader;
    protected String appVer;
    protected String packageName;

    private static AppConfig instance;

    public AppConfig(final String appKey, String keyHash, String kaHeader, String appVer, String packageName) {
        if (TextUtils.isEmpty(appKey)) {
            throw new KakaoException(KakaoException.ErrorType.MISS_CONFIGURATION, String.format("need to declare %s in your AndroidManifest.xml", CommonProtocol.APP_KEY_PROPERTY));
        }
        if (TextUtils.isEmpty(keyHash)) {
            throw new IllegalStateException("Key hash is null.");
        }
        this.appKey = appKey;
        this.keyHash = keyHash;
        this.kaHeader = kaHeader;
        this.appVer = appVer;
        this.packageName = packageName;
    }

    public static AppConfig getInstance(final Context context) {
        if (instance != null)
            return instance;
        SystemInfo.initialize(context);
        String appKey = Utility.getMetadata(context, CommonProtocol.APP_KEY_PROPERTY);
        String keyHash = Utility.getKeyHash(context);
        String kaHeader = SystemInfo.getKAHeader();
        String appVer = String.valueOf(Utility.getAppVersion(context));
        String packageName = context.getPackageName();
        instance = new AppConfig(appKey, keyHash, kaHeader, appVer, packageName);
        return instance;
    }

    public String getAppKey() {
        return appKey;
    }
    public String getKeyHash() {
        return keyHash;
    }
    public String getKaHeader() {
        return kaHeader;
    }
    public String getAppVer() {
        return appVer;
    }
    public String getPackageName() {
        return packageName;
    }
}
