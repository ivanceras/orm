package com.ivanceras.db.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ivanceras.db.model.ModelMetaData;
import com.ivanceras.db.shared.DAO;
import com.ivanceras.db.shared.Filter;
import com.ivanceras.db.shared.Order;
import com.ivanceras.db.shared.exception.DatabaseException;

/**
 * 
 * @author lee
 *
 */
public class Query {

	private List<ModelDef> involvedModels = new ArrayList<ModelDef>();
	private List<JoinPair> joinPairs = new LinkedList<JoinPair>() ;
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
	private List<Aggregate> aggregate = new ArrayList<Aggregate>();
	private List<String> groupedColumns = new ArrayList<String>();
	private ModelMetaData meta = null;
	private EntityManager em = null;
	private Map<String, Query> columnSubQuery = new HashMap<String, Query>();
	private Query baseQuery = null;
	private String queryString = null;
	private List<CombinedQuery> combinedQuery = new ArrayList<CombinedQuery>();
	private String baseQueryName;
	private Map<String, DeclaredQuery> declaredQuery = new LinkedHashMap<String, DeclaredQuery>();
	private List<WindowFunction> windowFunctions = new ArrayList<WindowFunction>();
	private QueryAnalysis analyzer;

	public Query(EntityManager em, Class<? extends DAO> daoClass) throws DatabaseException{
		this.em = em;
		ModelDef model = em.getDefinition(daoClass);
		meta = em.getDB().getModelMetaDataDefinition();
		analyzer = new QueryAnalysis(this); 
		involve(model);
	}

	public Query(ModelDef model){
		analyzer = new QueryAnalysis(this); 
		involve(model);
	}

	public Query(ModelMetaData meta, Class<? extends DAO> daoClass) throws DatabaseException{
		this.meta = meta;
		analyzer = new QueryAnalysis(this); 
		ModelDef model = meta.getDefinition(daoClass);
		involve(model);
	}

	public Query(String queryString){
		this.setQueryString(queryString);
		analyzer = new QueryAnalysis(this); 
	}

	public Query addAggregate(Aggregate... aggregate) {
		if(aggregate != null && aggregate.length > 0){
			this.enumerateColumns = false; // do not enumerate when there is an aggregate
			String[] column1 = new String[aggregate.length];
			String[] column2 = new String[aggregate.length];
			for(int i = 0; i < aggregate.length; i++){
				Aggregate aggr = aggregate[i];
				column1[i] = aggr.getColumn();
				column2[i] = aggr.getAsColumn();
				this.aggregate.add(aggr);

			}
		}
		return this;
	}

	public Query addColumnSubQuery(String column, Query subquery) {
		columnSubQuery.put(column, subquery);
		return this;
	}


	//	public Query(ModelMetaData meta, Class<? extends DAO> daoClass, TreeRecursion tree) throws DatabaseException{
	//		this.meta = meta;
	//		ModelDef model = meta.getDefinition(daoClass);
	//		analyzer = new QueryAnalysis(this); 
	//
	//		String declaration = "child";
	//		Query query1 = new Query(meta, daoClass);
	//		Filter[] startingFilter = tree.getFilters();
	//		query1.addFilter(startingFilter);
	//		Query query2 = new Query(meta, daoClass);
	//		ModelDef childModel = new ModelDef();
	//
	//		childModel.setModelName(declaration);
	//		involve(childModel);//First Involved Model is the Table to be Queried, So child is the first involved Model
	//		involve(model);//Then put in the rest
	//
	//		query2.addJoinPair(new JoinPair(meta.getDefinition(daoClass), childModel, new ColumnPair(tree.parentColumn, tree.column), Join.INNER));
	//		query1.combine(query2, Combine.UNION, false);
	//
	//		declare(declaration, null, query1, true);
	//		setSelectAllColumns();
	//
	//	}

	//	public Query(EntityManager em, Class<? extends DAO> daoClass, TreeRecursion tree) throws DatabaseException{
	//		this(em.getDB().getModelMetaDataDefinition(), daoClass, tree);
	//		this.em = em;
	//		analyzer = new QueryAnalysis(this); 
	//}

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

	public Query addJoinPair(JoinPair... joinPairs) {
		if(joinPairs != null && joinPairs.length > 0){
			for(JoinPair j : joinPairs){
				this.joinPairs.add(j);
				ModelDef model1 = j.getModel1();
				ModelDef model2 = j.getModel2();
				if(model1 != null){
					involve(model1);
				}
				if(model2 != null){
					involve(model2);
				}
			}
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

	public Query addWindowFunctions(WindowFunction windowFunctions) {
		this.windowFunctions.add(windowFunctions);
		return this;
	}

	public Query ascending(String... columns) {
		for(String c : columns){
			addOrder(new Order(c, Order.ASCENDING));
		}
		return this;
	}

	public Query combine(Query query, String combineType, boolean all){
		String combineModifier = null;
		if(all){
			combineModifier = Combine.ALL;
		}
		combinedQuery.add(new CombinedQuery(query, combineType, combineModifier));
		return this;
	}

	public Query copy(){

		Query copy = new Query(getModel());
		copy.involvedModels = involvedModels;
		copy.joinPairs = joinPairs;
		copy.orders = orders;
		copy.filters = filters;
		copy.page = page;
		copy.itemsPerPage = itemsPerPage;
		copy.selectAllColumns = selectAllColumns;
		copy.enumerateColumns = enumerateColumns;
		copy.distinct = distinct;
		copy.distinctColumns = distinctColumns;
		copy.keysOnly = keysOnly;
		copy.aggregate = aggregate;
		copy.groupedColumns = groupedColumns;
		copy.meta = meta;
		copy.em = em;
		copy.columnSubQuery = columnSubQuery;
		copy.baseQuery = baseQuery;
		copy.queryString = queryString;
		copy.combinedQuery = combinedQuery;
		copy.baseQueryName = baseQueryName;
		copy.declaredQuery = declaredQuery;
		copy.windowFunctions = windowFunctions;

		return copy;
	}

	/**
	 * When there is a need to have an inner query, 
	 * this will be using with query in postgresql http://www.postgresql.org/docs/9.1/static/queries-with.html
	 * @param name
	 * @param columns
	 * @param query
	 * @param recursive
	 */
	public Query declare(String name, String[] columns, Query query, boolean recursive){
		if(columns == null){//select all columns when no columns is specified
			query.setSelectAllColumns();
		}
		declaredQuery.put(name, new DeclaredQuery(name, columns, query, recursive));
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

	public Aggregate[] getAggregate() {
		return aggregate.toArray(new Aggregate[aggregate.size()]);
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

	public CombinedQuery[] getCombinedQuery(){
		return combinedQuery.toArray(new CombinedQuery[combinedQuery.size()]);
	}

	public Map<String, DeclaredQuery> getDeclaredQueries(){
		if(declaredQuery.size() == 0){
			return null;
		}
		return declaredQuery;
	}

	public DeclaredQuery getDeclaredQuery(String name){
		return declaredQuery.get(name);
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

	public JoinPair[] getJoinPairs() {
		if(joinPairs.size() == 0 ){
			return null;
		}
		return joinPairs.toArray(new JoinPair[joinPairs.size()]);
	}

	public Boolean getKeysOnly() {
		return keysOnly;
	}

	public Integer getLimit(){
		return itemsPerPage;
	}

	public ModelDef getModel(){
		if(involvedModels.size() > 0){
			return involvedModels.get(0);
		}
		return null;
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

	public String getRenamed(ModelDef model, String column){
		return analyzer.getRenamed(model, column);
	}

	public Map<String, ColumnPair> getRenamedColumnPairs(){
		return analyzer.getRenamedColumnPairs();
	}

	public Boolean getSelectAllColumns() {
		return selectAllColumns;
	}

	public List<WindowFunction> getWindowFunctions() {
		return windowFunctions;
	}

	private void involve(ModelDef model){
		if(!involvedModels.contains(model)){
			analyzer.determineConflictingColumns(model);
			involvedModels.add(model);
		}
	}

	public boolean isEnumerateColumns() {
		return enumerateColumns;
	}

	public void rename(Class<? extends DAO> daoClass, String column, String asColumn){
		ModelDef model = meta.getDefinition(daoClass);
		analyzer.rename(model, column, asColumn);
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


	public Query setDistinctColumns(String[] distinctColumns) {
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

	//	private void setMeta(ModelMetaData meta){
	//		this.meta = meta;
	//		analyzer = new QueryAnalysis(this); 
	//	}

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

	public boolean hasConflictedColumn(String column) {
		return analyzer.hasConflictedColumn(column);
	}

	public <T extends DAO> T[] execute() throws DatabaseException {
		return em.retrieveRecords(this);
	}


}
