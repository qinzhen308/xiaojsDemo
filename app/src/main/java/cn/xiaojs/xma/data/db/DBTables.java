package cn.xiaojs.xma.data.db;

import android.provider.BaseColumns;

/**
 * Created by maxiaobao on 2016/12/23.
 */

public class DBTables {

    /**
     * Contact table
     * ----------------------------------------------------------------
     * Update log:
     *
     * version 1
     * Init base columns: name,avator
     */
    public static final class TContact implements BaseColumns {
        //Table name
        public static final String TABLE_NAME = "contact";

        // Table columns

        //contact name
        public static final String NAME = "name";
        //contact avator's url
        public static final String AVATOR = "avator";

    }

    /**
     * Contact group table
     * ----------------------------------------------------------------
     * Update log:
     *
     * version 1
     * Init base columns: id,name
     */
    public static final class TGroup implements BaseColumns {
        //Table name
        public static final String TABLE_NAME = "cgroup";

        // Table columns

        //group id
        public static final String GID = "gid";
        //group name
        public static final String NAME = "name";


    }
}
