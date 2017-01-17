package cn.xiaojs.xma.ui.classroom.socketio;
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
 * Date:2017/1/10
 * Desc:
 *
 * ======================================================================================== */

import android.graphics.PointF;
import android.text.TextUtils;

import java.text.DecimalFormat;
import java.util.List;

import cn.xiaojs.xma.ui.classroom.whiteboard.core.Action;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.Doodle;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.GeometryShape;
import cn.xiaojs.xma.ui.classroom.whiteboard.shape.TextWriting;

public class Packer {

    /**
     * 封装新建形状（手写，几何图形，文字）的命令
     */
    public static String getBuildDoodleCmd(Doodle doodle, boolean whitTime) {
        if (doodle == null) {
            return null;
        }

        return getBuildDoodleCmd(doodle, doodle.getDoodleId(), whitTime);
    }

    /**
     * 封装新建形状（手写，几何图形，文字）的命令
     */
    public static String getBuildDoodleCmd(Doodle doodle, String doodleId, boolean whitTime) {
        if (doodle == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("$");
        sb.append(ProtocolConfigs.NEW);
        sb.append(":");
        sb.append(doodleId);
        sb.append("|");
        List<PointF> points = doodle.getPoints();

        int style = doodle.getStyle();
        String shape = "";
        switch (style) {
            case Doodle.STYLE_HAND_WRITING:
                shape = ProtocolConfigs.SHAPE_HAND_WRITING;
                break;
            case Doodle.STYLE_GEOMETRY:
                shape = transformToProtocolShape(((GeometryShape) doodle).getGeometryId());
                break;
            case Doodle.STYLE_TEXT:
                shape = ProtocolConfigs.SHAPE_TEXT_WRITING;
                break;
        }
        if (TextUtils.isEmpty(shape)) {
            return null;
        }

        sb.append(shape);
        if (doodle instanceof TextWriting) {
            int x = (int) (points.get(0).x * ProtocolConfigs.VIRTUAL_WIDTH);
            int y = (int) (points.get(0).y * ProtocolConfigs.VIRTUAL_HEIGHT);
            sb.append(",");
            sb.append(String.valueOf(x));
            sb.append(",");
            sb.append(String.valueOf(y));
            sb.append(",");
            String text = ((TextWriting)doodle).getTextString();
            text = java.net.URLDecoder.decode(text);
            sb.append(text);
        } else {
            for (int i = 0; i < points.size(); i++) {
                int w = (int) (points.get(i).x * ProtocolConfigs.VIRTUAL_WIDTH);
                int h = (int) (points.get(i).y * ProtocolConfigs.VIRTUAL_HEIGHT);
                sb.append(",");
                sb.append(String.valueOf(w));
                sb.append(",");
                sb.append(String.valueOf(h));
            }
        }

        if (whitTime) {
            sb.append(" #" + System.currentTimeMillis());
        }
        return sb.toString();
    }

    /**
     * 封装改变画笔样式命令
     */
    public static String getChangePaintCmd(float paintWidth, int paintColor, boolean whitTime) {
        String s = "$:" + ProtocolConfigs.PAINT + "|" + paintWidth + "," + Integer.toHexString(paintColor);
        if (whitTime) {
            return s + " #" + System.currentTimeMillis();
        }

        return s;
    }

    /**
     * 封装删除命令
     */
    public static String getDeleteCmd(Doodle doodle, boolean whitTime) {
        if (doodle == null) {
            return null;
        }

        String s = "$" + ProtocolConfigs.DEL + ":" + doodle.getDoodleId() + "|";

        if (whitTime) {
            return s + " #" + System.currentTimeMillis();
        }

        return s;
    }

    /**
     * 封装删除命令
     */
    public static String getClearCmd() {
        return  "$" + ProtocolConfigs.CLEAR + ":| #" + System.currentTimeMillis();
    }

    /**
     * 封装改变颜色命令
     */
    public static String getChangeColorCmd(Doodle doodle, boolean whitTime) {
        if (doodle == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("$");
        sb.append(ProtocolConfigs.CHANGE_COLOR);
        sb.append(":");
        sb.append(doodle.getDoodleId());
        sb.append("|");
        sb.append(Integer.toHexString(doodle.getPaint().getColor()));

        if (whitTime) {
            sb.append(" #" + System.currentTimeMillis());
        }

        return sb.toString();
    }

    /**
     * 封装移动命令(相对移动)
     */
    public static String getMoveCmd(Doodle doodle, float deltaX, float deltaY, int blackboardW, int blackboardH, boolean whitTime) {
        if (doodle == null) {
            return null;
        }

        deltaX = (deltaX * ProtocolConfigs.VIRTUAL_WIDTH) / blackboardW;
        deltaY = (deltaY * ProtocolConfigs.VIRTUAL_HEIGHT) / blackboardH;

        StringBuilder sb = new StringBuilder();
        sb.append("$");
        sb.append(ProtocolConfigs.MOVE_R);
        sb.append(":");
        sb.append(doodle.getDoodleId());
        sb.append("|");
        sb.append(String.valueOf((int) deltaX));
        sb.append(",");
        sb.append(String.valueOf((int) deltaY));

        if (whitTime) {
            sb.append(" #" + System.currentTimeMillis());
        }

        return sb.toString();
    }

    /**
     * 封装缩放，旋转命令(相对移动)
     */
    public static String getScaleCmd(Doodle doodle, float scale, boolean whitTime) {
        if (doodle == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("$");
        sb.append(ProtocolConfigs.ZOOM_R);
        sb.append(":");
        sb.append(doodle.getDoodleId());
        sb.append("|");
        String s = new DecimalFormat("#.###").format(scale);
        sb.append(s);

        if (whitTime) {
            sb.append(" #" + System.currentTimeMillis());
        }

        return sb.toString();
    }


    /**
     * 封装缩放，旋转命令(相对移动)
     */
    public static String getRotateCmd(Doodle doodle, float degree, boolean whitTime) {
        if (doodle == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("$");
        sb.append(ProtocolConfigs.ROTATE_R);
        sb.append(":");
        sb.append(doodle.getDoodleId());
        sb.append("|");
        String d = new DecimalFormat("#.###").format(degree);
        sb.append(d);

        if (whitTime) {
            sb.append(" #" + System.currentTimeMillis());
        }

        return sb.toString();
    }

    /**
     * 封装缩放和旋转命令(相对移动)
     */
    public static String getScaleAndRotateCmd(Doodle doodle, float scale, float degree, boolean whitTime) {
        if (doodle == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        String scaleCmd = getScaleCmd(doodle, scale, false);
        String rotateCmd = getRotateCmd(doodle, degree, false);

        sb.append(scaleCmd);
        sb.append(" ");
        sb.append(rotateCmd);

        if (whitTime) {
            sb.append(" #" + System.currentTimeMillis());
        }

        return sb.toString();
    }

    /**
     * 封装缩放，旋转命令(相对移动)
     */
    public static String getScaleOrRotateCmd(Doodle doodle, int action, int blackboardW, int blackboardH,
                                            float oldX, float oldY, float x, float y) {
        if (doodle == null) {
            return null;
        }

        oldX = (int) ((oldX * ProtocolConfigs.VIRTUAL_WIDTH) / blackboardW);
        oldY = (int) ((oldY * ProtocolConfigs.VIRTUAL_HEIGHT) / blackboardH);
        x = (int) ((x * ProtocolConfigs.VIRTUAL_WIDTH) / blackboardW);
        y = (int) ((y * ProtocolConfigs.VIRTUAL_HEIGHT) / blackboardH);

        StringBuilder sb = new StringBuilder();
        sb.append("$");
        sb.append(action == Action.SCALE_ACTION ? ProtocolConfigs.ZOOM_R : ProtocolConfigs.ROTATE_R);
        sb.append(":");
        sb.append(doodle.getDoodleId());
        sb.append("|");
        sb.append(String.valueOf(oldX));
        sb.append(String.valueOf(oldY));
        sb.append(String.valueOf(x));
        sb.append(String.valueOf(y));

        sb.append(" #" + System.currentTimeMillis());

        return sb.toString();
    }

    public static String transformToProtocolShape(int shapeId) {
        String shape = "";
        switch (shapeId) {
            case GeometryShape.RECTANGLE:
                shape = ProtocolConfigs.SHAPE_RECT;
                break;
            case GeometryShape.BEELINE:
                shape = ProtocolConfigs.SHAPE_BEELINE;
                break;
            case GeometryShape.OVAL:
                shape = ProtocolConfigs.SHAPE_OVAL;
                break;
            case GeometryShape.TRIANGLE:
                shape = ProtocolConfigs.SHAPE_TRIANGLE;
                break;
        }

        return shape;
    }
}
