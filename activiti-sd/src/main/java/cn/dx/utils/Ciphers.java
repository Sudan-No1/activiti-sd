package cn.dx.utils;

import java.security.GeneralSecurityException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class Ciphers {
	
	private static volatile Ciphers ciphersInstance;
	
	private static final byte[] salt = { (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32, (byte) 0x56,
			(byte) 0x34, (byte) 0xE3, (byte) 0x03 };
	private static final int uznig = 0x19d15ea;
	private static final int iterationCount = 19;
	private final Cipher ecipher;
	private final Cipher dcipher;
	
	private Ciphers() throws GeneralSecurityException {
		final String pPh = Integer.toString(uznig);
		final KeySpec keySpec = new PBEKeySpec(pPh.toCharArray(), salt, iterationCount);
		final SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
		final AlgorithmParameterSpec cypherParameters = new PBEParameterSpec(salt, iterationCount);

		ecipher = Cipher.getInstance(key.getAlgorithm());
		ecipher.init(Cipher.ENCRYPT_MODE, key, cypherParameters);

		dcipher = Cipher.getInstance(key.getAlgorithm());
		dcipher.init(Cipher.DECRYPT_MODE, key, cypherParameters);
	}

	private synchronized byte[] encrypt(final byte[] passwordBytesAsUTF8Encoding) throws IllegalBlockSizeException,
			BadPaddingException {
		return ecipher.doFinal(passwordBytesAsUTF8Encoding);
	}

	private synchronized byte[] decrypt(final byte[] encryptedPasswordBytes) throws IllegalBlockSizeException,
			BadPaddingException {
		return dcipher.doFinal(encryptedPasswordBytes);
	}
	
	private static Ciphers getCiphers() throws GeneralSecurityException {
		if (ciphersInstance == null) {
				if (ciphersInstance == null) {
					ciphersInstance = new Ciphers();
				}
		}
		return ciphersInstance;
	}
	
	public static String encrypt(final String password) {
		try {
			final byte[] passwordBytesAsUTF8Encoding = password.getBytes("UTF8");
			final byte[] encryptedPasswordBytes = getCiphers().encrypt(passwordBytesAsUTF8Encoding);
			return new sun.misc.BASE64Encoder().encode(encryptedPasswordBytes);
		} catch (final Exception e) {
		}
		return null;
	}
	/*public static void main(String[] args) throws Exception {
		String password = "datainput";
		String passwd = encrypt(password);
		System.out.println(passwd);
	}*/
}
