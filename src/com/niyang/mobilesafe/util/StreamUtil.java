package com.niyang.mobilesafe.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtil {

	/**
	 * 将输入流转为字符串
	 * @param in
	 * @return
	 */
	public static String readStream(InputStream in) {
		try {
			ByteArrayOutputStream bos=new ByteArrayOutputStream();
			byte[] buffer=new byte[1024];
			int len;
			while ((len=in.read(buffer))!=-1) {
				bos.write(buffer, 0, len);
			}
			return bos.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
