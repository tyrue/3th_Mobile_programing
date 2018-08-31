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
package com.kakao.kakaolink.internal;

import android.text.TextUtils;

import com.kakao.util.KakaoParameterException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author MJ
 */
public final class LinkObject {
    public enum OBJTYPE {
        UNKNOWN("", false),
        TEXT("label", false),
        IMAGE("image", false),
        BUTTON("button", true),
        TEXT_LINK("link", true);


        private final String value;
        private final boolean actionable;

        OBJTYPE(final String value, final boolean actionable) {
            this.value = value;
            this.actionable = actionable;
        }
    }

    public enum DISPLAY_TYPE {
        BOTH("both"),
        SENDER("sender"),
        RECEIVER("receiver");

        private final String value;
        DISPLAY_TYPE(String value) {
            this.value = value;
        }
    }

    private final OBJTYPE objType;
    private final String text;
    private final String imageSrc;
    private final int imageWidth;
    private final int imageHeight;
    private final Action action;
    private final DISPLAY_TYPE dispType;

    private LinkObject(final OBJTYPE objType, final String msg, final String imageSrc, final int imageWidth, final int imageHeight, final Action action, final DISPLAY_TYPE dispType) {
        this.objType = objType;
        this.text = msg;
        this.imageSrc = imageSrc;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.action = action;
        this.dispType = dispType;
    }

    public static LinkObject newText(final String text, final DISPLAY_TYPE dispType) throws KakaoParameterException {
        if (TextUtils.isEmpty(text)) {
            throw new KakaoParameterException(KakaoParameterException.ERROR_CODE.CORE_PARAMETER_MISSING, "text type needs text.");
        }

        return new LinkObject(OBJTYPE.TEXT, text, null, 0, 0, null, dispType);
    }

    public static LinkObject newImage(final String src, final int width, final int height, final DISPLAY_TYPE dispType) throws KakaoParameterException {
        if (TextUtils.isEmpty(src)) {
            throw new KakaoParameterException(KakaoParameterException.ERROR_CODE.CORE_PARAMETER_MISSING, "image type needs src.");
        }
        if (width <= 80) {
            throw new KakaoParameterException(KakaoParameterException.ERROR_CODE.MINIMUM_IMAGE_SIZE_REQUIRED, "width of image type should be bigger than 80.");
        }
        if (height <= 80) {
            throw new KakaoParameterException(KakaoParameterException.ERROR_CODE.MINIMUM_IMAGE_SIZE_REQUIRED, "height of image type should be bigger than 80.");
        }
        return new LinkObject(OBJTYPE.IMAGE, null, src, width, height, null, dispType);
    }

    public static LinkObject newButton(final String text, final Action action, final DISPLAY_TYPE dispType) throws KakaoParameterException {
        if (action == null) {
            throw new KakaoParameterException(KakaoParameterException.ERROR_CODE.CORE_PARAMETER_MISSING, "button needs action.");
        }
        return new LinkObject(OBJTYPE.BUTTON, text, null, 0, 0, action, dispType);
    }

    public static LinkObject newLink(final String text, final Action action, final DISPLAY_TYPE dispType) throws KakaoParameterException {
        if (action == null) {
            throw new KakaoParameterException(KakaoParameterException.ERROR_CODE.CORE_PARAMETER_MISSING, "link needs action.");
        }

        return new LinkObject(OBJTYPE.TEXT_LINK, text, null, 0, 0, action, dispType);
    }

    public JSONObject createJSONObject() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(KakaoTalkLinkProtocol.OBJ_OBJTYPE, objType.value);

        if (!TextUtils.isEmpty(text)) {
            json.put(KakaoTalkLinkProtocol.OBJ_TEXT, text);
        }
        if (!TextUtils.isEmpty(imageSrc) && objType == OBJTYPE.IMAGE) {

            json.put(KakaoTalkLinkProtocol.OBJ_SRC, imageSrc);
            if (imageWidth > 0) {
                json.put(KakaoTalkLinkProtocol.OBJ_WIDTH, imageWidth);
            }
            if (imageHeight > 0) {
                json.put(KakaoTalkLinkProtocol.OBJ_HEIGHT, imageHeight);
            }

        }
        if (action != null && objType.actionable) {
            json.put(KakaoTalkLinkProtocol.OBJ_ACTION, action.createJSONObject());
        }
        if (dispType != null && dispType != DISPLAY_TYPE.BOTH) {
            json.put(KakaoTalkLinkProtocol.OBJ_DISPLAY_TYPE, dispType.value);
        }
        return json;
    }
}
