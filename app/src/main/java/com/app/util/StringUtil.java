package com.app.util;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
	
	public static final String DEFAULT_SPLITER = ",";
	
	public static final String ZH_REGEX = "[\u4e00-\u9fa5]"; // 中文字符正则表达式
	public static final String URL_REGEX =	"^(http://){0,1}.+\\..+\\..+$"; // URL正则表达式
	public static final String EMAIL_REGEX = // EMAIL正则表达式 
		"\\b^['_a-z0-9-\\+]+(\\.['_a-z0-9-\\+]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*\\.([a-z]{2}|aero|arpa|asia|biz|com|coop|edu|gov|info|int|jobs|mil|mobi|museum|name|nato|net|org|pro|tel|travel|xxx)$\\b";
	public static final String NUM_REGEX = "^[0-9]+$"; // 整数正则表达式
	public static final String NUM_WORD_REGEX = "^[A-Za-z0-9]+$"; // 数字字符正则表达式
	public static final String MOBILE_REGEX = "^1\\d{10}$"; // 手机号正则表达式
	public static final String WORD_REGEX = "^[A-Za-z]+$"; // 字母正则表达式
	public static final String AMT_REGEX = "^(([1-9]\\d{0,9})|0)(\\.\\d{1,2})?$"; // 金额正则表达式
	public static final String HEX_REGEX = "^[0-9A-Fa-f]+$";
	public static final String PWD_REGEX = "^[A-Za-z0-9!@#$*()_+^&}{:?.]+$";
	public static final String UP_CHK_FILE_NM_REGEX = "^INN[0-9]{2}[0,1][0-9][0-3][0-9]{3}ZM_[0-9]{15}$";


	/**
	 * 根据指定长度初始化CHAR_MAP
	 * @param length
	 * @return
	 */
	public static String initCharMap(int length) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i ++) {
			sb.append("0");
		}
		return sb.toString();
	}
	


	/**
	 * Description: 将字符串由UTF-8转换成GBK编码格式
	 * @author QiTing  2014-5-12
	 * @param str
	 * @return
	 */
	public static String UTF8ToGBK(String str) {
		return new String(str.getBytes(), Charset.forName("GBK"));
	}
	
	/**
	 * 传入源字符串与期望截取的长度，按照GBK编码格式进行截取
	 * @param originStr
	 * @param truncateLength
	 * @return
	 */
	public static String truncateString(String originStr,int truncateLength){
		if(originStr.length() <= truncateLength/2) {
			return originStr;
		} else {
			int byteLength = originStr.getBytes(Charset.forName("GBK")).length;
			while(byteLength - truncateLength > 0) {
				
				int endPosition = originStr.length() - (int)Math.ceil(((float)(byteLength - truncateLength)/2));
				originStr = originStr.substring(0, endPosition);
				byteLength = originStr.getBytes(Charset.forName("GBK")).length;				
			}
			return originStr;
		}	
	}
	
	/**
	 * 字符串匹配正则表达式列表（20111102）
	 * @param srcStr
	 * @param regexArr
	 * @return 字符串能匹配一个正则表达式就返回true，否则返回false
	 */
	public static  boolean matchRegexs(String srcStr, String[] regexArr) {
		for (String regex : regexArr) {
			if (srcStr.matches(regex)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean matchRegex(String srcStr, String regex) {
		return srcStr.matches(regex);
	}
	
	/**
	 * 生成一个长度为length的随机密码，包含数字和字符
	 * @param length
	 * @return
	 */
	public static String randomPwd(int length) {
		StringBuilder pwd = new StringBuilder();
		String[] chars = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
		Random rm = new Random(new Double(Math.random() * 10000 * 59).longValue());
		for (int i = 0; i < 4; i++) {
			String rand = chars[rm.nextInt(chars.length)];
			pwd.append(rand);
		}
		chars = new String[] { "a", "b", "c", "d", "e", "z", "w", "y", "o", "i", "q", "t",
				"A", "B", "D", "E", "F", "G", "H", "J", "K", "L", "M", "N",
				"P", "R", "S", "U", "V", "W", "X", "Y", "Z", "_" };
		for (int i = 0; i < length - 4; i++) {
			String rand = chars[rm.nextInt(chars.length)];
			pwd.append(rand);
		}
		return pwd.toString();
	}
	
	public static String concat(String spliter, String... strs) {
		StringBuilder sb = new StringBuilder();
		if (spliter == null) {
			spliter = ",";
		}
		if (strs != null && strs.length > 0) {
			for (int i = 0; i < strs.length; i ++) {
				sb.append(strs[i]);
				if (i < strs.length - 1) {
					sb.append(spliter);
				}
			}
		}
		return sb.toString();
	}
	
	public static String concatSet(String spliter, Set<String> st) {
		StringBuilder sb = new StringBuilder();
		if (spliter == null) {
			spliter = ",";
		}
		if (st != null && st.size() > 0) {
			int c = 0;
			for (String s : st) {
				sb.append(s);
				c ++;
				if (c < st.size()) {
					sb.append(spliter);
				}
			}
		}
		return sb.toString();
	}
	
	public static String mask(String sourceStr, int fromIndex, int toIndex, char val) {
		char[] sourceChars = sourceStr.toCharArray();
		Arrays.fill(sourceChars, fromIndex, toIndex, val);
		return new String(sourceChars);
	}
	
	public static String valueOf(Object o) {
		return o == null ? "" : String.valueOf(o);
	}
	
	public static String valueOf(Object o, String deftVal) {
		return o == null ? deftVal : String.valueOf(o);
	}
	

	/**
	 * 指定长度随机数字字符串
	 * @param length
	 * @return
	 */
	public static String randomNumStr(int length) {
		String[] chars = new String[] {"1", "9", "8", "7", "6", "5", "4",
				"0", "1", "7", "2", "3", "4", "5", "6", "8", "0", "9"};
		Random rm = new Random(new Double(Math.random() * 10000 * 59).longValue());
		String strRand = "";
		for (int i = 0; i < length; i++) {
			String rand = chars[rm.nextInt(chars.length)];
			strRand += rand;
		}
		return strRand;
	}
	
	/**
	 * 根据正则表达式校验字符串
	 * @param val
	 * @param reg
	 * @param caseSensitive
	 * @return
	 */
	public static boolean validateReg(String val, String reg, boolean caseSensitive) {
		Pattern pattern = null;
		try {
			if (caseSensitive) {
	        	pattern = Pattern.compile(reg);
	        } else {
	        	pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
	        }
	        Matcher matcher = pattern.matcher(val);
	        return matcher.matches();
		} catch (Exception e) {
			return false;
		}
	}
	
	public static String trimStr(String str) {
		return str == null ? "" : str.trim();
	}
	
    public static String uuidStr() {
        String uuidStr = UUID.randomUUID().toString();

        StringBuilder sb = new StringBuilder();
        while (uuidStr.contains("-")) {
            sb.append(uuidStr.substring(0, uuidStr.indexOf('-')));
            uuidStr = uuidStr.substring(uuidStr.indexOf('-') + 1);
        }
        sb.append(uuidStr);
        return sb.toString();
    }


    public static boolean isNotBlank(String str){
		if(str==null||str==""||"".equals(str)){
			return false;
		}
		return true;
	}
	public static boolean isBlank(String str){
		if(str==null||str==""||"".equals(str)){
			return true;
		}
		return false;
	}
}