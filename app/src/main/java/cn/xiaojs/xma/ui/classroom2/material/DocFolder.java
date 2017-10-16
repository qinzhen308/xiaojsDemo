package cn.xiaojs.xma.ui.classroom2.material;

import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.model.material.LibDoc;

/**
 * Created by maxiaobao on 2017/10/11.
 */

public class DocFolder {
    public String directory;
    public List<LibDoc> data;
    public int page;
    public int filterIndex;
    public int sortIndex;
    public String searchKey;
    public boolean searching;

    public String title;
    public String tips;


    public DocFolder(String directory, List<LibDoc> data, int page,
                     int filterIndex, int sortIndex, String searchKey, boolean searching) {
        this.directory = directory;
        this.data = new ArrayList<>(data);
        this.page = page;
        this.filterIndex = filterIndex;
        this.sortIndex = sortIndex;
        this.searchKey = searchKey;
        this.searching = searching;
    }
}
