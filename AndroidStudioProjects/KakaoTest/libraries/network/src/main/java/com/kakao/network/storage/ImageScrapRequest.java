package com.kakao.network.storage;

import android.net.Uri;

import com.kakao.network.KakaoRequest;
import com.kakao.network.RequestConfiguration;
import com.kakao.network.ServerProtocol;
import com.kakao.network.StringSet;

/**
 * @author kevin.kang. Created on 2017. 3. 20..
 */

public class ImageScrapRequest extends KakaoRequest {
    private String imageUrl;
    private Boolean secureResource;

    protected ImageScrapRequest(RequestConfiguration configuration, final String imageUrl, final Boolean secureResource) {
        super(configuration);
        this.imageUrl = imageUrl;
        this.secureResource = secureResource;
    }

    @Override
    public String getMethod() {
        return POST;
    }

    @Override
    public Uri.Builder getUriBuilder() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(ServerProtocol.SCHEME);
        builder.authority(ServerProtocol.API_AUTHORITY);
        if (secureResource) {
            builder.appendQueryParameter(StringSet.SECURE_RESOURCE, String.valueOf(secureResource));
        }
        if (imageUrl != null) {
            builder.appendQueryParameter(StringSet.IMAGE_URL, imageUrl);
        }
        return builder;
    }
}
