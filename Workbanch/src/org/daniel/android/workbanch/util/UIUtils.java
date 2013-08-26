package org.daniel.android.workbanch.util;

import android.os.Build;

/**
 * TODO
 * 
 * @author jiaoyang<br>
 *         email: jiaoyang@360.cn
 * @date 2013-8-23 下午1:40:57
 * @version 1.0
 */
public class UIUtils {

	public static boolean hasHoneycombMR1() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
	}

	public static boolean hasGingerbread() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
	}

	public static boolean hasFroyo() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
	}

}
