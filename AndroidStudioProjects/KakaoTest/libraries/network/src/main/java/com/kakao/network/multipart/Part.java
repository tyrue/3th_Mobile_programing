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

import com.kakao.util.helper.log.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

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
public abstract class Part {
    private static final String CRLF = "\r\n";
    private static final String QUOTE = "\"";
    private static final String EXTRA = "--";
    private static final String CHARSET = "; charset=";
    private static final String CONTENT_TYPE = "Content-Type: ";
    private static final String CONTENT_DISPOSITION = "Content-Disposition: form-data; name=";
    private static final String CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding: ";

    protected static final byte[] CRLF_BYTES = MultipartRequestEntity.getAsciiBytes(CRLF);
    protected static final byte[] QUOTE_BYTES = MultipartRequestEntity.getAsciiBytes(QUOTE);
    protected static final byte[] EXTRA_BYTES = MultipartRequestEntity.getAsciiBytes(EXTRA);
    protected static final byte[] CHARSET_BYTES = MultipartRequestEntity.getAsciiBytes(CHARSET);
    protected static final byte[] CONTENT_TYPE_BYTES = MultipartRequestEntity.getAsciiBytes(CONTENT_TYPE);
    protected static final byte[] CONTENT_DISPOSITION_BYTES = MultipartRequestEntity.getAsciiBytes(CONTENT_DISPOSITION);
    protected static final byte[] CONTENT_TRANSFER_ENCODING_BYTES =
            MultipartRequestEntity.getAsciiBytes(CONTENT_TRANSFER_ENCODING);

    protected final String name;
    protected final String contentTypeString;
    protected final String charsetString;
    protected final String transferEncodingString;

    protected Part(final String name, final String contentTypeString, final String charsetString, final String transferEncodingString) {
        this.name = name;
        this.contentTypeString = contentTypeString;
        this.charsetString = charsetString;
        this.transferEncodingString = transferEncodingString;
    }

    protected abstract void sendData(OutputStream var1) throws IOException;

    public String getName() {
        return name;
    }

    public String getCharSet() {
        return charsetString;
    }

    public String getTransferEncoding() {
        return transferEncodingString;
    }

    public String getContentType() {
        return contentTypeString;
    }

    protected abstract long lengthOfData();

    /**
     * Gets the length of the multipart message including the given parts.
     *
     * @param parts The parts.
     * @param partBoundary The ASCII bytes to use as the part boundary.
     * @return The total length
     * @since 3.0
     */
    static long getLengthOfParts(List<Part> parts, byte[] partBoundary) {
        try {
            if (parts == null) {
                throw new IllegalArgumentException("Parts may not be null");
            }
            long total = 0;
            for (Part part : parts) {
                long l = part.length(partBoundary);
                if (l < 0) {
                    return -1;
                }
                total += l;
            }
            total += EXTRA_BYTES.length;
            total += partBoundary.length;
            total += EXTRA_BYTES.length;
            total += CRLF_BYTES.length;
            return total;
        } catch (Exception e) {
            Logger.e("An exception occurred while getting the length of the parts", e);
            return 0L;
        }
    }

    public long length(byte[] boundary) {
        final long lengthOfData = this.lengthOfData();
        return lengthOfData < 0L ? -1L :
               lengthOfData + (long) this.startLength(boundary) + this.dispositionHeaderLength() +
               this.contentTypeHeaderLength() + this.transferEncodingHeaderLength() + this.endOfHeaderLength() +
               this.endLength();
    }

    public static void sendParts(OutputStream out, List<Part> parts, byte[] boundary) throws IOException {
        if (parts == null || parts.isEmpty()) {
            throw new IllegalArgumentException("Parts may not be null or empty");
        }
        if (boundary == null || boundary.length <= 0) {
            throw new IllegalArgumentException("partBoundary may not be empty");
        }
        for (final Part part : parts) {
            part.sendStart(out, boundary);
            part.sendDispositionHeader(out);
            part.sendContentTypeHeader(out);
            part.sendTransferEncodingHeader(out);
            part.sendEndOfHeader(out);
            part.sendData(out);
            part.sendEnd(out);
        }
        out.write(EXTRA_BYTES);
        out.write(boundary);
        out.write(EXTRA_BYTES);
        out.write(CRLF_BYTES);
    }

    protected void sendStart(OutputStream out, byte[] boundary) throws IOException {
        out.write(EXTRA_BYTES);
        out.write(boundary);
    }

    protected int startLength(byte[] boundary) {
        return EXTRA_BYTES.length + boundary.length;
    }

    protected void sendDispositionHeader(OutputStream out) throws IOException {
        final String name = this.name;
        if (name != null) {
            out.write(CRLF_BYTES);
            out.write(CONTENT_DISPOSITION_BYTES);
            out.write(QUOTE_BYTES);
            out.write(MultipartRequestEntity.getAsciiBytes(name));
            out.write(QUOTE_BYTES);
        }
    }

    protected long dispositionHeaderLength() {
        long length = 0L;
        final String name = this.name;
        if (name != null && !name.isEmpty()) {
            length += CRLF_BYTES.length;
            length += CONTENT_DISPOSITION_BYTES.length;
            length += QUOTE_BYTES.length;
            length += MultipartRequestEntity.getAsciiBytes(name).length;
            length += QUOTE_BYTES.length;
        }
        return length;
    }

    protected void sendContentTypeHeader(OutputStream out) throws IOException {
        final String contentType = this.contentTypeString;
        if (contentType != null) {
            out.write(CRLF_BYTES);
            out.write(CONTENT_TYPE_BYTES);
            out.write(MultipartRequestEntity.getAsciiBytes(contentType));

            final String charSet = this.charsetString;
            if (charSet != null) {
                out.write(CHARSET_BYTES);
                out.write(MultipartRequestEntity.getAsciiBytes(charSet));
            }
        }
    }

    protected long contentTypeHeaderLength() {
        long length = 0L;
        final String contentType = this.contentTypeString;
        if (contentType != null) {
            length += CRLF_BYTES.length;
            length += CONTENT_TYPE_BYTES.length;
            length += MultipartRequestEntity.getAsciiBytes(contentType).length;

            final String charSet = this.charsetString;
            if (charSet != null) {
                length += CHARSET_BYTES.length;
                length += MultipartRequestEntity.getAsciiBytes(charSet).length;
            }
        }
        return length;
    }

    protected void sendTransferEncodingHeader(OutputStream out) throws IOException {
        final String transferEncoding = this.transferEncodingString;
        if (transferEncoding != null) {
            out.write(CRLF_BYTES);
            out.write(CONTENT_TRANSFER_ENCODING_BYTES);
            out.write(MultipartRequestEntity.getAsciiBytes(transferEncoding));
        }
    }

    protected long transferEncodingHeaderLength() {
        long length = 0L;
        final String transferEncoding = this.transferEncodingString;
        if (transferEncoding != null) {
            length += CRLF_BYTES.length;
            length += CONTENT_TRANSFER_ENCODING_BYTES.length;
            length += MultipartRequestEntity.getAsciiBytes(transferEncoding).length;
        }
        return length;
    }

    protected void sendEndOfHeader(OutputStream out) throws IOException {
        out.write(CRLF_BYTES);
        out.write(CRLF_BYTES);
    }

    protected long endOfHeaderLength() {
        return CRLF_BYTES.length * 2;
    }

    protected void sendEnd(OutputStream out) throws IOException {
        out.write(CRLF_BYTES);
    }

    protected long endLength() {
        return CRLF_BYTES.length;
    }
}
