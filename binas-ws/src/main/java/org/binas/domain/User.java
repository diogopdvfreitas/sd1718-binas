package org.binas.domain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.binas.exception.InvalidEmailException;

public class User {
	private String email;
	private Boolean hasBina;
	private Integer credit;
	
	public User(String email) throws InvalidEmailException {
		checkEmail(email);
		this.email = email;
		this.credit = 10;
		this.hasBina = false;
	}
	
	public User(String email, int userInitialPoints) throws InvalidEmailException {
		checkEmail(email);
		this.email = email;
		this.credit = userInitialPoints;
		this.hasBina = false;
	}
	
	public void checkEmail(String email) throws InvalidEmailException {
		 Pattern p = Pattern.compile("^(\\p{Alnum}+\\.?)+@(\\p{Alnum}+\\.?)+$");
		 Matcher m = p.matcher(email);
		 
		 if (!m.matches()) {
			 throw new InvalidEmailException("Invalid email: " +  email);
		 }
	}
	
	public Integer getCredit() {
		return this.credit;
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
		if (this.credit < 1) return false;
		this.credit--;
		this.hasBina = true;
		return true;
	}
	
	public void returnBina() {
		this.hasBina = false;
	}
	
	public void returnBina(int points) {
		this.credit += points;
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
		System.out.println("Comparing " + this.email + " with " + other.getEmail());
		return other.getEmail().equals(this.email);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("User email=");
		builder.append(this.email);
		builder.append(", credit=");
		builder.append(this.credit);
		builder.append(", has bina=");
		builder.append(this.hasBina ? "true" : "false");
		return builder.toString();
	}

}
