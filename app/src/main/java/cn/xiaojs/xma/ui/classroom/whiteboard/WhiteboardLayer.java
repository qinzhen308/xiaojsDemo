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
 * Date:2016/12/21
 * Desc:
 *
 * ======================================================================================== */

import cn.xiaojs.xma.ui.classroom.socketio.ProtocolConfigs;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.Doodle;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WhiteboardLayer {
    public static final float DOODLE_CANVAS_RATIO = ProtocolConfigs.VIRTUAL_WIDTH / (float)ProtocolConfigs.VIRTUAL_HEIGHT; // w:h
    private String mWhiteboardId;
    private String mWhiteboardName;
    /**
     * 是否是直播白板
     */
    private boolean isLive;

    private ArrayList<Doodle> mAllDoodles;
    private ArrayList<Doodle> mReDoStack;

    private List<Integer> mUndoRecordIds;
    private List<Integer> mRedoRecordIds;

    private int mRecordGroupId;

    /**
     * 课件的图片地址
     */
    private String mCoursePath;

    /**
     * 是否能接受服务器发来的白板绘制命令
     */
    private boolean isCanReceive;
    /**
     * 是否可以向服务器发送白板绘制命令
     */
    private boolean isCanSend;
    /**
     * layer的宽
     */
    private int width;
    /**
     * layer的高
     */
    private int height;

    public WhiteboardLayer() {
        mWhiteboardId = UUID.randomUUID().toString();
        init();
    }

    public WhiteboardLayer(String id) {
        mWhiteboardId = id;
        init();
    }

    private void init() {
        mAllDoodles = new ArrayList<Doodle>();
        mReDoStack = new ArrayList<Doodle>();
        mUndoRecordIds = new ArrayList<Integer>();
        mRedoRecordIds = new ArrayList<Integer>();
    }

    public ArrayList<Doodle> getAllDoodles() {
        return mAllDoodles;
    }

    public ArrayList<Doodle> getReDoStack() {
        return mReDoStack;
    }

    public String getWhiteboardId (){
        return mWhiteboardId;
    }

    public String getWhiteboardName() {
        return mWhiteboardName;
    }

    public void setWhiteboardName(String name) {
        mWhiteboardName = name;
    }

    public boolean isLive() {
        return isLive;
    }

    public void setIsLive(boolean live) {
        isLive = live;
    }

    public boolean isCanSend() {
        return isCanSend;
    }

    public void setCanSend(boolean canSend) {
        isCanSend = canSend;
    }

    public boolean isCanReceive() {
        return isCanReceive;
    }

    public void setCanReceive(boolean canReceive) {
        isCanReceive = canReceive;
    }

    public List<Integer> getRedoRecordIds() {
        return mRedoRecordIds;
    }

    public List<Integer> getUndoRecordIds() {
        return mUndoRecordIds;
    }

    public int getRecordGroupId() {
        return mRecordGroupId;
    }

    public void setRecordGroupId(int groupId) {
        mRecordGroupId = groupId;
    }

    public void incrementGroupId() {
        mRecordGroupId++;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getCoursePath() {
        return mCoursePath;
    }

    public void setCoursePath(String path) {
        mCoursePath = path;
    }
}
