package com.ivanceras.db.api;

import java.util.List;

import com.ivanceras.db.shared.DAO;
import com.ivanceras.db.shared.Filter;
import com.ivanceras.db.shared.Order;
import com.ivanceras.db.shared.exception.DatabaseException;

public class QueryBuilder {

	private ModelDef model;
	private Filter[] filters;
	private int page;
	private int itemsPerPage;
	private Order[] order;
	private List<String> orderColumns;
	private List<Boolean> asc;
	private EntityManager em;
	private Class<? extends DAO> daoClass;
	private Boolean selectAll;
	private Boolean enumerateColumns;
	private String[] groupBy;
	private String[] selectOnlyColumns;

	public QueryBuilder(){

	}

	public QueryBuilder em(EntityManager em){
		this.em = em;
		return this;
	}
	public QueryBuilder daoClass(Class<? extends DAO> daoClass){
		this.daoClass = daoClass;
		return this;
	}

	public QueryBuilder model(ModelDef model){
		this.model = model;
		return this;
	}

	public QueryBuilder filter(Filter... filter){
		this.filters = filter;
		return this;
	}

	public QueryBuilder page(int page){
		this.page = page;
		return this;
	}
	public QueryBuilder itemsPerPage(int itemsPerPage){
		this.itemsPerPage = itemsPerPage;
		return this;
	}

	public QueryBuilder orderAsc(String column){
		this.orderColumns.add(column);
		this.asc.add(true);
		return this;
	}

	public QueryBuilder selectAllColumns(boolean selectAll){
		this.selectAll = selectAll;
		return this;
	}
	public QueryBuilder enumerateColumns(boolean enumerate){
		this.enumerateColumns = enumerate;
		return this;
	}

	public QueryBuilder orderDesc(String column){
		this.orderColumns.add(column);
		this.asc.add(false);
		return this;
	}

	public QueryBuilder groupBy(String[] groupBy){
		this.groupBy = groupBy;
		return this;
	}

	public QueryBuilder selectOnlyColumns(String[] selectOnlyColumns){
		this.selectOnlyColumns = selectOnlyColumns;
		return this;
	}
	
	public QueryBuilder order(Order... order){
		this.order = order;
		return this;
	}

	public Query build() throws DatabaseException{
		Query query = null;
		if(model != null){
			query = new Query(model);
		}
		else if(em != null && daoClass != null){
			query = new Query(em, daoClass);
		}
		query.setSelectAllColumns(selectAll);
		query.setEnumerateColumns(enumerateColumns);
		query.setItemsPerPage(itemsPerPage);
		query.setPage(page);
		query.addFilter(filters);
		if(groupBy != null){
			query.addGroupedColumns(null, groupBy);
		}
		if(order != null){
			query.addOrder(order);
		}
		return query;
	}

}
