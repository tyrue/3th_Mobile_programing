package com.kakao.auth.mocks;

import com.kakao.util.AppConfig;

/**
 * @author kevin.kang. Created on 2017. 6. 1..
 */

public class TestAppConfig extends AppConfig {
    public TestAppConfig(String appKey, String keyHash, String kaHeader, String appVer, String packageName) {
        super(appKey, keyHash, kaHeader, appVer, packageName);
    }

    public static AppConfig createTestAppConfig() {
        return new AppConfig("sample_app_key", "sample_key_hash", "sample_ka_header", "sample_app_ver", "sample_package_name");
    }
}
