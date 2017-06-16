package cn.xiaojs.xma.model.ctl;

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

    private ScheduleOptions() {
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
            optionsMap.put("cycle", cycle);
            return this;
        }

        public Builder setNext(String next) {
            optionsMap.put("next", next);
            return this;
        }

        public Builder setPre(String pre) {
            optionsMap.put("pre", pre);
            return this;
        }

        public Builder setStart(String start) {
            optionsMap.put("start", start);
            return this;
        }

        public Builder setEnd(String end) {
            optionsMap.put("end", end);
            return this;
        }

        public Builder setUnformat(String unformat) {
            optionsMap.put("unformat", unformat);
            return this;
        }

        public Builder setType(String type) {
            optionsMap.put("type", type);
            return this;
        }

        public Builder setState(String state) {
            optionsMap.put("state", state);
            return this;
        }

        public Builder setRole(String role) {
            optionsMap.put("role", role);
            return this;
        }

        public Builder setQ(String q) {
            optionsMap.put("q", q);
            return this;
        }

    }
}
