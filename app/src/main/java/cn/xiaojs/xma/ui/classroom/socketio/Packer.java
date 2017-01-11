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
import android.graphics.RectF;

import java.util.List;

import cn.xiaojs.xma.ui.classroom.whiteboard.core.Action;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.Doodle;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.GeometryShape;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.Utils;
import cn.xiaojs.xma.ui.classroom.whiteboard.shape.TextWriting;

public class Packer {

    /**
     * 封装新建形状（手写，几何图形，文字）的命令
     */
    public String getBuildDoodleCmd(Doodle doodle, int blackboardW, int blackboardH, RectF doodleBounds) {
        if (doodle == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("$");
        sb.append(ProtocolConfigs.NEW);
        sb.append(":");
        sb.append(doodle.getDoodleId());
        sb.append("|");
        List<PointF> points = doodle.getPoints();

        int style = doodle.getStyle();
        String shape = "";
        switch (style) {
            case Doodle.STYLE_HAND_WRITING:
                break;
            case Doodle.STYLE_GEOMETRY:
                shape = transformToProtocolShape(((GeometryShape) doodle).getGeometryId());
                break;
            case Doodle.STYLE_TEXT:
                break;
        }
        sb.append(shape);
        if (doodle instanceof TextWriting) {
            PointF p = Utils.reNormalizeScreenPoint(points.get(0).x, points.get(0).y, doodleBounds);
            int w = (int) ((p.x * ProtocolConfigs.VIRTUAL_WIDTH) / blackboardW);
            int h = (int) ((p.y * ProtocolConfigs.VIRTUAL_HEIGHT) / blackboardH);
            sb.append(",");
            sb.append(String.valueOf(w));
            sb.append(",");
            sb.append(String.valueOf(h));
            sb.append(",");
            String text = ((TextWriting)doodle).getTextString();
            sb.append(text);
        } else {
            for (int i = 0; i < points.size(); i++) {
                PointF p = Utils.reNormalizeScreenPoint(points.get(i).x, points.get(i).y, doodleBounds);
                int w = (int) ((p.x * ProtocolConfigs.VIRTUAL_WIDTH) / blackboardW);
                int h = (int) ((p.y * ProtocolConfigs.VIRTUAL_HEIGHT) / blackboardH);
                sb.append(",");
                sb.append(String.valueOf(w));
                sb.append(",");
                sb.append(String.valueOf(h));
            }
        }

        return sb.toString();
    }

    /**
     * 封装改变画笔样式命令
     */
    public static String getChangePaintCmd(float paintWidth, int paintColor) {
        return "$:" + ProtocolConfigs.PAINT + "|" + paintWidth + "," + Integer.toHexString(paintColor);
    }

    /**
     * 封装删除命令
     */
    public static String getDeleteCmd(Doodle doodle) {
        if (doodle == null) {
            return null;
        }

        return "$" + ProtocolConfigs.DEL + ":" + doodle.getDoodleId() + "|";
    }

    /**
     * 封装改变颜色命令
     */
    public static String getChangeColorCmd(Doodle doodle) {
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

        return sb.toString();
    }

    /**
     * 封装移动命令(相对移动)
     */
    public static String getMoveCmd(Doodle doodle, int blackboardW, int blackboardH, float deltaX, float deltaY) {
        String cmd = null;
        if (doodle == null) {
            return null;
        }

        deltaX = (int) ((deltaX * ProtocolConfigs.VIRTUAL_WIDTH) / blackboardW);
        deltaY = (int) ((deltaY * ProtocolConfigs.VIRTUAL_HEIGHT) / blackboardH);

        StringBuilder sb = new StringBuilder();
        sb.append("$");
        sb.append(ProtocolConfigs.MOVE_R);
        sb.append(":");
        sb.append(doodle.getDoodleId());
        sb.append("|");
        sb.append(String.valueOf((int) deltaX));
        sb.append(",");
        sb.append(String.valueOf((int) deltaY));

        return cmd;
    }

    /**
     * 封装缩放，旋转命令(相对移动)
     */
    public static String getScaleOrRotateCmd(Doodle doodle, int action, float delta) {
        String cmd = null;
        if (doodle == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("$");
        sb.append(action == Action.SCALE_ACTION ? ProtocolConfigs.ZOOM_R : ProtocolConfigs.ROTATE_R);
        sb.append(":");
        sb.append(doodle.getDoodleId());
        sb.append("|");
        sb.append(String.valueOf(delta));

        return cmd;
    }

    /**
     * 封装缩放，旋转命令(相对移动)
     */
    public static String getScaleOrRotateCmd(Doodle doodle, int action, int blackboardW, int blackboardH,
                                            float oldX, float oldY, float x, float y) {
        String cmd = null;
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

        return cmd;
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
