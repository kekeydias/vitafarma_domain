package br.com.vitafarma.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encryption {
	public static String toMD5(String text) throws NoSuchAlgorithmException {
		String sen = ("");

		MessageDigest md = null;

		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException ex) {
			System.err.println(ex);

			throw ex;
		}

		BigInteger hash = new BigInteger(1, md.digest(text.getBytes()));

		sen = hash.toString(16);

		return sen;
	}
}
