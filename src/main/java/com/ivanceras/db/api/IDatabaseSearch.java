package com.ivanceras.db.api;


/**
 * Searchable database interface
 * @author lee
 *
 */
public interface IDatabaseSearch {
	
	void soundsLike(String keyword);
	void startsWith(String keyword);
	void contains(String keyword);
	void columns(String ...columns);
}
