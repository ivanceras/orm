package com.ivanceras.db.api;

import java.util.ArrayList;
import java.util.List;

import com.ivanceras.db.shared.DAO;
import com.ivanceras.db.shared.exception.DatabaseException;

public class Join {

	enum JoinType{
		CROSS,
		INNER,
		OUTER,
		NATURAL,
	}

	enum JoinModifier{
		LEFT,
		RIGHT,
		FULL,
	}

	private Class<? extends DAO> daoClass;
	private List<String> onColumn1 = new ArrayList<>();
	private List<String> onColumn2 = new ArrayList<>();

	private JoinType type;
	private JoinModifier modifier;



	/**
	 * TODO: multiple columns to be joined should also be working
	 * @param daoClass
	 * @param onColumn1
	 * @param onColumn2
	 */
	public Join(Class<? extends DAO> daoClass, String onColumn1, String onColumn2){
		this.daoClass = daoClass;
		this.onColumn1.add(onColumn1);
		this.onColumn2.add(onColumn2);
	}

	public Join(Class<? extends DAO> daoClass, String onColumn1, String onColumn2, String... columnPairs) throws DatabaseException{
		this.daoClass = daoClass;
		this.onColumn1.add(onColumn1);
		this.onColumn2.add(onColumn2);
		if(columnPairs != null){
			if(columnPairs.length % 2 != 0){
				throw new DatabaseException("column used in joins should have pairs.");
			}
			for(int i = 0 ; i < columnPairs.length; i++){
				if( i % 2 == 0){
					this.onColumn1.add(columnPairs[i]);
				}
				else{
					this.onColumn2.add(i, columnPairs[2]);
				}
			}
		}
	}

	public Class<? extends DAO> getDaoClass() {
		return daoClass;
	}

	public String[] getOnColumn1() {
		return onColumn1.toArray(new String[onColumn1.size()]);
	}

	public String[] getOnColumn2() {
		return onColumn2.toArray(new String[onColumn2.size()]);
	}

	public JoinModifier getModifier() {
		return modifier;
	}

	public void setModifier(JoinModifier modifier) {
		this.modifier = modifier;
	}

	public JoinType getType() {
		return type;
	}

	public void setType(JoinType type) {
		this.type = type;
	}

}
