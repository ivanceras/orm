package com.ivanceras.db.server.core;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.ivanceras.commons.conf.DBConfig;
import com.ivanceras.commons.strings.CStringUtils;
import com.ivanceras.db.api.Aggregate;
import com.ivanceras.db.api.ColumnDataType;
import com.ivanceras.db.api.DeclaredQuery;
import com.ivanceras.db.api.IDatabase;
import com.ivanceras.db.api.ModelDef;
import com.ivanceras.db.api.Query;
import com.ivanceras.db.api.QueryBreakdown;
import com.ivanceras.db.api.SchemaTable;
import com.ivanceras.db.api.WindowFunction;
import com.ivanceras.db.model.ModelMetaData;
import com.ivanceras.db.shared.DAO;
import com.ivanceras.db.shared.Filter;
import com.ivanceras.db.shared.datatype.DBDataType;
import com.ivanceras.db.shared.datatype.GenericDataType;
import com.ivanceras.db.shared.exception.DBConnectionException;
import com.ivanceras.db.shared.exception.DatabaseException;
import com.ivanceras.fluent.sql.SQL;

public class DB_Cassandra extends DB_Jdbc implements IDatabase {

	public DB_Cassandra(DBConfig config) throws DBConnectionException {
		super(config);
	}

	@Override
	public SchemaTable[] getTableNames(String schema, String tablePattern,
			String[] includedSchema) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
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
	String getDriverClass() {
		return "org.apache.cassandra.cql.jdbc.CassandraDriver";
	}

	@Override
	String getDBUrl() {
		StringBuffer url = new StringBuffer("jdbc:cassandra://"+dbHost+":"+dbPort); 
		if(dbName != null){
			url.append("/"+dbName);
		}
		return url.toString();
	}

	@Override
	protected String getAutoIncrementColumnConstraint() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	protected boolean supportConstraints() {
		return false;
	}

	@Override
	protected boolean appendReturningColumnClause() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected String getAutoIncrementColumn(String realTableName)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getTableSchema(String tableName) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String[] getUniqueKeys(String schema, String tableName)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getRealTableName(String schema, String tableName)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ColumnDataType getColumnDetails(String schema, String tableName)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object getEquivalentJavaObject(Object record, ModelDef model, String column) {
		if(record == null) return null;
		int dtIndex = CStringUtils.indexOf(model.getAttributes(), column);
		if(dtIndex < 0){
			return record;
		}
		String[] dataTypes = model.getDataTypes();
		String dataType = null;
		if(dataTypes != null){
			dataType = dataTypes[dtIndex];
		}
		if(dataType != null && dataType.equals(GenericDataType.DATE)){
			try{
				Date date = new Date(((Timestamp)record).getTime());
				return date;
			}
			catch(ClassCastException e){
				Date date = new Date(((Date)record).getTime());
				return date;
			}
		}
		if(dataType != null && dataType.equals(GenericDataType.LONG) && record.getClass().equals(BigDecimal.class)){
			return ((BigDecimal)record).longValue();
		}
		else{
			return record;
		}
	}

	@Override
	public String getEquivalentDBDataType(String genDataType){
		if(genDataType.equals(GenericDataType.DATE)){
			return DBDataType.TIMESTAMP;
		}
		else if(genDataType.equals(GenericDataType.TIMESTAMP)){
			return DBDataType.TIMESTAMP;
		}
		else if(genDataType.equals(GenericDataType.STRING)){
			return DBDataType.VARCHAR;
		}
		else if(genDataType.equals(GenericDataType.INTEGER)){
			return DBDataType.INT;
		}
		else{
			return DBDataType.fromGenericType(genDataType);
		}
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
	protected String forceKeyword() {
		// TODO Auto-generated method stub
		return null;
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
	public boolean prependTableName(){
		return false;
	}

	@Override
	protected boolean  caseSensitive(){
		return true;
	}

	@Override
	protected boolean returnsSqlStatements(){
		return false;
	}
	
	@Override
	protected boolean namePrimaryKey(){
		return false;
	}

	@Override
	protected boolean supportComments() {
		return false;
	}

	@Override
	protected Object getEquivalentDBObject(Object record) {
		if(record == null){ return null;}
		if(record.getClass().equals(Date.class)){
			return ((Date)record).getTime();
		}
		else if(record.getClass().equals(UUID.class)){
			return record.toString();
		}
		else if(record.getClass().equals(Integer.class)){
			return record.toString();
		}
		return record;
	}

	@Override
	protected boolean supportPreparedStatement() {
		return false;
	}
	
	
	@Override
	protected boolean supportExistChecking() {
		return false;
	}
	
	@Override
	protected boolean supportResultSetMetaData() {
		return false;
	}

	@Override
	protected boolean useTableKeyWord() {
		return false;
	}

	@Override
	public boolean existModel(ModelDef model) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected ForeignKey getExportedKeys(String schema, String tablename)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ForeignKey getImportedKeys(String schema, String tablename)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean schemaExist(String arg0) throws DatabaseException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getTableComment(String table) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object getEquivalentJavaObject(Object record) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DAO[] select(ModelMetaData meta, Query query)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


}
