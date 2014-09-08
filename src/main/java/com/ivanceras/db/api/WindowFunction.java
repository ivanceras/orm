package com.ivanceras.db.api;

import com.ivanceras.db.shared.Order;

/**
 * This is window functions described in postgresql article
 * http://www.postgresql.org/docs/9.1/static/tutorial-window.html
 * This adds additional functionalities such as commulative sum and partition of rows
 * @author lee
 *
 */
public class WindowFunction {

	private Aggregate aggregate;
	private Order[] order;
	private String[] partitionColumns;
	private String asColumn;

	public WindowFunction(Aggregate aggregate, Order[] order, String[] partitionColumns, String asColumn){
		this.aggregate = aggregate;
		this.order = order;
		this.partitionColumns = partitionColumns;
		this.asColumn = asColumn;
	}

	public Aggregate getAggregate() {
		return aggregate;
	}

	public Order[] getOrder() {
		return order;
	}

	public String[] getPartitionColumns() {
		return partitionColumns;
	}

	public void setAggregate(Aggregate aggregate) {
		this.aggregate = aggregate;
	}

	public void setOrder(Order[] order) {
		this.order = order;
	}

	public void setPartitionColumnss(String[] partiotionColumns) {
		this.partitionColumns = partiotionColumns;
	}

	public String getWindowAlias(){
		if(asColumn != null){
			return asColumn;
		}else{
			return this.aggregate.getAsColumn();
		}
	}
}
