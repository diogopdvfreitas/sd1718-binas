package org.binas.station.domain;

public class UserReplic {
	
	private String email;
	private int credit;
	private Tag tag;

	public UserReplic(String email, int credit, Tag tag) {
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
	
	public Tag getTag() {
		return this.tag;
	}

}
