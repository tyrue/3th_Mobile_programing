package com.kakao.kakaolink.v2.network;


import android.net.Uri;

import com.kakao.network.RequestConfiguration;
import com.kakao.network.ServerProtocol;
import com.kakao.network.storage.ImageUploadRequest;

import java.io.File;

/**
 * @author kevin.kang. Created on 2017. 3. 20..
 */

public class LinkImageUploadRequest extends ImageUploadRequest {
    public LinkImageUploadRequest(final RequestConfiguration configuration, final Boolean secureResource, final File file) {
        super(configuration, secureResource, file);
    }

    @Override
    public Uri.Builder getUriBuilder() {
        Uri.Builder builder = super.getUriBuilder();
        builder.path(ServerProtocol.LINK_IMAGE_UPLOAD_PATH);
        return builder;
    }
}
