package com.iktpreobuka.test.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


public class Encryption {

	public static String getPassEncoded(String pass) {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		return bCryptPasswordEncoder.encode(pass);
	}
// kasnije zakomentarisati, posle prve enkripcije adminovog passa:
	public static void main(String[] args) {
		System.out.println(getPassEncoded("admin"));
	}
	
	public static boolean isEqualToEncodedPass(String rawPassword, String encodedPassword) {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		return bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
		/*if(bCryptPasswordEncoder.encode(password).equals(bCryptPasswordEncoder.encode(password2)))
			return true;
		else
			return false;*/
	}

}
