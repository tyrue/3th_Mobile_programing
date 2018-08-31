package com.kakao.kakaolink.v2.network;

import android.net.Uri;

import com.kakao.network.RequestConfiguration;
import com.kakao.network.ServerProtocol;
import com.kakao.network.storage.ImageDeleteRequest;

/**
 * @author kevin.kang. Created on 2017. 3. 20..
 */

public class LinkImageDeleteRequest extends ImageDeleteRequest {
    public LinkImageDeleteRequest(RequestConfiguration configuration, final String imageUrl, final String imageToken) {
        super(configuration, imageUrl, imageToken);
    }

    @Override
    public Uri.Builder getUriBuilder() {
        Uri.Builder builder = super.getUriBuilder();
        builder.path(ServerProtocol.LINK_IMAGE_DELETE_PATH);
        return builder;
    }
}
