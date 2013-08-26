package org.daniel.android.workbanch.service;

import org.daniel.android.workbanch.util.BitmapUtils;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.os.RemoteException;

/**
 * 图片压缩的服务
 * 
 * @author jiaoyang<br>
 *         email: jiaoyang@360.cn
 * @date 2013-8-22 下午2:51:24
 * @version 1.0
 */
public class BitmapCompressService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	private final IBitmapCompress.Stub mBinder = new IBitmapCompress.Stub() {

		@Override
		public Bitmap compress(String srcPath, String dstPath, int minWidth,
				int minHeight) throws RemoteException {
			return BitmapUtils.resizeBitmap(srcPath, dstPath, minWidth,
					minHeight, Bitmap.Config.RGB_565);
		}
	};

}
