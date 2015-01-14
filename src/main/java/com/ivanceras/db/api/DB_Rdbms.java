package com.ivanceras.db.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ivanceras.commons.strings.CStringUtils;
import com.ivanceras.db.api.Join.JoinModifier;
import com.ivanceras.db.api.Join.JoinType;
//import com.ivanceras.db.api.JoinPair.Join;
import com.ivanceras.db.model.ModelMetaData;
import com.ivanceras.db.shared.DAO;
import com.ivanceras.db.shared.Filter;
import com.ivanceras.db.shared.Filter.Connector;
import com.ivanceras.db.shared.Filter.Equality;
import com.ivanceras.db.shared.Order;
import com.ivanceras.db.shared.Order.Direction;
import com.ivanceras.db.shared.datatype.DataTypeDB;
import com.ivanceras.db.shared.exception.DataTypeException;
import com.ivanceras.db.shared.exception.DatabaseException;
import com.ivanceras.db.shared.util.SpecialCase;
import com.ivanceras.keyword.sql.SQL;

import static com.ivanceras.keyword.sql.SQLStatics.*;

public abstract class DB_Rdbms{

	public static final String subclasstable = "subclasstable";

	public SQL[] buildColumnCommentStatement(ModelDef model) throws DatabaseException{

		String schema = model.getNamespace();
		schema = getDBElementName(model,schema);
		String table = getDBTableName(model);
		String[] columns = model.getAttributes();
		SQL[] sql = new SQL[columns.length];
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
				sql[i] = ql;
			}
		}
		return sql;
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
		SQL sql = CREATE_TABLE(tableName.toString());
		sql.openParen();
		boolean doComma = false;
		for(int i = 0; i < columns.length; i++){
			if(doComma){sql.comma();}else{doComma=true;}
			sql.FIELD(columns[i]);
			String dbDataType = getEquivalentDBDataType(dataTypes[i]);
			if(dbDataType != null){
				sql.keyword(dbDataType);
			}
		}
		if(getStorageEngine() != null){
			sql.keyword(getStorageEngine());
		}
		sql.closeParen();
		return sql;
	}

	protected SQL buildDeleteStatement(ModelMetaData meta, ModelDef model, Filter[] filters) throws DatabaseException{
		String schema = model.getNamespace();
		schema = getDBElementName(model,schema);
		String table = getDBTableName(model);
		if(table==null) throw new DatabaseException("No table indicated");

		if(schema != null && useSchema()){
			table = schema+"."+table;
		}
		SQL sql= DELETE().FROM(table);

		buildWhereClause(sql, filters);
		return sql;
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

		SQL sql = DROP_TABLE();
		if(supportExistChecking()){
			sql.IF_EXISTS();
		}
		StringBuilder tableName = new StringBuilder();
		if(schema != null && useSchema()){
			tableName.append(schema+".");
		}
		tableName.append(table);
		sql.FIELD(tableName.toString());
		if(forced){
			sql.keyword(forceKeyword());
		}
		return sql;
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
		SQL sql = TRUNCATE_TABLE(tableName.toString());
		if(forced){
			sql.keyword(" "+forceKeyword());
		}
		return sql;
	}

	protected SQL buildPrimaryContraintStatement(ModelDef model) throws DatabaseException{
		String schema = model.getNamespace();
		schema = getDBElementName(model,schema);
		String[] primaryAttributes = model.getPrimaryAttributes();
		SQL sql = null;
		if(primaryAttributes != null && primaryAttributes.length > 0){
			String table = getDBTableName(model);
			if(table==null) throw new DatabaseException("No table indicated");
			StringBuilder tableName = new StringBuilder();
			if(schema != null && useSchema()){
				tableName.append(schema+".");
			}
			tableName.append(model.getTableName());
			sql = ALTER_TABLE(tableName.toString());

			String constraintName = table+"_pkey";
			sql.ADD().CONSTRAINT(constraintName);
			sql.PRIMARY_KEY(primaryAttributes);
		}else{
			System.err.println("No primary key for: "+model.getTableName());
		}
		return sql;
	}

	protected SQL buildForeignContraintStatement(ModelDef model) throws DatabaseException{
		String schema = model.getNamespace();
		schema = getDBElementName(model,schema);
		String[] hasOne = model.getHasOne();
		String table = getDBTableName(model);
		if(table==null) throw new DatabaseException("No table indicated");
		SQL sql = null;
		if(hasOne != null && hasOne.length > 0){
			for(int i = 0; i < hasOne.length; i++){
				StringBuilder tableName = new StringBuilder();
				if(schema != null && useSchema()){
					tableName.append(schema+".");
				}
				tableName.append(model.getTableName());
				sql = ALTER_TABLE(tableName.toString());
				String hasOneSchema = getDBElementName(model, getTableSchema(hasOne[i]));

				String constraintName = table+"_"+CStringUtils.capitalize(model.getHasOneLocalColumn()[i])+"_fkey";
				sql.ADD().CONSTRAINT(constraintName);
				sql.FOREIGN_KEY(model.getHasOneLocalColumn()[i]);
				StringBuilder referencedTable = new StringBuilder();
				if(hasOneSchema != null && useSchema()){
					referencedTable.append(hasOneSchema+".");
				}
				referencedTable.append(hasOne[i]);
				sql.REFERENCES(referencedTable.toString(), model.getHasOneReferencedColumn()[i]);
				sql.ON_UPDATE().CASCADE()
				.ON_DELETE().RESTRICT()
				.DEFERRABLE().INITIALLY_DEFERRED();
			}

		}
		return sql;
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
		SQL sql = INSERT().INTO(table);
		sql.openParen();
		if(columns != null){
			List<String> finalColumns = new ArrayList<String>();
			for(int c = 0; c < columns.length; c++){
				if(!CStringUtils.inArray(curatedIgnoredColumns, columns[c])){
					finalColumns.add(columns[c]);
				}
				if(CStringUtils.inArray(defaultedColumValues, columns[c])){
					sql.keyword("DEFAULT");
				}
			}
			sql.FIELD(finalColumns.toArray(new String[finalColumns.size()]));
		}
		sql.closeParen();
		if(query != null){
			SQL sql2 = buildSQL(meta, query, false);
			sql.FIELD(sql2);
		}
		else{
			sql.VALUES().openParen();
			boolean doComma = false;
			for(int i = 0; i < columns.length; i++){
				if(!CStringUtils.inArray(curatedIgnoredColumns, columns[i])){
					Object value = null;
					if(dao != null) {
						value = dao.get_Value(columns[i]);
					}
					//					value = getEquivalentDBObject(value);

					if(supportPreparedStatement()){
						if(doComma){sql.comma();}else{doComma=true;}
						sql.VALUE(value);
					}else{
						sql.FIELD("'"+(value != null ? value.toString(): null)+"'");
					}
				}
			}
			sql.closeParen();
		}
		return sql;
	}


	//TODO: may include columns that are meant from other tables, needs improvement for identifying which table to correct from, this will introduce bug when joining multiple tables 
	//while ignoring specific table
	private String[] curateIgnoredColumns(String[] ignoredColumns) {
		if(ignoredColumns == null){
			return null;
		}else{
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
			return curated;
		}
	}


	public SQL buildRenameModel(ModelDef model, String newName) {
		String schema = model.getNamespace();
		StringBuilder tableName = new StringBuilder();
		if(schema != null && useSchema()){
			tableName.append(schema+".");
		}
		SQL sql = ALTER_TABLE(tableName.toString())
				.RENAME().TO(newName);
		return sql;
	}

	public SQL buildSQL(ModelMetaData meta, Query query, boolean useCursor) {
		SQL sql = new SQL();
		List<String> mentionedColumns = new ArrayList<String>();

		Filter[] filters = query.getFilters();
		Long offset = query.getOffset();
		Integer limit = query.getLimit();
		Order[] orders = query.getOrders();
		String[] excludedColumns = query.getExcludedColumns();

		String[] distinctColumns = query.getDistinctColumns();
		String[] groupedColumns = query.getGroupedColumns();

		String table = null;
//		if(model != null){
//			String[] subClasses = model.getSubClass();
//			if(subClasses != null && subClasses.length > 0){
//				Query subClassQuery = buildSubClassTableQuery(model);
//				SQL sql2 = buildSQL(meta, subClassQuery, false);
//				sql1.FIELD(sql2).AS("subclasstable");
//			}
//	
//			schema = model.getNamespace();
//			schema = getDBElementName(model,schema);
//			table = getDBTableName(model);
//		}
		String selectTable = query.getSelectTable();
		String schema = null;
		if(selectTable == null){
			ModelDef selectModel = query.getSelectModel();
			schema = selectModel.getNamespace();
			if(selectModel != null){
				selectTable = selectModel.getTableName();
			}
		}
		ModelDef[] involvedModels = query.getInvolvedModels();



		Map<String, Query> declaredQueries = query.getDeclaredQueries();
		if(declaredQueries != null){
			sql.WITH();
			for(Entry<String, Query> entry : declaredQueries.entrySet()){
				String name = entry.getKey();
				Query dq = entry.getValue();
				SQL dqsql = buildSQL(meta,dq, false);
				sql.FIELD(name);
				sql.AS(dqsql)
				.ln();
			}
		}

		Map<String, SQL> declaredSQL = query.getDeclaredSQL();
		if(declaredSQL != null){
			sql.WITH();
			for(Entry<String, SQL> entry : declaredSQL.entrySet()){
				String name = entry.getKey();
				SQL dsql = entry.getValue();
				sql.FIELD(name);
				sql.AS(dsql)
				.ln();
			}
		}
		
		sql.SELECT();
		
		Pair[] pairs = query.getSelectionColumn();
		for(Pair pair : pairs){
			String column = pair.getLeft();
			String asColumn = pair.getRight();
			if(pair != null ){
				sql.FIELD(column);
				if(asColumn != null){
					sql.AS(asColumn);
				}
			}
		}

		Boolean selectAllColumns = query.getSelectAllColumns();
		if(selectAllColumns != null &&selectAllColumns){
			sql.FIELD("*");
			query.enumerateColumns(false);
			Map<String, Pair[]> renames = query.getRenamedFields();
			for(Entry<String, Pair[]> renamedSet : renames.entrySet()){
				String renTable = renamedSet.getKey();
				Pair[] renPairs = renamedSet.getValue();
				if(renTable != null && renPairs != null && renPairs.length > 0){
					sql.ln();
					for(Pair renPair : renPairs){
						String origColumn = renPair.getLeft();
						String asColumn = renPair.getRight();
						sql.FIELD(renTable+"."+origColumn)
							.AS(asColumn);
					}
				}
			}
		}
		
		if(distinctColumns != null && distinctColumns.length > 0){//custom distinct columns
			String[] distinctColumns1 = new String[distinctColumns.length];
			for(int i = 0; i < distinctColumns.length; i++){
				if(table!=null && prependTableName()){
					distinctColumns1[i] = table+"."+distinctColumns1[i];
				}
			}
			sql.DISTINCT_ON(distinctColumns1);
		}
		//TODO: refined the logic here similar to select all columns
		if(query.isEnumerateColumns()){
			for(ModelDef inv : involvedModels){
				String[] columns = inv.getAttributes();
				if(columns != null){
					for(int i = 0; i < columns.length; i++){
						if(!CStringUtils.inArray(excludedColumns, columns[i])){
							String columnName = getDBElementName(inv,columns[i]);
							String tableName = inv.getTableName();
							if(tableName != null && prependTableName()){
								columnName = tableName+"."+columnName;
							}
							sql.FIELD(columnName);
							String asColumn = query.getRenamed(inv.getTableName(), columns[i]);
							if(asColumn != null){
								sql.AS(asColumn);
							}
						}
					}
				}
			}
		}
		
		
		if(selectTable != null){
			if(schema != null && useSchema()){
				selectTable = schema+"."+selectTable;
			}
			sql.FROM(selectTable);
		}
		if(query.getBaseQuery() != null){
			String baseQueryName = query.getBaseQueryName();
			SQL sql3 = buildSQL(meta, query.getBaseQuery(), useCursor);
			sql.FROM(sql3).AS(baseQueryName);
			mentionedColumns.add(baseQueryName);
		}

		List<Join> joins = query.getJoins();
		if(joins != null){
			for(Join join : joins){
				JoinModifier modifier = join.getModifier();
				sql.ln();
				if(JoinModifier.LEFT.equals(modifier)){
					sql.LEFT();
				}
				if(JoinModifier.RIGHT.equals(modifier)){
					sql.RIGHT();
				}
				if(JoinModifier.FULL.equals(modifier)){
					sql.FULL();
				}
				JoinType type = join.getType();
				if(JoinType.CROSS.equals(type)){
					sql.CROSS();
				}
				if(JoinType.INNER.equals(type)){
					sql.INNER();
				}
				if(JoinType.OUTER.equals(type)){
					sql.OUTER();
				}
				if(JoinType.NATURAL.equals(type)){
					sql.NATURAL();
				}
				
				
				
				ModelDef joinModel = meta.getDefinition(join.getDaoClass());
				
				String joinSchema = joinModel.getNamespace();
				String joinTable = joinModel.getTableName();
				
				if(joinSchema != null && useSchema()){
					joinTable = joinSchema+"."+joinTable;
				}
				sql.JOIN().FIELD(joinTable);
				String[] column1List = join.getOnColumn1();
				String[] column2List = join.getOnColumn2();
				for(int i = 0; i < column1List.length; i++){
					String column1 = column1List[i];
					String column2 = column2List[i];
					sql.ON(column1, column2);
				}
				
			}
		}
		
		buildWhereClause(sql, filters);

		if(groupedColumns != null && groupedColumns.length > 0){
			sql.GROUP_BY(groupedColumns);
		}
		if(orders != null && orders.length > 0){
			boolean doOrderBy = true;
			for(int i = 0; i < orders.length; i++){
				if(orders[i] != null){
					if(doOrderBy){
						sql.ORDER_BY();
						doOrderBy = false;
					}
					if(Direction.ASC.equals(orders[i].getDirection())){
						sql.FIELD(orders[i].getColumn()).ASC();
					}
					else{
						sql.FIELD(orders[i].getColumn()).DESC();
					}
				}
			}
		}
		if(!useCursor){
			if(limit != null){
				sql.LIMIT(limit);
			}
			if(offset != null ){
				sql.OFFSET(offset.intValue());
			}
		}

		return sql;
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
		SQL sql = new SQL();
		sql.keyword("COMMENT").keyword("ON");
		StringBuilder tableName = new StringBuilder();
		if(schema != null && useSchema()){
			tableName.append(schema+".");
		}
		tableName.append(table);
		sql.TABLE(tableName.toString());
		sql.keyword("IS")
		.keyword("$$")
		.FIELD(tableComment)
		.keyword("$$");
		return sql;
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
		SQL sql = UPDATE(tableName.toString());

		boolean doComma = false;
		sql.SET();
		for(int i = 0; i < columns.length; i++){
			if(curatedIgnoredColumns != null && CStringUtils.inArray(curatedIgnoredColumns, columns[i])){
				;
			}
			else{
				if(doComma){sql.comma();}else{doComma = true;}
				Object value = null;
				if(dao != null) {
					value = dao.get_Value(columns[i]);
					//					value = getEquivalentDBObject(value);
				}
				if(CStringUtils.inArray(defaultedColumnValues, columns[i])){
					sql.FIELD(columns[i]);
					sql.EQUAL();
					sql.FIELD("DEFAULT");
				}else{
					sql.FIELD(columns[i]);
					sql.EQUAL();
					if(supportPreparedStatement()){
						sql.VALUE(value);
					}
					else{
						sql.FIELD("'"+( value != null ? value.toString(): null )+"'");
					}
				}
			}
		}
		buildWhereClause(sql, filters);
		return sql;
	}


	private void buildWhereClause(SQL sql,
			Filter[] filters) {
		if(filters != null){
			sql.ln().WHERE();
			boolean doAnd = false;
			for(Filter filter: filters){
				if(filter != null){
					if(doAnd){sql.AND();}else{doAnd=true;}
					extractFilter(sql, filter);
				}
			}
		}
	}

//	protected abstract SQL buildWindowFunctions(ModelMetaData meta, List<WindowFunction> windowFunctions, boolean doComma);

	protected abstract boolean  caseSensitive();

	/**
	 * //some primary is defined together with auto increment column, such as SQLite
	 * @return
	 */

	protected boolean definePrimaryConstraint() {
		return true;
	}

	private void extractFilter(SQL sql, Filter... filterList) {
		if(filterList != null){
			for(Filter filter : filterList){
				if(filter != null){
					Filter[] childFilters = filter.getFilterList();
					if(childFilters != null && childFilters.length > 0){
						sql.openParen();
					}
					Connector con = filter.getConnector();// OR ,AND
					if(con!=null){
						if(Connector.AND.equals(con)){
							sql.AND();
						}
						if(Connector.OR.equals(con)){
							sql.OR();
						}
					}
					if(filter.attribute != null){
						sql.FIELD(filter.attribute);
					}
					if(filter.equalityOperator != null){
						Equality equality = filter.equalityOperator;
						switch(equality){
						case EQUAL:
							sql.EQUAL();
							break;
						case NOT_EQUAL:
							sql.NOT_EQUAL();
							break;
						case LESS_THAN:
							sql.LESS_THAN();
						case LESS_THAN_OR_EQUAL:
							sql.LESS_THAN_OR_EQUAL();
							break;
						case GREATER_THAN:
							sql.GREATER_THAN();
							break;
						case GREATER_THAN_OR_EQUAL:
							sql.GREATER_THAN_OR_EQUAL();
							break;
						case IN:
							sql.IN();
							break;
						case NOT_IN:
							sql.NOT().IN();
							break;
						case LIKE:
							sql.LIKE();
							break;
						case NULL:
							sql.NULL();
							break;
						case NOT_NULL:
							sql.NOT_NULL();
							break;
						case IS_NULL:
							sql.IS_NULL();
							break;
						default:
							break;
							
						}
					}
					if(filter.value != null){
						if(supportPreparedStatement()){
							sql.VALUE(filter.value);
						}else{
							//at least do a sql escaping here
							String value = filter.value != null ? filter.value.toString() : null;
							value = CStringUtils.escapeSQL(value);
							sql.FIELD("'"+value+"'");
						}
					}
					if(filter.query != null){
						SQL sql2 = buildSQL(null, filter.query, false);
						sql.FIELD(sql2);
					}

					if(filter.getFilterSql() != null){
						sql.FIELD(filter.getFilterSql());
					}

					if(childFilters != null && childFilters.length > 0){
						extractFilter(sql, childFilters);
						sql.closeParen();
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

	/**
	 * applies to tableSchema and elementName
	 * @param model
	 * @return
	 */
	public static String getDBElementName(ModelDef model, String elementName){
		return getDBElementName(model, elementName, "\"");
	}

	public static String getDBElementName(ModelDef model, String elementName, String quote){
		if(model!=null && model.isCaseSensitive()){
			return quote+elementName+quote;
		}
		return SpecialCase.correctKeyWords(elementName, false, quote);
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
