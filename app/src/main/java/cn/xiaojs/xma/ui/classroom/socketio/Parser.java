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
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

                                //$N:jb8g|rect,20,30,30,98,测试";
                                //$D:fae4";
                                //$M|20,30";
                                //$m:jb8g|-20,-30";
                                String pattern = "^\\$(\\w{1})((\\:(.{4}))*)((\\|([[-]\\S|\\,]*))*)(.*)";
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
                                            case 4:
                                                cmd.id = s;
                                                break;
                                            case 7:
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
        if (commends != null) {
            for (CommendLine d : commends) {
                Log.i("aaa", "cmd_line" + d.toString());
                if (d.whiteboardCommends != null) {
                    for (Commend c : d.whiteboardCommends) {
                        Log.i("aaa", "cmd=" + c.toString());
                    }
                }
            }
        }
        return commends;
    }

}
