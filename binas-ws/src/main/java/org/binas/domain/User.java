package org.binas.domain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.binas.exception.InvalidEmailException;

public class User {
	private String email;
	private Boolean hasBina;
	
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^(\\p{Alnum}+\\.?)+@(\\p{Alnum}+\\.?)+$");
	
	public User(String email) throws InvalidEmailException {
		checkEmail(email);
		this.email = email;
		this.hasBina = false;
	}
	
	public static void checkEmail(String email) throws InvalidEmailException {
		if (email == null) throw new InvalidEmailException("Invalid email");
		
		Matcher m = EMAIL_PATTERN.matcher(email);
		 
		if (!m.matches()) {
			throw new InvalidEmailException("Invalid email format: " +  email);
		}
	}
	
	public String getEmail() {
		return this.email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public Boolean hasBina() {
		return this.hasBina;
	}
	
	public boolean takeBina() {
		this.hasBina = true;
		return true;
	}
	
	public boolean takeBina(int points) {
		this.hasBina = true;
		return true;
	}
	
	public void returnBina() {
		this.hasBina = false;
	}
	
	public void returnBina(int points) {
		this.hasBina = false;
	}
	
	@Override
	public int hashCode() {
		return this.email.hashCode();
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return other.getEmail().equals(this.email);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("User email=");
		builder.append(this.email);
		builder.append(", has bina=");
		builder.append(this.hasBina ? "true" : "false");
		return builder.toString();
	}

}
