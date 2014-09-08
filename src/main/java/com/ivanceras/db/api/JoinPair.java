package com.ivanceras.db.api;

import java.io.Serializable;

import com.ivanceras.db.shared.DAO;
import com.ivanceras.db.shared.exception.DatabaseException;



public class JoinPair{
	
	public static class Join implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7831613344862076932L;
		public static final String LEFT = "LEFT";
		public static final String RIGHT = "RIGHT";
		public static final String INNER = "INNER";
		public static final String LEFT_OUTER = "LEFT OUTER";
		public static final String RIGHT_OUTER = "RIGHT OUTER";
		public static final String CROSS = "CROSS";


	}

	
	private ModelDef model1;
	private ModelDef model2;
	private ColumnPair[] columnPairs;
	private String joinType;
	
	public JoinPair(EntityManager em, Class<? extends DAO> model1, Class<? extends DAO> model2, ColumnPair pair) throws DatabaseException{
		this(em, model1, model2, pair, Join.LEFT);
	}

	
	public JoinPair(EntityManager em, Class<? extends DAO> model1, Class<? extends DAO> model2, ColumnPair pair, String joinType) throws DatabaseException{
		this.model1 = em.getDefinition(model1);
		this.model2 = em.getDefinition(model2);
		this.columnPairs = pair.getPairs();
		this.joinType = joinType;
	}
	
//	public JoinPair(ModelDef model1, ModelDef model2, ColumnPair pair, String joinType){
//		this.model1 = model1;
//		this.model2 = model2;
//		this.columnPairs = pair.getPairs();
//		this.joinType = joinType;
//	}
//	public JoinPair(String model1, String model2, ColumnPair pair){
//		this.model1 = new ModelDef();
//		this.model1.setTableName(model1);
//		this.model1.setModelName(model1);
//		this.model2 = new ModelDef();
//		this.model2.setTableName(model2);
//		this.model2.setModelName(model2);
//		this.columnPairs = pair.getPairs();
//		this.joinType = Join.LEFT;
//	}
//	public JoinPair(String model1, String model2, ColumnPair pair, String joinType){
//		this.model1 = new ModelDef();
//		this.model1.setTableName(model1);
//		this.model2 = new ModelDef();
//		this.model2.setTableName(model2);
//		this.columnPairs = pair.getPairs();
//		this.joinType = joinType;
//	}

	public ModelDef getModel1() {
		return model1;
	}


	public ModelDef getModel2() {
		return model2;
	}

	public ColumnPair[] getColumnPairs() {
		return columnPairs;
	}

	public void setColumnPairs(ColumnPair[] columnPairs) {
		this.columnPairs = columnPairs;
	}



	public String getJoinType() {
		return joinType;
	}

	public void setJoinType(String joinType) {
		this.joinType = joinType;
	}
	
	@Override
	public String toString(){
		StringBuffer str = new StringBuffer();
		str.append(" FROM ");
		str.append(model1.getModelName());
		str.append(" "+joinType+" JOIN ON ");
		str.append(model2.getModelName());
		for(ColumnPair pair : columnPairs){
			str.append(" ON ");
			str.append(model1.getModelName());
			str.append("."+pair.getColumn1());
			str.append(" = ");
			str.append(model2.getModelName());
			str.append("."+pair.getColumn2());
		}
		return str.toString();
	}
	
	

}
