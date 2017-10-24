package cn.xiaojs.xma.model.live;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;

import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.model.material.LibDoc;
import cn.xiaojs.xma.model.social.Dimension;

/**
 * Created by maxiaobao on 2017/2/6.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Board {
    public String title;
    public long sort = System.currentTimeMillis();
    public int type = Live.BoardType.WHITE;
    public DrawDimension drawing;
    public ArrayList<LibDoc.ExportImg> pages;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DrawDimension extends Dimension {
        public String snapshot;
    }

}
