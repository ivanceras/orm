package com.ivanceras.db.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.ivanceras.db.api.Join.JoinModifier;
import com.ivanceras.db.api.Join.JoinType;
import com.ivanceras.db.model.ModelMetaData;
import com.ivanceras.db.shared.DAO;
import com.ivanceras.db.shared.Filter;
import com.ivanceras.db.shared.Order;
import com.ivanceras.db.shared.exception.DatabaseException;
import com.ivanceras.keyword.sql.SQL;

/**
 * 
 * @author lee
 *
 */
public class Query {

	private Set<ModelDef> involvedModels = new HashSet<ModelDef>();
	private List<Join> joins = new LinkedList<Join>();
	private List<Order> orders = new LinkedList<Order>();
	private List<Filter> filters = new LinkedList<Filter>();
	private Integer page;
	private Integer itemsPerPage;
	private Boolean selectAllColumns = false;
	private boolean enumerateColumns = true;
	private Boolean distinct;
	private List<String> distinctColumns = new ArrayList<String>();
	/**
	 * Ignore column feature when updating objects
	 */
	private List<String> excludedColumns = new ArrayList<String>();
	private Boolean keysOnly;
	private List<String> groupedColumns = new ArrayList<String>();
	private EntityManager em = null;
	private Map<String, Query> columnSubQuery = new HashMap<String, Query>();
	private Query baseQuery = null;
	private String queryString = null;
	private String baseQueryName;
	private Map<String, Query> declaredQuery = new LinkedHashMap<String, Query>();
	private Map<String, SQL> declaredSQL = new LinkedHashMap<String, SQL>();
	private String selectTable;
	private ModelDef selectModel;

	
	public Query(){

	}
	
	public Query(EntityManager em) throws DatabaseException{
		this.em = em;
	}
	
	public Query(EntityManager em, Class<? extends DAO> daoClass) throws DatabaseException{
		this.em = em;
		involve(daoClass);
	}
	
	

	


	public Query addColumnSubQuery(String column, Query subquery) {
		columnSubQuery.put(column, subquery);
		return this;
	}


	public String getSelectTable(){
		return selectTable;
	}


	public Query addFilter(Filter... filters) {
		if(filters != null){
			for(Filter f : filters){
				this.filters.add(f);
			}
		}
		return this;
	}

	public Query addGroupedColumns(String modelName, String groupedColumn, String function) {
		StringBuffer colC = new StringBuffer();
		if(function != null){
			colC.append(function+"(");
		}
		if(modelName != null){
			colC.append(modelName+".");
		}
		colC.append(groupedColumn);
		if(function != null){
			colC.append(")");
		}
		this.groupedColumns.add(colC.toString());
		return this;
	}

	public Query addGroupedColumns(String modelName, String[] groupedColumns) {
		for(String col : groupedColumns){
			StringBuffer colC = new StringBuffer();
			if(modelName != null){
				colC.append(modelName+".");
			}
			colC.append(col);
			this.groupedColumns.add(colC.toString());
		}
		return this;
	}


	public Query addOrder(Order... orders) {
		if(orders != null && orders.length > 0){
			for(Order o : orders){
				this.orders.add(o);
			}
		}
		return this;
	}

	public Query ascending(String... columns) {
		for(String c : columns){
			addOrder(new Order(c, Order.ASCENDING));
		}
		return this;
	}



	/**
	 * When there is a need to have an inner query, 
	 * this will be using with query in postgresql http://www.postgresql.org/docs/9.1/static/queries-with.html
	 * @param name
	 * @param columns
	 * @param query
	 * @param recursive
	 */
	public Query declare(String name, Query query){
		setSelectAllColumns();
		declaredQuery.put(name, query);
		return this;
	}

	public Query declare(String name, SQL sql){
		declaredSQL.put(name, sql);
		return this;
	}

	public Query descending(String... columns) {
		for(String c : columns){
			addOrder(new Order(c, Order.DESCENDING));
		}
		return this;
	}

	public Query excludeColumns(String... excludeColumns) {
		if(excludeColumns != null){
			for(String c : excludeColumns){
				this.excludedColumns.add(c);
			}
		}
		return this;
	}

	public Query filter(String column, String operator, Object value) {
		filters.add(new Filter(column, operator, value));
		return this;
	}

	public Query getBaseQuery() {
		return baseQuery;
	}

	public String getBaseQueryName() {
		return baseQueryName;
	}

	public Map<String, Query> getColumnSubQuery(){
		return columnSubQuery;
	}

	public Map<String, Query> getDeclaredQueries(){
		if(declaredQuery.size() == 0){
			return null;
		}
		return declaredQuery;
	}

	public Boolean getDistinct() {
		return distinct;
	}

	public String[] getDistinctColumns() {
		if(distinctColumns.size() == 0 ){
			return null;
		}
		return distinctColumns.toArray(new String[distinctColumns.size()]);
	}

	public String[] getExcludedColumns() {
		return excludedColumns.toArray(new String[excludedColumns.size()]);
	} 


	public Filter[] getFilters() {
		if(filters.size() == 0 ){
			return null;
		}
		return filters.toArray(new Filter[filters.size()]);
	}

	public String[] getGroupedColumns() {
		if(groupedColumns.size() == 0 ){
			return null;
		}
		return groupedColumns.toArray(new String[groupedColumns.size()]);
	}

	public ModelDef[] getInvolvedModels(){
		return involvedModels.toArray(new ModelDef[involvedModels.size()]);
	}

	public Integer getItemsPerPage() {
		return itemsPerPage;
	}

	public Boolean getKeysOnly() {
		return keysOnly;
	}

	public Integer getLimit(){
		return itemsPerPage;
	}

	public Long getOffset(){
		if(page == null){
			return null;
		}
		else if(page != null && page <= 0){
			String msg = "Page shoud be positive integer starting from 1";
			System.err.println(msg);
		}
		else if(itemsPerPage != null){
			Long offset = (long) (((long)page -1) * (long)itemsPerPage); 
			return offset;
		}
		return null;
	}

	public Order[] getOrders() {
		if(orders.size() == 0 ){
			return null;
		}
		return orders.toArray(new Order[orders.size()]);
	}

	public Integer getPage() {
		return page;
	}

	public String getQueryString() {
		return queryString;
	}




	public boolean isEnumerateColumns() {
		return enumerateColumns;
	}

	public Query setBaseQuery(Query baseQuery, String baseQueryName) {
		this.baseQuery = baseQuery;
		this.baseQueryName = baseQueryName;
		return this;
	}

	public void setBaseQueryName(String baseQueryName) {
		this.baseQueryName = baseQueryName;
	}

	public Query setDistinct() {
		this.distinct = true;
		return this;
	}


	public Query setDistinct(Boolean distinct) {
		this.distinct = distinct;
		return this;
	}


	public Query setDistinctColumns(String... distinctColumns) {
		if(distinctColumns != null && distinctColumns.length > 0){
			for(String col : distinctColumns){
				this.distinctColumns.add(col);
			}
			this.distinct = true;
		}
		return this;
	}

	public Query setEnumerateColumns(boolean enumerateColumns) {
		this.enumerateColumns = enumerateColumns;
		return this;
	}

	public Query setItemsPerPage(Integer itemsPerPage) {
		this.itemsPerPage = itemsPerPage;
		return this;
	}

	public Query setKeysOnly() {
		this.keysOnly = true;
		return this;
	}

	public Query setKeysOnly(Boolean keysOnly) {
		this.keysOnly = keysOnly;
		return this;
	}

	public Query setLimit(Integer limit){
		itemsPerPage = limit;
		return this;
	}

	public Query setPage(Integer page) {
		this.page = page;
		return this;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public Query setSelectAllColumns() {
		this.selectAllColumns = true;
		this.enumerateColumns = false;
		return this;
	}

	public Query setSelectAllColumns(Boolean selectAllColumns) {
		this.selectAllColumns = selectAllColumns;
		return this;
	}


	public <T extends DAO> T[] execute() throws DatabaseException {
		return em.retrieveRecords(this);
	}

	public Map<String, SQL> getDeclaredSQL() {
		if(declaredSQL.size() > 0){
			return this.declaredSQL;
		}else{
			return null;
		}
	}

	public void selectFromTable(String selectTable) {
		this.selectTable = selectTable;
	}

	private Query join(Join join) throws DatabaseException{
		this.joins.add(join);
		involve(join.getDaoClass());
		return this;
	}


	public Query innerJoin(Class<? extends DAO> daoClass, String onColumn1, String onColumn2) throws DatabaseException{
		Join join = new Join(daoClass, onColumn1, onColumn2);
		join.setType(JoinType.INNER);
		join(join);
		return this;
	}
	public Query leftJoin(Class<? extends DAO> daoClass, String onColumn1, String onColumn2) throws DatabaseException{
		Join join = new Join(daoClass, onColumn1, onColumn2);
		join.setModifier(JoinModifier.LEFT);
		join(join);
		return this;
	}

	public Query rightJoin(Class<? extends DAO> daoClass, String onColumn1, String onColumn2) throws DatabaseException{
		Join join = new Join(daoClass, onColumn1, onColumn2);
		join.setModifier(JoinModifier.RIGHT);
		join(join);
		return this;
	}
	public List<Join> getJoins(){
		if(joins.size() > 0){
			return this.joins;
		}
		else{
			return null;
		}
	}

	private Map<String, Pair[]> renamedColumnPairs = new HashMap<String, Pair[]>();
	private boolean hasProcessedConflicts = false;;

	public String getRenamed(String tableName, String column){
		if(renamedColumnPairs.containsKey(tableName)){
			Pair[] pairs = renamedColumnPairs.get(tableName);
			if(pairs != null && pairs.length > 0){
				for(Pair pair : pairs){
					if(column != null && column.equals(pair.getLeft())){
						return pair.getRight();
					}
				}
			}
		}
		return null;
	}
	
	
	public Map<String, Pair[]> getRenamedFields(){
		if(!hasProcessedConflicts ){
			processConflicts();
			hasProcessedConflicts = true;
		}
		return renamedColumnPairs;
	}

	private void involve(Class<? extends DAO> daoClass) throws DatabaseException{
		if(em == null){
			throw new DatabaseException("EntityManager should be supplied");
		}
		ModelDef model = em.getDefinition(daoClass);
		involve(model);
	}
	
	private void involve(ModelDef model){
		involvedModels.add(model);
	}

	void processConflicts(){
		System.err.println("Processing conflicts...");
		ModelDef[] involvedModels = getInvolvedModels();
		for(ModelDef inv : involvedModels){
			recordConflicts(inv);
		}
	}

	void recordConflicts(ModelDef model){
		ModelDef[] involvedModels = getInvolvedModels();
		for(ModelDef inv : involvedModels){
			if(!inv.equals(model)){//skip comparing to itself
				System.err.println("Now processing conflicts in "+model.getTableName());
				String[] sameAttributes = inv.getSameAttributes(model);
				//put each table to rename
				if(sameAttributes != null && sameAttributes.length > 0){
					String tableName = model.getTableName();
					rename(tableName, sameAttributes);
					System.err.println("There are the conflicts in "+tableName+" "+Arrays.asList(sameAttributes));
				}
			}
		}
	}

	private void rename(String tableName, String[] sameAttributes) {
		if(tableName != null && sameAttributes != null){
			Pair[] existingPairs = renamedColumnPairs.get(tableName);
			String[] intersection  = getIntersection(existingPairs, sameAttributes);
			Pair[] pairs = new Pair[intersection.length];
			for(int i = 0; i < intersection.length; i++){
				String column = intersection[i];
				String asColumn = tableName+"_"+column;
				pairs[i] = new Pair(column, asColumn);
			}
			renamedColumnPairs.put(tableName, pairs);
		}
	}

	private String[] getIntersection(Pair[] existingPairs,
			String[] sameAttributes) {
		Set<String> intersection = new TreeSet<String>();
		if(existingPairs != null){
			for(Pair exp : existingPairs){
				String column1 = exp.getLeft();
				intersection.add(column1);
			}
		}
		if(sameAttributes != null ){
			for(String sa : sameAttributes){
				intersection.add(sa);
			}
		}
		return intersection.toArray(new String[intersection.size()]);
	}

	public Boolean getSelectAllColumns() {
		return selectAllColumns;
	}

	public ModelDef getSelectModel() {
		return selectModel;
	}

	public void setSelectModel(ModelDef selectModel) {
		this.selectModel = selectModel;
	}

}
