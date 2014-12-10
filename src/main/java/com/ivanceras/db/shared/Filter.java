package com.ivanceras.db.shared;

import java.util.ArrayList;

import java.util.List;

import com.ivanceras.db.api.LiteralString;
import com.ivanceras.db.api.Query;
import com.ivanceras.keyword.sql.SQL;

/**
 * TODO: change Constant expressions to use enums
 * @author lee
 *
 */
public class Filter {
	
	public enum Connector{
		AND,
		OR
	}
	
	public enum Equality{
		EQUAL,
		NOT_EQUAL,
		LESS_THAN,
		LESS_THAN_OR_EQUAL,
		GREATER_THAN,
		GREATER_THAN_OR_EQUAL,
		IN,
		NOT_IN,
		LIKE,
		NULL,
		NOT_NULL,
		IS_NULL,
	}
	

	public String attribute;
	public Equality equalityOperator;
	public Object value;
	public Query query;

	public String literalValue;

	private Connector connector;

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
		this(filter.attribute, filter.equalityOperator, filter.value);
		this.literalValue = filter.literalValue;
		this.query = filter.query;
	}

	public Filter(String attribute, Equality operator, Object value){
		this.attribute = attribute;
		this.equalityOperator = operator;
		this.value = value;
	}
	public Filter(String attribute, Equality operator, SQL sql){
		this.attribute = attribute;
		this.equalityOperator = operator;
		this.filterSql = sql;
	}
	public Filter(String attribute, Equality operator){
		this.attribute = attribute;
		this.equalityOperator = operator;
	}

	public Filter(String attribute, Equality operator, Query query){
		this.attribute = attribute;
		this.equalityOperator = operator;
		this.query = query;
	}

	public Filter(String attribute, Equality operator, LiteralString value){
		this.attribute = attribute;
		this.equalityOperator = operator;
		this.literalValue = value!=null ? value.toString() : null;
	}

	public Filter or(Filter filter){
		filter.connector = Connector.OR;
		filterList.add(filter);
		return this;
	}

	public Filter and(Filter filter){
		if(filter != null){
			filter.connector = Connector.AND;
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

	public Filter[] getFilterList(){
		return filterList.toArray(new Filter[filterList.size()]);
	}

	public Connector getConnector() {
		return connector;
	}


	public void setConnector(Connector connector) {
		this.connector = connector;
	}


	@Override
	public String toString(){
		return this.attribute+" "+this.equalityOperator+" "+(this.value!=null?this.value:this.literalValue);
	}

	public SQL getFilterSql() {
		return filterSql;
	}

	public void setFilterSql(SQL filterSql) {
		this.filterSql = filterSql;
	}

}
