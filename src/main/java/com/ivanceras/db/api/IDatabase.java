package com.ivanceras.db.api;

import java.io.OutputStream;
import java.util.Map;

import com.ivanceras.commons.conf.DBConfig;
import com.ivanceras.db.model.ModelMetaData;
import com.ivanceras.db.shared.DAO;
import com.ivanceras.db.shared.Filter;
import com.ivanceras.db.shared.exception.DatabaseException;
import com.ivanceras.keyword.sql.SQL;

/**
 * This is the database interface which will should be implemented to you the specifics of each database platform
 * 
 * Uses only DAO objects and ModelDef class
 * ModeDef is the definition of a DAO's column
 * DAO contains the column values
 * 
 * @author lee
 *
 */
public interface IDatabase{
	
	public final static String SUBCLASSTABLE = "subclasstable";
	public static final String RETURN_FROM_INSERT = "_return_from_insert";
	
	
	/**
	 * begin a database transaction to avoid Dirty Reads/Writes of the database.
	 * @throws DatabaseException
	 */
	public void beginTransaction() throws DatabaseException;
	
	/**
	 * Commits all the database operation since {@link #beginTransaction()} is invoked.
	 * @throws DatabaseException
	 */
	public void commitTransaction() throws DatabaseException;
	
	/**
	 * Cancels all database operation since {@link #beginTransaction()} is invoked
	 * @throws DatabaseException
	 */
	public void rollbackTransaction() throws DatabaseException;
	
	/**
	 * Check to see if all database operations has already been transacted (commited/rollbacked)
	 * @return
	 * @throws DatabaseException
	 */
	public boolean isTransacted() throws DatabaseException;
	
	/**
	 * Set the meta data object for later preference of the database
	 * @param metaData1
	 * @throws DatabaseException
	 */
	public void setModelMetaDataDefinition(ModelMetaData metaData1) throws DatabaseException;
	
	/**
	 * Get the meta data object {@link #setModelMetaDataDefinition(ModelMetaData)}
	 * @return
	 * @throws DatabaseException
	 */
	public ModelMetaData getModelMetaDataDefinition() throws DatabaseException;
	
	/**
	 * Do a select operation in the database
	 * @param meta
	 * @param query
	 * @return
	 * @throws DatabaseException
	 */
	public DAO[] select(ModelMetaData meta, Query query) throws DatabaseException;
	
	/**
	 * Do a select operation in the database
	 * @param meta
	 * @param query
	 * @return
	 * @throws DatabaseException
	 */
	public DAO[] select(String sql, Object[] parameters) throws DatabaseException;

	/**
	 * Each database has its own way of executing stuffs, creating a model may differ a lot from each other
	 * This is creating the table
	 * @param model
	 * @return
	 * @throws DatabaseException
	 */
	boolean createModel(ModelDef model) throws DatabaseException;

	/**
	 * Rename the table that is already existed in the database
	 * @param model
	 * @param newName
	 * @return
	 * @throws DatabaseException
	 */
	boolean renameModel(ModelDef model, String newName) throws DatabaseException;
	
	/**
	 * Update values of a database tables, the values are stored in the DAO object, with additional filters
	 * and ignore columns which will not be affecting the update
	 * @param dao
	 * @param model
	 * @param filters
	 * @param ignoreColumns
	 * @return
	 * @throws DatabaseException
	 */
	public DAO update(DAO dao, ModelDef model, Filter[] filters) throws DatabaseException;

	/**
	 * Delete a record in the database using the filter
	 * @param model
	 * @param filters
	 * @return the number of deleted records
	 * @throws DatabaseException
	 */
	public int delete(ModelDef model, Filter[] filters) throws DatabaseException;
	
	/**
	 * Remove all records of this table
	 * force empty is a cascaded empty to truncate the table including records that depends on it
	 * @param model
	 * @param forced
	 * @return
	 * @throws DatabaseException
	 */
	public int empty(ModelDef model, boolean forced) throws DatabaseException;
	
	/**
	 * Remove the table from the database
	 * @param model
	 * @param forced
	 * @return
	 * @throws DatabaseException
	 */
	public boolean drop(ModelDef model, boolean forced) throws DatabaseException;

	/**
	 * Checks to see if the connection to the database is already closed.
	 * @return
	 */
	public boolean isClosed(); 
	
	/**
	 * Check to see if the connection to the database is valid
	 * @return
	 */
	public boolean isValid();

	
	/**
	 * This method is called when a connection is to be reused - it should ensure that there are no transactions pending 
	 * (it should rollback if there is), and do any other required housecleaning if needed, so the connection can be re-used.
	 * @return
	 */
	public boolean reset();
//	/**
//	 * This is queries directly from the database, so this will be costly. Only used this on initialization processes
//	 * @param schema
//	 * @param tableName
//	 * @return
//	 * @throws DatabaseException
//	 */
//	public ModelDef getModelMetaData(String schema, String tableName) throws DatabaseException;
//	public SchemaTable[] getTableNames(String schema, String tablePattern, String[] includedSchema)
//			throws DatabaseException;
	
	boolean createSchema(String schema) throws DatabaseException;
	boolean dropNamespace(String schema, boolean forced) throws DatabaseException;
	
	void debugSql(boolean b);
	
	/**
	 * writing blob to the database
	 * @param buf
	 * @return
	 * @throws DatabaseException
	 */
	long writeToBlob(byte[] buf) throws DatabaseException;
	long writeToBlob(String filename) throws DatabaseException;
	void writeToOutputStream(Long blob_data, OutputStream out) throws DatabaseException;
	
	byte[] getBlob(long oid) throws DatabaseException;
	boolean setForeignConstraint(ModelDef model) throws DatabaseException;
	
	boolean prependTableName();
	
	String[] getSubClasses(String tableName) throws DatabaseException;
	String getParentClass(String tableName) throws DatabaseException;

	boolean isConnected();
	
//	Map<String, String> getTableColumnComments(String tableName, String schema)
//			throws DatabaseException;
	String getTableComment(String tableName, String schema)
			throws DatabaseException;
	
	public DBConfig getConfig();
	
	/**
	 * close the connection to the database
	 * @throws DatabaseException
	 */
	public void close() throws DatabaseException;
	
	/**
	 * Check to see if a table exist
	 * @param model
	 * @return
	 */
	public boolean existModel(ModelDef model);
	
	/**
	 * Colnverts the Query object into a SQL object that will be readily executed by the Database platform
	 * @param meta
	 * @param query
	 * @param useCursor
	 * @return
	 */
	public SQL buildSQL(ModelMetaData meta,Query query, boolean useCursor);
	
	/**
	 * Executes a SQL statement and set which columns have been renamed to
	 * @param sql
	 * @param renamedColumns
	 * @return
	 * @throws DatabaseException
	 */
	public <T extends DAO > T[] select(SQL sql, Map<String, Pair[]> renamedColumns) throws DatabaseException;
	
//	/**
//	 * Get the comments of this table
//	 * @param table
//	 * @return
//	 * @throws DatabaseException
//	 */
//	public String getTableComment(String table) throws DatabaseException;
	
	/**
	 * Insert a DAO object with the definition defined in the model argument
	 * Query when inserting a data that is coming from a Query
	 * meta is a lookup for the query building to be used
	 * @param dao
	 * @param meta
	 * @param model
	 * @param query
	 * @return
	 * @throws DatabaseException
	 */
	public DAO insert(DAO dao, ModelMetaData meta, ModelDef model, Query query) throws DatabaseException;
	
	/**
	 * Search a set of record from the base Query that would have been returned by the base query
	 * @param query
	 * @param keyword
	 */
	public void search(Query query, String keyword);

	/**
	 * Actually converting the from whatever JDBC converts the object to the correct type that we intend to be using
	 * @param daoList
	 * @param model
	 */
	public void correctDataTypes(DAO[] daoList, ModelDef model);

	boolean setPrimaryConstraint(ModelDef model) throws DatabaseException;
	

}
