package com.ivanceras.db.shared;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import com.ivanceras.commons.conf.DBConfig;
import com.ivanceras.db.api.Aggregate;
import com.ivanceras.db.api.ColumnPair;
import com.ivanceras.db.api.DB_Rdbms;
import com.ivanceras.db.api.DeclaredQuery;
import com.ivanceras.db.api.IDatabase;
import com.ivanceras.db.api.IDatabaseDev;
import com.ivanceras.db.api.ModelDef;
import com.ivanceras.db.api.Query;
import com.ivanceras.db.api.SchemaTable;
import com.ivanceras.db.api.WindowFunction;
import com.ivanceras.db.model.ModelMetaData;
import com.ivanceras.db.shared.exception.DataTypeException;
import com.ivanceras.db.shared.exception.DatabaseException;
import com.ivanceras.fluent.SQL;

/**
 * Generic Queries in SQLite, should work for DB_SQLiteJdbc, DB_WebSQL, DB_SQL.js
 * This should work on SQLite server side and websql browsers and sql.js(javascript compiled sqlite3.c)
 *  Get the foreing keys
 *  https://www.sqlite.org/pragma.html#pragma_foreign_key_list
 *  PRAGMA foreign_key_list(table-name);
 *  
 *  get the indexes
 *  PRAGMA index_list(table-name);
 *  
 * @author lee
 *
 */
public abstract class DB_SQLite extends DB_Rdbms implements IDatabase, IDatabaseDev {
	
	

	@Override
	public void beginTransaction() throws DatabaseException {
		// TODO Auto-generated method stub

	}

	@Override
	public void commitTransaction() throws DatabaseException {
		// TODO Auto-generated method stub

	}

	@Override
	public void rollbackTransaction() throws DatabaseException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isTransacted() throws DatabaseException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setModelMetaDataDefinition(ModelMetaData metaData1)
			throws DatabaseException {
		// TODO Auto-generated method stub

	}

	@Override
	public ModelMetaData getModelMetaDataDefinition() throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public boolean createModel(ModelDef model) throws DatabaseException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean renameModel(ModelDef model, String newName)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int delete(ModelDef model, Filter[] filters)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int empty(ModelDef model, boolean forced) throws DatabaseException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean drop(ModelDef model, boolean forced)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isClosed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean reset() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ModelDef getModelMetaData(String schema, String tableName)
			throws DatabaseException {
		// TODO Auto-generated method stub
		String[] primaryKeys = getPrimaryKeys(schema, tableName);
		String tableComment = getTableComment(schema, tableName);


		return null;
	}

	private String[] getPrimaryKeys(String schema, String tableName) throws DatabaseException {
		SQL sql = new SQL();
		sql.keyword("PRAGMA index_list("+tableName+")");
		DAO[] daoList = select(sql, null);
		for(DAO dao : daoList){
			System.out.println("dao: "+dao);
		}
		return null;
	}

	@Override
	public SchemaTable[] getTableNames(String schema, String tablePattern,
			String[] includedSchema) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean createSchema(String schema) throws DatabaseException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean dropNamespace(String schema, boolean forced)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void debugSql(boolean b) {
		// TODO Auto-generated method stub

	}

	@Override
	public long writeToBlob(byte[] buf) throws DatabaseException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long writeToBlob(String filename) throws DatabaseException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToOutputStream(Long blob_data, OutputStream out)
			throws DatabaseException {
		// TODO Auto-generated method stub

	}

	@Override
	public byte[] getBlob(long oid) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setForeignConstraint(ModelDef model)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String[] getSubClasses(String tableName) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getParentClass(String tableName) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Map<String, String> getTableColumnComments(String tableName,
			String schema) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTableComment(String tableName, String schema)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public DBConfig getConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() throws DatabaseException {
		// TODO Auto-generated method stub

	}


	@Override
	public boolean existModel(ModelDef model) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getTableComment(String table) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean namePrimaryKey() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean supportConstraints() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean caseSensitive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected String getStorageEngine() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean useSchema() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected String getAutoIncrementColumnConstraint() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getTableSchema(String tableName) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getDBElementName(ModelDef model, String schema) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean supportExistChecking() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean useTableKeyWord() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected String forceKeyword() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean supportPreparedStatement() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean prependTableName() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected Query buildSubClassTableQuery(ModelDef model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected SQL buildDeclaredQuery(ModelMetaData meta,
			Map<String, DeclaredQuery> declaredQueries) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected SQL buildWindowFunctions(ModelMetaData meta,
			List<WindowFunction> windowFunctions, boolean doComma) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected SQL buildAggregateQuery(ModelMetaData meta,
			Aggregate[] aggregates, boolean doComma) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getEquivalentGeneralDataType(String dbDataType)
			throws DataTypeException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected Object getEquivalentDBObject(Object record) {
		if(record.equals("null")){return null;}
		if(record.equals("(null)")){return null;}
		if(record.equals("undefined")){return null;}
		else{
			return record;
		}
	}

	@Override
	public DAO[] select(ModelMetaData meta, Query query)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DAO update(DAO dao, ModelDef model, Filter[] filters)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends DAO> T[] select(SQL sql,
			Map<String, ColumnPair> renamedColumns) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DAO insert(DAO dao, ModelMetaData meta, ModelDef model, Query query)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void search(Query query, String keyword) {
		// TODO Auto-generated method stub

	}

	@Override
	public void correctDataTypes(DAO[] daoList, ModelDef model) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean setPrimaryConstraint(ModelDef model)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public DAO[] select(String sql, Object[] parameters)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


}
