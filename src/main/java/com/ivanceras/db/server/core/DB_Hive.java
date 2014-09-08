/*******************************************************************************
 * Copyright by CMIL
 ******************************************************************************/
package com.ivanceras.db.server.core;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ivanceras.commons.conf.DBConfig;
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
import com.ivanceras.db.shared.exception.DBConnectionException;
import com.ivanceras.db.shared.exception.DatabaseException;
import com.ivanceras.fluent.sql.Breakdown;
import com.ivanceras.fluent.sql.SQL;

/**
 * Future release plan for apache hive/hadoop support
 */
public class DB_Hive extends DB_Jdbc implements IDatabase {

	private static Logger log = LogManager.getLogger("DB_Hive");
	private static String supportVersion = "1";


	public DB_Hive(DBConfig config) throws DBConnectionException {
		super(config);
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
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
		System.out.println("supportVersion: "+supportVersion);
		if(supportVersion.equals("2")){
			return "org.apache.hive.jdbc.HiveDriver";
		}
		else{
			return "org.apache.hadoop.hive.jdbc.HiveDriver";
		}
	}

	@Override
	String getDBUrl() {
		if(supportVersion.equals("2")){
			return "jdbc:hive2://"+dbHost+":"+dbPort+"/"+dbName+"";
		}
		else{
			return "jdbc:hive://"+dbHost+":"+dbPort+"/"+dbName+"";
		}
	}

	@Override
	protected String getAutoIncrementColumnConstraint() {
		// TODO Auto-generated method stub
		return null;
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
	protected Object getEquivalentJavaObject(Object record, ModelDef model,
			String column) {
		return record;
	}

	@Override
	public boolean existModel(ModelDef model) {
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
	protected String forceKeyword() {
		// TODO Auto-generated method stub
		return null;
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
	protected boolean supportPreparedStatement() {
		return true;
	}

	/**
	 * Return the generic data type as is since hive uses those generic types already
	 */
	@Override
	public String getEquivalentDBDataType(String genDataType){
		return genDataType;
	}

	@Override
	protected boolean returnsSqlStatements(){
		return false;
	}

	@Override
	public boolean createModel(ModelDef model) throws DatabaseException{
		SQL sql = buildCreateTableStatement(model);
		boolean ret = executeUpdateSQL(sql) > 0;
		return ret;
	}

//	@Override
//	public SQL buildCreateTableStatement(ModelDef model) throws DatabaseException{
//		String schema = model.getNamespace();
//		schema = getDBElementName(model,schema);
//
//		String table = model.getTableName();
//		if(table==null) throw new DatabaseException("No table indicated");
//
//		String[] columns = model.getAttributes();
//		for(int i = 0; i < columns.length; i++){
//			columns[i] = getDBElementName(model,columns[i]);
//		}
//
//		String[] dataTypes = model.getDataTypes();
//
//		String autoIncrementColumn = model.getGeneratedAttribute();
//		autoIncrementColumn = getDBElementName(model,autoIncrementColumn);
//
//		String[] primaryColumns = model.getPrimaryAttributes();
//		if(primaryColumns != null){
//			for(int i = 0; i < primaryColumns.length; i++){
//				primaryColumns[i] = getDBElementName(model,primaryColumns[i]);
//			}
//		}
//		String[] uniqueColumns = model.getUniqueAttributes();
//		if(uniqueColumns != null){
//			for(int i = 0; i < uniqueColumns.length; i++){
//				uniqueColumns[i] = getDBElementName(model,uniqueColumns[i]);
//			}
//		}
//		String[] hasOne = model.getHasOne();
//		if(hasOne != null){
//			for(int i = 0; i < hasOne.length; i++){
//				hasOne[i] = getDBElementName(model,hasOne[i]);
//			}
//		}
//
//		StringBuffer sql = new StringBuffer();
//		sql.append("CREATE EXTERNAL TABLE ");
//		if(schema != null && useSchema()){
//			sql.append(schema+".");
//		}
//		sql.append(table);
//		sql.append("(\n");
//		boolean doComma = false;
//		for(int i = 0; i < columns.length; i++){
//			if(doComma){sql.append(", \n");
//			}else{ doComma = true;}
//			sql.append("\t"+columns[i]);
//			if(autoIncrementColumn != null && autoIncrementColumn.equals(columns[i])){
//				sql.append(" "+getAutoIncrementColumnConstraint());
//			}else{
//				String dbDataType = null;
//				dbDataType = getEquivalentDBDataType(dataTypes[i]);
//				if(dbDataType!=null){
//					sql.append(" "+dbDataType);
//				}
//			}
//		}
//		sql.append(")");
//		return sql.toString();
//	}



	@Override
	protected ForeignKey getExportedKeys(String arg0, String arg1)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ForeignKey getImportedKeys(String arg0, String arg1)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean schemaExist(String arg0) throws DatabaseException {
		// TODO Auto-generated method stub
		return false;
	}

	public void createExternalModel(ModelDef model, String directory, String[] partitions, String[] partitionsDataType,
			String fieldKill, String lineKill) throws DatabaseException{
		String table = getTable(model);
		SQL sql = SQL.CREATE();
		sql.keyword("EXTERNAL");
		sql.TABLE(table);
		String[] columns = model.getAttributes();
		String[] dataTypes = model.getDataTypes();
		for(int i = 0; i < columns.length; i++){
			sql.FIELD(columns[i], dataTypes[i]);
		}
		if(partitions != null){
			for(int j = 0; j < partitions.length; j++){
				sql.PARTITION_BY(partitions[j], partitionsDataType[j]);
			}
		}
		if(fieldKill != null){
			sql.keyword("ROW_FORMAT_DELIMITED");
			sql.keyword("FIELDS_TERMINATED_BY").keyword(fieldKill);
		}
		if(lineKill != null){
			sql.keyword("ROW_FORMAT_DELIMITED");
			sql.keyword("LINES_TERMINATED_BY").keyword(lineKill);
		}

		sql.keyword("STORED_AS_TEXTFILE");
		sql.keyword("LOCATION").keyword(directory);
		executeUpdateSQL(sql);
	}

	public void addJars(String... jars) throws DatabaseException{
		for(String jar : jars){
			SQL sql = new SQL().keyword("ADD_JAR").keyword(jar);
			Breakdown bk = sql.build();
			executeInsertSQL(sql, false);
		}
	}

	public void addFile(String... files){

	}

	@Override
	public boolean drop(ModelDef model, boolean forced) throws DatabaseException{
		String table = getTable(model);
		System.out.println("table: "+table);
		SQL sql = SQL.DROP_TABLE(table);
		return executeUpdateSQL(sql) >= 0;
	}

	public void reclaimPartition(String directory, ModelDef model, String[] partitions) throws DatabaseException{
		PartitionedDirectories partition = new PartitionedDirectories(directory, partitions);
		String table = getTable(model);
		try {
			Map<String, Map<String, String>> map = partition.getHash();
			for(Entry<String, Map<String, String>> list : map.entrySet()){
				SQL sql = SQL.ALTER_TABLE(table);
				for(Entry<String, String> li : list.getValue().entrySet()){
					String key = li.getKey();
					String value = li.getValue();
					sql.keyword("ADD_PARTITION").FIELD(key, value);
				}
				String fullPath = list.getKey();
				sql.keyword("LOCATION").keyword(fullPath);
				executeUpdateSQL(sql);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static class PartitionedDirectories {

		private String[] patterns;
		private Map<String, String> matchedDirectories;
		private String location;
		private boolean runned = false;

		public PartitionedDirectories(String baseLocation, String[] partitions) {
			this.location = baseLocation;
			this.patterns = partitions;
			matchedDirectories = new HashMap<String, String>();
		}

		public Map<String, String> getDirectories() throws IOException{
			if(!runned){
				listDown(0, location);
				runned = true;
			}
			return matchedDirectories;
		}

		public Map<String, Map<String, String>> getHash() throws IOException{
			Map<String, String> dirs = this.getDirectories();
			Map<String, Map<String, String>> list = new HashMap<String, Map<String,String>>();
			for(Entry<String, String> dirEntry : dirs.entrySet()){
				String dir = dirEntry.getKey();
				String fullPath = dirEntry.getValue();
				String[] splinters = dir.split("/");
				HashMap<String, String> hash = new LinkedHashMap<String, String>();
				for(String spl : splinters){
					String[] tips = spl.split("=");
					if(tips.length > 1){
						hash.put(tips[0], tips[1]);
					}
				}
				list.put(fullPath, hash);
			}
			return list;
		}


		private void listDown(int depth, String directory) throws IOException {
			if(depth >= patterns.length){
				return;
			}
			String pattern = patterns[depth];
			File dir = new File(directory);
			File[] contents = dir.listFiles();
			for(File content : contents){
				if(content.isDirectory()){
					String dirName = content.getName();
					if(dirName.startsWith(pattern)){
						String[] splinters = dirName.split("=");
						if(splinters.length > 1){
							String column = splinters[0];
							//						String value = splinters[1];
							if(column.equals(pattern)){
								String fullPath = content.getCanonicalPath();
								if(depth == patterns.length - 1){
									System.out.println("dirname: "+fullPath);
									int s1 = location.length();
									int s2 = fullPath.length();
									String dirPath = fullPath.substring(s1, s2).replace("^/", "");
									matchedDirectories.put(dirPath, fullPath);
								}
								listDown(depth+1, fullPath);
							}
						}
					}
				}
				else{
					;//no operation for files, only for directories
				}

			}
		}
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
