package cn.xiaojs.xma.model;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:huangyong
 * Date:2017/4/11
 * Desc: 资料库
 *
 * ======================================================================================== */

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class DocumentLibrary implements Serializable {
    private static final long serialVersionUID = 8971497648743261403L;

    public String id;
    public Integer used;
    public Long quota;
    public List<Document> documents;

    public static class Document {
        public String key;
        public String name;
        public String mimeType;
        public Date uploadedOn;
        public Long used;
        public String id;
        public String state;
        public Export exported;
        public Owner owner;
        public String typeName;
    }

    public static class Export {
        public String total;
        public List<Image> images;
    }

    public static class Image {
        public String name;
    }

    public static class Owner {
        public String id;
        public String name;
        public String type;
    }
}
