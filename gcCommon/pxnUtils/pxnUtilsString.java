package com.growcontrol.gcCommon.pxnUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;


public final class pxnUtilsString {
	private pxnUtilsString() {}
	@Override
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}


	// exception to string
	public static String ExceptionToString(Throwable e) {
		if(e == null) return null;
		StringWriter writer = new StringWriter(256);
		e.printStackTrace(new PrintWriter(writer));
		return writer.toString().trim();
	}


	// md5
	public static String MD5(String str) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		md.update(str.getBytes());
		byte[] byteData = md.digest();
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			String hex = Integer.toHexString(0xFF & byteData[i]);
			if (hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}
		return hexString.toString();
	}


	// generate a random string
	public static String RandomString(int length) {
		if(length == 0) return "";
		if(length <  0) return null;
		String str = "";
		while(str.length() < length) {
			String s = UUID.randomUUID().toString();
			if(s == null) throw new NullPointerException();
			str += s;
		}
		return str.substring( 0, pxnUtilsMath.MinMax(length, 0, str.length()) );
	}


	// add strings with delimiter
	public static String addSet(String baseString, String addThis, String delim) {
		if(addThis.isEmpty())    return baseString;
		if(baseString.isEmpty()) return addThis;
		return baseString + delim + addThis;
	}
	public static String addSet(String baseString, List<String> addThis, String delim) {
		return addSet(baseString, (String[]) addThis.toArray(new String[0]), delim);
	}
	public static String addSet(String baseString, String[] addThis, String delim) {
		if(baseString == null) baseString = "";
		for(String line : addThis) {
			if(!baseString.isEmpty()) baseString += delim;
			baseString += line;
		}
		return baseString;
	}


}
