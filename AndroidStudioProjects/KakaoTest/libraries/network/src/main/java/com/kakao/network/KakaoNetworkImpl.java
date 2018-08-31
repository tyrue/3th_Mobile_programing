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

package com.kakao.network;

import com.kakao.network.multipart.MultipartRequestEntity;
import com.kakao.network.multipart.Part;
import com.kakao.util.helper.log.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * INetwork를 구현한 구현체.
 * google android에서 제공하는 HttpURLConnection을 기준으로 만들어졌다.
 * Kakao Api를 요청하기위한 하나의 Connection의 용도로 사용되며, Thread Safe 하지 않음.
 * @author leo.shin
 */
public class KakaoNetworkImpl implements INetwork {
    private static final String ISO_CHARSET = "ISO-8859-1";
    private static final int DEFAULT_CONNECTION_TO_IN_MS = 5000;
    private static final int DEFAULT_REQUEST_TO_IN_MS = 30 * 1000;

    private final List<Part> parts = new ArrayList<Part>();
    private final Map<String, String> params = new HashMap<String, String>();
    private final Map<String, String> header = new HashMap<String, String>();

    private HttpURLConnection urlConnection = null;
    private String charset = ISO_CHARSET;
    private int statusCode = -1;

    private static final TrustManager[] SIMPLE_TRUST_MANAGERS = new TrustManager[]{new SimpleX509TrustManager(null)};

    private static final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        @Override
        public boolean verify(final String hostname, final SSLSession session) {
            return true;
        }
    };

    /**
     * HttpUrlConnection 을 생성하며, property설정을 한다.
     * @param url 연결될 url.
     * @param method 연결 메소드
     * @throws IOException if there is an error while opening a connection such as SSL error
     */
    @Override
    public void create(final String url, final String method, final String charset) throws IOException {
        Logger.d("++ url: " + url);
        Logger.d("++ method: " + method);
        this.charset = charset;
        this.urlConnection = (HttpURLConnection) new URL(url).openConnection(Proxy.NO_PROXY);
        if (url.startsWith("https")) {
            final HttpsURLConnection secure = (HttpsURLConnection) urlConnection;
            final SSLContext sslContext;
            try {
                sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, SIMPLE_TRUST_MANAGERS, new SecureRandom());
            } catch (GeneralSecurityException e) {
                throw new IOException(e.getMessage());
            }
            secure.setSSLSocketFactory(sslContext.getSocketFactory());
            secure.setHostnameVerifier(DO_NOT_VERIFY);
        }
        urlConnection.setRequestMethod(method);
    }

    @Override
    public void configure() throws IOException {
        urlConnection.setDoInput(true);
        urlConnection.setConnectTimeout(DEFAULT_CONNECTION_TO_IN_MS);
        urlConnection.setReadTimeout(DEFAULT_REQUEST_TO_IN_MS);
        urlConnection.setInstanceFollowRedirects(false);
        urlConnection.setRequestProperty("Connection", "keep-alive");

        if (!header.isEmpty()) {
            for (final String key : header.keySet()) {
                urlConnection.setRequestProperty(key, header.get(key));
            }
        }

        final String reqType = urlConnection.getRequestMethod();
        if ("POST".equals(reqType) || "PUT".equals(reqType)) {
            urlConnection.setRequestProperty("Content-Length", "0");
            urlConnection.setDoOutput(true);
            int contentLength = 0;
            String postParamString = null;
            MultipartRequestEntity mre = null;
            if (!params.isEmpty()) {
                postParamString = getPostDataString(params);
                contentLength += postParamString.length();
            } else if (!parts.isEmpty()) {
                mre = new MultipartRequestEntity(parts);
                contentLength += mre.getContentLength();
                urlConnection.setRequestProperty("Content-Type", mre.getContentType());
            }

            if (contentLength > 0) {
                urlConnection.setFixedLengthStreamingMode(contentLength);
                urlConnection.setRequestProperty("Content-Length", String.valueOf(contentLength));
            }

            if (postParamString != null && !postParamString.isEmpty()) {
                urlConnection.getOutputStream().write(postParamString.getBytes(charset));
            }

            if (mre != null) {
                mre.writeRequest(urlConnection.getOutputStream());
            }
        }
    }

    @Override
    public void connect() throws IOException {
        try {
            statusCode = urlConnection.getResponseCode();
        } catch (IOException e) {
            statusCode = urlConnection.getResponseCode();
        }
    }

    @Override
    public void disconnect() {
        params.clear();
        header.clear();
        parts.clear();
        if (urlConnection != null) {
            urlConnection.disconnect();
        }
        statusCode = HttpURLConnection.HTTP_OK;
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public void addHeader(final String key, final String value) {
        header.put(key, value);
    }

    @Override
    public void addParam(final String key, final String value) {
        params.put(key, value);
    }

    @Override
    public byte[] readFully() throws IOException {
        final InputStream is = getInputStream(urlConnection);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] byteBuffer = new byte[1024];
            int nLength;
            while ((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                baos.write(byteBuffer, 0, nLength);
            }
            return baos.toByteArray();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ignore) {
            }
        }
    }

    @Override
    public void addPart(final Part part) {
        parts.add(part);
    }

    private String getPostDataString(final Map<String, String> params) throws UnsupportedEncodingException {
        final StringBuilder result = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (result.length() > 0) {
                result.append("&");
            }
            result.append(URLEncoder.encode(entry.getKey(), charset));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), charset));
        }
        return result.toString();
    }

    private InputStream getInputStream(final HttpURLConnection urlConnection) throws IOException {
        if (urlConnection.getResponseCode() < 400) {
            return urlConnection.getInputStream();
        } else {
            final InputStream ein = urlConnection.getErrorStream();
            return ein != null ? ein : new ByteArrayInputStream(new byte[0]);
        }
    }

    /**
     * A simple X509TrustManager which checks only server certificates
     */
    private static final class SimpleX509TrustManager implements X509TrustManager {

        private X509TrustManager standardTrustManager;

        private SimpleX509TrustManager(final KeyStore keystore) {
            try {
                final TrustManagerFactory tmf;
                tmf = TrustManagerFactory.getInstance("X509");
                tmf.init(keystore);
                final TrustManager[] trustManagers = tmf.getTrustManagers();
                if (trustManagers == null || trustManagers.length == 0) {
                    throw new NoSuchAlgorithmException("no trust manager found.");
                }
                standardTrustManager = (X509TrustManager) trustManagers[0];
            } catch (Exception e) {
                Logger.d("failed to initialize the standard trust manager: " + e.getMessage());
                standardTrustManager = null;
            }
        }

        @Override
        public void checkClientTrusted(final X509Certificate[] certificates, final String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(final X509Certificate[] certificates, final String authType) throws CertificateException {
            if (certificates == null) {
                throw new IllegalArgumentException("there were no certificates.");
            }
            if (certificates.length == 1) {
                // if certificates have only one, check the validity simply
                certificates[0].checkValidity();
            } else if (standardTrustManager != null) {
                standardTrustManager.checkServerTrusted(certificates, authType);
            } else {
                throw new CertificateException("there were one more certificates but no trust manager found.");
            }
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }
}
