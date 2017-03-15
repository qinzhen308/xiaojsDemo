package cn.xiaojs.xma.model.live;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

/**
 * Created by maxiaobao on 2017/1/17.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TalkItem {

    public Date time;
    public TalkPerson from;
    public TalkContent body;


    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TalkPerson {

        public String name;
        public String avatar;
        public String accountId;

    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TalkContent{
        public String text;
        public int contentType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != getClass()) {
            return false;
        }

        long mill = ((TalkItem)obj).time != null ? ((TalkItem)obj).time.getTime() : 0;
        long thisMill = time != null ? time.getTime() : 0;

        if (mill != thisMill) {
            return false;
        }

        String fromAcc = ((TalkItem)obj).from != null ? ((TalkItem)obj).from.accountId : "";
        String thisFromAcc = from != null ? from.accountId : "";

        if (fromAcc != null && !fromAcc.equals(thisFromAcc)) {
            return false;
        }

        String txt = ((TalkItem)obj).body != null ? ((TalkItem)obj).body.text : "";
        String thisTxt = body != null ? body.text : "";

        return mill == thisMill
                && (fromAcc != null && fromAcc.equals(thisFromAcc))
                && (txt != null && txt.equals(thisTxt));
    }
}

