package cn.xiaojs.xma.ui.classroom.whiteboard;
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

public class WhiteboardCollection implements Parcelable{
    public final static int DEFAULT = 1;
    public final static int COURSE_WARE = 2;

    /**
     * 是否是直播白板, 学生端只有能有一块正在直播的白板集；
     * 而老是端则可以切换不同的直播白板集，也就是设置主屏
     */
    private boolean isLive;
    /**
     * 默认版本，课件白板
     */
    private int style;
    private String id;
    private String title;
    private String url;
    private String coverUrl;
    private List<WhiteboardLayer> whiteboardLayer;
    private int currIndex = 0;

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
        this.style = style;
        this.title = title;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getTitle() {
        return title;
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

    public int getCurrIndex() {
        return currIndex;
    }

    public void setCurrIndex(int index) {
        currIndex = index;
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
        dest.writeString(title);
        dest.writeString(url);
        dest.writeString(coverUrl);
        dest.writeInt(currIndex);
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
        title = in.readString();
        url = in.readString();
        coverUrl = in.readString();
        currIndex = in.readInt();
    }

    public boolean isDefaultWhiteboard() {
        return style == DEFAULT;
    }
}
