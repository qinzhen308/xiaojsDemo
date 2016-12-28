package cn.xiaojs.xma.ui.home;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:zhanghui
 * Date:2016/12/28
 * Desc:
 *
 * ======================================================================================== */

import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.model.social.Comment;

public class HomeBusiness {
    //递归拆分评论列表的数据
    private static void resolveComments(List<Comment> comments,Comment target,List<Comment> result){
        for (Comment comment : comments){
            if (comment.replies != null && comment.replies.size() > 0){
                resolveComments(comment.replies,comment,result);
                result.add(comment);
            }else {
                comment.target = target;
                result.add(comment);
            }
        }
    }

    /**
     * 对服务器返回的评论数据做本地拆分
     * @param comments
     * @return
     */
    public static List<Comment> resolveComments(List<Comment> comments){
        List<Comment> commentList = new ArrayList<>();
        resolveComments(comments,null,commentList);
        return commentList;
    }
}
