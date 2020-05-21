package org.onegang.access.utils;

import org.jasypt.util.text.BasicTextEncryptor;

/**
 * Run and pass <SALT> <PASSWORD_TO_ENCRYPT>
 * @author TC
 *
 */
public class PasswordEncrypt {

	public static void main(String[] args) {
//		BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
//		textEncryptor.setPassword(args[0]);
		BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
		textEncryptor.setPasswordCharArray(args[0].toCharArray());
		System.out.println(textEncryptor.encrypt(args[1]));
	}
}
