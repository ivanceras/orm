package com.ivanceras.db.shared;

import java.io.Serializable;


/**
 * TODO: change Constant expressions to use enums
 * @author lee
 *
 */
public class Order implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9043514286984649403L;
	private String column;
	private Direction direction;
	
	
	public enum Direction{
		ASC,
		DESC
	}
	
	
	public Order(){
		
	}
	
	public Order(String column, Direction direction){
		this.column = column;
		this.direction = direction;
	}
	
	public Order(String column){
		this(column, Direction.ASC);
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public Direction getDirection(){
		return direction;
	}
	
}
