package com.ivanceras.db.shared;

import java.util.ArrayList;
import java.util.List;

import com.ivanceras.db.api.LiteralString;
import com.ivanceras.db.api.Query;
import com.ivanceras.keyword.sql.SQL;
public class Filter {
	public static final String LESS_THAN = "<";
	public static final String LESS_THAN_OR_EQUAL = "<=";
	public static final String EQUAL = "=";
	public static final String GREATER_THAN = ">";
	public static final String GREATER_THAN_OR_EQUAL = ">=";
	public static final String NOT_EQUAL = "!=";
	public static final String IN = "IN";
	public static final String NOT_IN = "NOT IN";
	public static final String LIKE = "LIKE";
	public static final String NOT_NULL = "NOT NULL";
	public static final String NULL = "NULL";
	public static final String IS_NULL = "IS NULL";

	private static final String AND = "AND";
	private static final String OR = "OR";
	public static final String NOT_LIKE = "NOT LIKE";

	public String attribute;
	public String operator;
	public Object value;
	public Query query;

	public String literalValue;

	private String connector;

	private List<Filter> filterList = new ArrayList<Filter>();

	private SQL filterSql;


	/**
	 * Provides a way to exempt unsupported functions, new features to be used
	 * @param filterInStringForm
	 */
	public Filter(SQL filterSql){
		this.setFilterSql(filterSql);
	}

	/**
	 * For NULL or IS_NULL test
	 * @param attribute
	 * @param equality
	 */
	public Filter(Filter filter){
		this(filter.attribute, filter.operator, filter.value);
		this.literalValue = filter.literalValue;
		this.query = filter.query;
	}

	public Filter(String attribute, String operator, Object value){
		this.attribute = attribute;
		this.operator = operator;
		this.value = value;
	}
	public Filter(String attribute, String operator, SQL sql){
		this.attribute = attribute;
		this.operator = operator;
		this.filterSql = sql;
	}
	public Filter(String attribute, String operator){
		this.attribute = attribute;
		this.operator = operator;
	}

	public Filter(String attribute, String operator, Query query){
		this.attribute = attribute;
		this.operator = operator;
		this.query = query;
	}

	public Filter(String attribute, String operator, LiteralString value){
		this.attribute = attribute;
		this.operator = operator;
		this.literalValue = value!=null ? value.toString() : null;
	}

	public Filter or(Filter filter){
		filter.connector = OR;
		filterList.add(filter);
		return this;
	}

	public Filter and(Filter filter){
		if(filter != null){
			filter.connector = AND;
			filterList.add(filter);
		}
		return this;
	}

	public Filter and(Filter... filters){
		for(Filter f : filters){
			and(f);
		}
		return this;
	}

	public String getFilterType() {
		return connector;
	}
	public Filter[] getFilterList(){
		return filterList.toArray(new Filter[filterList.size()]);
	}

	public String getConnector() {
		return connector;
	}


	public void setConnector(String connector) {
		this.connector = connector;
	}


	@Override
	public String toString(){
		return this.attribute+" "+this.operator+" "+(this.value!=null?this.value:this.literalValue);
	}

	public SQL getFilterSql() {
		return filterSql;
	}

	public void setFilterSql(SQL filterSql) {
		this.filterSql = filterSql;
	}

}
