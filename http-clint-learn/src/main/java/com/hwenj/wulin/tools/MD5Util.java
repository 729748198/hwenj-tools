package com.hwenj.wulin.tools;


import java.math.BigInteger;
import java.security.MessageDigest;

public class MD5Util {

	protected MD5Util(){

	}

	private static final String ALGORITH_NAME = "md5";

	private static final int HASH_ITERATIONS = 2;
	public static void main(String[] args) {

//		System.out.println(MD5String("123456"));
		String tokenMd5 = MD5Util.MD5String("9A7D10D07C71CA07B3547DE5FFB47A39" + ":" + "C6LAPL33FSML22WSTXYD7HSWPZVD62QS" + ":" + "1597026654");
		System.out.println(tokenMd5);
	}

	public static String MD5String(String plainText) {
		byte[] secretBytes = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			secretBytes = md.digest();
		} catch (Exception e) {
			throw new RuntimeException("没有md5这个算法！");
		}
		String md5code = new BigInteger(1, secretBytes).toString(16);// 16进制数字
		while(md5code.length()<32){
			md5code = "0" + md5code;
		}
		return md5code;
	}
}
