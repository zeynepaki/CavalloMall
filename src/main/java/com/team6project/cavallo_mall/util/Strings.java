package com.team6project.cavallo_mall.util;

import lombok.extern.slf4j.Slf4j;

/**
 * description:
 * author: Yuchen Bai
 * email: y.bai19@newcastle.ac.uk
 * date: 2021/2/20 11:14
 */
@Slf4j
public class Strings {

	private Strings(){
    	
    }

	/**
	 * Null value replacement to avoid null exception for strings
	 * @param args String
	 * @return "" if the string is null, Otherwise, returns back the same String with unnecessary whitespace gone.
	 */
	public static String nvl(String...args) {
		if (args == null ) return "";
		for (String arg : args) {
			if (arg != null && !"".equals(arg.trim())) {
				return arg.trim();
			}
		}
		return "";
	}

	/**
	 * If a string of given length isn't null, for each letter in the string another given string is repeated
	 * when the looping through the letters has finished, the original String is appened at the end of the new String
	 *
	 * @param str String
	 * @param len length of str
	 * @param pad another String
	 * @return a new String formed from the given string or null if the initial string was null.
	 */
	public static String lpad(String str, int len, String pad) {
		if (str != null) {
			int len1 = len - str.length();
			StringBuffer sb = new StringBuffer();
			if (len1 > 0) {
				for (int i = 0; i < len1; i ++) {
					sb.append(pad);
				}
			}
			return sb.append(str).toString();
		}
		return null;
	}

	/**
	 * Checks if a string is equal to null
	 * @param s String s
	 * @return
	 */
	public static boolean isEmpty(String s) {
		return s == null || s.trim().equals("");
	}

	/**
	 * Joins the String objects within the key parameter and given string
	 * @param keys Array of type String
	 * @param splitment String
	 * @return "" if keys is null, otherwise a toString of appended keys
	 */
	public static String join(String[] keys, String splitment){
		if (isEmpty(splitment)) {
			splitment = "";
		}
		if (keys == null) {
			return "";
		}
		StringBuffer ret = new StringBuffer("");
		for (int i = 0; i < keys.length; i ++) {
			if (i > 0) {
				ret.append(splitment);
			}
			ret.append(keys[i]);
		}
		return ret.toString();
	}

}
