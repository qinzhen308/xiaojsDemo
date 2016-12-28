package cn.xiaojs.xma.model.social;

import cn.xiaojs.xma.model.CollectionPage;

/**
 * Created by maxiaobao on 2016/12/28.
 */

public class DynamicDetail extends Dynamic {
    public CollectionPage<Comment> comments;
    public CollectionPage<LikedRecord> likedRecords;
}
