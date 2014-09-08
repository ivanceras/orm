package com.ivanceras.db.shared.exception;

public class TransactionErrorException extends DatabaseException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1662330727515581706L;

	public TransactionErrorException(){
		super();
	}
	
	public TransactionErrorException(String msg){
		super(msg);
	}

}
