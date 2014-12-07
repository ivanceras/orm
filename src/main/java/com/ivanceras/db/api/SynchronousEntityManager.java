/*******************************************************************************
 * Copyright by CMIL
 ******************************************************************************/
package com.ivanceras.db.api;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.ivanceras.db.shared.DAO;
import com.ivanceras.db.shared.Filter;
import com.ivanceras.db.shared.Order;
import com.ivanceras.db.shared.exception.DAOInstanceFactoryException;
import com.ivanceras.db.shared.exception.DatabaseException;
import com.ivanceras.db.shared.util.DAOUtils;
import com.ivanceras.keyword.sql.SQL;

import static com.ivanceras.keyword.sql.SQLStatics.*;

public class SynchronousEntityManager implements EntityManager{

	/**
	 * Globally enable/disable record changelog. Enable by default
	 */
//	private ContextProvider contextProvider;

	static SynchronousEntityManager singleton;

	IDatabase db;
	/**
	 * If the entity is transaction based, get a database connection to (read/write) database
	 * else(read-only query) have to get a read-only database connection which would be faster.
	 */
	boolean isTransactionBased = false;

	private HashMap<String, Object> context = new HashMap<String, Object>();

	/**
	 * Use this carefully, use only when writing scripts to export data between two database sources
	 * @param db
	 * @throws DatabaseException
	 */
	public SynchronousEntityManager(IDatabase db) throws DatabaseException{
		this.db = db;
		if(db==null){
			throw new DatabaseException("Unable to create Database connection");
		}
		singleton = this;
	}

	@Override
	public void beginTransaction() throws DatabaseException
	{
		if(db!=null && !db.isClosed()) db.beginTransaction();
		isTransactionBased = true;
	}



//	private Filter buildSoundexFilter(String column, String keyword){
//		String columnWrapper = "soundex(substring("+column+" from 1 for "+keyword.length()+"))";
//		Filter f = new Filter(columnWrapper, Filter.EQUAL, keyword);
//		f.valueWrapperLeft = "soundex(";
//		f.valueWrapperRight = ")";
//		return f;
//	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends DAO> T cast(Class<? extends DAO> daoClass, DAO dao) throws DatabaseException{
		if(dao == null){
			return null;
		}
		if(daoClass == null){
			return (T) dao; //no casting
		}
		String modelName = db.getModelMetaDataDefinition().getModelName(daoClass);
		T instance = null;
		try {
			instance = db.getModelMetaDataDefinition().getInstanceProvider().getInstance(daoClass);//Not using reflection, reflection is not supported in GWT, might be used GWT client-side the future
			return DAO_Operator.cast(instance, modelName, dao);
		} catch (DAOInstanceFactoryException e) {
			throw new DatabaseException(e.getMessage());
		}
	}

	@Override
	public <T extends DAO> T[] cast(Class<? extends DAO> daoClass, DAO[] daoList) throws DatabaseException{
		T[] arrayInstance = null;
		try {
			DAOFactory instanceProvider = db.getModelMetaDataDefinition().getInstanceProvider();
			if(instanceProvider == null){
				throw new DatabaseException("InstanceProvider is not set");
			}
			assert instanceProvider != null;
			if(instanceProvider != null && daoList != null){
				arrayInstance = instanceProvider.getArrayInstance(daoClass, daoList.length);
				for(int i = 0; i < daoList.length; i++)	{
					arrayInstance[i] = cast(daoClass , daoList[i]);
				}
			}
			return arrayInstance;
		} catch (DAOInstanceFactoryException e) {
			throw new DatabaseException(e.getMessage());
		}
	}

	@Override
	public void commitTransaction() throws DatabaseException
	{ if(db!=null && !db.isClosed()) db.commitTransaction(); 
	isTransactionBased = false;
	}

	@Override
	public int count(Query query) throws DatabaseException {
		ModelDef model = new ModelDef();
		Query countQuery = new Query(model);
		query.setItemsPerPage(null);//set to null when counting
		query.setPage(null);//set to null when counting
		countQuery.setBaseQuery(query, "t1");
		model.setAttributes(new String[]{"count(*)"});
		DAO[] daoList = db.select(db.getModelMetaDataDefinition(), countQuery);
		if(daoList != null && daoList.length > 0){
			return (int)getCount(daoList[0]);
		}
		return -1;

	}

	@Override
	public boolean createModel(ModelDef model) throws DatabaseException{
		return db.createModel(model);
	}

	@Override
	public boolean createSchema(String schema) throws DatabaseException{
		return db.createSchema(schema);
	}



	@Override
	public int delete(Class<? extends DAO> daoClass, Filter... filters) throws DatabaseException{
		//TODO: find the dependent records of this record and delete  it when forced is true
		return deleteRecord(db.getModelMetaDataDefinition().getDefinition(daoClass), filters);
	}

	@Override
	public int delete(DAO dao) throws DatabaseException {
		//TODO: find the dependent records of this record and delete  it when forced is true
		Filter[] filters = ApiUtils.getFilters(db.getModelMetaDataDefinition(), dao, db.prependTableName());
		return deleteRecord(db.getModelMetaDataDefinition().getDefinition(dao.getModelName()), filters);
	}

	private int deleteRecord(ModelDef model, Filter... filters) throws DatabaseException{
//		DAO[] affectedRecords = null;
//		if(contextProvider != null && contextProvider.isEnableRecordChangelog()){
//			affectedRecords = getAffectedRecords(model, filters);
//		}
		int ret = db.delete(model, filters);
//		if(contextProvider != null && contextProvider.isEnableRecordChangelog()){
//			contextProvider.recordDeleteChange(model, affectedRecords);
//		}
		return ret;
	}

	@Override
	public boolean drop(Class<? extends DAO> daoClass) throws DatabaseException{
		return drop(daoClass, false);
	}

	@Override
	public boolean drop(Class<? extends DAO> daoClass, boolean forced) throws DatabaseException{
		return db.drop(db.getModelMetaDataDefinition().getDefinition(daoClass), forced);
	}

	@Override
	public void dropNameSpace(String schema, boolean forced) throws DatabaseException {
		db.dropNamespace(schema, forced);
	}

	@Override
	public int empty(Class<? extends DAO> daoClass) throws DatabaseException{
		return empty(daoClass, false);
	}

	@Override
	public int empty(Class<? extends DAO>daoClass, boolean forced) throws DatabaseException{
		ModelDef model  = db.getModelMetaDataDefinition().getDefinition(daoClass);
		return db.empty(model, forced);
	}

	/**
	 * Use this when checking the record exist in these table while using all the values as the filter
	 * 
	 */
	public boolean exist(DAO dao) throws DatabaseException{
		ModelDef model = db.getModelMetaDataDefinition().getDefinition((dao.getModelName()));
		DAO exists = getOne(getDaoClass(dao.getModelName()), ApiUtils.getAllFilters(model,dao, db.prependTableName()));
		if(exists != null){
			return true;
		}
		return false;
	}

	@Override
	public boolean existModel(ModelDef model) throws DatabaseException {
		return db.existModel(model);
	}

	@Override
	public void finalize() throws Throwable
	{
		if(db!=null) try
		{
			Exception e = new Exception("An Entity Manager was not properly closed.  FIX IMMEDIATELY!  (╯°□°）╯︵ ┻━┻");
			e.printStackTrace();
			if(db.isTransacted())
			{
				e = new Exception("An Entity Manager started a transaction, but did not commit or rollback.  FIX IMMEDIATELY! (ノಠ益ಠ)ノ彡┻━┻");
				e.printStackTrace();
			}
		} catch(DatabaseException se) { se.printStackTrace(); throw se; }

		super.finalize();
	}

//	/**
//	 * This are the affected records before updating or deleting
//	 * @param model
//	 * @param filters
//	 * @return
//	 * @throws DatabaseException 
//	 */
//	private DAO[] getAffectedRecords(ModelDef model, Filter[] filters) throws DatabaseException{
//		Class<? extends DAO> clazz = getDaoClass(model.getModelName());
//		DAO[] daoList = getAll(clazz, filters);
//		return daoList;
//	}

	@Override
	public <T extends DAO> T[] getAll(Class<? extends T> daoClass) throws DatabaseException{
		return retrieveRecords(new Query(db.getModelMetaDataDefinition(), daoClass));
	}

	@Override
	public <T extends DAO> T[] getAll(Class<? extends T> daoClass, Boolean distinct) throws DatabaseException{
		Query query = new Query(db.getModelMetaDataDefinition(), daoClass);
		query.setDistinct(distinct);
		return retrieveRecords(query);
	}

	@Override
	public <T extends DAO> T[] getAll(Class<? extends T> daoClass, Filter... filters) throws DatabaseException{
		Query query = new Query(db.getModelMetaDataDefinition(), daoClass);
		query.addFilter(filters);
		return retrieveRecords(query);
	}


	@Override
	public <T extends DAO> T[] getAll(Class<? extends T> daoClass, Order order,
			Filter... filters) throws DatabaseException {
		Query query = new Query(db.getModelMetaDataDefinition(), daoClass);
		query.addFilter(filters);
		query.addOrder(order);
		return retrieveRecords(query);
	}

	@Override
	public byte[] getBlob(long oid) throws DatabaseException {
		return db.getBlob(oid);
	}

	private long getCount(DAO dao){
		return (Long)dao.get_Value("count");
	}


	@Override
	public Class<? extends DAO> getDaoClass(String modelName) throws DatabaseException{
		return db.getModelMetaDataDefinition().getDaoClass(modelName);
	}

	/**
	 * initialize with default meta data
	 */

	@Override
	public IDatabase getDB(){
		return db;
	}

	@Override
	public ModelDef getDefinition(Class<? extends DAO> daoClass) throws DatabaseException {
		return db.getModelMetaDataDefinition().getDefinition(daoClass);
	}

	@Override
	public <T extends DAO> T getMatch(DAO dao, Boolean exact)  throws DatabaseException{
		Class<? extends DAO> daoClass = getDaoClass(dao.getModelName());
		ModelDef modelDef = db.getModelMetaDataDefinition().getDefinition(daoClass);
		Filter[] filters = null;
		if(exact){
			filters = ApiUtils.getFilters(db.getModelMetaDataDefinition(), dao, db.prependTableName());
		}
		else{
			filters = ApiUtils.getFiltersForUnique(modelDef, dao);
		}
		if(filters == null){
			return null;
		}
		Query query = new Query(modelDef);
		query.addFilter(filters);
		query.setLimit(1);


		DAO[] daoList = retrieveRecords(query);
		if(daoList != null && daoList.length > 0){
			return cast(daoClass, daoList[0]);
		}else{
			return null;
		}
	}

	@Override
	public String getModelName(Class<? extends DAO> daoClass) throws DatabaseException {
		return  db.getModelMetaDataDefinition() .getModelName(daoClass);
	}

	@Override
	public <T extends DAO> T getOne(Class<? extends T> daoClass, Filter... filters)  throws DatabaseException{
		Query query = new Query(db.getModelMetaDataDefinition(), daoClass);
		query.addFilter(filters);
		query.setLimit(1);
		DAO[] daoList = retrieveRecords(query);

		if(daoList != null && daoList.length > 0){
			if(daoList.length > 1){
				System.err.println("Warning: Records returned are more than 1 rows");
			}
			return cast(daoClass, daoList[0]);
		}else{
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends DAO> T getOne(DAO dao) throws DatabaseException{
		return (T) getOne(getDaoClass(dao.getModelName()),
				ApiUtils.getFilters(db.getModelMetaDataDefinition(),dao, db.prependTableName()));
	}

	/**
	 * The primary keys should have defaults on the database  to make this work
	 */
	@Override
	public <T extends DAO> T insert(DAO dao, boolean excludePrimaryKeys)  throws DatabaseException{
		ModelDef model = db.getModelMetaDataDefinition().getDefinition(dao.getModelName());
		if(excludePrimaryKeys){
			dao.add_IgnoreColumn( model.getPrimaryAttributes());
		}
		return insertRecord(dao, model, true);
	} 
	@Override
	public <T extends DAO> T insert(DAO dao)  throws DatabaseException{
		ModelDef model = db.getModelMetaDataDefinition().getDefinition(dao.getModelName());
		return insertRecord(dao, model, true);
	} 

	/**
	 * Insert the record without bothering changelog, to avoid infinite method recursive calls when inserting changelogs into record_changelog table
	 * @param dao
	 * @param model
	 * @return
	 * @throws DatabaseException
	 */
	@Override
	public <T extends DAO> T insertNoChangeLog(DAO dao, ModelDef model) throws DatabaseException{
		DAO ret = db.insert(dao, null, model, null);
		Class<? extends DAO> clazz = getDaoClass(dao.getModelName());
		return cast(clazz, ret);
	}

	@SuppressWarnings("unchecked")
	protected <T extends DAO> T insertRecord(DAO dao, ModelDef model, boolean autoCast) throws DatabaseException{
		DAO ret = db.insert(dao, null, model, null);
//		if(contextProvider != null && contextProvider.isEnableRecordChangelog() && contextProvider.isEnableInsertChangelog()){
//			contextProvider.recordInsertChange(dao);
//		}
		if(autoCast){
			Class<? extends DAO> clazz = getDaoClass(dao.getModelName());
			return cast(clazz, ret);
		}
		else{
			return (T) ret;
		}
	}

	/**
	 * Use this when checking the record exist in these table while using all the values as the filter
	 * 
	 */
	@SuppressWarnings("unchecked")
	public <T extends DAO> T match(DAO dao) throws DatabaseException{
		ModelDef model = db.getModelMetaDataDefinition().getDefinition((dao.getModelName()));
		return (T) getOne(getDaoClass(dao.getModelName()), ApiUtils.getPrimaryAndUniqueFilters(model,dao, db.prependTableName()));
	}

	@Override
	public void resetDB(){
		this.db = null;
	}

	@Override
	public <T extends DAO> T[] retrieveRecords(Query query) throws DatabaseException {
		return retrieveRecords(query, true);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends DAO> T[] retrieveRecords(Query query, boolean autoCast) throws DatabaseException {
		DAO[] daoList = null;
		SQL sql = db.buildSQL(db.getModelMetaDataDefinition(), query, false);
		daoList = execute(sql, query.getRenamedColumnPairs());
		db.correctDataTypes(daoList, query.getModel());
		if(daoList != null){
			for(DAO dao : daoList){
				dao.setRenamedColumns(query.getRenamedColumnPairs());
			}
		}

		if(autoCast){
			ModelDef model = query.getModel();
			Class<? extends DAO> clazz = null;
			if(model != null){
				clazz = getDaoClass(model.getModelName());
			}
			if(clazz != null && daoList != null){//no casting when model or clazz is null
				return cast(clazz, daoList);
			}
			else{
				return (T[]) daoList;//no cast
			}
		}
		else{
			return (T[]) daoList;
		}
	}

	@Override
	public void rollbackTransaction() throws DatabaseException{ 
		if(db!=null && !db.isClosed()) db.rollbackTransaction(); 
		isTransactionBased = false;
	}


	public <T extends DAO> T[] search(Query query, String keyword) throws DatabaseException {
		db.search(query, keyword);
		return retrieveRecords(query, true);
	}



	@Override
	public <T extends DAO > T[] execute(SQL sql) throws DatabaseException {
		return db.select(sql, null);
	}

	@Override
	public DAO[] execute(String sql, Object[] parameters) throws DatabaseException {
		return db.select(sql, parameters);
	}
	
	@Override
	public <T extends DAO > T[] execute(SQL sql, Map<String, ColumnPair> renamedColumns) throws DatabaseException {
		return db.select(sql, renamedColumns);
	}

//	@Override
//	public void setContextProvider(ContextProvider context) {
//		this.contextProvider = context;
//	}

	@Override
	public boolean setPrimaryConstraint(ModelDef model)
			throws DatabaseException {
		return db.setPrimaryConstraint(model);
	}
	@Override
	public boolean setForeignConstraint(ModelDef model)
			throws DatabaseException {
		return db.setForeignConstraint(model);
	}

	@Override
	public void startPrintSqls() {
		this.db.debugSql(true);
	}

	@Override
	public void stopPrintSqls() {
		this.db.debugSql(false);
	}



	public <T extends DAO> T update(DAO dao) throws DatabaseException{
		return update(dao, (Filter)null);
	}

	@Override
	public <T extends DAO> T update(DAO dao, Filter...filters) throws DatabaseException{
		ModelDef model = db.getModelMetaDataDefinition().getDefinition(dao.getModelName());
		return updateRecord(dao, model, filters);
	}


	private <T extends DAO> T updateRecord(DAO dao, ModelDef model, Filter[] filters) throws DatabaseException{
//		DAO[] affectedRecords = null;
//		if(contextProvider != null && contextProvider.isEnableRecordChangelog()){
//			affectedRecords = getAffectedRecords(model, filters);
//			System.err.println("There are "+affectedRecords.length+" affected record(s)");
//			for(DAO ar : affectedRecords){
//				System.err.println(ar);
//			}
//		}
		DAO ret = db.update(dao, model, filters);
		Class<? extends DAO> clazz = getDaoClass(dao.getModelName());
//		if(contextProvider != null && contextProvider.isEnableRecordChangelog()){
//			contextProvider.recordUpdateChange(dao, affectedRecords);
//		}
		return cast(clazz, ret);
	}

	@Override
	public long writeToBlob(byte[] buf) throws DatabaseException {
		long ret = db.writeToBlob(buf);
		return ret;
	}

	@Override
	public long writeToBlob(String filename) throws DatabaseException {
		long ret = db.writeToBlob(filename);
		return ret;
	}

	@Override
	public void writeToOutputStream(Long blob_data, OutputStream out)
			throws DatabaseException {
		db.writeToOutputStream(blob_data, out);
	}

	@Override
	public void setContext(String key, Object value) {
		context.put(key, value);
	}

	@Override
	public Map<String, Object> getContext() {
		return this.context;
	}

	@Override
	public Object getContext(String key) {
		return this.context.get(key);
	}

}

