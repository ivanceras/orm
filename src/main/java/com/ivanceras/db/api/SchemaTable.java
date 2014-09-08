/*******************************************************************************
 * Copyright by CMIL
 ******************************************************************************/
package com.ivanceras.db.api;


public class SchemaTable {
	
	private String schema;
	private String tableName;
	
	public SchemaTable(String schema, String tableName){
		this.schema = schema;
		this.tableName = tableName;
	}
	
	public String getSchema(){
		return schema;
	}
	
	public String getTableName(){
		return tableName;
	}

}
