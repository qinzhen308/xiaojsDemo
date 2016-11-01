package com.benyuan.xiaojs.common.crop;

import com.benyuan.xiaojs.util.FileUtil;

/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:Administrator
 * Date:2016/11/1
 * Desc:
 *
 * ======================================================================================== */
public class CropImagePath {
	public static final String CROP_IMAGE_WIDTH = "width";
	public static final String CROP_IMAGE_HEIGHT = "height";
	public static final String CROP_NEVER = "never_crop";
	public static final String UPLOAD_COMPRESS = "upload_compress"; //上传的图片是否需要压缩

	public static final int CHOOSE_IMAGE = 1; // 选择图片
	public static final int TAKE_PHOTO = 2; // 拍照
	public static final int CROP_IMAGE_REQUEST_CODE = 3; // 裁剪
	public static final int START_CROP_IMAGE = 4; // 开始裁剪
	
	public static final int SELECT_PIC_REQUEST = 2000;	// 剪切图片的requestcode
	public static final String UPLOAD_IMAGE_PATH = FileUtil.SDCARD_PATH
			+ "/xjs/image_upload"; // 选择图片地址

	public static final String CROP_IMAGE_PATH_TAG = "cropImagePath"; // 返回图片intent
																		// key

	public static final String CROP_IMAGE_PATH = "/xjs/crop_image"; // 剪切后的图片地址
}
