package com.kakao.kakaolink.v2.network;

import android.net.Uri;

import com.kakao.network.RequestConfiguration;
import com.kakao.network.ServerProtocol;
import com.kakao.network.storage.ImageScrapRequest;

/**
 * @author kevin.kang. Created on 2017. 3. 20..
 */

public class LinkImageScrapRequest extends ImageScrapRequest {
    public LinkImageScrapRequest(RequestConfiguration configuration, final String imageUrl, final Boolean secureResource) {
        super(configuration, imageUrl, secureResource);
    }

    @Override
    public Uri.Builder getUriBuilder() {
        Uri.Builder builder = super.getUriBuilder();
        builder.path(ServerProtocol.LINK_IMAGE_SCRAP_PATH);
        return builder;
    }
}
