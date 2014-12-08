package com.ivanceras.db.api;


/**
 * Generic Pair, can be use as table.columns, schema.columns
 * @author lee
 *
 */
public class Pair {
	
	private String left;
	private String right;
	
	public Pair(String left, String right){
		this.left = left;
		this.right = right;
	}
	
	public String getLeft(){
		return left;
	}
	
	public String getRight(){
		return right;
	}

}
