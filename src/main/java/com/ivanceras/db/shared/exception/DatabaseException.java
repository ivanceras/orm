package com.ivanceras.db.shared.exception;

public class DatabaseException extends Exception{

	public DatabaseException(String msg) {
		super(msg);
	}
	
	public DatabaseException(){
		super();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 588316239403318350L;

}
