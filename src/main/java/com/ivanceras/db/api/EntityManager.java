package com.ivanceras.db.api;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.ivanceras.db.shared.DAO;
import com.ivanceras.db.shared.Filter;
import com.ivanceras.db.shared.Order;
import com.ivanceras.db.shared.exception.DatabaseException;
import com.ivanceras.fluent.sql.SQL;

public interface EntityManager{


	/**
	 * @author lee
	 * @throws DatabaseException
	 */
	public void beginTransaction() throws DatabaseException;

	public <T extends DAO> T cast(Class<? extends DAO> daoClass, DAO dao) throws DatabaseException;

	public <T extends DAO> T[] cast(Class<? extends DAO> daoClass, DAO[] daoList)
			throws DatabaseException;

	public void commitTransaction() throws DatabaseException;

	public int count(Query query) throws DatabaseException;

	public boolean createModel(ModelDef model) throws DatabaseException;

	public boolean createSchema(String schema) throws DatabaseException;

	public int delete(Class<? extends DAO> daoClass, Filter... filters) throws DatabaseException;

	public int delete(DAO dao) throws DatabaseException;

	public boolean drop(Class<? extends DAO> daoClass) throws DatabaseException;

	public boolean drop(Class<? extends DAO> daoClass, boolean forced) throws DatabaseException;

	public void dropNameSpace(String schema, boolean forced) throws DatabaseException;

	public int empty(Class<? extends DAO> daoClass) throws DatabaseException;

	public int empty(Class<? extends DAO>daoClass, boolean forced) throws DatabaseException;

	public boolean exist(DAO dao) throws DatabaseException;

	public boolean existModel(ModelDef model) throws DatabaseException;

	public void finalize() throws Throwable;

	public <T extends DAO> T[] getAll(Class<? extends T> daoClass) throws DatabaseException;


	public <T extends DAO> T[] getAll(Class<? extends T> daoClass, Boolean distinct) throws DatabaseException;

	public <T extends DAO> T[] getAll(Class<? extends T> daoClass, Filter... filters) throws DatabaseException;

	public <T extends DAO> T[] getAll(Class<? extends T> daoClass, Order order, Filter... filters) throws DatabaseException;

	public byte[] getBlob(long oid) throws DatabaseException;

	public Class<? extends DAO> getDaoClass(String modelName) throws DatabaseException;

	public IDatabase getDB();

	public ModelDef getDefinition(Class<? extends DAO> daoClass) throws DatabaseException;

	public <T extends DAO> T getMatch(DAO dao, Boolean exactMatch)  throws DatabaseException;

	public String getModelName(Class<? extends DAO> daoClass) throws DatabaseException;

	public <T extends DAO> T getOne(Class<? extends T> daoClass, Filter... filters)  throws DatabaseException;

	public <T extends DAO> T getOne(DAO dao) throws DatabaseException;

	public <T extends DAO> T insert(DAO dao) throws DatabaseException;
	
	public <T extends DAO> T insert(DAO dao, boolean excludePrimaryKeys) throws DatabaseException;

	public <T extends DAO> T insertNoChangeLog(DAO dao, ModelDef model) throws DatabaseException;

	public <T extends DAO> T match(DAO dao) throws DatabaseException;

	public void resetDB();

	public <T extends DAO> T[] retrieveRecords(Query query) throws DatabaseException;

	public <T extends DAO> T[] retrieveRecords(Query query, boolean autoCast) throws DatabaseException;

	public void rollbackTransaction() throws DatabaseException;

	public <T extends DAO > T[]  execute(SQL sql) throws DatabaseException;

	public <T extends DAO > T[] execute(SQL sql, Map<String, ColumnPair> renamedColumns)
			throws DatabaseException;

//	void setContextProvider(ContextProvider context);

	public boolean setForeignConstraint(ModelDef model) throws DatabaseException;

	
	public void startPrintSqls();

	public void stopPrintSqls();

	public <T extends DAO> T update(DAO dao) throws DatabaseException;

	public <T extends DAO> T update(DAO dao, Filter...filters) throws DatabaseException;

	long writeToBlob(byte[] buf) throws DatabaseException;

	public long writeToBlob(String filename) throws DatabaseException;

	public void writeToOutputStream(Long blob_data, OutputStream out) throws DatabaseException;

	public void setContext(String key, Object value);
	
	public Map<String, Object> getContext();
	
	public Object getContext(String key);

	
}
