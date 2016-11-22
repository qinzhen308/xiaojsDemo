package com.benyuan.xiaojs.util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

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
 * Date:2016/11/22
 * Desc:
 *
 * ======================================================================================== */
public class ObjectSerializableUtil {
	private final static String TAG = "ObjectSerializable";

	// write single object
	public static <T> void writeObject(T obj, String cachePath) {
		if (cachePath == null) {
			return;
		}

		ObjectOutputStream os = null;
		try {
			File cache = new File(cachePath);
			if (!cache.exists()) {
				cache.getParentFile().mkdirs();
				cache.createNewFile();
			}
			FileOutputStream fs = new FileOutputStream(cachePath);
			os = new ObjectOutputStream(fs);
			os.writeObject(obj);
		} catch (IOException e) {
			Log.w(TAG, "write object:" + cachePath + " error:" + e.getMessage());
		} finally {
			if (os != null)
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	// read single object
	@SuppressWarnings("unchecked")
	public static <T> T readObject(String cachePath) {
		if (cachePath == null) {
			return null;
		}

		T obj = null;
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new FileInputStream(cachePath));
			obj = (T) in.readObject();
		} catch (IOException e) {
			Log.w(TAG, "read object:" + cachePath + " error:" + e.getMessage());
		} catch (ClassNotFoundException e) {
			Log.w(TAG, "read object:" + cachePath + " error:" + e.getMessage());
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return obj;
	}

	public static <T> void writeObjects(ArrayList<T> objList, String cachePath) {
		if (cachePath == null) {
			return;
		}

		ObjectOutputStream os = null;
		try {
			File cache = new File(cachePath);
			if (!cache.exists()) {
				cache.getParentFile().mkdirs();
				cache.createNewFile();
			}
			FileOutputStream fs = new FileOutputStream(cachePath);
			os = new ObjectOutputStream(fs);
			for (T obj : objList) {
				os.writeObject(obj);
			}
			// end: write null
			os.writeObject(null);
		} catch (IOException e) {
			Log.w(TAG,
					"write object list:" + cachePath + " error:"
							+ e.getMessage());
		} finally {
			if (os != null)
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> ArrayList<T> readObjects(String cachePath) {
		ArrayList<T> objList = new ArrayList<T>();
		if (cachePath == null) {
			return objList;
		}

		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new FileInputStream(cachePath));
			while (true) {
				Object obj = in.readObject();
				if (obj != null) {
					objList.add((T) obj);
				} else {
					break;
				}
			}
		} catch (IOException e) {
			Log.w(TAG,
					"read object list:" + cachePath + " error:"
							+ e.getMessage());
		} catch (ClassNotFoundException e) {
			Log.w(TAG,
					"read object list:" + cachePath + " error:"
							+ e.getMessage());
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return objList;
	}
}
