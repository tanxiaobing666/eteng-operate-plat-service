/**
 * Copyright (c) 2021 ShangHai P&C Information Technology Co.,Ltd. All rights reserved.
 * 
 * <p>项目名称	:jmhtest</p>
 * <p>包名称    	:cn.com.yitong.crypto</p>
 * <p>文件名称	:SampleCryptoUtil.java</p>
 * <p>创建时间	:2021-3-22 17:15:42 </p>
 */
package cn.com.yitong.ares.util;

import java.nio.charset.Charset;

import cn.hutool.core.util.HexUtil;

public class SimpleCryptoUtil {

	public static final Charset UTF8 = Charset.forName("UTF-8");

	public static void main(String[] args) {
		String content = "6225885512344321"; // 需要加密的字符
		String key = "abcdef"; // 密钥
		String resultHex = encryptToHex(content, key);
		System.out.println("明文数据：\t" + content);
		System.out.println("加密后HEX值：\t" + resultHex);
		System.out.println("解密后的值：\t" + new String(decrypt(resultHex, key)));
	}

	/**
	 * 报文加密
	 *
	 * @param content 明文字符串
	 * @param key     加密key
	 * @return the byte[] 加密后字节数组
	 */
	public static byte[] encrypt(String content, String key) {
		return encrypt(content, key, UTF8);
	}

	/**
	 * 报文加密
	 *
	 * @param content 明文字符串
	 * @param key     加密key
	 * @return the byte[] 加密后字节数组
	 */
	public static String encryptToHex(String content, String key) {
		return encryptToHex(content, key, UTF8);
	}

	/**
	 * 报文加密
	 *
	 * @param content 明文字符串
	 * @param key     加密key
	 * @param charset 字符集
	 * @return the byte[] 加密后字节数组
	 */
	public static String encryptToHex(String content, String key, Charset charset) {
		return HexUtil.encodeHexStr(encrypt(content, key, charset));
	}

	/**
	 * 报文加密
	 *
	 * @param content 明文字符串
	 * @param key     加密key
	 * @param charset 字符集
	 * @return the byte[]
	 */
	public static byte[] encrypt(String content, String key, Charset charset) {
		byte[] contentBytes;
		contentBytes = content.getBytes(charset);
		byte[] keyBytes = key.getBytes(UTF8);

		byte dkey = 0;
		for (byte b : keyBytes) {
			dkey ^= b;
		}

		byte salt = 0; // 随机盐值
		byte[] result = new byte[contentBytes.length];
		for (int i = 0; i < contentBytes.length; i++) {
			salt = (byte) (contentBytes[i] ^ dkey ^ salt);
			result[i] = salt;
		}
		return result;
	}

	/**
	 * 解密
	 *
	 * @param contentHex 加密后的16进制字符串
	 * @param key        加密key
	 * @return String 明文字符串，UTF-8编码
	 */
	public static String decryptToStr(String contentHex, String key) {
		return new String(decrypt(contentHex, key), UTF8);
	}

	/**
	 * 解密
	 *
	 * @param contentHex 加密后的16进制字符串
	 * @param key        加密key
	 * @return the byte[] 明文字节数组
	 */
	public static byte[] decrypt(String contentHex, String key) {
		byte[] contentBytes = HexUtil.decodeHex(contentHex);
		byte[] keyBytes = key.getBytes(UTF8);

		byte dkey = 0;
		for (byte b : keyBytes) {
			dkey ^= b;
		}

		byte salt = 0; // 随机盐值
		byte[] result = new byte[contentBytes.length];
		for (int i = contentBytes.length - 1; i >= 0; i--) {
			if (i == 0) {
				salt = 0;
			} else {
				salt = contentBytes[i - 1];
			}
			result[i] = (byte) (contentBytes[i] ^ dkey ^ salt);
		}
		return result;
	}

}
