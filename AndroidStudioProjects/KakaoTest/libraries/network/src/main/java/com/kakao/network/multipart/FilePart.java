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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Copyright 2010 Ning, Inc.
 *
 * Ning licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
public class FilePart extends Part {
    private static final String FILE_NAME = "; filename=";
    private static final String DEFAULT_FILE_PART_CONTENT_TYPE = "application/octet-stream";
    private static final String DEFAULT_FILE_PART_CHARSET = "ISO-8859-1";
    private static final String DEFAULT_FILE_PART_TRANSFER_ENCODING = "binary";
    private static final byte[] FILE_NAME_BYTES = MultipartRequestEntity.getAsciiBytes(FILE_NAME);

    private final File content;

    public FilePart(final String fieldName, final File content) {
        this(fieldName, content, DEFAULT_FILE_PART_CONTENT_TYPE, DEFAULT_FILE_PART_CHARSET, DEFAULT_FILE_PART_TRANSFER_ENCODING);
    }

    public FilePart(final String fieldName,
                    final File content,
                    final String contentType,
                    final String charset,
                    final String transferEncoding) {
        super(fieldName,
                contentType == null || contentType.isEmpty() ? DEFAULT_FILE_PART_CONTENT_TYPE : contentType,
                charset == null || charset.isEmpty() ? DEFAULT_FILE_PART_CHARSET : charset,
                transferEncoding == null || transferEncoding.isEmpty() ? DEFAULT_FILE_PART_TRANSFER_ENCODING : transferEncoding);
        this.content = content;
    }

    @Override
    protected void sendDispositionHeader(OutputStream out) throws IOException {
        super.sendDispositionHeader(out);
        String filename = content.getName();
        if (filename != null) {
            out.write(FILE_NAME_BYTES);
            out.write(QUOTE_BYTES);
            out.write(MultipartRequestEntity.getAsciiBytes(filename));
            out.write(QUOTE_BYTES);
        }
    }

    @Override
    protected long dispositionHeaderLength() {
        String filename = content.getName();
        long length = super.dispositionHeaderLength();
        if (filename != null) {
            length += FILE_NAME_BYTES.length;
            length += QUOTE_BYTES.length;
            length += MultipartRequestEntity.getAsciiBytes(filename).length;
            length += QUOTE_BYTES.length;
        }
        return length;
    }

    @Override
    protected void sendData(OutputStream out) throws IOException {
        if (lengthOfData() == 0) {

            // this file contains no data, so there is nothing to send.
            // we don't want to create a zero length buffer as this will
            // cause an infinite loop when reading.
            return;
        }

        byte[] tmp = new byte[4096];
        InputStream instream = content != null ? new FileInputStream(content) : new ByteArrayInputStream(new byte[]{});
        try {
            int len;
            while ((len = instream.read(tmp)) >= 0) {
                out.write(tmp, 0, len);
            }
        } finally {
            // we're done with the stream, close it
            instream.close();
        }
    }

    @Override
    protected long lengthOfData() {
        if (content != null) {
            return content.length();
        }

        return 0;
    }
}
