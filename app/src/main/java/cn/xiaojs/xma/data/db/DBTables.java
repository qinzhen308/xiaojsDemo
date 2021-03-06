package cn.xiaojs.xma.data.db;

import android.provider.BaseColumns;

/**
 * Created by maxiaobao on 2016/12/23.
 */

public class DBTables {

    /**
     * Download table
     * ----------------------------------------------------------------
     * Update log:
     *
     * version 1
     * Init base columns:
     *
     * version 2 :
     * nothing
     *
     * version 3 :
     * add new column: hidden, owner
     *
     * * version 4 :
     * add new column: typename
     *
     */
    public static final class TDownload implements BaseColumns {
        //Table name
        public static final String TABLE_NAME = "downloads";

        public static final String KEY = "key";
        public static final String URL = "url";
        public static final String TITLE = "title";
        public static final String FILE_NAME = "filename";
        public static final String ICON = "icon";
        public static final String STATUS = "status";
        public static final String MIME_TYPE = "mimetype";
        public static final String NUM_FAILED = "numfailed";
        public static final String LAST_MOD = "lastmod";
        public static final String TOTAL_BYTES = "totalbytes";
        public static final String CURRENT_BYTES = "currentbytes";
        public static final String LOCAL = "local";
        public static final String DELETED = "deleted";
        public static final String CONTROL = "control";
        public static final String DESCRIPTION = "description";
        public static final String ETAG = "etag";
        public static final String REFERER = "referer";
        public static final String COOKIES = "cookies";
        public static final String USER_AGENT = "userAgent";
        public static final String ALLOWED_NETWORK_TYPES = "nettype";
        public static final String ALLOWROADMING = "roaming";
        public static final String ALLOW_METERED = "metered";
        public static final String FLAGS = "flags";
        public static final String ERROR_MSG = "error";
        public static final String HIDDEN = "hidden";
        public static final String OWNER = "owner";
        public static final String TYPE_NAME = "typename";

    }


    /**
     * Contact table
     * ----------------------------------------------------------------
     * Update log:
     *
     * version 1
     * Init base columns: cid, gid, name, avator, followType
     *
     * version 2 :1.0.2.26
     * new column: chatid
     *
     * version 3 :
     * nothing
     *
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
        //the file key
        public static final String KEY = "ukey";
        public static final String SUBTYPE = "subtype";
        public static final String SUBJECT = "subject";
        public static final String STATE = "state";
        public static final String STARTED = "startted";

        public static final String UNREAD = "unread";
        public static final String LAST_MSG = "lastMessage";

        public static final String GNAME = "gname";
        public static final String COVER = "cover";

        public static final String CHAT_ID = "chatid";

        //public static final String UN_FOLLOWABLE = "unfollowable";

    }

    /**
     * Contact group table
     * ----------------------------------------------------------------
     * Update log:
     *
     * version 1
     * Init base columns: id,name
     *
     *
     * version 2 :
     * nothing
     *
     *
     * version 3 :
     * nothing
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
