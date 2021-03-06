package com.ivanceras.db.server.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.postgresql.PGConnection;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;
import org.postgresql.util.PGobject;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivanceras.commons.conf.DBConfig;
import com.ivanceras.commons.strings.CStringUtils;
//import com.ivanceras.db.api.Aggregate;
import com.ivanceras.db.api.ColumnDataType;
import com.ivanceras.db.api.IDatabase;
import com.ivanceras.db.api.IDatabaseDev;
import com.ivanceras.db.api.LiteralString;
import com.ivanceras.db.api.ModelDef;
import com.ivanceras.db.api.Query;
import com.ivanceras.db.api.Pair;
import com.ivanceras.db.model.ModelMetaData;
import com.ivanceras.db.shared.DAO;
import com.ivanceras.db.shared.Filter;
import com.ivanceras.db.shared.Filter.Connector;
import com.ivanceras.db.shared.Filter.Equality;
import com.ivanceras.db.shared.Order;
import com.ivanceras.db.shared.datatype.DataTypeGeneric;
import com.ivanceras.db.shared.exception.DBConnectionException;
import com.ivanceras.db.shared.exception.DatabaseException;
import com.ivanceras.keyword.sql.SQL;

import static com.ivanceras.keyword.sql.SQLStatics.*;

public class DB_PostgreSQL extends DB_Jdbc implements IDatabase, IDatabaseDev{


	public DB_PostgreSQL(){

	}

	public DB_PostgreSQL(DBConfig config)
			throws DBConnectionException {
		super(config);
	}

	@Override
	protected String getDBUrl(){
		if(dbConfig.isSslConnection()){
			return "jdbc:postgresql://"+dbHost+":"+dbPort+"/"+dbName+"?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
		}else{
			return "jdbc:postgresql://"+dbHost+":"+dbPort+"/"+dbName;
		}
	}

	@Override
	String getDriverClass() {
		return "org.postgresql.Driver";
	}

	/**
	 * Integer serial, BIGSERIAL for Long or BigInteger
	 * @return
	 */
	protected String getAutoIncrementColumnConstraint(){
		return "SERIAL NOT NULL";
	}

	protected String getRealTableName(String schema, String tableName) throws DatabaseException{
		StringBuffer sql = new StringBuffer();

//		SQL sql1 = SELECT("c.relname")
//				.FROM("pg_catalog.pg_class").AS("c")
//				.FIELD("pg_class.relname")
//				.LEFT_JOIN("pg_catalog.pg_namespace").AS("n")
//				.ON("n.oid","c.relnamespace")
//				.WHERE(LOWER("c.relname")).EQUAL_TO(LOWER(tableName));

		sql.append("SELECT "+
				" c.relname as \"Table\""+
				" FROM"+
				" pg_catalog.pg_class c" +
				" LEFT JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace"+
				" WHERE lower(c.relname) = lower(?)");
		if(schema != null){
			sql.append(" AND n.nspname = lower(?)");
		}
		sql.append(" ORDER BY c.relname");
		Object[] parameters = null;
		if(schema != null){
			parameters = new Object[2];
			parameters[0] = tableName;
			parameters[1] = schema;
		}else{
			parameters = new Object[1];
			parameters[0] = tableName;
		}

		try {
			ResultSet rs = executeSelectSQL(sql.toString(), parameters);
			if(rs != null){
				if(rs.next()){
					String realTableName = rs.getString(1);
					return realTableName;
				}
			}
		} catch (SQLException e) {
			//System.err.println(sql+"["+(parameters!=null?Arrays.asList(parameters):"")+"]");
			e.printStackTrace();
			throw new DatabaseException();
		}
		return null;

	}

	@Override
	public String getTableComment(String table) throws DatabaseException{
		String sql = "SELECT pg_catalog.obj_description('"+table+"'::regclass, 'pg_class')";
		ResultSet rs = executeSelectSQL(sql, null);
		try {
			if(rs != null && rs.next()){
				String comment = rs.getString(1);
				return comment;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}
		return null;
	}


	@Override
	protected String getTableSchema(String tableName) throws DatabaseException {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT "+
				" n.nspname as \"Table\""+
				" FROM"+
				" pg_catalog.pg_class c" +
				" LEFT JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace"+
				" WHERE lower(c.relname) = lower(?)");
		sql.append(" ORDER BY c.relname");
		Object[] parameters = null;
		parameters = new Object[1];
		parameters[0] = tableName.replace("\"", "");
		try {
			ResultSet rs = executeSelectSQL(sql.toString(), parameters);
			if(rs != null){
				if(rs.next()){
					String realTableName = rs.getString(1);
					return realTableName;
				}
			}
		} catch (SQLException e) {
			System.err.println(sql+"["+(parameters!=null?Arrays.asList(parameters):"")+"]");
			e.printStackTrace();
			throw new DatabaseException();
		}
		return null;

	}

	protected ColumnDataType getColumnDetails(String schema, String tableName) throws DatabaseException{
		String sql = 
				"SELECT "+
						" c.relname as \"Table\","+
						" a.attname as \"Column\"," +
						" pg_catalog.format_type(a.atttypid, a.atttypmod) as \"Datatype\""+
						" FROM"+
						" pg_catalog.pg_attribute a"+
						" LEFT JOIN pg_catalog.pg_class c"+
						" ON c.oid = a.attrelid"+
						" LEFT JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace" +
						" WHERE lower(c.relname) = lower(?)"+
						" AND n.nspname = lower(?)"+
						" AND a.attnum > 0"+
						" AND NOT a.attisdropped";
		Object[] parameters = new Object[2];
		parameters[0] = tableName;
		parameters[1] = schema;
		try {
			ResultSet rs = executeSelectSQL(sql, parameters);
			List<String> columnList = new ArrayList<String>();
			List<String> dataTypeList = new ArrayList<String>();
			List<String> commentList = new ArrayList<String>();
			Map<String, String> tableComments = getTableColumnComments(tableName, schema);
			if(rs != null){
				while(rs.next()){
					String columnName = rs.getString("Column");
					String dataType = rs.getString("DataType");
					columnList.add(columnName);
					dataTypeList.add(getEquivalentGeneralDataType(dataType));
					String comment = tableComments.get(columnName);
					commentList.add(comment);
				}
				ColumnDataType columns = new ColumnDataType();
				columns.setColumns(columnList.toArray(new String[columnList.size()]));
				columns.setDataTypes(dataTypeList.toArray(new String[dataTypeList.size()]));
				columns.setComments(commentList.toArray(new String[columnList.size()]));
				return columns;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ResultSet getColumns(String schema, String tABLE_NAME) {
		DatabaseMetaData dmd;
		ResultSet rs=null;
		try {
			dmd = getMetaData();
			rs = dmd.getColumns(null, schema, tABLE_NAME.toLowerCase(), null); 
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}


	public ResultSet getPrimaryKeys(String tABLE_NAME) {

		ResultSet rs=null;
		try {
			DatabaseMetaData dmd = getMetaData();
			rs = dmd.getPrimaryKeys(dbName, dbSchema, tABLE_NAME.toLowerCase());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}


	@Override
	public boolean schemaExist(String schema) throws DatabaseException{
		String sql = "SELECT schema_name FROM information_schema.schemata WHERE schema_name = '"+schema+"'";
		ResultSet rs = executeSelectSQL(sql, null);
		try {
			if(rs != null && rs.next()){
				String existingSchema = rs.getString(1);
				if(existingSchema != null){
					return true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}
		return false;
	}

	@Override
	protected String[] getUniqueKeys(String schema, String tableName) throws DatabaseException {
		String sql =" select "+
				" t.relname as table_name,"+
				" i.relname as index_name,"+
				" a.attname as column_name"+
				" from"+
				" pg_class t,"+
				" pg_class i,"+
				" pg_index ix,"+
				" pg_attribute a"+
				" where"+
				" t.oid = ix.indrelid"+
				" and i.oid = ix.indexrelid"+
				" and a.attrelid = t.oid"+
				" and a.attnum = ANY(ix.indkey)"+
				" and t.relkind = 'r'"+
				" and t.relname = ?"+
				" and indisunique = 't'"+
				" and indisprimary != 't'"+
				" order by"+
				" t.relname,"+
				" i.relname";
		Object[] parameters = new Object[1];
		parameters[0] = tableName;
		ResultSet rs = executeSelectSQL(sql, parameters);
		List<String> uniqueAttributes = new ArrayList<String>();
		try {
			while(rs.next()){
				uniqueAttributes.add(rs.getString("column_name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return uniqueAttributes.toArray(new String[uniqueAttributes.size()]);

	}

	private DatabaseMetaData getMetaData() throws SQLException {
		return connection.getMetaData();
	}

	@Override
	public Pair[] getTableNames(String owner, String tablePattern, String[] includedSchema) throws DatabaseException{
		System.out.println("Extracting table names which woud match ["+tablePattern+"]");
		SQL sql1 = SELECT("table_name", "table_schema")
				.FROM("information_schema.tables")
				.WHERE("table_schema").NOT_IN("pg_catalog", "information_schema")
				.ORDER_BY("table_name");

		DAO[] daoList =  select(sql1, null);

		Pair[] schema_tableNames = new Pair[daoList.length];
		for(int i = 0; i < schema_tableNames.length; i++){
			DAO dao = daoList[i];
			String schema = (String) dao.get_Value("table_schema");
			String tableName = (String) dao.get_Value("table_name");
			schema_tableNames[i] = new Pair(schema, tableName);
		}
		return schema_tableNames;	
	}

	@Override
	public long writeToBlob(byte[] buf) throws DatabaseException{
		try {
			LargeObjectManager lobj = ((PGConnection)connection).getLargeObjectAPI();
			long oid = lobj.createLO(LargeObjectManager.READ | LargeObjectManager.WRITE);
			LargeObject obj = lobj.open(oid, LargeObjectManager.WRITE);
			obj.write(buf);
			obj.close();
			return oid;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Unable to write Binary Large Object");
		}
	}

	@Override
	public long writeToBlob(String filename) throws DatabaseException
	{
		try
		{
			LargeObjectManager lobj = ((PGConnection)connection).getLargeObjectAPI();
			long oid = lobj.createLO(LargeObjectManager.READ | LargeObjectManager.WRITE);
			LargeObject obj = lobj.open(oid, LargeObjectManager.WRITE);
			File file = new File(filename);
			FileInputStream fis=null;
			try
			{
				fis = new FileInputStream(file);
			} catch (FileNotFoundException e) { e.printStackTrace(); }

			if(null!=fis)
			{
				// Copy the data from the file to the large object
				byte buf[] = new byte[2048];

				try
				{
					int s = 0;
					while ((s = fis.read(buf, 0, 2048)) > 0) { obj.write(buf, 0, s); }
				} catch (IOException e) { e.printStackTrace(); }
			}


			// Close the large object
			obj.close();
			fis.close();
			return oid;
		} catch (SQLException e)
		{
			e.printStackTrace();
			throw new DatabaseException("Unable to write Binary Large Object");
		} catch (IOException e) {
			e.printStackTrace();
			throw new DatabaseException("Unable to write Binary Large Object");
		}
	}



	@Override
	public byte[] getBlob(long oid) throws DatabaseException 
	{
		try{
			//removed transaction management for better controll at the calling Services
			LargeObjectManager lobj = ((PGConnection)connection).getLargeObjectAPI();
			LargeObject obj = lobj.open(oid, LargeObjectManager.READ);
			byte buff[] = new byte[obj.size()];
			obj.read(buff, 0, obj.size());
			obj.close();
			return buff;
		}
		catch(SQLException e){
			e.printStackTrace();
			throw new DatabaseException("Unable to retrieve Binary Large Object");
		}
	}

	@Override
	public void writeToOutputStream(Long oid, OutputStream out) throws DatabaseException {
		try{
			LargeObjectManager lobj = ((PGConnection)connection).getLargeObjectAPI();
			LargeObject obj = lobj.open(oid, LargeObjectManager.READ);
			byte buff[] = new byte[obj.size()];
			obj.read(buff, 0, obj.size());
			try { out.write(buff); }
			catch (IOException e) { e.printStackTrace(); }
		}
		catch(SQLException e){
			e.printStackTrace();
			throw new DatabaseException("Unable to retrieve Binary Large Object");
		}

	}

	@Override
	protected String getAutoIncrementColumn(String realTableName) throws DatabaseException {
		try {
			String sql = "SELECT attname, adsrc as default_value" +
					" FROM pg_attrdef pad, pg_attribute pat, pg_class pc" +
					" WHERE pc.relname = ? " +
					" AND pc.oid=pat.attrelid" +
					" AND pat.attrelid=pad.adrelid AND pat.attnum=pad.adnum" +
					" AND adsrc LIKE 'nextval(%)'";
			String[] parameters = new String[1];
			parameters[0] = realTableName;
			ResultSet rs = executeSelectSQL(sql, parameters);
			int cnt = 0;
			String columnName = null;
			while(rs.next()){
				columnName = rs.getString("attname");
				cnt++;
			}
			if(cnt > 1){
				try{
					throw new DatabaseException("Table ["+realTableName+"] Should only have one generated column...");
				}
				catch(DatabaseException e){
					e.printStackTrace();
				}
			}
			return columnName;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Unable to find the auto increment column");
		}
	}

	protected String getInterval(int inc, String unit){
		return "INTERVAL '"+inc+" "+unit+"'";
	}


	@Override
	protected String forceKeyword() {
		return "CASCADE";
	}

	@Override
	protected boolean appendReturningColumnClause() {
		return true;
	}

	@Override
	protected Object getEquivalentJavaObject(Object record, ModelDef model, String column) {
		if(record == null) return null;
		int dtIndex = -1; 
		if(model != null){
			dtIndex = CStringUtils.indexOf(model.getAttributes(), column);
		}
		if(dtIndex < 0){
			return record;
		}
		String[] dataTypes = model.getDataTypes();
		String dataType = null;
		if(dataTypes != null){
			dataType = dataTypes[dtIndex];
		}
		if(dataType != null && dataType.equals(DataTypeGeneric.DATE)){
			try{
				System.err.println("Casting "+column+" of dataType: "+dataType+"value: "+record+" to date");
				Date date = new Date(((Timestamp)record).getTime());
				return date;
			}
			catch(ClassCastException e){
				Date date = new Date(((Date)record).getTime());
				return date;
			}
		}
		if(dataType != null && dataType.equals(DataTypeGeneric.LONG) && record.getClass().equals(BigDecimal.class)){
			return ((BigDecimal)record).longValue();
		}
		else{
			return record;
		}
	}


	@Override
	protected boolean useSchema() {
		return true;
	}

	@Override
	protected String getStorageEngine() {
		return "";
	}

	@Override
	//select relname, ( select relname from pg_class where oid = pg_inherits.inhparent ) as parent from pg_class
	public String[] getSubClasses(String tableName) throws DatabaseException{
		try {
			String sql = " select relname, " +
					"( select relname from pg_class where oid = pg_inherits.inhrelid ) as subclass" +
					" from pg_inherits" +
					" left join pg_class on pg_class.oid = pg_inherits.inhparent" +
					" where relname = ? ";
			String[] parameters = new String[1];
			parameters[0] = tableName;
			ResultSet rs = executeSelectSQL(sql, parameters);
			List<String> subClassList = new ArrayList<String>();
			while(rs.next()){
				String subclass = rs.getString("subclass");
				subClassList.add(subclass);
			}
			return subClassList.toArray(new String[subClassList.size()]);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Unable to find the auto increment column");
		}
	}

	@Override
	public String getParentClass(String tableName) throws DatabaseException{
		try {
			String sql = "select relname, " +
					" ( select relname from pg_class where oid = pg_inherits.inhparent ) as parent" +
					" from pg_class " +
					" left join pg_inherits " +
					" on pg_class.oid = pg_inherits.inhrelid" +
					" where relname = ?";
			String[] parameters = new String[1];
			parameters[0] = tableName;
			ResultSet rs = executeSelectSQL(sql, parameters);
			if(rs.next()){
				String parentTable = rs.getString("parent");
				return parentTable;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Unable to find the auto increment column");
		}
		return null;
	}

	public Map<String, String> getTableColumnComments(String tableName, String schema) throws DatabaseException{
		String sql = "select pa.attname as column, pd.description as comment" +
				" from pg_description pd, pg_class pc, pg_attribute pa, pg_namespace n" +
				" where" +
				" pa.attrelid = pc.oid" +
				" and pd.objoid = pc.oid" +
				" and pd.objsubid = pa.attnum" +
				" and n.oid = pc.relnamespace" +
				" and pc.relname = ?" +
				" and nspname = ?";
		String[] parameters = new String[]{tableName, schema};
		ResultSet rs;
		try {
			rs = executeSelectSQL(sql, parameters);
			Map<String, String> map = new HashMap<String, String>();
			while(rs.next()){
				String column = rs.getString("column");
				String comment = rs.getString("comment");
				map.put(column, comment);
			}
			return map;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Unable to find the auto increment column");
		}
	}

	public String getTableComment(String tableName, String schema) throws DatabaseException{
		String sql = "select pg_description.description as comment from pg_description " +
				" inner join pg_class" +
				" on pg_class.oid = pg_description.objoid " +
				" inner join pg_namespace on pg_namespace.oid = pg_class.relnamespace" +
				" where" +
				" pg_class.relname = ? and" +
				" pg_namespace.nspname = ? and" +
				" objsubid = 0";
		String[] parameters = new String[]{tableName, schema};
		ResultSet rs;
		try {
			rs = executeSelectSQL(sql, parameters);
			Statement stm = rs.getStatement();
			//			System.out.println("SQL: "+stm.toString());
			if(rs.next()){
				String comment = rs.getString("comment");
				return comment;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Unable to find the auto increment column");
		}
		return null;
	}


	@Override
	public Query buildSubClassTableQuery(ModelDef model) {
		ModelDef subclassModel = new ModelDef();
		subclassModel.setModelName("pg_class");
		String[] subclassColumn = {"relname"};
		subclassModel.setAttributes(subclassColumn);
		Query subclassQuery = new Query();
		subclassQuery.selectFromModel(subclassModel);
		Filter subclassFilter = new Filter("oid", Equality.EQUAL, new LiteralString(model.getModelName()+"."+"tableoid"));
		subclassQuery.filter(subclassFilter);
		return subclassQuery;
	}


	@Override
	public boolean prependTableName(){
		return true;
	}

	@Override
	protected boolean  caseSensitive(){
		return false;
	}

	@Override
	public boolean existModel(ModelDef model) {
		String sql = 
				"SELECT EXISTS ( " +
						"\n\tSELECT * FROM INFORMATION_SCHEMA.TABLES " +
						"\n\tWHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? )";
		Object[] parameters = new Object[2];
		String namespace = model.getNamespace() == null ? "public" : model.getNamespace();
		parameters[0] =	 namespace;
		parameters[1] =  model.getTableName();
		try {
			ResultSet rs = executeSelectSQL(sql, parameters);
			if(rs != null && rs.next()){
				return rs.getBoolean(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean isValid() {
		if(getVersion() != null){
			return true;
		}
		return false;
	}

	private String getVersion(){
		String sql = 
				"SELECT version()";
		try {
			ResultSet rs = executeSelectSQL(sql, null);
			if(rs != null && rs.next()){
				return rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		return null;
	}


	@Override
	protected ForeignKey getImportedKeys(String schema, String tablename) throws DatabaseException{

		/**
		 * from 	http://stackoverflow.com/questions/1152260/postgres-sql-to-list-table-foreign-keys		
		 */

		String sql = 
				"SELECT " +
						"\n 	DISTINCT ON (ccu.table_name) ccu.table_name as foreigntable, " +
						"\n 	ccu.column_name AS referedcolumn, " +
						"\n 	kcu.column_name AS localcolumn " +
						"\n	FROM information_schema.table_constraints as tc"+
						"\n JOIN information_schema.key_column_usage kcu" +
						"\n    	ON tc.constraint_name = kcu.constraint_name "+
						"\n JOIN information_schema.constraint_column_usage ccu" +
						"\n 	ON ccu.constraint_name = kcu.constraint_name" +
						"\n WHERE kcu.table_name = '"+tablename+"'" +
						"\n 	AND kcu.table_schema = '"+schema+"'"+
						"\n		AND (ccu.table_name != '"+tablename+"'"+
						"\n		OR (ccu.table_name = '"+tablename+"' AND ccu.column_name != '"+tablename+"_id'))" +
						"\n 	AND tc.constraint_type = 'FOREIGN KEY'";

		try {
			ResultSet rs = executeSelectSQL(sql, null);
			List<String> foreignTables = new ArrayList<String>();
			List<String> referedColumn = new ArrayList<String>();
			List<String> localColumn = new ArrayList<String>();
			while(rs != null && rs.next()){
				foreignTables.add(rs.getString(1));
				referedColumn.add(rs.getString(2));
				localColumn.add(rs.getString(3));
			}
			ForeignKey fk = new ForeignKey();
			fk.foreignTable = foreignTables.toArray(new String[foreignTables.size()]);//new String[]{"users"};
			fk.localColumn = referedColumn.toArray(new String[referedColumn.size()]);//new String[]{"author_id"};
			fk.referedColumn = localColumn.toArray(new String[localColumn.size()]);//new String[]{"user_id"};
			return fk;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		} 
	}

	/**
	 * TODO: need to be fast, this is very slow. Need to use pg_catalog since information_schema is slow
	 */
	@Override
	protected ForeignKey getExportedKeys(String schema, String tablename) throws DatabaseException{

		String sql = "SELECT " +
				"\n 	DISTINCT ON(kcu.table_name) kcu.table_name AS foreigntable,"+
				"\n 	kcu.column_name AS referedcolumn, " +
				"\n 	ccu.column_name AS localcolumn " +
				"\n FROM information_schema.table_constraints tc "+
				"\n JOIN information_schema.key_column_usage kcu " +
				"\n 	ON tc.constraint_name = kcu.constraint_name "+
				"\n JOIN information_schema.constraint_column_usage ccu " +
				"\n 	ON ccu.constraint_name = kcu.constraint_name " +
				"\n WHERE ccu.table_name = '"+tablename+"' " +
				"\n 	AND ccu.table_schema = '"+schema+"'" +
				"\n AND (kcu.table_name != '"+tablename+"' "+
				"\n OR (kcu.table_name = '"+tablename+"'  AND kcu.column_name != '"+tablename+"_id'))"+
				"\n 	AND tc.constraint_type = 'FOREIGN KEY'";

		try {
			ResultSet rs = executeSelectSQL(sql, null);
			List<String> foreignTables = new ArrayList<String>();
			List<String> referedColumn = new ArrayList<String>();
			List<String> localColumn = new ArrayList<String>();
			while(rs != null && rs.next()){
				foreignTables.add(rs.getString(1));
				referedColumn.add(rs.getString(2));
				localColumn.add(rs.getString(3));
			}
			ForeignKey fk = new ForeignKey();
			fk.foreignTable = foreignTables.toArray(new String[foreignTables.size()]);
			fk.localColumn = referedColumn.toArray(new String[referedColumn.size()]);
			fk.referedColumn = localColumn.toArray(new String[localColumn.size()]);
			return fk;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		} 
	}



	/**
	 * Remapping json directly to object as opposed to traversing the tree
	 * @param recordValue
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	private Object jsonToObject(String recordValue) throws JsonParseException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		Object json = mapper.readValue(recordValue, Object.class);	
		return json;
	}

	/**
	 * 
	 * Immediate conversion of Datatypes from Postgresql that has JDBC did not do mapping to Java data types
	 * such as JSON to String, or HashMap, 
	 */
	@Override
	protected Object getEquivalentJavaObject(Object record) {
		if(record == null) return null;
		if(PGobject.class.equals(record.getClass())){
			PGobject pgObject = ((PGobject)record);
			String type = pgObject.getType();
			if("json".equals(type)){
				String recordValue = pgObject.getValue();
				try {
					return jsonToObject(recordValue);
				} catch (JsonParseException e) {
					e.printStackTrace();
				} catch (JsonMappingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return record;
	}



	@Override
	protected Object getEquivalentDBObject(Object record) {
		if (record == null) {
			return null;
		}
		if(record instanceof Map || record instanceof List){//List and Maps are json objects
			PGobject pgo = new PGobject();
			try {
				pgo.setType("json");
				ObjectMapper mapper = new ObjectMapper();
				mapper.setSerializationInclusion(Include.NON_NULL);
				String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(record);
				pgo.setValue(json);
			} catch (SQLException | JsonProcessingException e) {
				e.printStackTrace();
			}
			return pgo;
		}
		else if (record.getClass().equals(java.util.Date.class)) {
			Timestamp time = new Timestamp(((Date) record).getTime());
			return time;
		}else {
			return record;
		}
	}




	/**
	 * Most of postgresql database datatype already mapped to the correct data type by the JDBC
	 */
	@Override
	public void correctDataTypes(DAO[] daoList, ModelDef model) {
		for(DAO dao : daoList){
			correctDataTypes(dao, model);
		}
	}

	public void correctDataTypes(DAO dao, ModelDef model) {
		if(model != null){
			String[] dataTypes = model.getDataTypes();
			String[] attributes = model.getAttributes();
			for(int i = 0 ; i < attributes.length; i++){
				Object value = dao.get_Value(attributes[i]);
				correctDataType(value, dataTypes[i]);
			}
		}

	}
	/**
	 * add logic here if PostgreSQL JDBC didn't map DB data type to their correct Java Data type
	 * @param value
	 * @param dataType
	 * @return
	 */

	private Object correctDataType(Object value, String dataType) {
		if(value == null ){return null;}
		return value;
	}


	@Override
	public ModelDef getModelMetaData(String schema, String tableName)
			throws DatabaseException {

		String realTableName = getRealTableName(schema, tableName);
		boolean caseSensitive = true;
		if (realTableName != null) {
			caseSensitive = isCaseSensitive(tableName, realTableName);
		} else {
			String msg = "No equivalent table in the database: [" + schema
					+ "." + tableName + "]";
			System.err.println(msg);
			throw new DatabaseException(msg);
		}
		if (schema == null) {
			schema = getTableSchema(tableName);
		}
		ColumnDataType columns = getColumnDetails(schema, realTableName);
		String[] primaryKeys = getPrimaryKeysList(schema, realTableName);
		String tableComment = getTableComment(realTableName, schema);

		String autoIncrementColumn = getAutoIncrementColumn(realTableName);
		ForeignKey fki = getImportedKeys(schema, realTableName);
		ForeignKey fke = getExportedKeys(schema, realTableName);

		ModelDef modelDef = new ModelDef();
		modelDef.setNamespace(schema);
		modelDef.setDescription(tableComment);
		modelDef.setModelName(tableName);
		modelDef.setTableName(realTableName);
		modelDef.setCaseSensitive(caseSensitive);
		modelDef.setAttributes(columns.getColumns());
		modelDef.setDataTypes(columns.getDataTypes());
		modelDef.setAttributeComments(columns.getComments());
		modelDef.setGeneratedAttribute(autoIncrementColumn);
		modelDef.setPrimaryAttributes(primaryKeys);
		modelDef.setUniqueAttributes(getUniqueKeys(schema, realTableName));

		if (fki != null) {
			modelDef.setHasOne(fki.foreignTable);
			modelDef.setHasOneLocalColumn(fki.localColumn);
			modelDef.setHasOneReferredColumn(fki.referedColumn);
		}
		if (fke != null) {
			modelDef.setHasMany(fke.foreignTable);
			modelDef.setHasManyForeignColumn(fke.localColumn);
			modelDef.setHasManyReferredColumn(fke.referedColumn);
		}
		modelDef.setParentClass(getParentClass(tableName));
		modelDef.setSubClass(getSubClasses(tableName));

		return modelDef;
	}


}
