package org.daniel.android.simpledemo;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import org.daniel.android.simpledemo.utils.LauncherUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by jiaoyang on 2016/3/10.
 */
public class ShortcutActivity extends AppCompatActivity implements View.OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shortcut);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.add:
				createShortCut(getApplicationContext(), this.getClass(), "快捷方式", R.mipmap.ic_launcher);
				break;
			case R.id.update:
				Intent intent = new Intent(getApplicationContext(), getClass());
				intent.setAction("android.intent.action.MAIN");
				Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.plus);
				updateShortcutIcon(getApplicationContext(), "快捷方式", intent, bitmap, "哈哈哈哈");
				break;
			case R.id.remove:
				break;
		}
	}


	public static void createShortCut(Context context, Class clazz, String name, int iconResId) {
		Intent shortcutIntent = new Intent(context, clazz);
		shortcutIntent.setAction("android.intent.action.MAIN");

		Intent addIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
		// 快捷方式的名称
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
		// 不允许重复创建
		addIntent.putExtra("duplicate", false);

		// 这里必须为Intent设置一个action，可以任意(但安装和卸载时该参数必须一致)
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);

		// 快捷方式的图标
		Intent.ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(context, iconResId);
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);

		context.sendBroadcast(addIntent);
	}


	private static final String TAG = "Shortcut";

	/**
	 * 更新桌面快捷方式图标，不一定所有图标都有效<br/>
	 * 如果快捷方式不存在，则不更新<br/>.
	 */
	public static void updateShortcutIcon(Context context, String title, Intent intent, Bitmap bitmap, String newTitle) {
		if (bitmap == null) {
			Log.i(TAG, "update shortcut icon,bitmap empty");
			return;
		}
		try {
			final ContentResolver cr = context.getContentResolver();
			StringBuilder uriStr = new StringBuilder();
			String urlTemp = "";
			String authority = LauncherUtil.getAuthorityFromPermissionDefault(context);
			if (authority == null || authority.trim().equals("")) {
				authority = LauncherUtil.getAuthorityFromPermission(context, LauncherUtil.getCurrentLauncherPackageName(context) + ".permission.READ_SETTINGS");
			}
			uriStr.append("content://");
			if (TextUtils.isEmpty(authority)) {
				int sdkInt = android.os.Build.VERSION.SDK_INT;
				if (sdkInt < 8) { // Android 2.1.x(API 7)以及以下的
					uriStr.append("com.android.launcher.settings");
				} else if (sdkInt < 19) {// Android 4.4以下
					uriStr.append("com.android.launcher2.settings");
				} else {// 4.4以及以上
					uriStr.append("com.android.launcher3.settings");
				}
			} else {
				uriStr.append(authority);
			}
			urlTemp = uriStr.toString();
			uriStr.append("/favorites?notify=true");
			Uri uri = Uri.parse(uriStr.toString());
			Log.i(TAG, uri.toString());
			Cursor c = cr.query(uri, new String[]{"_id", "title", "intent"},
					"title=?  and intent=? ",
					new String[]{title, intent.toUri(0)}, null);
			int index = -1;
			if (c != null && c.getCount() > 0) {
				c.moveToFirst();
				Log.i(TAG, "title=" + c.getString(c.getColumnIndex("title")) + ", intent="
						+ c.getString(c.getColumnIndex("intent")));
				index = c.getInt(0);//获得图标索引
				ContentValues cv = new ContentValues();
				cv.put("icon", flattenBitmap(bitmap));
				cv.put("title", newTitle);
				Uri uri2 = Uri.parse(urlTemp + "/favorites/" + index + "?notify=true");

				int i = context.getContentResolver().update(uri2, cv, null, null);
				context.getContentResolver().notifyChange(uri, null);//此处不能用uri2，是个坑

				Log.i(TAG, "update ok: affected " + i + " rows,index is" + index);
			} else {
				Log.i(TAG, "update result failed");
			}
			if (c != null && !c.isClosed()) {
				c.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.i(TAG, "update shortcut icon,get errors:" + ex.getMessage());
		}
	}


	private static byte[] flattenBitmap(Bitmap bitmap) {
		// Try go guesstimate how much space the icon will take when serialized
		// to avoid unnecessary allocations/copies during the write.
		int size = bitmap.getWidth() * bitmap.getHeight() * 4;
		ByteArrayOutputStream out = new ByteArrayOutputStream(size);
		try {
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
			return out.toByteArray();
		} catch (IOException e) {
			Log.w(TAG, "Could not write icon");
			return null;
		}
	}


}
