package cn.xiaojs.xma.ui.classroom;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:huangyong
 * Date:2016/12/28
 * Desc:
 *
 * ======================================================================================== */

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardLayer;

public class WhiteboardCollection implements Parcelable{
    public final static int DEFAULT = 1;
    public final static int COURSE_WARE = 2;

    /**
     * 是否是直播白板
     */
    private boolean isLive;
    private int style;
    private String id;
    private String name;
    private String url;
    private String coverUrl;
    private List<WhiteboardLayer> whiteboardLayer;

    public WhiteboardCollection() {
        whiteboardLayer = new ArrayList<WhiteboardLayer>();
        style = DEFAULT;
    }

    public WhiteboardCollection(int style) {
        whiteboardLayer = new ArrayList<WhiteboardLayer>();
        this.style = style;
    }

    public WhiteboardCollection(String name, int style) {
        whiteboardLayer = new ArrayList<WhiteboardLayer>();
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public void setWhiteboardLayer(List<WhiteboardLayer> layers) {
        whiteboardLayer = layers;
        if (whiteboardLayer != null) {
            for (WhiteboardLayer layer : whiteboardLayer) {
                layer.setIsLive(isLive);
            }
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public List<WhiteboardLayer> getWhiteboardLayer() {
        return whiteboardLayer;
    }

    public void addWhiteboardLayer(WhiteboardLayer layer) {
        if (layer != null) {
            layer.setIsLive(isLive);
        }
        whiteboardLayer.add(layer);
    }

    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean live) {
        isLive = live;
        if (whiteboardLayer != null) {
            for (WhiteboardLayer layer : whiteboardLayer) {
                layer.setIsLive(live);
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(url);
        dest.writeString(coverUrl);
    }

    private WhiteboardCollection(Parcel in) {
        readFromParcel(in);
    }

    public static final Parcelable.Creator<WhiteboardCollection> CREATOR = new
            Parcelable.Creator<WhiteboardCollection>() {
                public WhiteboardCollection createFromParcel(Parcel in) {
                    return new WhiteboardCollection(in);
                }

                public WhiteboardCollection[] newArray(int size) {
                    return new WhiteboardCollection[size];
                }
            };

    public void readFromParcel(Parcel in) {
        id = in.readString();
        name = in.readString();
        url = in.readString();
        coverUrl = in.readString();
    }

    public boolean isDefaultWhiteboard() {
        return style == DEFAULT;
    }
}
