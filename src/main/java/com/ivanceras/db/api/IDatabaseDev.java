package com.ivanceras.db.api;

import java.util.Map;
import com.ivanceras.db.shared.exception.DatabaseException;


/**
 * Database Interface for database platform that
 * can be used as your development Database,
 * therefore must have the ability, to extract table meta data information
 * 
 * @author lee
 *
 */
public interface IDatabaseDev {
	
	
	/**
	 * Build the ModelDef object based on the extracted meta data info from database
	 * This is queries directly from the database, so this will be costly. Only used this on initialization processes
	 * @param schema
	 * @param tableName
	 * @return
	 * @throws DatabaseException
	 */
	public ModelDef getModelMetaData(String schema, String tableName) throws DatabaseException;
	public Pair[] getTableNames(String schema, String tablePattern, String[] includedSchema)
			throws DatabaseException;
	
	Map<String, String> getTableColumnComments(String tableName, String schema)
			throws DatabaseException;
	String getTableComment(String tableName, String schema)
			throws DatabaseException;
	
	/**
	 * Get the comments of this table
	 * @param table
	 * @return
	 * @throws DatabaseException
	 */
	public String getTableComment(String table) throws DatabaseException;
	
}
