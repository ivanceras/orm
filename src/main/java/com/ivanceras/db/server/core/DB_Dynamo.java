package com.ivanceras.db.server.core;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.CreateTableResult;
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteItemResult;
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteTableResult;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ListTablesRequest;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.ivanceras.commons.conf.DBConfig;
import com.ivanceras.commons.strings.CStringUtils;
import com.ivanceras.db.api.ColumnPair;
import com.ivanceras.db.api.IDatabase;
import com.ivanceras.db.api.ModelDef;
import com.ivanceras.db.api.Query;
import com.ivanceras.db.api.SchemaTable;
import com.ivanceras.db.date.DateFunctions;
import com.ivanceras.db.model.ModelMetaData;
import com.ivanceras.db.shared.DAO;
import com.ivanceras.db.shared.Filter;
import com.ivanceras.db.shared.datatype.GenericDataType;
import com.ivanceras.db.shared.exception.DatabaseException;
import com.ivanceras.fluent.sql.SQL;

public class DB_Dynamo  implements IDatabase{

	AmazonDynamoDBClient client;
	private ModelMetaData meta;

	public DB_Dynamo(DBConfig config){
		client = new AmazonDynamoDBClient(new BasicAWSCredentials("keys", "secrets"));
		//		client.setEndpoint("http://localhost:8000");
		String url = "http://"+config.getDbHost()+":"+config.getDbPort();
		System.out.println("URL: "+url);
		client.setEndpoint(url);		
	}

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
	public ModelMetaData getModelMetaDataDefinition() throws DatabaseException {
		return this.meta;
	}

	@Override
	public void setModelMetaDataDefinition(ModelMetaData meta){
		this.meta = meta;
	}
	
	@Override
	public DAO[] select(ModelMetaData meta, Query query)
			throws DatabaseException {
		ModelDef model = query.getModel();
		Filter[] filters = query.getFilters();
		if(filters == null){
			return scanTable(model);
		}
		String[] attributes = model.getAttributes();
		String[] dataTypes = model.getDataTypes();

		HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
		for(Filter f : filters){
			String attr = f.attribute;
			int index = CStringUtils.indexOf(attributes, attr);
			String dt = dataTypes[index];
			Object value = f.value;
			AttributeValue attValue = toAttributeValue(dt, value);
			key.put(attr, attValue);
		}

		String tableName = model.getTableName();

		GetItemRequest getItemRequest = new GetItemRequest()
		.withTableName(tableName)
		.withKey(key);

		System.out.println("getItemRequest: "+getItemRequest);

		GetItemResult itemResult = client.getItem(getItemRequest);
		System.out.println("itemResult: "+itemResult);
		Map<String, AttributeValue> item = itemResult.getItem();
		if(item != null){
			DAO dao = toDao(model, item);
			DAO[] daoList = new DAO[1];
			daoList[0] = dao;
			return daoList;
		}
		return null;

	}

	private DAO[] scanTable(ModelDef model) {
		String tableName = model.getTableName();
		ScanRequest scanRequest = new ScanRequest("Book");
		ScanResult scanResult = client.scan(scanRequest );
		
		List<Map<String, AttributeValue>> itemResult = scanResult.getItems();
		System.out.println("scanResult: "+itemResult);
		return null;
	}

	private DAO toDao(ModelDef model, Map<String, AttributeValue> map){
		String[] attributes = model.getAttributes();
		String[] dataTypes = model.getDataTypes();
		String modelName = model.getModelName();
		DAO dao = new DAO(modelName);
		System.out.println("map: "+map);
		for(Entry<String, AttributeValue> entry : map.entrySet()){
			String key = entry.getKey();
			AttributeValue value = entry.getValue();
			int index = CStringUtils.indexOf(attributes, key);
			String dt = dataTypes[index];
			Object obj = convertObject(dt, value);
			dao.set_Value(key, obj);
		}
		return dao;
	}

	@Override
	public boolean createModel(ModelDef model) throws DatabaseException {
		ProvisionedThroughput provisionedThroughput = new ProvisionedThroughput()
		.withReadCapacityUnits(10L)
		.withWriteCapacityUnits(10L);

		String tableName = model.getTableName();
		String[] attributes = model.getAttributes();
		String[] dataTypes = model.getDataTypes();
		String[] primaryKeys = model.getPrimaryAttributes();

		ArrayList<AttributeDefinition> attributeDefinitions= new ArrayList<AttributeDefinition>();

		//atribute definitions
		//		for(int i = 0; i < attributes.length; i++){
		//			String att = attributes[i];
		//			String dt = dataTypes[i];
		//			String type = getType(dt);
		//			attributeDefinitions.add(new AttributeDefinition()
		//										.withAttributeName(att)
		//										.withAttributeType(type));
		//		}

		ArrayList<KeySchemaElement> ks = new ArrayList<KeySchemaElement>();
		for(int i = 0; i < primaryKeys.length; i++){
			String pk = primaryKeys[i];
			int index = CStringUtils.indexOf(attributes, pk);
			String dt = dataTypes[index];
			String type= getType(dt);
			attributeDefinitions.add(new AttributeDefinition()
			.withAttributeName(pk)
			.withAttributeType(type));
			ks.add(new KeySchemaElement().withAttributeName(pk).withKeyType(KeyType.HASH));
		}

		CreateTableRequest request = new CreateTableRequest()
		.withTableName(tableName)
		.withAttributeDefinitions(attributeDefinitions)
		.withKeySchema(ks)
		.withProvisionedThroughput(provisionedThroughput);

		System.out.println("createTableRequest: "+request);

		CreateTableResult result = client.createTable(request);
		return result != null;
	}

	private String getType(String dt){
		if(dt.equals(GenericDataType.STRING)){
			return "S";
		}
		else if(dt.equals(GenericDataType.INTEGER)){
			return "N";
		}
		else if(dt.equals(GenericDataType.BIGINTEGER)){
			return "N";
		}
		else if(dt.equals(GenericDataType.FLOAT)){
			return "N";
		}
		else if(dt.equals(GenericDataType.DOUBLE)){
			return "N";
		}
		else if(dt.equals(GenericDataType.BIGDECIMAL)){
			return "N";
		}
		else if(dt.equals(GenericDataType.DATE)){
			return "S";
		}
		else{
			return "S";
		}
	}
	private Object convertObject(String dt, AttributeValue value){
		if(dt.equals(GenericDataType.STRING)){
			return value.getS();
		}
		else if(dt.equals(GenericDataType.INTEGER)){
			return Integer.parseInt(value.getN());
		}
		else if(dt.equals(GenericDataType.BIGINTEGER)){
			return BigInteger.valueOf(Long.parseLong(value.getN()));
		}
		else if(dt.equals(GenericDataType.FLOAT)){
			return Float.parseFloat(value.getN());
		}
		else if(dt.equals(GenericDataType.DOUBLE)){
			return Double.parseDouble(value.getN());
		}
		else if(dt.equals(GenericDataType.BIGDECIMAL)){
			return BigDecimal.valueOf(Long.parseLong(value.getN()));
		}
		else if(dt.equals(GenericDataType.DATE)){
			try {
				return DateFunctions.getDate(value.getS());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return null;
		}
		else{
			return value.getS();
		}
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
		System.out.println("deleting: "+model);
		System.out.println("Filters: "+Arrays.asList(filters));

		String[] attributes = model.getAttributes();
		String[] dataTypes = model.getDataTypes();

		String tableName = model.getTableName();

		DeleteItemRequest deleteRequest = new DeleteItemRequest().withTableName(tableName);
		Map<String, AttributeValue> keys = new HashMap<String, AttributeValue>();
		for(Filter f : filters){
			String attr = f.attribute;
			Object value  = f.value;
			int index = CStringUtils.indexOf(attributes, attr);
			String dt = dataTypes[index];
			AttributeValue attrValue = toAttributeValue(dt, value);
			keys.put(attr, attrValue);
		}
		deleteRequest.setKey(keys);
		DeleteItemResult deleteResult = client.deleteItem(deleteRequest);
		return 1;
	}

	@Override
	public int empty(ModelDef model, boolean forced) throws DatabaseException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean drop(ModelDef model, boolean forced)
			throws DatabaseException {
		String tableName = model.getTableName();
		DeleteTableRequest deleteTableRequest = new DeleteTableRequest()
		.withTableName(tableName);
		DeleteTableResult result = client.deleteTable(deleteTableRequest);
		return result != null;
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
		List<SchemaTable> tableNames = new ArrayList<SchemaTable>();
		String lastEvaluatedTableName = null;
		do {
			ListTablesRequest listTablesRequest = new ListTablesRequest()
			//			.withLimit(10)
			.withExclusiveStartTableName(lastEvaluatedTableName);
			ListTablesResult result = client.listTables(listTablesRequest);
			lastEvaluatedTableName = result.getLastEvaluatedTableName();
			for (String name : result.getTableNames()) {
				SchemaTable schemaTable = new SchemaTable(null, name);
				tableNames.add(schemaTable);
			}
		} while (lastEvaluatedTableName != null);
		return tableNames.toArray(new SchemaTable[tableNames.size()]);
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
	public boolean prependTableName() {
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

	private AttributeValue toAttributeValue(String dt, Object value){
		if(dt.equals(GenericDataType.STRING)){
			String daoValue = (String) value;
			AttributeValue itemValue = new AttributeValue().withS(daoValue);
			return itemValue;
		}
		else if(dt.equals(GenericDataType.INTEGER)){
			String daoValue = Integer.toString((Integer) value);
			AttributeValue itemValue = new AttributeValue().withN(daoValue);
			return itemValue;
		}
		else if(dt.equals(GenericDataType.LONG)){
			String daoValue = Long.toString((Long) value);
			AttributeValue itemValue = new AttributeValue().withN(daoValue);
			return itemValue;
		}
		else if(dt.equals(GenericDataType.FLOAT)){
			String daoValue = Float.toString((Float) value);
			AttributeValue itemValue = new AttributeValue().withN(daoValue);
			return itemValue;
		}
		else if(dt.equals(GenericDataType.BIGDECIMAL)){
			String daoValue = ((BigDecimal) value).toString();
			AttributeValue itemValue = new AttributeValue().withN(daoValue);
			return itemValue;
		}
		else if(dt.equals(GenericDataType.DATE)){
			Date daoValue = (Date) value;
			String daoString = DateFunctions.getAsString(daoValue);
			AttributeValue itemValue = new AttributeValue().withS(daoString);
			return itemValue;
		}else{
			String daoValue = value.toString();
			AttributeValue itemValue = new AttributeValue().withS(daoValue);
			return itemValue;
		}
	}

	//convert Date -> String format {.withS}
	//Integer,BigDecimal,Float -> Numbers {.withN}
	//Binay Blob to ByteBuffer {.withB}
	private Map<String, AttributeValue> toItem(ModelDef model, DAO dao){
		String[] dataTypes = model.getDataTypes();
		String[] attributes = model.getAttributes();

		Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();

		for(int i = 0; i < dataTypes.length; i++){
			String dt = dataTypes[i];
			String att = attributes[i];
			Object value = dao.get_Value(att);
			AttributeValue itemValue = toAttributeValue(dt, value);
			item.put(att, itemValue);
		}
		return item;
	}

	@Override
	public String getTableComment(String table) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public com.ivanceras.fluent.sql.SQL buildSQL(ModelMetaData meta, Query query,
			boolean useCursor) {
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
	public void search(Query query, String keyword) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DAO update(DAO dao, ModelDef model, Filter[] filters)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DAO insert(DAO dao, ModelMetaData meta, ModelDef model, Query query)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


}
