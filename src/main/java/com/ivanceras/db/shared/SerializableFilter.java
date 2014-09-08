/*******************************************************************************
 * Copyright by CMIL
 ******************************************************************************/
package com.ivanceras.db.shared;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.UUID;

public class SerializableFilter implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7952466901128511496L;
	public static final String LESS_THAN = "LESS_THAN";
	public static final String LESS_THAN_OR_EQUAL = "LESS_THAN_OR_EQUAL";
	public static final String EQUAL = "EQUAL";
	public static final String GREATER_THAN = "GREATER_THAN";
	public static final String GREATER_THAN_OR_EQUAL = "GREATER_THAN_OR_EQUAL";
	public static final String NOT_EQUAL = "NOT_EQUAL";
	public static final String IN = "IN";
	public static final String LIKE = "LIKE";
	
	public String attribute;
	public String operator;
	private String stringValue;
	private Integer integerValue;
	private Boolean booleanValue;
	private Double doubleValue;
	private Float floatValue;
	private Date dateValue;
	private BigDecimal bigDecimalValue;
	private BigInteger bigIntegerValue;
	private UUID uuidValue;
	
	public SerializableFilter(){
		
	}
	
	public void setFilterValue(String value){
		this.stringValue = value;
	}

	public void setFilterValue(Integer value){
		this.integerValue = value;
	}

	public void setFilterValue(Boolean value){
		this.booleanValue = value;
	}
	public void setFilterValue(Double value){
		this.doubleValue = value;
	}
	public void setFilterValue(Float value){
		this.floatValue = value;
	}
	public void setFilterValue(Date value){
		this.dateValue = value;
	}
	public void setFilterValue(BigDecimal value){
		this.bigDecimalValue = value;
	}
	public void setFilterValue(BigInteger value){
		this.bigIntegerValue = value;
	}
	public void setFilterValue(UUID value){
		this.uuidValue = value;
	}
	
	
	public String getFilterValueAsString(){
		return stringValue;
	}
	public Integer getFilterValueAsInteger(){
		return integerValue;
	}
	public Boolean getFilterValueAsBoolean(){
		return booleanValue;
	}
	public Double getFilterValueAsDouble(){
		return doubleValue;
	}
	public Float getFilterValueAsFloat(){
		return floatValue;
	}
	public Date getFilterValueAsDate(){
		return dateValue;
	}
	public BigDecimal getFilterValueAsBigDecimal(){
		return bigDecimalValue;
	}
	public BigInteger getFilterValueAsBigInteger(){
		return bigIntegerValue;
	}
	
	private UUID getFilterValueAsUuid() {
		return uuidValue;
	}

	public static Filter[] cast(SerializableFilter[] sfilters){
		if(sfilters == null){
			return null;
		}
		Filter[] filters = new Filter[sfilters.length];
		for(int i = 0; i < sfilters.length; i++){
			filters[i] = cast(sfilters[i]);
		}
		return filters;
	}
	
	public static Filter cast(SerializableFilter sfilter){
		if(sfilter == null){
			return null;
		}
		Object value = sfilter.getFilterValueAsString();
		if(value == null){
			value = sfilter.getFilterValueAsInteger();
		}
		if(value == null){
			value = sfilter.getFilterValueAsBoolean();
		}
		if(value == null){
			value = sfilter.getFilterValueAsDouble();
		}
		if(value == null){
			value = sfilter.getFilterValueAsFloat();
		}
		if(value == null){
			value = sfilter.getFilterValueAsDate();
		}
		if(value == null){
			value = sfilter.getFilterValueAsBigDecimal();
		}
		if(value == null){
			value = sfilter.getFilterValueAsBigInteger();
		}
		if(value == null){
			value = sfilter.getFilterValueAsUuid();
		}
		System.out.println("casting value is: "+value);
		Filter filter = new Filter(sfilter.attribute, sfilter.operator, value);
		return filter;
	}
	

	public static SerializableFilter[] cast(Filter[] filters){
		if(filters == null){
			return null;
		}
		SerializableFilter[] sfilters = new SerializableFilter[filters.length];
		for(int i = 0; i < filters.length; i++){
			sfilters[i] = cast(filters[i]);
		}
		return sfilters;
	}

	
	public static SerializableFilter cast(Filter filter){
		
		if(filter == null){
			return null;
		}
		
		SerializableFilter sfilter = new SerializableFilter();
		Object value = filter.value;
		sfilter.attribute = filter.attribute;
		sfilter.operator = filter.operator;
		
		if(value == null){
			;//don't do anything
		}
		else if(value.getClass().equals(java.lang.String.class)){
			sfilter.setFilterValue((String)value);
		}
		else if(value.getClass().equals(java.lang.Integer.class)){
			sfilter.setFilterValue((Integer)value);
		}
		else if(value.getClass().equals(java.lang.Boolean.class)){
			sfilter.setFilterValue((Boolean)value);
		}
		else if(value.getClass().equals(java.lang.Double.class)){
			sfilter.setFilterValue((Double)value);
		}
		else if(value.getClass().equals(java.lang.Float.class)){
			sfilter.setFilterValue((Float)value);
		}
		else if(value.getClass().equals(BigDecimal.class)){
			sfilter.setFilterValue((BigDecimal)value);
		}
		else if(value.getClass().equals(BigInteger.class)){
			sfilter.setFilterValue((Integer)value);
		}
		else if(value.getClass().equals(java.util.Date.class)){
			sfilter.setFilterValue((Date)value);
		}
		else if(value.getClass().equals(java.util.UUID.class)){
			sfilter.setFilterValue((UUID)value);
		}
		return sfilter;
	}
	
}
