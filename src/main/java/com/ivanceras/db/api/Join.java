package com.ivanceras.db.api;

import com.ivanceras.db.shared.DAO;

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
	private String onColumn1;
	private String onColumn2;
	
	private JoinType type;
	private JoinModifier modifier;
	
	

	public Join(Class<? extends DAO> daoClass, String onColumn1, String onColumn2){
		this.daoClass = daoClass;
		this.onColumn1 = onColumn1;
		this.onColumn2 = onColumn2;
	}

	public Class<? extends DAO> getDaoClass() {
		return daoClass;
	}

	public String getOnColumn1() {
		return onColumn1;
	}

	public String getOnColumn2() {
		return onColumn2;
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
