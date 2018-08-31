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

import com.kakao.network.response.ResponseBody.ResponseBodyException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
public class ResponseBodyArray {
    private final JSONArray jsonArray;
    private final int statusCode;

    public int getStatusCode() {
        return statusCode;
    }

    public ResponseBodyArray(int statusCode, byte[] body) throws ResponseBodyException {
        this.statusCode = statusCode;
        if (body == null) {
            throw new ResponseBodyException();
        }

        try {
            this.jsonArray = new JSONArray(new String(body));
        } catch (JSONException e) {
           throw new ResponseBodyException(e);
        }
    }

    public ResponseBodyArray(int statusCode, JSONArray jsonArray) throws ResponseBodyException {
        this.statusCode = statusCode;
        if (jsonArray == null) {
            throw new ResponseBodyException();
        }
        this.jsonArray = jsonArray;
    }
    public int length() {
        return jsonArray.length();
    }
    public long getLong(int i) throws ResponseBodyException {
        try {
            Object obj = getOrThrow(i);
            if (obj instanceof Integer) {
                return (Integer) obj;
            }
            else if (obj instanceof Long) {
                return (Long) obj;
            }
            else {
                throw new ResponseBodyException();
            }
        }
        catch(Exception e) {
            throw new ResponseBodyException(e);
        }
    }
    public ResponseBody getBody(int i) throws ResponseBodyException {
        try {
            return new ResponseBody(getStatusCode(), (JSONObject) getOrThrow(i));
        }
        catch(ResponseBodyException e) {
            throw e;
        }
        catch(Exception e) {
            throw new ResponseBodyException(e);
        }
    }
    public String getString(int i) throws ResponseBodyException {
        try {
            return (String) getOrThrow(i);
        }
        catch(Exception e) {
            throw new ResponseBodyException(e);
        }
    }

    public int getInt(int i) throws ResponseBodyException {
        try {
            return (Integer) getOrThrow(i);
        }
        catch(Exception e) {
            throw new ResponseBodyException(e);
        }
    }
    
    public Boolean getBoolean(int i) throws ResponseBodyException {
        try {
            return (Boolean) getOrThrow(i);
        }
        catch(Exception e) {
            throw new ResponseBodyException(e);
        }
    }
    
    private Object getOrThrow(int index) {
        Object v = null;
        try {
            v = jsonArray.get(index);
        } catch (JSONException ignore) {
        }

        if (v == null) {
            throw new NoSuchElementException();
        }
        return v;
    }

    public <F, T> List<T> getConvertedList(ArrayConverter<F, T> converter) throws ResponseBodyException {
        if (jsonArray.length() > 0) {
            List<T> list = new ArrayList<T>(jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(converter.convert(converter.fromArray(jsonArray, i)));
            }
            return list;
        }
        return Collections.emptyList();
    }

    public <F, T> List<T> optConvertedList(ArrayConverter<F, T> converter, List<T> def) {
        try {
            return getConvertedList(converter);
        } catch (Exception e) {

        }
        return def;
    }

    @Override
    public String toString() {
        return jsonArray.toString();
    }

    public interface ArrayConverter<F, T> {
        F fromArray(JSONArray array, int i) throws ResponseBodyException;
        T convert(F o) throws ResponseBodyException;
    }

    public abstract static class PrimitiveConverter<T> implements ArrayConverter<T, T> {
        @Override
        public final T convert(T o) throws ResponseBodyException {
            return o;
        }
    }

    public static final PrimitiveConverter<String> STRING_CONVERTER = new PrimitiveConverter<String>() {
        @Override
        public String fromArray(JSONArray array, int i) throws ResponseBodyException {
            try {
                return array.getString(i);
            } catch (JSONException e) {
                throw new ResponseBodyException(e);
            }
        }
    };

    public static <T> List<T> toList(ResponseBodyArray array) throws ResponseBodyException {
        List<T> list = new ArrayList<T>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.getOrThrow(i);
            if(value instanceof JSONArray) {
                value = toList(new ResponseBodyArray(array.getStatusCode(), (JSONArray) value));
            }

            else if(value instanceof JSONObject) {
                value = ResponseBody.toMap(new ResponseBody(array.getStatusCode(), (JSONObject) value));
            }
            list.add((T)value);
        }
        return list;
    }
}
