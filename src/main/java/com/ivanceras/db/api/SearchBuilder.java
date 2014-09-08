package com.ivanceras.db.api;

import java.util.ArrayList;
import java.util.List;

import com.ivanceras.db.shared.DAO;
import com.ivanceras.db.shared.Filter;
import com.ivanceras.db.shared.exception.DatabaseException;

public class SearchBuilder {

	private String keyword;
	private boolean caseSensitive = true;
	private boolean useFuzzy = false;
	private boolean useStartsWith = false;
	private boolean useContains = false;
	private Class<? extends DAO> daoClass;
	private List<String> searchColumns = new ArrayList<String>();
	private List<String> distinctColumns = new ArrayList<String>();
	
	public SearchBuilder(Query query){
		
	}

	public SearchBuilder(Class<? extends DAO> daoClass){
		this.daoClass = daoClass;
	}
	
	public SearchBuilder keyword(String keyword){
		this.keyword = keyword;
		return this;
	}
	
	public SearchBuilder searchColumn(String... column){
		for(String c : column){
			searchColumns.add(c);
		}
		return this;
	}
	
	public SearchBuilder distintColumn(String... distintColumn){
		for(String c : distintColumn){
			this.distinctColumns.add(c);
		}
		return this;
	}
	public SearchBuilder contains(String keyword){
		this.useContains = true;
		this.keyword = keyword;
		return this;
	}
	public SearchBuilder startsWith(String keyword){
		this.useStartsWith = true;
		this.keyword = keyword;
		return this;
	}
	public SearchBuilder soundsLike(String keyword){
		this.keyword = keyword;
		this.useFuzzy = true;
		return this;		
	}
	public SearchBuilder ignoreCase(){
		caseSensitive = false;
		return this;		
	}
	
	
	public Query build(EntityManager em) throws DatabaseException{
		Query query = new Query(em, daoClass);
		String[] columns = searchColumns.toArray(new String[searchColumns.size()]);
		if(columns == null || columns.length == 0){
			ModelDef model = em.getDefinition(daoClass);
			columns = model.getAttributes();
		}

		
		if(columns.length > 0){
			Filter firstFilter = null;
			if(useFuzzy){//if fuzzy, use only the first column supplied
				firstFilter = buildSoundexFilter(columns[0], keyword);
				query.addFilter(firstFilter);
			}
			for(int i = 0; i < columns.length; i++){
				if(useContains){
					Filter conFilter = new Filter(columns[i], Filter.LIKE, new LiteralString("'%"+keyword+"%'"));
					if(!caseSensitive){
						conFilter.columnWrapperLeft = "lower(";
						conFilter.columnWrapperRight = ")";
						conFilter.valueWrapperLeft = "lower(";
						conFilter.valueWrapperRight = ")";
					}
					firstFilter.or(conFilter);
				}
				if(useStartsWith){
					Filter startFilter = new Filter(columns[i], Filter.LIKE, new LiteralString("'"+keyword+"%'"));
					if(!caseSensitive){
						startFilter.columnWrapperLeft = "lower(";
						startFilter.columnWrapperRight = ")";
						startFilter.valueWrapperLeft = "lower(";
						startFilter.valueWrapperRight = ")";
					}
					firstFilter.or(startFilter);
				}
			}
		}
		
		String[] distinctColumns = this.distinctColumns.toArray(new String[this.distinctColumns.size()]);
		query.setDistinctColumns(distinctColumns);
		return query;
	}
	
	private Filter buildSoundexFilter(String column, String keyword){
		String columnWrapper = "soundex(substring("+column+" from 1 for "+keyword.length()+"))";
		Filter f = new Filter(columnWrapper, Filter.EQUAL, keyword);
		f.valueWrapperLeft = "soundex(";
		f.valueWrapperRight = ")";
		return f;
	}
}
