package com.benyuan.xiaojs.util;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

public class ToastUtil {
	private static Toast mToast = null;

	public void showToast(Context context, String text, int duration) {
		try {
			if (mToast == null) {
				mToast = Toast.makeText(context.getApplicationContext(), text,
						duration);
			} else {
				mToast.setText(text);
				mToast.setDuration(duration);
			}
			mToast.show();
		} catch (Exception e) {

		}
	}

	public void showToast(Context context, int resId, int duration) {
		if (mToast == null) {
			mToast = Toast.makeText(context.getApplicationContext(), resId,
					duration);
		} else {
			mToast.setText(resId);
			mToast.setDuration(duration);
		}
		mToast.show();
	}

	public static void cancel() {
		if (mToast != null) {
			mToast.cancel();
		}
	}

	public static void showToast(Context context,String text){
		if (mToast == null) {
			mToast = new Toast(context.getApplicationContext());
		}

		mToast.setText(text);
		mToast.setGravity(Gravity.CENTER, 0, 0);
		mToast.setDuration(Toast.LENGTH_SHORT);
		mToast.show();
	}

	public static void showToast(Context context,int textId){
		if (mToast == null) {
			mToast = new Toast(context.getApplicationContext());
		}

		mToast.setText(textId);
		mToast.setGravity(Gravity.CENTER, 0, 0);
		mToast.setDuration(Toast.LENGTH_SHORT);
		mToast.show();
	}

	public static void showView(Context ctx, View view, int duration,
			int gravity) {
		if (mToast == null) {
			mToast = new Toast(ctx.getApplicationContext());
		}
		mToast.setView(view);
		mToast.setGravity(gravity, 0, 0);
		mToast.setDuration(duration);
		mToast.show();
	}
}
