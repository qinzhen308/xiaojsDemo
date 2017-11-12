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

    public long time;
    public long stime;
    public TalkPerson from;
    public TalkContent body;
    public String to;

    public boolean showTime;

    public String tips; //仅用于系统消息；

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TalkPerson {

        public String name;
        public String accountId;
        public String avatar;

    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TalkContent{
        public String text;
        public int contentType;
        public QiNiuImg drawing;
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class QiNiuImg{
        public String name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != getClass()) {
            return false;
        }

        long mill = ((TalkItem)obj).time;
        long thisMill = time;

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

