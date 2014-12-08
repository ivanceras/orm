package com.ivanceras.db.shared;

import java.io.Serializable;

public class Order implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9043514286984649403L;
	private String column;
	private boolean ascending;
	
	public static boolean ASCENDING = true;
	public static boolean DESCENDING = false;
	
	public Order(){
		
	}
	
	public Order(String column, boolean ascending){
		this.column = column;
		this.ascending = ascending;
	}
	
	public Order(String column){
		this(column, true);
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public boolean isAscending() {
		return ascending;
	}

	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}
	
}
