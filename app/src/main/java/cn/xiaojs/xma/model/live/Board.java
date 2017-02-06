package cn.xiaojs.xma.model.live;

import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.model.social.Dimension;

/**
 * Created by maxiaobao on 2017/2/6.
 */

public class Board {
    public String title;
    public long sort = System.currentTimeMillis();
    public int type = Live.BoardType.WHITE;
    public Dimension drawing;
    public SlidePage[] pages;

}
