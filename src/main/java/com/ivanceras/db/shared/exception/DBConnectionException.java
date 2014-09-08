package com.ivanceras.db.shared.exception;

public class DBConnectionException extends Exception{
	
	public DBConnectionException(String msg) {
		super(msg);
	}
	
	public DBConnectionException(){
		super();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 588316239403318350L;
}
