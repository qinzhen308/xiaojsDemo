package cn.xiaojs.xma.model.ctl;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by maxiaobao on 2017/6/16.
 */

public class ScheduleOptions {

    private Map<String, String> options;

    private ScheduleOptions(Builder builder) {
        this.options = builder.optionsMap;
    }

    public Map<String, String> getOptions() {
        return options;
    }



    /**
     * The create scheduleOptions builder
     */
    public static class Builder {

        private Map<String, String> optionsMap;

        public Builder() {
            optionsMap = new HashMap<>();
        }


        public ScheduleOptions build() {
            return new ScheduleOptions(this);
        }


        public Builder setCycle(String cycle) {

            if(TextUtils.isEmpty(cycle)) {
                return this;
            }

            optionsMap.put("cycle", cycle);
            return this;
        }

        public Builder setNext(String next) {

            if(TextUtils.isEmpty(next)) {
                return this;
            }

            optionsMap.put("next", next);
            return this;
        }

        public Builder setPre(String pre) {

            if(TextUtils.isEmpty(pre)) {
                return this;
            }

            optionsMap.put("pre", pre);
            return this;
        }

        public Builder setStart(String start) {

            if(TextUtils.isEmpty(start)) {
                return this;
            }

            optionsMap.put("start", start);
            return this;
        }

        public Builder setEnd(String end) {

            if(TextUtils.isEmpty(end)) {
                return this;
            }

            optionsMap.put("end", end);
            return this;
        }

        public Builder setUnformat(String unformat) {

            if(TextUtils.isEmpty(unformat)) {
                return this;
            }

            optionsMap.put("unformat", unformat);
            return this;
        }

        public Builder setType(String type) {

            if(TextUtils.isEmpty(type)) {
                return this;
            }

            optionsMap.put("type", type);
            return this;
        }

        public Builder setState(String state) {

            if(TextUtils.isEmpty(state)) {
                return this;
            }

            optionsMap.put("state", state);
            return this;
        }

        public Builder setRole(String role) {

            if(TextUtils.isEmpty(role)) {
                return this;
            }

            optionsMap.put("role", role);
            return this;
        }

        public Builder setQ(String q) {

            if(TextUtils.isEmpty(q)) {
                return this;
            }

            optionsMap.put("q", q);
            return this;
        }

    }
}
