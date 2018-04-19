package org.binas.station.domain;

public class UserReplic {
	
	private String email;
	private int credit;
	private int tag;

	public UserReplic(String email, int credit, int tag) {
		this.email = email;
		this.credit = credit;
		this.tag = tag;
	}
	
	public int getValue() {
		return this.credit;
	}
	
	public String getEmail() {
		return this.email;
	}
	
	public int getTag() {
		return this.tag;
	}

}
