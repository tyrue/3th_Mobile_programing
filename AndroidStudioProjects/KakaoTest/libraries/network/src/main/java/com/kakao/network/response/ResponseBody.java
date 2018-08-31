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
package com.kakao.network.response;

import com.kakao.util.helper.log.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class ResponseBody {
    public static class ResponseBodyException extends Exception {
        private static final long serialVersionUID = 8171429617556607125L;

        public ResponseBodyException() {
        }

        public ResponseBodyException(String errMsg) {
            super(errMsg);
        }

        public ResponseBodyException(Exception e) {
            super(e);
        }
    }

    private JSONObject json = null;
    private final int statusCode;

    public int getStatusCode() {
        return statusCode;
    }

    public ResponseBody(int statusCode, byte[] body) throws ResponseBodyException {
        this.statusCode = statusCode;
        if (body == null) {
            throw new ResponseBodyException();
        }

        if (body.length != 0) {
            try {
                this.json = new JSONObject(new String(body));
            } catch (JSONException e) {
                throw new ResponseBodyException(e);
            }
        }
    }

    public ResponseBody(int statusCode, JSONObject body) throws ResponseBodyException {
        this.statusCode = statusCode;
        if (body == null) {
            throw new ResponseBodyException();
        }
        this.json = body;
    }
    
    private Object getOrThrow(String key) {
        Object v = null;
        try {
            v = json.get(key);
        } catch (JSONException ignor) {
        }

        if (v == null) {
            throw new NoSuchElementException(key);
        }

        if (v == JSONObject.NULL) {
            return null;
        }
        return v;
    }

    public long getLong(String key) throws ResponseBodyException {
        try {
            Object obj = getOrThrow(key);
            if (obj instanceof Integer) {
                return (Integer) obj;
            }
            else if (obj instanceof Long) {
                return (Long) obj;
            }
            else {
                throw new ResponseBodyException();
            }
        } catch (ResponseBodyException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseBodyException(e);
        }
    }

    public int getInt(String key) throws ResponseBodyException {
        try {
            return (Integer) getOrThrow(key);
        } catch (Exception e) {
            throw new ResponseBodyException(e);
        }
    }

    public boolean has(String key) {
        return json.has(key);
    }

    public int optInt(String key, int def) {
        if (has(key)) {
            try {
                return getInt(key);
            } catch (ResponseBodyException e) {
            }
        }
        return def;
    }

    public String getString(String key) throws ResponseBodyException {
        try {
            return (String) getOrThrow(key);
        } catch (Exception e) {
            throw new ResponseBodyException(e);
        }
    }

    public String optString(String key, String def) {
        if (has(key)) {
            try {
                return getString(key);
            } catch (ResponseBodyException e) {
            }
        }
        return def;
    }

    public boolean getBoolean(String key) throws ResponseBodyException {
        try {
            return (Boolean) getOrThrow(key);
        } catch (Exception e) {
            throw new ResponseBodyException(e);
        }
    }

    public ResponseBodyArray getArray(String key) throws ResponseBodyException {
        try {
            return new ResponseBodyArray(getStatusCode(), (JSONArray) getOrThrow(key));
        } catch (ResponseBodyException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseBodyException(e);
        }
    }

    public ResponseBodyArray optArray(String key, ResponseBodyArray def) {
        if (has(key)) {
            try {
                return getArray(key);
            } catch (ResponseBodyException e) {
            }
        }
        return def;
    }

    public ResponseBody getBody(String key) throws ResponseBodyException {
        try {
            return new ResponseBody(getStatusCode(), (JSONObject)getOrThrow(key));
        } catch (Exception e) {
            throw new ResponseBodyException(e);
        }
    }

    public ResponseBody optBody(String key, ResponseBody def) {
        if (has(key)) {
            try {
                return getBody(key);
            } catch (ResponseBodyException e) {
            }
        }
        return def;
    }

    public JSONObject getJson() {
        return json;
    }

    public boolean optBoolean(String key, boolean def) {
        if (has(key)) {
            try {
                return getBoolean(key);
            } catch (ResponseBodyException e) {
            }
        }
        return def;
    }

    public long optLong(String key, long def) {
        if (has(key)) {
            try {
                return getLong(key);
            } catch (ResponseBodyException e) {
            }
        }
        return def;
    }

    @Override
    public String toString() {
        return json.toString();
    }

    public <F, T> List<T> getConvertedList(String key, Converter<F, T> converter) throws ResponseBodyException {
        ResponseBodyArray array = getArray(key);
        if (array.length() > 0) {
            List<T> list = new ArrayList<T>(array.length());
            for (int i = 0; i < array.length(); i++) {
                list.add(converter.convert(converter.fromArray(array, i)));
            }
            return list;
        }
        return Collections.emptyList();
    }

    public <F, T> List<T> optConvertedList(String key, Converter<F, T> converter, List<T> def) throws ResponseBodyException {
        if (has(key)) {
            return getConvertedList(key, converter);
        }
        return def;
    }

    public <T> T getConverted(String key, BodyConverter<T> converter) throws ResponseBodyException {
        return converter.convert(getBody(key));
    }

    public <T> T optConverted(String key, BodyConverter<T> converter, T def) throws ResponseBodyException {
        if (has(key)) {
            return converter.convert(getBody(key));
        }
        return def;
    }

    public interface Converter<F, T> {
        F fromArray(ResponseBodyArray array, int i) throws ResponseBodyException;
        T convert(F o) throws ResponseBodyException;
    }

    public abstract static class BodyConverter<T> implements Converter<ResponseBody, T> {
        @Override
        public final ResponseBody fromArray(ResponseBodyArray array, int i) throws ResponseBodyException {
            return array.getBody(i);
        }

        public abstract T convert(ResponseBody o) throws ResponseBodyException;
    }

    public abstract static class PrimitiveConverter<T> implements Converter<T, T> {
        @Override
        public final T convert(T o) throws ResponseBodyException {
            return o;
        }
    }

    public static final PrimitiveConverter<Long> LONG_CONVERTER = new PrimitiveConverter<Long>() {
        @Override
        public Long fromArray(ResponseBodyArray array, int i) throws ResponseBodyException {
            return array.getLong(i);
        }
    };

    public static final PrimitiveConverter<String> STRING_CONVERTER = new PrimitiveConverter<String>() {
        @Override
        public String fromArray(ResponseBodyArray array, int i) throws ResponseBodyException {
            return array.getString(i);
        }
    };

    private Iterator<String> getKeys() {
        return json == null ? null : json.keys();
    }

    public static <T> Map<String, T> toMap(ResponseBody body) throws ResponseBodyException {
        Map<String, T> map = new HashMap<String, T>();

        Iterator<String> keysItr = body.getKeys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = body.getOrThrow(key);

            if(value instanceof JSONArray) {
                value = ResponseBodyArray.toList(new ResponseBodyArray(body.getStatusCode(), (JSONArray) value));
            }

            else if(value instanceof JSONObject) {
                value = toMap(new ResponseBody(body.getStatusCode(), (JSONObject) value));
            }
            map.put(key, (T)value);
        }
        return map;
    }
}
