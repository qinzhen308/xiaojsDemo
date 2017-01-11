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
 * Date:2017/1/8
 * Desc:
 *
 * ======================================================================================== */

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.xiaojs.xma.ui.classroom.whiteboard.Whiteboard;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.GeometryShape;

public class Parser {

    public synchronized static List<CommendLine> unpacking(Object... args) {
        List<CommendLine> commends = null;
        if (args != null && args.length > 0) {
            commends = new ArrayList<CommendLine>();
            for (Object obj : args) {
                if (obj != null) {
                    String commendStr = obj.toString();
                    String[] commendLineArr = commendStr.split(" ");
                    if (commendLineArr != null && commendLineArr.length > 0) {
                        CommendLine commendLine = new CommendLine();
                        commendLine.src = commendStr;
                        commendLine.whiteboardCommends = new ArrayList<Commend>();

                        for (int i = 0; i < commendLineArr.length; i++) {
                            String result = commendLineArr[i];
                            if (TextUtils.isEmpty(result)) {
                                continue;
                            }

                            if (i == commendLineArr.length - 1) {
                                //get time
                                String[] tArr = result.split("#");
                                if (tArr != null) {
                                    try {
                                        if (tArr.length == 1) {
                                            commendLine.time = Long.parseLong(tArr[0]);
                                        } else if (tArr.length > 1) {
                                            commendLine.time = Long.parseLong(tArr[1]);
                                        }
                                    } catch (Exception e) {

                                    }
                                }
                            } else {
                                Commend cmd = new Commend();
                                cmd.src = result;

                                //非标准格式  冒号: 竖线| 可能不存在
                                //$N:jb8g|rect,20,30,30,98,测试;
                                //$D:fae4;
                                //$M|20,30;
                                //$m:jb8g|-20,-30;
                                //String pattern = "^\\$(\\w{1})((\\:(.{4}))*)((\\|([[-]\\S|\\,]*))*)(.*)";
                                //1 = cm, 4 = id, 7 = params

                                //标准格式  冒号: 竖线|同时存在
                                //$m:jb8g|-20,-30;
                                //$D:jb8g;
                                //$P:|2,FF00FF;
                                //$T:|
                                String pattern = "^\\$(\\w{1})\\:(.{4})*\\|([[-]\\S|\\,]*)(.*)";
                                //1 = cm, 2 = id, 3 = params
                                Pattern r = Pattern.compile(pattern);
                                Matcher m = r.matcher(result);
                                if (m.find()) {
                                    int count = m.groupCount();
                                    for (int j = 1; j < count; j++) {
                                        String s = m.group(j);
                                        switch (j) {
                                            case 1:
                                                cmd.cm = s;
                                                break;
                                            case 2:
                                                cmd.id = s;
                                                break;
                                            case 3:
                                                cmd.params = s;
                                                break;
                                        }
                                    }
                                }

                                commendLine.whiteboardCommends.add(cmd);
                            }
                        }

                        commends.add(commendLine);
                    }
                }

            }
        }

        //test: print
        /*if (commends != null) {
            for (CommendLine d : commends) {
                Log.i("aaa", "cmd_line" + d.toString());
                if (d.whiteboardCommends != null) {
                    for (Commend c : d.whiteboardCommends) {
                        Log.i("aaa", "cmd=" + c.toString());
                    }
                }
            }
        }*/
        return commends;
    }


    public static int getShapeId(String[] params) {
        if (params == null || params.length == 0) {
            return -1;
        }

        String p = params[0];

        if (ProtocolConfigs.SHAPE_RECT.equals(p)) {
            return GeometryShape.RECTANGLE;
        } else if (ProtocolConfigs.SHAPE_BEELINE.equals(p)) {
            return GeometryShape.BEELINE;
        } else if (ProtocolConfigs.SHAPE_OVAL.equals(p)) {
            return GeometryShape.OVAL;
        } else if (ProtocolConfigs.SHAPE_TRIANGLE.equals(p)) {
            return GeometryShape.TRIANGLE;
        }

        return -1;
    }

    public static int getDoodleMode(String[] params) {
        if (params == null || params.length == 0) {
            return Whiteboard.MODE_NONE;
        }

        String p = params[0];

        if (ProtocolConfigs.SHAPE_RECT.equals(p)) {
            return Whiteboard.MODE_GEOMETRY;
        } else if (ProtocolConfigs.SHAPE_BEELINE.equals(p)) {
            return Whiteboard.MODE_GEOMETRY;
        } else if (ProtocolConfigs.SHAPE_OVAL.equals(p)) {
            return Whiteboard.MODE_GEOMETRY;
        } else if (ProtocolConfigs.SHAPE_TRIANGLE.equals(p)) {
            return Whiteboard.MODE_GEOMETRY;
        }

        return Whiteboard.MODE_NONE;
    }

}
