package com.kakao.sdk.link.sample.kakaolink;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.AbstractCursor;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import com.kakao.util.helper.log.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author kevin.kang
 * Created by kevin.kang on 2017. 2. 13..
 */

public class KakaoLinkSampleProvider extends ContentProvider {
    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(final Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return new AbstractCursor() {
            @Override
            public int getCount() {
                return 1;
            }

            @Override
            public String[] getColumnNames() {
                return new String[] {
                    OpenableColumns.DISPLAY_NAME
                };
            }

            @Override
            public String getString(int column) {
                return uri.getLastPathSegment();
            }

            @Override
            public short getShort(int column) {
                return 0;
            }

            @Override
            public int getInt(int column) {
                return 0;
            }

            @Override
            public long getLong(int column) {
                return 0;
            }

            @Override
            public float getFloat(int column) {
                return 0;
            }

            @Override
            public double getDouble(int column) {
                return 0;
            }

            @Override
            public boolean isNull(int column) {
                return false;
            }
        };
//        return null;
    }

    @Override
    public String getType(Uri uri) {
//        String fileName = uri.getLastPathSegment();
//        String[] splits = fileName.split(".");
//        if (splits.length > 1) {
//            return splits[splits.length - 1];
//        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public AssetFileDescriptor openAssetFile(Uri uri, String mode) throws FileNotFoundException {
        if (getContext() == null) {
            return null;
        }
        AssetManager am = getContext().getAssets();
        String fileName = uri.getLastPathSegment();

        AssetFileDescriptor afd = null;
        if (fileName == null) {
            throw new NullPointerException("Null file name passed.");
        }
        try {
            afd = am.openFd(fileName);
        } catch (IOException e) {
            Logger.e(e.toString());
        }
        return afd;
    }
}
