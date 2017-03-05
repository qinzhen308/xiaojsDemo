package cn.xiaojs.xma.model.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import cn.xiaojs.xma.model.AliasTags;


/**
 * Created by maxiaobao on 2017/2/28.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompetencySubject {

    public int claimed;
    public Competen competency;
    public AliasTags aliasAndTags;
}
