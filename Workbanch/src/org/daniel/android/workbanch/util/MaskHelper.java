package org.daniel.android.workbanch.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

/**
 * 用于显示在页面上浮层用的类
 * 
 * @author 焦阳 <br>
 *         email:yangjiao623@gmail.com
 * @version 创建时间: Apr 19, 2013 3:25:28 PM
 * */
public class MaskHelper {
	private static final String SF_NAME = "mask_helper";
	private static final int INVALID_ID = -1;

	private LayoutParams mWindowParams;
	private WindowManager mWindowManager;
	private View mView;

	public void create(Context context, View v) {
		mView = v;
		mWindowParams = new WindowManager.LayoutParams();
		mWindowParams.gravity = Gravity.TOP | Gravity.LEFT;
		mWindowParams.x = 0;
		mWindowParams.y = 0;

		mWindowParams.height = WindowManager.LayoutParams.MATCH_PARENT;
		mWindowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
		mWindowParams.format = PixelFormat.TRANSLUCENT;
		mWindowParams.windowAnimations = 0;

		mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		mWindowManager.addView(mView, mWindowParams);

		v.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				destroy();
			}
		});
	}

	/**
	 * 通过layout id创建蒙版
	 * */
	public void create(Context context, int rsid) {
		View v = LayoutInflater.from(context).inflate(rsid, null);
		create(context, v);
	}

	/**
	 * 通过layout id创建蒙版, 但只显示一次
	 * */
	public void createOnce(Context context, int rsid) {
		if (checkOnce(context, rsid)) {
			create(context, rsid);
		}
	}

	public void createOnce(Context context, View view, int rsid) {
		if (checkOnce(context, rsid)) {
			create(context, view);
		}
	}

	private boolean checkOnce(Context context, int id) {
		SharedPreferences spref = context.getSharedPreferences(SF_NAME, Context.MODE_PRIVATE);
		String idStr = String.valueOf(id);
		int result = spref.getInt(idStr, INVALID_ID);
		boolean isNew = result == INVALID_ID;
		if (isNew) {
			Editor editor = spref.edit();
			editor.putInt(idStr, id);
			editor.commit();
		}

		return isNew;
	}

	/**
	 * 重置首次标识
	 * */
	public static void reset(Context context, int rsid) {
		SharedPreferences spref = context.getSharedPreferences(SF_NAME, Context.MODE_PRIVATE);
		Editor editor = spref.edit();
		editor.putInt(String.valueOf(INVALID_ID), rsid);
		editor.commit();
	}

	public void destroy() {
		try {
			mWindowManager.removeView(mView);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void show() {
		if (mView.getVisibility() != View.VISIBLE) {
			mView.setVisibility(View.VISIBLE);
		}
	}

	public void hide() {
		if (mView.getVisibility() == View.VISIBLE) {
			mView.setVisibility(View.INVISIBLE);
		}
	}

}
