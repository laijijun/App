package com.app.util;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.text.DecimalFormat;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class EncryptUtil {

	static final String DEFAULT_ENCODING = "UTF-8";
	private static final Pattern md5Pattern = Pattern.compile("^([\\da-f]{32})$");
	private static final Pattern base64Pattern = Pattern
			.compile("^([A-Za-z0-9\\+\\/]{4})*([A-Za-z0-9\\+\\/]{2}==|[A-Za-z0-9\\+\\/]{3}=)?$");
	private static final DecimalFormat DEFAULT_DECIMAL_FORMATTER = new DecimalFormat("#.##");

	public static byte[] encryptData(PublicKey publicKey, byte[] rawData){
		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);

			return cipher.doFinal(rawData);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static Cipher tripleDESDecryptCipher;
	static{
		try {
			tripleDESDecryptCipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static byte[] TripleDESDecrypt(byte[] key, byte[] data)
			throws NoSuchAlgorithmException, GeneralSecurityException {
		SecretKey deskey = new SecretKeySpec(key, "DESede");

		byte[] res = null;
		synchronized (tripleDESDecryptCipher) {
			tripleDESDecryptCipher.init(Cipher.DECRYPT_MODE, deskey);
			res = tripleDESDecryptCipher.doFinal(data);
		}
		return res;
	}
}
