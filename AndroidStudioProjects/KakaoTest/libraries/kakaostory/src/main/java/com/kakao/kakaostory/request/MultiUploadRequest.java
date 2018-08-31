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
package com.kakao.kakaostory.request;

import com.kakao.auth.StringSet;
import com.kakao.network.ServerProtocol;
import com.kakao.auth.network.request.ApiRequest;
import com.kakao.network.multipart.FilePart;
import com.kakao.network.multipart.Part;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author leoshin, created at 15. 7. 30..
 */
public class MultiUploadRequest extends ApiRequest {
    private final List<Part> partList;

    public MultiUploadRequest(List<File> fileList) {
        this.partList = new ArrayList<Part>();
        if (fileList != null) {
            for (int i = 0; i < fileList.size(); i++) {
                partList.add(new FilePart(StringSet.file + "_" + String.valueOf(i + 1), fileList.get(i)));
            }
        }

    }

    @Override
    public String getMethod() {
        return POST;
    }

    @Override
    public String getUrl() {
        return createBaseURL(ServerProtocol.API_AUTHORITY, ServerProtocol.STORY_MULTI_UPLOAD_PATH);
    }

    @Override
    public Map<String, String> getParams() {
        return Collections.emptyMap();
    }

    @Override
    public List<Part> getMultiPartList() {
        return partList;
    }
}
