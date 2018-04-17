package org.binas.station.domain;

public class UserReplic {
	
	private String email;
	private int credit;

	public UserReplic(String email, int credit) {
		this.email = email;
		this.credit = credit;
	}
	
	public UserReplic(String email) {
		this.email = email;
		this.credit = 0;
	}
	
	public int getBalance() {
		return this.credit;
	}
	
	public void setBalance(int credit) {
		this.credit = credit;
	}
	
	public String getEmail() {
		return this.email;
	}

}
