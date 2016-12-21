package cn.xiaojs.xma.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by maxiaobao on 2016/11/8.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Pagination {

    private int maxNumOfObjectsPerPage;
    private int page;

    public int getMaxNumOfObjectsPerPage() {
        return maxNumOfObjectsPerPage;
    }

    public void setMaxNumOfObjectsPerPage(int maxNumOfObjectsPerPage) {
        this.maxNumOfObjectsPerPage = maxNumOfObjectsPerPage;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
