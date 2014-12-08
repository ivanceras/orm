package com.ivanceras.db.api;

/**
 * String literal that filter won't have to assign as value in SQL prepared statement
 * @author lee
 *
 */
public class LiteralString {
	
	private String literal;
	
	public LiteralString(String literal){
		this.literal = literal;
	}
	
	public String toString(){
		return this.literal;
	}

}
