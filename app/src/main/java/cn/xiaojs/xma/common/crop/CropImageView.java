package cn.xiaojs.xma.common.crop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import cn.xiaojs.xma.util.BitmapUtils;
import cn.xiaojs.xma.util.XjsUtils;

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
 * Date:2016/11/1
 * Desc:
 *
 * ======================================================================================== */
public class CropImageView extends View {

	// 单点触摸的时候
	private float oldX = 0;
	private float oldY = 0;

	// 多点触摸的时候
	private float oldx_0 = 0;
	private float oldy_0 = 0;

	private float oldx_1 = 0;
	private float oldy_1 = 0;

	// 状态
	private final int STATUS_Touch_SINGLE = 1;// 单点
	private final int STATUS_TOUCH_MULTI_START = 2;// 多点开始
	private final int STATUS_TOUCH_MULTI_TOUCHING = 3;// 多点拖拽中

	private int mStatus = STATUS_Touch_SINGLE;

	// 默认的裁剪图片宽度与高度
	private final int defaultCropWidth = 300;
	private final int defaultCropHeight = 300;
	private int cropWidth = defaultCropWidth;
	private int cropHeight = defaultCropHeight;
	private boolean isNotCrop = false;

	protected float oriRationWH = 0;// 原始宽高比率
	protected final float maxZoomOut = 5.0f;// 最大扩大到多少倍
	protected final float minZoomIn = 0.333333f;// 最小缩小到多少倍

	protected Drawable mDrawable;// 原图
	protected FloatDrawable mFloatDrawable;// 浮层
	protected Rect mDrawableSrc = new Rect();
	protected Rect mDrawableDst = new Rect();
	protected Rect mDrawableFloat = new Rect();// 浮层选择框，就是头像选择框
	protected boolean isFrist = true;

	protected Context mContext;

	protected int mLastDegree = 0;
	protected int mRotateDegree = 0;

	private int w = 0;
	private int h = 0;

	private boolean mFlagChange = false; // 图片旋转90°或者270°的时候被true
	
	private int mScreenWidth;
	private int mScreenHeight;

	public CropImageView(Context context) {
		super(context);
		init(context);
	}

	public CropImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CropImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		this.mContext = context;
		mFloatDrawable = new FloatDrawable(context);// 头像选择框

		DisplayMetrics dm = getResources().getDisplayMetrics();
		mScreenWidth = dm.widthPixels;
		mScreenHeight = dm.heightPixels;
	}

	public void setDrawable(Drawable mDrawable, int cropWidth, int cropHeight,
							boolean notCrop) {
		this.mDrawable = mDrawable;
		if (cropWidth > 0) {
			this.cropWidth = cropWidth;
		}
		if (cropHeight > 0) {
			this.cropHeight = cropHeight;
		}
		isNotCrop = notCrop;
		this.isFrist = true;
		invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (event.getPointerCount() > 1) {
			if (mStatus == STATUS_Touch_SINGLE) {
				mStatus = STATUS_TOUCH_MULTI_START;

				oldx_0 = event.getX(0);
				oldy_0 = event.getY(0);

				oldx_1 = event.getX(1);
				oldy_1 = event.getY(1);
			} else if (mStatus == STATUS_TOUCH_MULTI_START) {
				mStatus = STATUS_TOUCH_MULTI_TOUCHING;
			}
		} else {
			if (mStatus == STATUS_TOUCH_MULTI_START
					|| mStatus == STATUS_TOUCH_MULTI_TOUCHING) {
				oldx_0 = 0;
				oldy_0 = 0;

				oldx_1 = 0;
				oldy_1 = 0;

				oldX = event.getX();
				oldY = event.getY();
			}

			mStatus = STATUS_Touch_SINGLE;
		}

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			oldX = event.getX();
			oldY = event.getY();
			break;

		case MotionEvent.ACTION_UP:
			checkBounds();
			break;

		case MotionEvent.ACTION_POINTER_1_DOWN:
			break;

		case MotionEvent.ACTION_POINTER_UP:
			break;

		case MotionEvent.ACTION_MOVE:
			if (mStatus == STATUS_TOUCH_MULTI_TOUCHING) {

				float newx_0 = event.getX(0);
				float newy_0 = event.getY(0);

				float newx_1 = event.getX(1);
				float newy_1 = event.getY(1);

				float oldWidth = Math.abs(oldx_1 - oldx_0);
				float oldHeight = Math.abs(oldy_1 - oldy_0);

				float newWidth = Math.abs(newx_1 - newx_0);
				float newHeight = Math.abs(newy_1 - newy_0);

				boolean isDependHeight = Math.abs(newHeight - oldHeight) > Math
						.abs(newWidth - oldWidth);

				float ration = isDependHeight ? (newHeight / oldHeight)
						: (newWidth / oldWidth);

				int centerX = mDrawableDst.centerX();
				int centerY = mDrawableDst.centerY();
				int _newWidth = 0;
				int _newHeight = 0;

				if (mFlagChange) {
					_newWidth = (int) (mDrawableDst.height() * ration);
					_newHeight = (int) (_newWidth / oriRationWH);
				} else {
					_newWidth = (int) (mDrawableDst.width() * ration);
					_newHeight = (int) (_newWidth / oriRationWH);
				}
				float tmpZoomRation = 0;
				tmpZoomRation = (float) _newWidth
						/ (float) mDrawableSrc.width();

				if (mFlagChange) {
					if (tmpZoomRation >= maxZoomOut) {
						_newWidth = (int) (maxZoomOut * mDrawableSrc.width());
						_newHeight = (int) (_newWidth / oriRationWH);
					} else if (tmpZoomRation <= minZoomIn) {
						_newWidth = (int) (minZoomIn * mDrawableSrc.width());
						_newHeight = (int) (_newWidth / oriRationWH);
					}
				} else {
					if (tmpZoomRation >= maxZoomOut) {
						_newWidth = (int) (maxZoomOut * mDrawableSrc.width());
						_newHeight = (int) (_newWidth / oriRationWH);
					} else if (tmpZoomRation <= minZoomIn) {
						_newWidth = (int) (minZoomIn * mDrawableSrc.width());
						_newHeight = (int) (_newWidth / oriRationWH);
					}
				}

				if (mFlagChange) {
					mDrawableDst.set(centerX - _newHeight / 2, centerY
							- _newWidth / 2, centerX + _newHeight / 2, centerY
							+ _newWidth / 2);
				} else {
					mDrawableDst.set(centerX - _newWidth / 2, centerY
							- _newHeight / 2, centerX + _newWidth / 2, centerY
							+ _newHeight / 2);
				}

				invalidate();

				oldx_0 = newx_0;
				oldy_0 = newy_0;

				oldx_1 = newx_1;
				oldy_1 = newy_1;
			} else if (mStatus == STATUS_Touch_SINGLE) {
				int dx = (int) (event.getX() - oldX);
				int dy = (int) (event.getY() - oldY);

				oldX = event.getX();
				oldY = event.getY();

				if (!(dx == 0 && dy == 0)) {
					mDrawableDst.offset(dx, dy);
					invalidate();
				}
			}
			break;
		}

		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mDrawable == null) {
			return; // couldn't resolve the URI
		}

		if (mDrawable.getIntrinsicWidth() == 0
				|| mDrawable.getIntrinsicHeight() == 0) {
			return; // nothing to draw (empty bounds)
		}
		int degree = mRotateDegree % 360;

		if (mLastDegree != degree) {
			mLastDegree = degree;
			try {
				mDrawable.setCallback(null);
				((BitmapDrawable)mDrawable).getBitmap().recycle();
				mDrawable = null;
				mDrawable = new BitmapDrawable(getResources(), BitmapUtils.createNewBitmapAndCompressByFile(
						CropImagePath.UPLOAD_IMAGE_PATH, new int[]{mScreenWidth, mScreenHeight}, degree));
				setDrawableDst();
				oldx_0 = 0;
				oldx_1 = 0;
				oldy_0 = 0;
				oldy_1 = 0;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		configureBounds();
		mDrawable.draw(canvas);
		canvas.save();
		if (!isNotCrop) {
			canvas.clipRect(mDrawableFloat, Region.Op.DIFFERENCE);
			canvas.drawColor(Color.parseColor("#a0000000"));
			canvas.restore();
			mFloatDrawable.draw(canvas);
		}
	}

	public void setRotate(float degree) {
		mRotateDegree += degree;
		float aDegree = mRotateDegree % 360;

		if (Math.abs(aDegree) == 90 || Math.abs(aDegree) == 270) {
			mFlagChange = true;
			mDrawableDst.set(mDrawableDst.centerX() - h / 2,
					mDrawableDst.centerY() - w / 2, mDrawableDst.centerX() + h
							/ 2, mDrawableDst.centerY() + w / 2);
			mDrawableSrc.set(mDrawableDst);
		} else {
			mFlagChange = false;
			mDrawableDst.set(mDrawableDst.centerX() - w / 2,
					mDrawableDst.centerY() - h / 2, mDrawableDst.centerX() + w
							/ 2, mDrawableDst.centerY() + h / 2);
			mDrawableSrc.set(mDrawableDst);
		}
		invalidate();
	}

	protected void configureBounds() {
		if (isFrist) {
			oriRationWH = ((float) mDrawable.getIntrinsicWidth())
					/ ((float) mDrawable.getIntrinsicHeight());
			setDrawableDst();
			int floatWidth = XjsUtils.dip2px(mContext, cropWidth);
			int floatHeight = XjsUtils.dip2px(mContext, cropHeight);
			if (floatWidth > getWidth()) {
				floatWidth = getWidth();
				floatHeight = cropHeight * floatWidth / cropWidth;
			}

			if (floatHeight > getHeight()) {
				floatHeight = getHeight();
				floatWidth = cropWidth * floatHeight / cropHeight;
			}

			if (floatWidth > mScreenWidth) {
				floatWidth = mScreenWidth;
			}
			if (floatHeight > mScreenHeight) {
				floatHeight = mScreenHeight;
			}
			if (cropWidth == cropHeight) {
				int num = Math.min(floatWidth, floatHeight);
				floatHeight = num;
				floatWidth = num;
			}

			int floatLeft = (mScreenWidth - floatWidth) / 2;
			int floatTop = (mScreenHeight - floatHeight) / 3 + 50;
			mDrawableFloat.set(floatLeft, floatTop, floatLeft + floatWidth,
					floatTop + floatHeight);
			isFrist = false;
		} 
		mDrawable.setBounds(mDrawableDst);
		mFloatDrawable.setBounds(mDrawableFloat);
	}

	private void setDrawableDst() {
		final float scale = mContext.getResources().getDisplayMetrics().density;
		if (mFlagChange) {
			h = Math.min(getHeight(), (int) (mDrawable.getIntrinsicHeight()
					* scale + 0.5f));
			w = (int) (h / oriRationWH);
		} else {
			w = Math.min(getWidth(), (int) (mDrawable.getIntrinsicWidth()
					* scale + 0.5f));
			h = (int) (w / oriRationWH);
		}
		int left = (getWidth() - w) / 2;
		int top = (getHeight() - h) / 2;
		int right = left + w;
		int bottom = top + h;
		mDrawableSrc.set(left, top, right, bottom);
		int width = right - left;
		int height = bottom - top;
		int xW = mScreenWidth / width;
		int xH = mScreenHeight / height;
		float count = Math.min(xW, xH) / 2;
		left = (int) (left - width * count);
		right = (int) (right + width * count);
		top = (int) (top - height * count);
		bottom = (int) (bottom + height * count);
		mDrawableDst.set(left, top, right, bottom);
	}

	protected void checkBounds() {
		int newLeft = mDrawableDst.left;
		int newTop = mDrawableDst.top;

		boolean isChange = false;
		if (mDrawableDst.left < -mDrawableDst.width()) {
			newLeft = -mDrawableDst.width();
			isChange = true;
		}

		if (mDrawableDst.top < -mDrawableDst.height()) {
			newTop = -mDrawableDst.height();
			isChange = true;
		}

		if (mDrawableDst.left > getWidth()) {
			newLeft = getWidth();
			isChange = true;
		}

		if (mDrawableDst.top > getHeight()) {
			newTop = getHeight();
			isChange = true;
		}

		mDrawableDst.offsetTo(newLeft, newTop);
		if (isChange) {
			invalidate();
		}
	}

	public Bitmap getCropImage() {
		if (isNotCrop) {
			try {
				Bitmap temp = BitmapUtils.createNewBitmapAndCompressByFile(CropImagePath.UPLOAD_IMAGE_PATH,
						new int[] { 720, 1280 }, 0);
				return temp;
			} catch (OutOfMemoryError e) {
				
			}
			return null;
		}
		
		Bitmap ret = null;
		Bitmap tmpBitmap = null;
		Bitmap newRet = null;
		try {
			tmpBitmap = Bitmap.createBitmap(getWidth(), getHeight(),
					Config.ARGB_8888);
			Canvas canvas = new Canvas(tmpBitmap);
			//生成的新图片背景设置为白色
			canvas.drawColor(Color.WHITE);
			mDrawable.draw(canvas);
	
			Matrix matrix = new Matrix();
			float scale = (float) (mDrawableSrc.width())
					/ (float) (mDrawableDst.width());
			matrix.postScale(scale, scale);
	
			adjustDrawableFloat();
			
			if (mDrawableFloat.left + mDrawableFloat.width() > tmpBitmap.getWidth()
					&& mDrawableFloat.height() + mDrawableFloat.top > tmpBitmap
							.getHeight()) {
				ret = Bitmap.createBitmap(tmpBitmap, 0, 0, tmpBitmap.getWidth(),
						tmpBitmap.getHeight(), matrix, true);
			} else if (mDrawableFloat.left + mDrawableFloat.width() > tmpBitmap
					.getWidth()
					&& !(mDrawableFloat.height() + mDrawableFloat.top > tmpBitmap
							.getHeight())) {
				ret = Bitmap
						.createBitmap(tmpBitmap, 0, mDrawableFloat.top,
								tmpBitmap.getWidth(), mDrawableFloat.height(),
								matrix, true);
			} else if (!(mDrawableFloat.left + mDrawableFloat.width() > tmpBitmap
					.getWidth())
					&& mDrawableFloat.height() + mDrawableFloat.top > tmpBitmap
							.getHeight()) {
				ret = Bitmap
						.createBitmap(tmpBitmap, mDrawableFloat.left, 0,
								mDrawableFloat.width(), tmpBitmap.getHeight(),
								matrix, true);
			} else {
				ret = Bitmap.createBitmap(tmpBitmap, mDrawableFloat.left,
						mDrawableFloat.top, mDrawableFloat.width(),
						mDrawableFloat.height(), matrix, true);
			}
			tmpBitmap.recycle();
			tmpBitmap = null;
	
			newRet = Bitmap.createScaledBitmap(ret, cropWidth, cropHeight, false);
			ret.recycle();
			ret = null;
		} catch (Exception e) {
			
		} finally {
			if (tmpBitmap != null  && !tmpBitmap.isRecycled()) {
				tmpBitmap.recycle();
				tmpBitmap = null;
			}
			
			if (ret != null  && !ret.isRecycled()) {
				ret.recycle();
				ret = null;
			}
		}
		
		return newRet;
	}
	
	/**
	 *  校正裁剪浮动框
	 */
	private void adjustDrawableFloat() {
		if (mDrawableFloat.left == mDrawableFloat.right) {
			if (mDrawableFloat.left >= getWidth()) {
				mDrawableFloat.left = getWidth() - 1;
				mDrawableFloat.right = getWidth();
			} else {
				mDrawableFloat.right = mDrawableFloat.left + 1;
			}
		}
		
		if (mDrawableFloat.top == mDrawableFloat.bottom) {
			if (mDrawableFloat.top >= getHeight()) {
				mDrawableFloat.top = getHeight() - 1;
				mDrawableFloat.bottom = getHeight();
			} else {
				mDrawableFloat.bottom = mDrawableFloat.top + 1;
			}
		}
	}
	
	public int getRotateDegree() {
		return mRotateDegree;
	}
}
