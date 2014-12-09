package com.ivanceras.db.server.core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.AccessControlException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ivanceras.commons.conf.DBConfig;
import com.ivanceras.db.api.ColumnDataType;
import com.ivanceras.db.api.DB_Rdbms;
import com.ivanceras.db.api.IDatabase;
import com.ivanceras.db.api.ModelDef;
import com.ivanceras.db.api.Pair;
import com.ivanceras.db.api.Query;
import com.ivanceras.db.model.ModelMetaData;
import com.ivanceras.db.shared.DAO;
import com.ivanceras.db.shared.Filter;
import com.ivanceras.db.shared.datatype.DataTypeGeneric;
import com.ivanceras.db.shared.exception.DBConnectionException;
import com.ivanceras.db.shared.exception.DataEntryException;
import com.ivanceras.db.shared.exception.DataUpdateException;
import com.ivanceras.db.shared.exception.DatabaseException;
import com.ivanceras.keyword.sql.Breakdown;
import com.ivanceras.keyword.sql.SQL;


public abstract class DB_Jdbc extends DB_Rdbms implements IDatabase {

	protected class ForeignKey {
		public String[] foreignTable;
		/** the table that is refered to */
		public String[] localColumn;
		/** the local column of this table that has a reference to the table */
		public String[] referedColumn;
		/**
		 * the column of the referred table that the local column is referring
		 * to
		 */
	}
	protected static Logger log = LogManager.getLogger(DB_Jdbc.class.getSimpleName());

	private static HashMap<String, String> propToHash(Properties prop) {
		HashMap<String, String> hash = new HashMap<String, String>();
		for (Entry<Object, Object> entry : prop.entrySet()) {
			hash.put((String) entry.getKey(), (String) entry.getValue());
		}
		return hash;
	}
	private static HashMap<String, String> readPropertyFile(
			String etcCustomConfigurationFile) throws IOException {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(etcCustomConfigurationFile));
			log.debug("Found custom config file: ["
					+ etcCustomConfigurationFile + "]");
		} catch (FileNotFoundException e) {
			String msg = "No custom config file: " + etcCustomConfigurationFile;
			log.debug(msg);
			log.info("Using hard coded config.");
		} catch (AccessControlException e) {
			throw e;
		} catch (IOException e) {
			log.error("Error in reading property file");
			e.printStackTrace();
			throw new IOException(e);
		}
		return propToHash(prop);
	}
	protected Connection connection = null;
	private String currentStatement;

	protected DBConfig dbConfig;

	protected String dbHost;
	protected String dbName;
	protected String dbPassword;
	protected String dbPort;
	protected String dbSchema;
	protected String dbUser;
	private boolean debugSql = false;

	protected boolean initialized = false;

	private boolean isTransacted = false;

	private ModelMetaData meta;

	public DB_Jdbc(){

	}

	public DB_Jdbc(DBConfig config) throws DBConnectionException {
		this.dbConfig = config; // shall we overwite the config with the custom
		// property file or not?
		try {
			String overwriteFile = config.getOverwriteFile();
			if (overwriteFile != null) {
				HashMap<String, String> etc = readPropertyFile(overwriteFile);
				config.overwrite(etc);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new DBConnectionException(e.getMessage());
		}
		this.dbHost = config.getDbHost();
		this.dbPort = config.getDbPort();
		this.dbName = config.getDbName();
		this.dbUser = config.getDbUser();
		this.dbPassword = config.getDbPassword();
		if(config.doConnection){
			this.init();
		}
	}

	abstract protected boolean appendReturningColumnClause();

	public void beginTransaction() throws DatabaseException {
		try {
			if (connection.getAutoCommit() == true) {
				connection.setAutoCommit(false);
				isTransacted = false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException();
		}
	}

	@Override
	public void close() throws DatabaseException {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}
	}


	public void commitTransaction() throws DatabaseException {
		try {
			connection.commit();
			connection.setAutoCommit(true);
			isTransacted = true;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException();
		}
	}

	private Object[] correctParametersType(Object[] parameters) {
		if (parameters == null) {
			return null;
		}
		Object[] correctedParameters = new Object[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			correctedParameters[i] = getEquivalentDBObject(parameters[i]);
		}
		return correctedParameters;
	}

	@Override
	public boolean createModel(ModelDef model) throws DatabaseException {
		return createModel(model,true);
	}
	public boolean createModel(ModelDef model, boolean ensureSchemaExist) throws DatabaseException {
		SQL sql = buildCreateTableStatement(model);
		if(ensureSchemaExist){
			ensureSchemaExist(model.getNamespace());
		}
		boolean ret = executeUpdateSQL(sql) > 0;
		if (supportComments()) {
			createModelComment(model);
			createModelAttributeComment(model);
		}
		return ret;
	}

	private boolean createModelAttributeComment(ModelDef model)
			throws DatabaseException {
		SQL[] sql = buildColumnCommentStatement(model);
		if (sql != null) {
			for (SQL ql : sql) {
				if (ql != null) {
					executeUpdateSQL(ql);
				}
			}
			return true;
		}
		return false;
	}


	private boolean createModelComment(ModelDef model) throws DatabaseException {
		SQL sql = buildTableCommentStatement(model);
		if (sql != null) {
			return executeUpdateSQL(sql) > 0;
		}
		return false;
	}

	@Override
	public boolean createSchema(String schema) throws DatabaseException {
		SQL sql = buildCreateSchemaStatement(schema);
		return executeUpdateSQL(sql) > 0;
	}

	public void debugSql(boolean debugsql) {
		this.debugSql = debugsql;
	}

	@Override
	public int delete(ModelDef model, Filter[] filters)
			throws DatabaseException {
		SQL sql = buildDeleteStatement(meta, model, filters);
		String msg = "\nAs safety precaution a delete operation without filter is not permitted in this API"
				+ " coz it will delete all the records in these table ["
				+ model.getModelName()
				+ "]. "
				+ "\nIf that is what you intended to do, use the truncate method instead.";
		if (filters == null) {
			throw new DatabaseException(msg);
		} else if (filters.length == 0) {
			throw new DatabaseException(msg);
		}
		return executeUpdateSQL(sql);
	}

	@Override
	public boolean drop(ModelDef model, boolean forced)
			throws DatabaseException {
		SQL sql = buildDropStatement(model, forced);
		return executeUpdateSQL(sql) >= 0;
	}

	@Override
	public boolean dropNamespace(String schema, boolean forced)
			throws DatabaseException {
		SQL sql = buildDropSchemaStatement(schema, forced);
		return executeUpdateSQL(sql) >= 0;
	}

	@Override
	public int empty(ModelDef model, boolean forced) throws DatabaseException {
		SQL sql = buildEmptyTableStatement(model, forced);
		return executeUpdateSQL(sql);
	}

	public boolean ensureSchemaExist(String schema) throws DatabaseException{
		if(schemaExist(schema)){
			return true;
		}else{
			createSchema(schema);
			return true;
		}
	}

	public Object executeInsertSQL(SQL sql, boolean returnValues) throws DatabaseException {
		Breakdown bk = sql.build();
		Statement pstmt = getPreparedStatement(bk.getSql(), bk.getParameters(), returnValues);
		try{
			if(supportPreparedStatement()){
				if(returnValues){
					((PreparedStatement)pstmt).executeUpdate();//was executeQuery
					logSQL(pstmt, bk.getSql(), bk.getParameters(), false);
					ResultSet genKeys = pstmt.getGeneratedKeys();
					if (genKeys.next() && returnValues) {
						Object ret = genKeys.getObject(1);
						log.debug("Returning something.."+ret);
						return ret;
					}
					else{
						log.debug("Nothing is returned..");
					}
				}else{//no return values
					((PreparedStatement)pstmt).executeUpdate();
					logSQL(pstmt, bk.getSql(), bk.getParameters(), false);
				}
			}
			else{
				pstmt.execute(bk.getSql());//Prone to SQL injection if the Database driver dont support PreparedStatements
				logSQL(pstmt, bk.getSql(), bk.getParameters(), false);
			}
		}catch(SQLException e){
			logSQL(pstmt, bk.getSql(), bk.getParameters(), true);
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}
		return true;

	}

	@Override
	public DAO[] select(String sql, Object[] parameters)
			throws DatabaseException {
		return executeSelect(sql, parameters);
	}

	/**
	 * Execute generic SQL statement
	 * @param sql
	 * @param parameters
	 * @return
	 * @throws DatabaseException
	 */
	public DAO[] executeSelect(String sql, Object[] parameters)
			throws DatabaseException {
		ResultSet rs = executeSelectSQL(sql, parameters, null, false);
		return resultSetToDAO(rs, null);
	}

	private DAO[] executeSelectAll(SQL sql, Map<String, Pair[]> renamedColumns)
			throws DatabaseException {
		Breakdown bk = sql.build();
		ResultSet rs = executeSelectSQL(bk.getSql(), correctParametersType(bk.getParameters()));
		return resultSetToDAO(rs, renamedColumns);
	}

	public ResultSet executeSelectSQL(String sql, Object[] parameters)
			throws DatabaseException {
		return executeSelectSQL(sql, parameters, null, false);
	}

	public ResultSet executeSelectSQL(String sql, Object[] parameters,
			Integer fetchSize, boolean useCursor) throws DatabaseException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = connection.prepareStatement(sql);
			if (useCursor) {
				log.debug("USing cursor of size: " + fetchSize);
				pstmt.setFetchSize(fetchSize);// Turn use of the cursor ON
			}
			if (parameters != null) {
				int cnt = 0;
				for (int i = 0; i < parameters.length; i++) {
					cnt++;
					pstmt.setObject(cnt, parameters[i]);
				}
			}
			rs = pstmt.executeQuery();
			logSQL(pstmt, sql, parameters, false);
			currentStatement = pstmt.toString();
			if (useCursor) {
				pstmt.setFetchSize(0);// turn the cursor OFF
			}
		} catch (SQLException e) {
			logSQL(pstmt, sql, parameters, true);
			e.printStackTrace();
			throw new DatabaseException("SQL: "+sql+"\n"+e.getMessage());
		}
		return rs;
	}

	public int executeUpdateSQL(SQL sql)
			throws DatabaseException {
		if(sql != null){
			Breakdown bk = sql.build();
			Statement pstmt = null;
			try {
				pstmt = getPreparedStatement(bk.getSql(), bk.getParameters(), false);
				int ret = ((PreparedStatement)pstmt).executeUpdate();
				logSQL(pstmt, bk.getSql(), bk.getParameters(), false);
				return ret;
			} catch (SQLException e) {
				logSQL(pstmt, bk.getSql(), bk.getParameters(), true);
				e.printStackTrace();
				throw new DataUpdateException(e.getMessage());
			}
		}else{
			return -1;
		}
	}

	@Override
	public abstract boolean existModel(ModelDef model);

	abstract protected String getAutoIncrementColumn(String realTableName)
			throws DatabaseException;

	/**
	 * Different implementation accross database
	 * 
	 * @return
	 */
	protected abstract String getAutoIncrementColumnConstraint();

	protected abstract ColumnDataType getColumnDetails(String schema,
			String tableName) throws DatabaseException;

	@Override
	public DBConfig getConfig() {
		return dbConfig;
	}

	public Connection getDBConnection(String dbUrl, String dbUser,
			String dbPassword) throws DBConnectionException {
		Connection c = null;
		try {
			c = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
		} catch (SQLException se) {
			log.error("Unable to connect to database " + dbUrl);
			log.error(se.getMessage());
			se.printStackTrace();
			throw new DBConnectionException("Y U NO CONNECT DB? ლ(ಠ益ಠლ)");
		}
		return c;
	}


	abstract String getDBUrl();

	abstract String getDriverClass();


	public String getEquivalentGeneralDataType(String dbDataType) {
		return DataTypeGeneric.fromDBDataType(dbDataType);
	}

	protected abstract Object getEquivalentJavaObject(Object record);

	abstract protected Object getEquivalentJavaObject(Object record,
			ModelDef model, String column);

	protected abstract ForeignKey getExportedKeys(String schema, String tablename) throws DatabaseException;

	protected abstract ForeignKey getImportedKeys(String schema, String tablename) throws DatabaseException;

	public String getLastExecuteStatement() {
		return currentStatement;
	}


	@Override
	public ModelMetaData getModelMetaDataDefinition() throws DatabaseException {
		return meta;
	}
	/**
	 * Get the SQL statements based on different cases
	 *   DB does not support PrepareStatement
	 *   DB does not allow returning of Generated Keys
	 * @param sql
	 * @param parameters
	 * @return
	 * @throws DatabaseException
	 */
	public Statement getPreparedStatement(String sql,
			Object[] parameters, boolean returnValues) throws DatabaseException {
		Statement stmt = null;
		try {
			if(supportPreparedStatement()){
				if (appendReturningColumnClause() && returnValues) {
					stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
				} else {
					stmt = connection.prepareStatement(sql);
				}
				if (parameters != null) {
					for (int i = 0; i < parameters.length; i++) {
						((PreparedStatement)stmt).setObject(i + 1,getEquivalentDBObject(parameters[i]));
					}
				}
			}else{//no substition of parameters
				stmt = connection.createStatement();
				return stmt;
			}
		} catch (SQLException e) {
			logSQL(stmt, sql, parameters, true);
			e.printStackTrace();
			throw new DataEntryException(e.getMessage());
		}
		return stmt;
	}

	//TODO: convert this to database specific query
	protected String[] getPrimaryKeysList(String schema, String tableName)
			throws DatabaseException {
		try {
			DatabaseMetaData dmd = connection.getMetaData();
			ResultSet rs = dmd.getPrimaryKeys(dbName, schema, tableName);
			List<String> columnList = new ArrayList<String>();
			try {
				while (rs.next()) {
					columnList.add(rs.getString("COLUMN_NAME"));
				}
			} finally {
				rs.close();
			}
			String[] primaryKeys = columnList.toArray(new String[columnList
			                                                     .size()]);
			return primaryKeys;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException();
		}
	}

	protected abstract String getRealTableName(String schema, String tableName)
			throws DatabaseException;

	abstract protected String getTableSchema(String tableName)
			throws DatabaseException;

	abstract protected String[] getUniqueKeys(String schema, String tableName)
			throws DatabaseException;

	public boolean init() throws DBConnectionException {
		if (initialized) {
			log.info("initialized!");
			return true;
		}
		try {
			if (isDriverAvailable(getDriverClass())) {
				log.info("Using database: " + getDBUrl());
				connection = getDBConnection(getDBUrl(), dbUser, dbPassword);
				if (connection == null) {
					String errorMsg = "Unable to connect to Database";
					log.error(errorMsg);
					log.error(errorMsg);
					throw new DBConnectionException(errorMsg);
				} else {
					initialized = true;
					log.info("Connected!..");
					return true;
				}
			} else {
				log.error("No driver available");
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public DAO insert(DAO dao, ModelMetaData meta, ModelDef model, Query query) throws DatabaseException {
		String autoIncrementColumn = model.getGeneratedAttribute();
		autoIncrementColumn = getDBElementName(model, autoIncrementColumn);
		SQL sql = buildInsertStatement(dao, meta, model, query);
		Object ret = executeInsertSQL(sql, true);
		if(ret != null){

			dao.set_Value(IDatabase.RETURN_FROM_INSERT, ret);
		}
		return dao;

	}

	@Override
	public boolean isClosed() {
		try {
			if (connection != null) {
				return connection.isClosed();
			} else {
				log.error("Unable to create database connection");
				return true;
			}
		} catch (SQLException sx) {
			sx.printStackTrace(); // Something is wrong with the connection,
			// let's report it as closed
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException sx2) {
				sx2.printStackTrace();
			}
			return true;
		}
	}

	/**
	 * based on simple query
	 * http://stackoverflow.com/questions/5276816/how-to-determine
	 * -if-java-sql-connection-is-valid-with-postgresql-jdbc4
	 */
	@Override
	public boolean isConnected() {
		try {
			ResultSet rs = executeSelectSQL("SELECT 1", null);
			if (rs.next()) {
				return true;
			}
			return false;
		} catch (DatabaseException e) {
			e.printStackTrace();
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

	protected boolean isDriverAvailable(String driverClass)
			throws ClassNotFoundException {
		Class.forName(driverClass);
		return true;
	}

	public boolean isTransacted() throws DatabaseException {
		return isTransacted;
	}

	void logSQL(Statement pstmt, String sql, Object[] parameters, boolean error){
		if(debugSql){
			//System.out.println(""+pstmt.toString());
		}
		if(error){
			System.out.println(""+pstmt.toString());
		}
		if (returnsSqlStatements()) {
			log.debug(pstmt.toString());
		} else {
			log.debug(sql+" "+(parameters != null ? Arrays.asList(parameters) : parameters));
		}
	}

	@Override
	protected boolean namePrimaryKey() {
		return true;
	}

	@Override
	public boolean renameModel(ModelDef model, String newName)
			throws DatabaseException {
		SQL sql = buildRenameModel(model, newName);
		executeUpdateSQL(sql);
		return true;
	}

	@Override
	public boolean reset() {
		boolean rolledback = false;
		try {
			if (connection.getAutoCommit() == false) {
				connection.rollback();
				connection.setAutoCommit(true);
				rolledback = true;
			}
		} catch (SQLException sx) {
			sx.printStackTrace();
		}
		return rolledback;
	}

	private DAO[] resultSetToDAO(ResultSet rs,  Map<String, Pair[]> renamedColumns) throws DatabaseException{
		try {
			if (rs != null) {
				ResultSetMetaData md = rs.getMetaData();
				int columnCount = md.getColumnCount();
				String modelName = null;
				List<DAO> recordObjs = new ArrayList<DAO>();
				while (rs.next()) {
					HashMap<String, Object> row = new HashMap<String, Object>();
					DAO dao = new DAO(modelName);
					dao.setRenamedFields(renamedColumns);
					if (supportResultSetMetaData()) {
						for (int i = 0; i < columnCount; i++) {
							String columnName = md.getColumnName(i + 1).replace("\"", "").toLowerCase();
							Object dbRecord = rs.getObject(i + 1);
							Object record = getEquivalentJavaObject(dbRecord);
							row.put(columnName, record);
						}
					}
					dao.setProperties(row);
					recordObjs.add(dao);
				}
				return recordObjs.toArray(new DAO[recordObjs.size()]);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException();
		}
		return null;
	}

	protected boolean returnsSqlStatements() {
		return true;
	}

	public void rollbackTransaction() throws DatabaseException {
		try {
			if (connection.getAutoCommit() == true)
				; // autocommit
			connection.rollback();
			connection.setAutoCommit(true);
			isTransacted = true;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException();
		}
	}

	public abstract boolean schemaExist(String schema) throws DatabaseException;

	@Override
	public void search(Query query, String keyword) {
		//		SearchBuilder sb = new SearchBuilder(query);
		//		sb.keyword(keyword);

	}

	@Override
	public DAO[] select(ModelMetaData meta, Query query)
			throws DatabaseException {
		SQL sql = buildSQL(meta, query, true);
		return executeSelectAll(sql, query.getRenamedFields());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends DAO> T[] select(SQL sql, Map<String, Pair[]> renamedColumns)
			throws DatabaseException {
		return (T[]) executeSelectAll(sql, renamedColumns);
	}

	@Override
	public boolean setPrimaryConstraint(ModelDef model)
			throws DatabaseException {
		SQL sql = buildPrimaryContraintStatement(model);
		return executeUpdateSQL(sql) > 0;
	}

	@Override
	public boolean setForeignConstraint(ModelDef model)
			throws DatabaseException {
		if (model.getHasOne() != null && model.getHasOne().length > 0) {
			SQL sql = buildForeignContraintStatement(model);
			return executeUpdateSQL(sql) > 0;
		}
		return false;
	}

	@Override
	public void setModelMetaDataDefinition(ModelMetaData metaData1){
		meta = metaData1;
	}

	protected boolean supportComments() {
		return true;
	}

	@Override
	protected boolean supportConstraints() {
		return true;
	}

	@Override
	protected boolean supportExistChecking() {
		return true;
	}

	@Override
	protected boolean supportPreparedStatement() {
		return true;
	}

	protected boolean supportResultSetMetaData() {
		return true;
	}

	@Override
	public DAO update(DAO dao, ModelDef model, Filter[] filters) throws DatabaseException {
		SQL sql1 = buildUpdateStatement(meta, dao, model, filters);
		int ret = executeUpdateSQL(sql1);
		if (ret > 0) {
			return dao;
		} else {
			return null;
		}
	}

	@Override
	protected boolean useTableKeyWord() {
		return true;
	}


}
