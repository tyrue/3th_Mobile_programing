package com.kakao.network.storage;

import com.kakao.network.response.ResponseBody;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

/**
 * @author kevin.kang. Created on 2017. 3. 20..
 */

public class ImageUploadResponse {
    private ImageInfo original;

    private static final String INFOS = "infos";
    private static final String URL = "url";
    private static final String LENGTH = "length";
    private static final String CONTENT_TYPE = "content_type";
    private static final String WIDTH = "width";
    private static final String HEIGHT = "height";

    public ImageUploadResponse(final ResponseBody responseBody) {
        if (responseBody.getStatusCode() != HttpURLConnection.HTTP_OK) {
            throw new KakaoException(KakaoException.ErrorType.ILLEGAL_ARGUMENT, responseBody.toString());
        }
        JSONObject resJson = responseBody.getJson();

        try {
            JSONObject infos = resJson.getJSONObject(INFOS);
            this.original = getImageInfo(infos.getJSONObject(ImageInfo.ImageSize.ORIGINAL.getValue()));
        } catch (JSONException e) {
            Logger.w(e.toString());
        }
    }

    public ImageInfo getOriginal() {
        return original;
    }

    ImageInfo getImageInfo(final JSONObject jsonObject) throws JSONException {
        String url = jsonObject.getString(URL);
        Integer length = jsonObject.getInt(LENGTH);
        String contentType =jsonObject.getString(CONTENT_TYPE);
        Integer width = jsonObject.getInt(WIDTH);
        Integer height = jsonObject.getInt(HEIGHT);
        return new ImageInfo(url, length, contentType, width, height);
    }
}
