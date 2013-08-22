package org.daniel.android.workbanch.service;

import android.graphics.Bitmap;

/**
 * 图片压缩的接口
 * 
 * @author jiaoyang<br>
 *         email: jiaoyang@360.cn
 * @date 2013-8-22 下午2:52:54
 * @version 1.0
 */
interface IBitmapCompress {
	Bitmap compress(String srcPath, String dstPath, int minWidth, int minHeight);
}
