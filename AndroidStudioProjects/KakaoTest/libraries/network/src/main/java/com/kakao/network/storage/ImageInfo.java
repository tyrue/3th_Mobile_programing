package com.kakao.network.storage;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author kevin.kang. Created on 2017. 3. 22..
 */

public class ImageInfo implements Parcelable {
    public enum ImageSize {
        ORIGINAL("original");
        private final String imageSize;
        ImageSize(final String imageSize) {
            this.imageSize = imageSize;
        }
        public String getValue() {
            return this.imageSize;
        }
    }

    private String url;
    private Integer length;
    private String contentType;
    private Integer width;
    private Integer height;

    ImageInfo(String url, Integer length, String contentType, Integer width, Integer height) {
        this.url = url;
        this.length = length;
        this.contentType = contentType;
        this.width = width;
        this.height = height;
    }

    public String getUrl() {
        return url;
    }

    public Integer getLength() {
        return length;
    }

    public String getContentType() {
        return contentType;
    }

    public Integer getWidth() {
        return width;
    }

    public Integer getHeight() {
        return height;
    }

    protected ImageInfo(Parcel in) {
        this.url = in.readString();
        this.length = in.readInt();
        this.contentType = in.readString();
        this.width = in.readInt();
        this.height = in.readInt();
    }


    public static final Creator<ImageInfo> CREATOR = new Creator<ImageInfo>() {
        @Override
        public ImageInfo createFromParcel(Parcel in) {
            return new ImageInfo(in);
        }

        @Override
        public ImageInfo[] newArray(int size) {
            return new ImageInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeInt(length);
        dest.writeString(contentType);
        dest.writeInt(width);
        dest.writeInt(height);
    }
}
