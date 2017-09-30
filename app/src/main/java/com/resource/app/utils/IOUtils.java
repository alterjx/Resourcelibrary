package com.resource.app.utils;

import java.io.Closeable;
import java.io.IOException;

import com.resource.mark_net.utils.log.LogUtils;

public class IOUtils {
	/** 关闭流 */
	public static boolean close(Closeable io) {
		if (io != null) {
			try {
				io.close();
			} catch (IOException e) {
				LogUtils.e("IOUtils", e.getMessage());
			}
		}
		return true;
	}
}
