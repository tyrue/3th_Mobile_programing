/**
 * Copyright 2014-2016 Kakao Corp.
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
package com.kakao.network.multipart;

import java.io.IOException;
import java.io.OutputStream;

public class StringPart extends Part {

    private static final String DEFAULT_STRING_PART_CONTENT_TYPE = "text/plain";
    private static final String DEFAULT_STRING_PART_CHARSET = "UTF-8";
    private static final String DEFAULT_STRING_PART_TRANSFER_ENCODING = "8bit";

    private final String value;
    private byte[] content;

    public StringPart(String fieldName, String value) {
        this(fieldName, value, DEFAULT_STRING_PART_CONTENT_TYPE, DEFAULT_STRING_PART_CHARSET, DEFAULT_STRING_PART_TRANSFER_ENCODING);
    }

    public StringPart(final String fieldName,
                      final String value,
                      final String contentType,
                      final String charset,
                      final String transferEncoding) {
        super(fieldName,
                contentType == null || contentType.isEmpty() ? DEFAULT_STRING_PART_CONTENT_TYPE : contentType,
                charset == null || charset.isEmpty() ? DEFAULT_STRING_PART_CHARSET : charset,
                transferEncoding == null || transferEncoding.isEmpty() ? DEFAULT_STRING_PART_TRANSFER_ENCODING : transferEncoding);
        this.value = value;
    }

    @Override
    protected void sendData(OutputStream out) throws IOException {
        out.write(this.getContent());
    }

    @Override
    protected long lengthOfData() {
        return (long)this.getContent().length;
    }

    private byte[] getContent() {
        if(this.content == null) {
            this.content = MultipartRequestEntity.getBytes(this.value, charsetString);
        }
        return this.content;
    }
}
