package org.binas.station.domain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.binas.station.domain.exception.InvalidEmailException;

public class UserReplic {
	
	private int credit;
	private Tag tag;
	
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^(\\p{Alnum}+\\.?)+@(\\p{Alnum}+\\.?)+$");

	public UserReplic(int credit, Tag tag) throws InvalidEmailException {
		this.credit = credit;
		this.tag = tag;
	}
	
	public static void checkEmail(String email) throws InvalidEmailException {
		if (email == null) throw new InvalidEmailException("Invalid email");
		
		Matcher m = EMAIL_PATTERN.matcher(email);
		 
		if (!m.matches()) {
			throw new InvalidEmailException("Invalid email format: " +  email);
		}
	}
	
	public int getValue() {
		return this.credit;
	}
	
	public Tag getTag() {
		return this.tag;
	}

}
