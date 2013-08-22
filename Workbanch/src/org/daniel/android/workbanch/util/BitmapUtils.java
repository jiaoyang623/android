package org.daniel.android.workbanch.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 图片处理的工具类
 * 
 * @author jiaoyang<br>
 *         email: jiaoyang@360.cn
 * @date 2013-8-22 上午11:08:44
 * @version 1.0
 */
public class BitmapUtils {

	/**
	 * 用option.sampleSize进行解码加载图片。<br>
	 * 首先会读取图片的大小，然后根据minWidth和minHeight，计算缩放级别，在不失真的情况下进行缩放
	 * 
	 * @param path
	 *            图片的路径
	 * @param minWidth
	 *            最小宽度
	 * @param minHeight
	 *            最小高度
	 * @param channelConfig
	 *            图片的加载质量，用RGB_565会节省加载需要的内存，是ARGB_8888的1/2内存使用量。设置成null则按照默认加载
	 * */
	public static Bitmap decode(String path, final int minWidth,
			final int minHeight, Bitmap.Config channelConfig) {
		// 读取图片的宽高
		BitmapFactory.Options options = getBitmapOption(path);
		// 计算SampleSize
		options.inSampleSize = (int) calculateInSampleSize(options, minWidth,
				minHeight);

		// 用SampleSize进行解码
		if (channelConfig != null) {
			options.inPreferredConfig = channelConfig;
		}

		return BitmapFactory.decodeFile(path, options);
	}

	/**
	 * 用于获取图片参数，inJustDecodeBounds=false不需要重新设置
	 * */
	private static BitmapFactory.Options getBitmapOption(String path) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		options.inJustDecodeBounds = false;

		return options;
	}

	private static float calculateInSampleSize(BitmapFactory.Options options,
			final int reqWidth, final int reqHeight) {
		// 读取图片的宽高
		final int height = options.outHeight;
		final int width = options.outWidth;
		float inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// 计算宽高的压缩率
			final float heightRatio = (float) height / (float) reqHeight;
			final float widthRatio = (float) width / (float) reqWidth;

			// 取压缩率较小的作为图片的压缩略，inSampleSize >= 1
			inSampleSize = Math.max(1f, heightRatio < widthRatio ? heightRatio
					: widthRatio);
		}

		return inSampleSize;
	}

	/**
	 * 将图片按比例压缩成指定大小，替换指定路径图片，并将解码出的数据返回
	 * 
	 * @param srcPath
	 *            源文件地址
	 * @param dstPath
	 *            目标文件地址
	 * @param minWidth
	 *            最小宽度
	 * @param minHeight
	 *            最小高度
	 * @param channelConfig
	 *            图片的加载质量，用RGB_565会节省加载需要的内存，是ARGB_8888的1/2内存使用量。设置成null则按照默认加载
	 * 
	 * */
	public static Bitmap resizeBitmap(String srcPath, String dstPath,
			final int minWidth, final int minHeight, Bitmap.Config channelConfig) {
		Thread currentThread = Thread.currentThread();
		File srcFile = new File(srcPath);
		if (!srcFile.exists()) {
			return null;
		}

		if (currentThread.isInterrupted()) {
			return null;
		}
		// 读取图片的宽高
		BitmapFactory.Options options = getBitmapOption(srcPath);
		// 如果大小相同，则复制或者不做处理
		if (options.outHeight == minWidth && options.outHeight == minHeight) {
			if (!srcPath.equals(dstPath)) {
				try {
					copyFile(srcPath, dstPath);
				} catch (IOException e) {
				}
			}
			return BitmapFactory.decodeFile(srcPath);
		}

		if (currentThread.isInterrupted()) {
			return null;
		}

		options.inSampleSize = (int) calculateInSampleSize(options, minWidth,
				minHeight);

		// 用SampleSize进行解码
		if (channelConfig != null) {
			options.inPreferredConfig = channelConfig;
		}

		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, options);

		if (currentThread.isInterrupted()) {
			return null;
		}

		// 计算压缩比例
		float ratio = calculateInSampleSize(options, minWidth, minHeight);
		// 压缩图片
		Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap,
				(int) (options.outWidth / ratio),
				(int) (options.outHeight / ratio), true);
		bitmap.recycle();

		if (currentThread.isInterrupted()) {
			return null;
		}

		// 保存文件
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(dstPath);
			final int QUALITY = 100;
			resizedBitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return resizedBitmap;
	}

	/**
	 * 文件复制
	 * */
	private static void copyFile(String src, String dst) throws IOException {
		final int BUFFER_SIZE = 1024;
		byte[] buffer = new byte[BUFFER_SIZE];
		FileInputStream fis = new FileInputStream(src);
		FileOutputStream fos = new FileOutputStream(dst);
		Thread currentThread = Thread.currentThread();
		while (fis.read(buffer) != -1) {
			// 请求中断则停止拷贝
			if (currentThread.isInterrupted()) {
				break;
			}
			fos.write(buffer);
		}
		fos.flush();
		fos.close();
		fis.close();

		// 如果说被中断，则需要删除拷贝的目标文件
		if (currentThread.isInterrupted()) {
			new File(src).delete();
		}
	}
}
