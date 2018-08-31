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
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Random;

/**
 * @author leoshin, created at 15. 7. 31..
 */
public class MultipartRequestEntity {
    private static final String MULTIPART_FORM_CONTENT_TYPE = "multipart/form-data";
    private static final byte[] MULTIPART_CHARS =
            getAsciiBytes("-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
    private static final String ASCII_CHARSET_NAME = "US-ASCII";
    private static final Random RANDOM = new Random();

    public static byte[] getAsciiBytes(String data) {
        if (data == null) {
            throw new IllegalArgumentException("data may not be null");
        }
        try {
            return data.getBytes(ASCII_CHARSET_NAME);
        } catch (UnsupportedEncodingException var2) {
            throw new RuntimeException(var2);
        }
    }

    public static byte[] getBytes(String data, String charset) {
        if (data == null) {
            throw new IllegalArgumentException("data may not be null");
        }
        if (charset == null || charset.isEmpty()) {
            throw new IllegalArgumentException("charset may not be null or empty");
        }
        try {
            return data.getBytes(charset);
        } catch (UnsupportedEncodingException var3) {
            throw new IllegalArgumentException(String.format("Unsupported encoding: %s", new Object[]{charset}));
        }
    }

    private static byte[] generateMultipartBoundary() {
        final byte[] bytes = new byte[RANDOM.nextInt(11) + 30]; // a random size from 30 to 40
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = MULTIPART_CHARS[RANDOM.nextInt(MULTIPART_CHARS.length)];
        }
        return bytes;
    }

    private final List<Part> parts;

    private final byte[] multipartBoundary;

    private final String contentType;

    private final long contentLength;

    /**
     * Creates a new multipart entity containing the given parts.
     * @param parts The parts to include.
     */
    public MultipartRequestEntity(List<Part> parts) {
        this.parts = parts;
        this.multipartBoundary = generateMultipartBoundary();
        this.contentType = computeContentType(MULTIPART_FORM_CONTENT_TYPE);
        this.contentLength = Part.getLengthOfParts(parts, multipartBoundary);
    }

    private String computeContentType(String base) {
        final StringBuilder buffer = new StringBuilder(base);
        if (!base.endsWith(";")) {
            buffer.append(";");
        }
        try {
            return buffer.append(" boundary=").append(new String(multipartBoundary, ASCII_CHARSET_NAME)).toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeRequest(OutputStream out) throws IOException {
        Part.sendParts(out, parts, multipartBoundary);
    }

    public long getContentLength() {
        return contentLength;
    }

    public String getContentType() {
        return contentType;
    }
}
