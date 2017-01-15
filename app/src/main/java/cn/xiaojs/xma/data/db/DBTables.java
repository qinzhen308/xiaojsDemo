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
     * Init base columns: cid, gid, name, avator, followType
     */
    public static final class TContact implements BaseColumns {
        //Table name
        public static final String TABLE_NAME = "contact";

        // Table columns

        //contact id
        public static final String CID = "cid";
        //group id which contact belong to
        public static final String GID = "gid";
        //contact name
        public static final String NAME = "name";
        //contact avator's url
        public static final String AVATOR = "avator";
        //contact follow type
        public static final String FOLLOW_TYPE = "followType";

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
