package org.daniel.android.workbanch.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * 用于监听SD卡插入事件，并调用扫描下载目录
 * 
 * @author jiaoyang<br>
 *         email: yangjiao623@gmail.com
 * @date Aug 27, 2013 6:49:41 PM
 * @version 1.0
 */
public class SDCardActionReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
	}

	/**
	 * 注册监听器
	 * */
	public void register(Context context) {
		IntentFilter filter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
		filter.addDataScheme("file");
		context.registerReceiver(this, filter);
	}

	/**
	 * 注销监听器
	 * */
	public void unregister(Context context) {
		context.unregisterReceiver(this);
	}
}
