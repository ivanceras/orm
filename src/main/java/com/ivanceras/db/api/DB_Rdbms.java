/*******************************************************************************
 * Copyright by CMIL
 ******************************************************************************/
package com.ivanceras.db.api;

import static com.ivanceras.fluent.sql.SQL.Statics.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ivanceras.commons.strings.CStringUtils;
import com.ivanceras.db.api.JoinPair.Join;
import com.ivanceras.db.model.ModelMetaData;
import com.ivanceras.db.shared.DAO;
import com.ivanceras.db.shared.Filter;
import com.ivanceras.db.shared.Order;
import com.ivanceras.db.shared.datatype.DataTypeDB;
import com.ivanceras.db.shared.exception.DataTypeException;
import com.ivanceras.db.shared.exception.DatabaseException;
import com.ivanceras.db.shared.util.SpecialCase;
import com.ivanceras.fluent.sql.SQL;

public abstract class DB_Rdbms{

	public static final String subclasstable = "subclasstable";

	private void addJoins(SQL sql, Query query){
		JoinPair[] joinPairs = query.getJoinPairs();
		if(joinPairs != null){
			for(JoinPair pair : joinPairs){
				String table2 = getTable(pair.getModel2());
				if(pair.getJoinType().equals(Join.LEFT)){
					sql.LEFT_JOIN(table2);
				}
				else if(pair.getJoinType().equals(Join.RIGHT)){
					sql.RIGHT_JOIN(table2);
				}
				else if(pair.getJoinType().equals(Join.CROSS)){
					sql.CROSS_JOIN(table2);
				}
				else if(pair.getJoinType().equals(Join.INNER)){
					sql.INNER_JOIN(table2);
				}
				else if(pair.getJoinType().equals(Join.LEFT_OUTER)){
					sql.LEFT_OUTER_JOIN(table2);
				}
				else if(pair.getJoinType().equals(Join.RIGHT_OUTER)){
					sql.RIGHT_OUTER_JOIN(table2);
				}
				ColumnPair[] columnPairs = pair.getColumnPairs();
				for(ColumnPair cpair : columnPairs){
					String column1 = cpair.getColumn1();
					String column2 = cpair.getColumn2();
					sql.ON(column1, column2);
				}
			}
		}

	}


	protected abstract SQL buildAggregateQuery(ModelMetaData meta, Aggregate[] aggregates, boolean doComma);

	public SQL[] buildColumnCommentStatement(ModelDef model) throws DatabaseException{

		String schema = model.getNamespace();
		schema = getDBElementName(model,schema);
		String table = getDBTableName(model);
		String[] columns = model.getAttributes();
		SQL[] sql1 = new SQL[columns.length];
		String[] comments = model.getAttributeComments();
		if(comments == null){
			return null;
		}
		for(int i = 0; i < columns.length; i++){
			if(comments[i] != null){
				columns[i] = getDBElementName(model,columns[i]);
				SQL ql = new SQL();
				ql.keyword("COMMENT ON COLUMN");
				StringBuilder columnName = new StringBuilder();
				if(schema != null && useSchema()){
					columnName.append(schema+".");
				}
				columnName.append(table+"."+columns[i]);
				ql.FIELD(columnName.toString())
				.keyword("IS")
				.keyword("$$")
				.FIELD(comments[i])
				.keyword("$$");
				sql1[i] = ql;
			}
		}
		return sql1;
	}
	protected SQL buildCreateSchemaStatement(String schema){
		return CREATE().SCHEMA(schema);
	}

	public SQL buildCreateTableStatement(ModelDef model) throws DatabaseException{
		String schema = model.getNamespace();
		schema = getDBElementName(model,schema);

		String table = getDBTableName(model);
		if(table==null) throw new DatabaseException("No table indicated");

		String[] columns = model.getAttributes();
		for(int i = 0; i < columns.length; i++){
			columns[i] = getDBElementName(model,columns[i]);
		}

		String[] dataTypes = model.getDataTypes();

		String autoIncrementColumn = model.getGeneratedAttribute();
		autoIncrementColumn = getDBElementName(model,autoIncrementColumn);

		String[] primaryColumns = model.getPrimaryAttributes();
		if(primaryColumns != null){
			for(int i = 0; i < primaryColumns.length; i++){
				primaryColumns[i] = getDBElementName(model,primaryColumns[i]);
			}
		}
		String[] uniqueColumns = model.getUniqueAttributes();
		if(uniqueColumns != null){
			for(int i = 0; i < uniqueColumns.length; i++){
				uniqueColumns[i] = getDBElementName(model,uniqueColumns[i]);
			}
		}
		String[] hasOne = model.getHasOne();
		if(hasOne != null){
			for(int i = 0; i < hasOne.length; i++){
				hasOne[i] = getDBElementName(model,hasOne[i]);
			}
		}

		StringBuilder tableName = new StringBuilder();
		if(schema != null && useSchema()){
			tableName.append(schema+".");
		}
		tableName.append(table);
		SQL sql1 = CREATE_TABLE(tableName.toString());
		sql1.openParen();
		boolean doComma = false;
		for(int i = 0; i < columns.length; i++){
			if(doComma){sql1.comma();}else{doComma=true;}
			sql1.FIELD(columns[i]);
			String dbDataType = getEquivalentDBDataType(dataTypes[i]);
			if(dbDataType != null){
				sql1.keyword(dbDataType);
			}
		}
		if(getStorageEngine() != null){
			sql1.keyword(getStorageEngine());
		}
		sql1.closeParen();
		return sql1;
	}

	protected abstract SQL buildDeclaredQuery(ModelMetaData meta, Map<String, DeclaredQuery> declaredQueries);

	protected SQL buildDeleteStatement(ModelMetaData meta, ModelDef model, Filter[] filters) throws DatabaseException{
		String schema = model.getNamespace();
		schema = getDBElementName(model,schema);
		String table = getDBTableName(model);
		if(table==null) throw new DatabaseException("No table indicated");

		if(schema != null && useSchema()){
			table = schema+"."+table;
		}
		SQL sql1= DELETE().FROM(table);

		buildWhereClause(sql1, filters);
		return sql1;
	}

	public SQL buildDropSchemaStatement(String schema, boolean forced){
		SQL sql = DROP()
				.SCHEMA().IF_EXISTS()
				.FIELD(schema);
		if(forced){
			sql.keyword(forceKeyword());
		}
		return sql;
	}

	protected SQL buildDropStatement(ModelDef model, boolean forced) {
		String schema = model.getNamespace();
		schema = getDBElementName(model,schema);
		String table = getDBTableName(model);

		SQL sql1 = DROP_TABLE();
		if(supportExistChecking()){
			sql1.IF_EXISTS();
		}
		StringBuilder tableName = new StringBuilder();
		if(schema != null && useSchema()){
			tableName.append(schema+".");
		}
		tableName.append(table);
		sql1.FIELD(tableName.toString());
		if(forced){
			sql1.keyword(forceKeyword());
		}
		return sql1;
	}

	protected SQL buildEmptyTableStatement(ModelDef model, boolean forced) {
		String schema = model.getNamespace();
		schema = getDBElementName(model,schema);
		String table = getDBTableName(model);


		StringBuilder tableName = new StringBuilder();
		if(schema != null && useSchema()){
			tableName.append(schema+".");
		}
		tableName.append(table);
		SQL sql1 = TRUNCATE_TABLE(tableName.toString());
		if(forced){
			sql1.keyword(" "+forceKeyword());
		}
		return sql1;
	}

	protected SQL buildPrimaryContraintStatement(ModelDef model) throws DatabaseException{
		String schema = model.getNamespace();
		schema = getDBElementName(model,schema);
		String[] primaryAttributes = model.getPrimaryAttributes();
		SQL sql1 = null;
		if(primaryAttributes != null && primaryAttributes.length > 0){
			String table = getDBTableName(model);
			if(table==null) throw new DatabaseException("No table indicated");
			StringBuilder tableName = new StringBuilder();
			if(schema != null && useSchema()){
				tableName.append(schema+".");
			}
			tableName.append(model.getTableName());
			sql1 = ALTER_TABLE(tableName.toString());

			String constraintName = table+"_pkey";
			sql1.ADD().CONSTRAINT(constraintName);
			sql1.PRIMARY_KEY(primaryAttributes);
		}else{
			System.err.println("No primary key for: "+model.getTableName());
		}
		return sql1;
	}

	protected SQL buildForeignContraintStatement(ModelDef model) throws DatabaseException{
		String schema = model.getNamespace();
		schema = getDBElementName(model,schema);
		String[] hasOne = model.getHasOne();
		String table = getDBTableName(model);
		if(table==null) throw new DatabaseException("No table indicated");
		SQL sql1 = null;
		if(hasOne != null && hasOne.length > 0){
			for(int i = 0; i < hasOne.length; i++){
				StringBuilder tableName = new StringBuilder();
				if(schema != null && useSchema()){
					tableName.append(schema+".");
				}
				tableName.append(model.getTableName());
				sql1 = ALTER_TABLE(tableName.toString());
				String hasOneSchema = getDBElementName(model, getTableSchema(hasOne[i]));

				String constraintName = table+"_"+CStringUtils.capitalize(model.getHasOneLocalColumn()[i])+"_fkey";
				sql1.ADD().CONSTRAINT(constraintName);
				sql1.FOREIGN_KEY(model.getHasOneLocalColumn()[i]);
				StringBuilder referencedTable = new StringBuilder();
				if(hasOneSchema != null && useSchema()){
					referencedTable.append(hasOneSchema+".");
				}
				referencedTable.append(hasOne[i]);
				sql1.REFERENCES(referencedTable.toString(), model.getHasOneReferencedColumn()[i]);
				sql1.ON_UPDATE().CASCADE()
				.ON_DELETE().RESTRICT()
				.DEFERRABLE().INITIALLY_DEFERRED();
			}

		}
		return sql1;
	}

	/**
	 * 
	 * @param dao
	 * @param meta
	 * @param model
	 * @param doReturnColumn
	 * @param query
	 * @param insertAsIs do not allow computation of primary keys or auto-increment columns
	 * @return
	 */
	protected SQL buildInsertStatement(DAO dao, ModelMetaData meta, ModelDef model, Query query) {
		String schema = model.getNamespace();
		schema = getDBElementName(model,schema);
		String table = getDBTableName(model);
		String[] columns = model.getAttributes();
		String[] correctedColumns = new String[columns.length];
		String[] ignoredColumns = dao.get_IgnoreColumn();
		String[] curatedIgnoredColumns = curateIgnoredColumns(ignoredColumns);
		String[] defaultedColumValues = dao.get_DefaultedColumnValues();
		for(int i = 0; i < columns.length; i++){
			correctedColumns[i] = getDBElementName(model,columns[i]);
		}

		if(schema != null && useSchema()){
			table = schema+"."+table;
		}
		SQL sql1 = INSERT().INTO(table);
		sql1.openParen();
		if(columns != null){
			List<String> finalColumns = new ArrayList<String>();
			for(int c = 0; c < columns.length; c++){
				if(!CStringUtils.inArray(curatedIgnoredColumns, columns[c])){
					finalColumns.add(columns[c]);
				}
				if(CStringUtils.inArray(defaultedColumValues, columns[c])){
					sql1.keyword("DEFAULT");
				}
			}
			sql1.FIELD(finalColumns.toArray(new String[finalColumns.size()]));
		}
		sql1.closeParen();
		if(query != null){
			SQL sql2 = buildSQL(meta, query, false);
			sql1.FIELD(sql2);
		}
		else{
			sql1.VALUES().openParen();
			boolean doComma = false;
			for(int i = 0; i < columns.length; i++){
				if(!CStringUtils.inArray(curatedIgnoredColumns, columns[i])){
					Object value = null;
					if(dao != null) {
						value = dao.get_Value(columns[i]);
					}
					//					value = getEquivalentDBObject(value);

					if(supportPreparedStatement()){
						if(doComma){sql1.comma();}else{doComma=true;}
						sql1.VALUE(value);
					}else{
						sql1.FIELD("'"+(value != null ? value.toString(): null)+"'");
					}
				}
			}
			sql1.closeParen();
		}
		return sql1;
	}


	//TODO: may include columns that are meant from other tables, needs improvement for identifying which table to correct from, this will introduce bug when joining multiple tables 
	//while ignoring specific table
	private String[] curateIgnoredColumns(String[] ignoredColumns) {
		if(ignoredColumns == null){
			return null;
		}else{
			System.out.println("ignored columns: "+Arrays.asList(ignoredColumns));
			String[] curated = new String[ignoredColumns.length];
			for(int i = 0; i < ignoredColumns.length; i++){
				if(ignoredColumns[i] != null){
					String[] splinters = ignoredColumns[i].split("\\.");
					if(splinters != null && splinters.length > 0){
						String last = splinters[splinters.length - 1];
						curated[i] = last;
					}
				}
			}
			System.out.println("curated: "+Arrays.asList(curated));
			return curated;
		}
	}


	public SQL buildRenameModel(ModelDef model, String newName) {
		String schema = model.getNamespace();
		StringBuilder tableName = new StringBuilder();
		if(schema != null && useSchema()){
			tableName.append(schema+".");
		}
		SQL sql1 = ALTER_TABLE(tableName.toString())
				.RENAME().TO(newName);
		return sql1;
	}

	public SQL buildSQL(ModelMetaData meta,Query query, boolean useCursor) {
		SQL sql1 = new SQL();
		List<String> mentionedColumns = new ArrayList<String>();

		ModelDef model = query.getModel();
		Filter[] filters = query.getFilters();
		Long offset = query.getOffset();
		Integer limit = query.getLimit();
		Order[] orders = query.getOrders();
		String[] excludedColumns = query.getExcludedColumns();

		String[] distinctColumns = query.getDistinctColumns();
		String[] groupedColumns = query.getGroupedColumns();

		String table = null;
		String schema = null;
		if(model != null){
			String[] subClasses = model.getSubClass();
			if(subClasses != null && subClasses.length > 0){
				Query subClassQuery = buildSubClassTableQuery(model);
				SQL sql2 = buildSQL(meta, subClassQuery, false);
				sql1.FIELD(sql2).AS("subclasstable");
			}
	
			schema = model.getNamespace();
			schema = getDBElementName(model,schema);
			table = getDBTableName(model);
		}
		String selectTable = query.getSelectTable();
		ModelDef[] involvedModels = query.getInvolvedModels();



		Map<String, DeclaredQuery> declaredQueries = query.getDeclaredQueries();
		SQL declaredEntry = null;
		if(declaredQueries != null){
			declaredEntry = buildDeclaredQuery(meta, declaredQueries);
			sql1.FIELD(declaredEntry);
		}
		if(distinctColumns != null && distinctColumns.length > 0){//custom distinct columns
			String[] distinctColumns1 = new String[distinctColumns.length];
			for(int i = 0; i < distinctColumns.length; i++){
				distinctColumns1[i] = getDBElementName(model,distinctColumns[i]);
				if(table!=null && prependTableName()){
					distinctColumns1[i] = table+"."+distinctColumns1[i];
				}
			}
			sql1.DISTINCT_ON(distinctColumns1);
		}

		Map<String, SQL> declaredSQL = query.getDeclaredSQL();
		if(declaredSQL != null){
			sql1.WITH();
			for(Entry<String, SQL> entry : declaredSQL.entrySet()){
				String name = entry.getKey();
				sql1.FIELD(name);
				SQL dsql = entry.getValue();
				sql1.AS(dsql);
			}
		}
		

		Boolean selectAllColumns = query.getSelectAllColumns();
		if(selectAllColumns != null &&selectAllColumns){
			sql1.SELECT("*");
			query.setEnumerateColumns(false);
			for(Entry<String, ColumnPair> entry: query.getRenamedColumnPairs().entrySet()){
				String tableName = getDBTableName(entry.getKey());
				String original = entry.getValue().getColumn1();
				String newName = entry.getValue().getColumn2();
				sql1.FIELD(tableName+"."+original).AS(newName);
			}
		}
		else{
			sql1.append(SELECT());
		}
		if(query.isEnumerateColumns()){
			for(ModelDef inv : involvedModels){
				String[] columns = inv.getAttributes();
				String invTable = getDBTableName(inv);
				if(columns != null){
					for(int i = 0; i < columns.length; i++){
						if(!CStringUtils.inArray(excludedColumns, columns[i])){
							String columnName = getDBElementName(inv,columns[i]);
							if(invTable!=null ){
								if(query.hasConflictedColumn(columns[i]) ||  prependTableName()){
									columnName = invTable+"."+columnName;
								}

							}
							String asColumn = query.getRenamed(inv, columns[i]);
							sql1.FIELD(columnName);
							if(asColumn != null){
								sql1.AS(asColumn);
							}
						}
					}
				}
			}
		}
		if(selectTable != null){
			sql1.FROM(selectTable);
		}
		else{
			String fromTable = null;
			if(table != null){
				fromTable = table;
			}
			if(schema != null && useSchema()){
				fromTable = schema+"."+fromTable;
			}
			if(fromTable != null){
				sql1.FROM(fromTable);
			}
		}
		if(query.getBaseQuery() != null){
			String baseQueryName = query.getBaseQueryName();
			SQL sql3 = buildSQL(meta, query.getBaseQuery(), useCursor);
			sql1.FROM(sql3).AS(baseQueryName);
			mentionedColumns.add(baseQueryName);
		}

		addJoins(sql1, query);
		buildWhereClause(sql1, filters);

		if(groupedColumns != null && groupedColumns.length > 0){
			sql1.GROUP_BY(groupedColumns);
		}
		if(orders != null && orders.length > 0){
			boolean doOrderBy = true;
			for(int i = 0; i < orders.length; i++){
				if(orders[i] != null){
					if(doOrderBy){
						sql1.ORDER_BY();
						doOrderBy = false;
					}
					if(orders[i].isAscending()){
						sql1.FIELD(orders[i].getColumn());
					}
					else{
						sql1.FIELD(orders[i].getColumn()).DESC();
					}
				}
			}
		}
		if(!useCursor){
			if(limit != null){
				sql1.LIMIT(limit);
			}
			if(offset != null ){
				sql1.OFFSET(offset.intValue());
			}
		}
		CombinedQuery[] combinedQueries = query.getCombinedQuery();
		if(combinedQueries != null){
			for(CombinedQuery cq : combinedQueries){
				sql1.FIELD(cq.getCombineType());
				String modifier = cq.getCombineModifier();
				if(modifier != null){
					sql1.FIELD(modifier);
				}
				SQL sql4 = buildSQL(meta, cq.getQuery(), useCursor);
				sql1.FIELD(sql4);
			}
		}

		return sql1;
	}

	protected abstract Query buildSubClassTableQuery(ModelDef model);

	public SQL buildTableCommentStatement(ModelDef model) throws DatabaseException{
		String schema = model.getNamespace();
		schema = getDBElementName(model,schema);
		String table = getDBTableName(model);
		String tableComment = model.getDescription();
		if(tableComment == null){
			return null;
		}
		SQL sql1 = new SQL();
		sql1.keyword("COMMENT").keyword("ON");
		StringBuilder tableName = new StringBuilder();
		if(schema != null && useSchema()){
			tableName.append(schema+".");
		}
		tableName.append(table);
		sql1.TABLE(tableName.toString());
		sql1.keyword("IS")
		.keyword("$$")
		.FIELD(tableComment)
		.keyword("$$");
		return sql1;
	}

	protected SQL buildUpdateStatement(ModelMetaData meta, DAO dao, ModelDef model, Filter[] filters) {
		String schema = model.getNamespace();
		schema = getDBElementName(model,schema);
		String table = getDBTableName(model);
		String[] columns = model.getAttributes();
		String[] ignoreColumns = dao.get_IgnoreColumn();
		String[] curatedIgnoredColumns = curateIgnoredColumns(ignoreColumns);
		String[] defaultedColumnValues = dao.get_DefaultedColumnValues();

		for(int i = 0; i < columns.length; i++){
			columns[i] = getDBElementName(model,columns[i]);
		}
		String autoIncrementColumn = model.getGeneratedAttribute();
		autoIncrementColumn = getDBElementName(model, autoIncrementColumn);

		StringBuilder tableName = new StringBuilder();
		if(schema != null && useSchema()){
			tableName.append(schema+".");
		}
		tableName.append(table);
		SQL sql1 = UPDATE(tableName.toString());

		boolean doComma = false;
		sql1.SET();
		for(int i = 0; i < columns.length; i++){
			if(curatedIgnoredColumns != null && CStringUtils.inArray(curatedIgnoredColumns, columns[i])){
				;
			}
			else{
				if(doComma){sql1.comma();}else{doComma = true;}
				Object value = null;
				if(dao != null) {
					value = dao.get_Value(columns[i]);
					//					value = getEquivalentDBObject(value);
				}
				if(CStringUtils.inArray(defaultedColumnValues, columns[i])){
					sql1.FIELD(columns[i]);
					sql1.EQUAL();
					sql1.FIELD("DEFAULT");
				}else{
					sql1.FIELD(columns[i]);
					sql1.EQUAL();
					if(supportPreparedStatement()){
						sql1.VALUE(value);
					}
					else{
						sql1.FIELD("'"+( value != null ? value.toString(): null )+"'");
					}
				}
			}
		}
		buildWhereClause(sql1, filters);
		return sql1;
	}


	private void buildWhereClause(SQL sql1,
			Filter[] filters) {
		if(filters != null){
			sql1.WHERE();
			boolean doAnd = false;
			for(Filter filter: filters){
				if(filter != null){
					if(doAnd){sql1.AND();}else{doAnd=true;}
					extractFilter(sql1, filter);
				}
			}
		}
	}

	protected abstract SQL buildWindowFunctions(ModelMetaData meta, List<WindowFunction> windowFunctions, boolean doComma);

	protected abstract boolean  caseSensitive();

	/**
	 * //some primary is defined together with auto increment column, such as SQLite
	 * @return
	 */

	protected boolean definePrimaryConstraint() {
		return true;
	}

	private void extractFilter(SQL sql1, Filter... filterList) {
		if(filterList != null){
			for(Filter filter : filterList){
				if(filter != null){
					Filter[] childFilters = filter.getFilterList();
					if(childFilters != null && childFilters.length > 0){
						sql1.openParen();
					}
					String con = filter.getConnector();// OR ,AND
					if(con!=null){
						sql1.keyword(con);
					}
					if(filter.attribute != null){
						sql1.FIELD(filter.attribute);
					}
					if(filter.operator != null){
						sql1.keyword(filter.operator);
					}
					if(filter.value != null){
						if(supportPreparedStatement()){
							sql1.VALUE(filter.value);
						}else{
							//at least do a sql escaping here
							String value = filter.value != null ? filter.value.toString() : null;
							value = CStringUtils.escapeSQL(value);
							sql1.FIELD("'"+value+"'");
						}
					}
					if(filter.query != null){
						SQL sql2 = buildSQL(null, filter.query, false);
						sql1.FIELD(sql2);
					}

					if(filter.getFilterSql() != null){
						sql1.FIELD(filter.getFilterSql());
					}

					if(childFilters != null && childFilters.length > 0){
						extractFilter(sql1, childFilters);
						sql1.closeParen();
					}
				}
			}
		}
	}


	abstract protected String forceKeyword();

	/**
	 * Different implementation accross database
	 * @return
	 */
	protected abstract String getAutoIncrementColumnConstraint();


	protected String getDBElementName(ModelDef model, String element) {
		return ApiUtils.getDBElementName(model, element);
	}

	protected String getDBTableName(ModelDef model) {
		if(model==null) return null;
		String tableName = model.getTableName();
		if(caseSensitive()){//if database implementation is case sensitive;
			return tableName;
		}
		if(tableName == null){
			tableName = model.getModelName();
		}
		if(model.isCaseSensitive() || SpecialCase.isSpecial(tableName)){
			return "\""+model.getTableName()+"\"";
		}
		else{
			return tableName;
		}
	}

	private String getDBTableName(String tableName) {
		if(SpecialCase.isSpecial(tableName)){
			return "\""+tableName.toLowerCase()+"\"";
		}
		else{
			return tableName;
		}
	}

	public String getEquivalentDBDataType(String genDataType) {
		return DataTypeDB.fromGenericType(genDataType);
	}
	protected abstract Object getEquivalentDBObject(Object record);


	protected abstract String getEquivalentGeneralDataType(String dbDataType) throws DataTypeException;

	abstract protected String getStorageEngine();

	protected String getTable(ModelDef model){
		StringBuilder sb = new StringBuilder();
		if(useSchema() && model.getNamespace() != null){
			sb.append(model.getNamespace()+".");
		}
		sb.append(model.getTableName());
		return sb.toString();

	}

	abstract protected String getTableSchema(String tableName) throws DatabaseException;

	protected boolean isCaseSensitive(String tableName, String realTableName){
		int cmp = realTableName.compareTo(tableName.toLowerCase());
		return !(cmp == 0);
	}

	protected abstract boolean namePrimaryKey();


	protected abstract boolean prependTableName();

	protected abstract boolean supportConstraints();

	protected abstract boolean supportExistChecking();

	protected abstract boolean supportPreparedStatement();

	abstract protected boolean useSchema();

	protected abstract boolean useTableKeyWord();

}
