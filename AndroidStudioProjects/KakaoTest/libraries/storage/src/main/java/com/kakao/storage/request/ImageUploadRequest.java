/**
 * Copyright 2014-2015 Kakao Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kakao.storage.request;

import com.kakao.auth.network.request.ApiRequest;
import com.kakao.network.ServerProtocol;
import com.kakao.network.helper.QueryString;
import com.kakao.network.multipart.FilePart;
import com.kakao.network.multipart.Part;
import com.kakao.storage.StringSet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author leoshin on 15. 9. 8.
 */
public class ImageUploadRequest extends ApiRequest {
    private final File imageFile;
    private final boolean secureResource;

    public ImageUploadRequest(File imageFile, boolean secureResource) {
        this.imageFile = imageFile;
        this.secureResource = secureResource;
    }

    @Override
    public String getMethod() {
        return POST;
    }

    @Override
    public String getUrl() {
        String url = ApiRequest.createBaseURL(ServerProtocol.API_AUTHORITY, ServerProtocol.STORAGE_UPLOAD_IMAGE);
        if (secureResource) {
            QueryString qs = new QueryString();
            qs.add(StringSet.secure_resource, String.valueOf(secureResource));
            url = url + "?" + qs.toString();
        }
        return url;
    }

    @Override
    public List<Part> getMultiPartList() {
        List<Part> filePart = new ArrayList<Part>();
        if (imageFile != null && imageFile.exists()) {
            filePart.add(new FilePart(StringSet.file, imageFile));
        }
        return filePart;
    }
}
