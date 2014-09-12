package com.ivanceras.db.shared;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ivanceras.commons.conf.DBConfig;
import com.ivanceras.commons.strings.CStringUtils;
import com.ivanceras.db.api.Aggregate;
import com.ivanceras.db.api.ColumnPair;
import com.ivanceras.db.api.DB_Rdbms;
import com.ivanceras.db.api.DeclaredQuery;
import com.ivanceras.db.api.IDatabase;
import com.ivanceras.db.api.ModelDef;
import com.ivanceras.db.api.Query;
import com.ivanceras.db.api.QueryBreakdown;
import com.ivanceras.db.api.SchemaTable;
import com.ivanceras.db.api.WindowFunction;
import com.ivanceras.db.model.ModelMetaData;
import com.ivanceras.db.shared.datatype.GenericDataType;
import com.ivanceras.db.shared.exception.DataTypeException;
import com.ivanceras.db.shared.exception.DatabaseException;
import com.ivanceras.fluent.sql.SQL;


/**
 * This should work on SQLite server side and websql browsers and sql.js(javascript compiled sqlite3.c)
 * @author lee
 *
 */
public class DB_SQLite extends DB_Rdbms implements IDatabase {

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
	protected String getEquivalentDBDataType(String javaDataType)
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


}
