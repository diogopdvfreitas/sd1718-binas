package org.binas.station.domain;

public class Tag {

	private int seq;
	private int clientID;
	
	public Tag(int seq, int clientID) {
		this.seq = seq;
		this.clientID = clientID;
	}
	
	public int getSeq() {
		return this.seq;
	}
	
	public int getClientID() {
		return this.clientID;
	}

}
