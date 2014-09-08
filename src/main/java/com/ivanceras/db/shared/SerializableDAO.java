/*******************************************************************************
 * Copyright by CMIL
 ******************************************************************************/
package com.ivanceras.db.shared;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

public class SerializableDAO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3648601226085586742L;
	
	
	protected HashMap<String, String> stringProperties = new HashMap<String, String>();
	protected HashMap<String, UUID> uuidProperties = new HashMap<String, UUID>();
	protected HashMap<String, Integer> integerProperties = new HashMap<String, Integer>();
	protected HashMap<String, Boolean> booleanProperties = new HashMap<String, Boolean>();
	protected HashMap<String, Double> doubleProperties = new HashMap<String, Double>();
	protected HashMap<String, Float> floatProperties = new HashMap<String, Float>();
	protected HashMap<String, Date> dateProperties = new HashMap<String, Date>();
	protected HashMap<String, Byte[]> byteArrayProperties = new HashMap<String, Byte[]>();
	protected HashMap<String, BigDecimal> bigDecimalProperties = new HashMap<String, BigDecimal>();
	protected HashMap<String, BigInteger> bigIntegerProperties = new HashMap<String, BigInteger>();
	protected HashMap<String, SerializableDAO> daoProperties = new HashMap<String, SerializableDAO>();
	protected HashMap<String, SerializableDAO[]> daoArrayProperties = new HashMap<String, SerializableDAO[]>();

	private String modelName;
	
	public SerializableDAO(){
		
	}
	
	public SerializableDAO(String modelName){
		this.modelName = modelName;
	}
	public String getModelName(){
		return this.modelName;
	}
	public void set_Value(String attribute, UUID value){
		uuidProperties.put(attribute, value);
	}
	public void set_Value(String attribute, String value){
		stringProperties.put(attribute, value);
	}
	public void set_Value(String attribute, Integer value){
		integerProperties.put(attribute, value);
	}
	public void set_Value(String attribute, Boolean value){
		booleanProperties.put(attribute, value);
	}
	public void set_Value(String attribute, Double value){
		doubleProperties.put(attribute, value);
	}
	public void set_Value(String attribute, Float value){
		floatProperties.put(attribute, value);
	}
	public void set_Value(String attribute, Date value){
		dateProperties.put(attribute, value);
	}
	public void set_Value(String attribute, Byte[] value){
		byteArrayProperties.put(attribute, value);
	}
	public void set_Value(String attribute, BigDecimal value){
		bigDecimalProperties.put(attribute, value);
	}
	public void set_Value(String attribute, BigInteger value){
		bigIntegerProperties.put(attribute, value);
	}
	public void set_Value(String attribute, SerializableDAO value){
		daoProperties.put(attribute, value);
	}
	public void set_Value(String attribute, SerializableDAO[] value){
		daoArrayProperties.put(attribute, value);
	}
	

	
	protected void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public String get_ValueAsString(String attribute){
		return stringProperties.get(attribute);
	}
	public Integer get_ValueAsInteger(String attribute){
		return integerProperties.get(attribute);
	}
	public Boolean get_ValueAsBoolean(String attribute){
		return booleanProperties.get(attribute);
	}
	public Double get_ValueAsDouble(String attribute){
		return doubleProperties.get(attribute);
	}
	public Float get_ValueAsFloat(String attribute){
		return floatProperties.get(attribute);
	}
	public Date get_ValueAsDate(String attribute){
		return dateProperties.get(attribute);
	}
	public Byte[] get_ValueAsByteArray(String attribute){
		return byteArrayProperties.get(attribute);
	}
	public BigDecimal get_ValueAsBigDecimal(String attribute){
		return bigDecimalProperties.get(attribute);
	}
	public BigInteger get_ValueAsBigInteger(String attribute){
		return bigIntegerProperties.get(attribute);
	}
	public SerializableDAO get_ValueAsDAO(String attribute){
		return daoProperties.get(attribute);
	}
	public SerializableDAO[] get_ValueAsDaoArray(String attribute){
		return daoArrayProperties.get(attribute);
	}
	
	
	//generated setter / modified getters
	
	public HashMap<String, String> getStringProperties() {
		return stringProperties;
	}

	
	public HashMap<String, UUID> getUuidProperties() {
		return uuidProperties;
	}
	
	public HashMap<String, Integer> getIntegerProperties() {
		return integerProperties;
	}
	
	
	public HashMap<String, Boolean> getBooleanProperties() {
		return booleanProperties;
	}
	
	
	public HashMap<String, Double> getDoubleProperties() {
		return doubleProperties;
	}
	
	
	public HashMap<String, Float> getFloatProperties() {
		return floatProperties;
	}
	
	public HashMap<String, Date> getDateProperties() {
		return dateProperties;
	}
	
	
	public HashMap<String, Byte[]> getByteArrayProperties() {
		return byteArrayProperties;
	}
	
	
	public HashMap<String, BigDecimal> getBigDecimalProperties() {
		return bigDecimalProperties;
	}
	
	
	public HashMap<String, BigInteger> getBigIntegerProperties() {
		return bigIntegerProperties;
	}
	
	
	public HashMap<String, SerializableDAO> getDaoProperties() {
		return daoProperties;
	}
	
	
	public HashMap<String, SerializableDAO[]> getDaoArrayProperties() {
		return daoArrayProperties;
	}
	
	
	public static DAO cast(SerializableDAO sdao){
		if(sdao == null){
			return null;
		}
		HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.putAll(sdao.getStringProperties());
		properties.putAll(sdao.getUuidProperties());
		properties.putAll(sdao.getIntegerProperties());
		properties.putAll(sdao.getBooleanProperties());
		properties.putAll(sdao.getDoubleProperties());
		properties.putAll(sdao.getFloatProperties());
		properties.putAll(sdao.getDateProperties());
		properties.putAll(sdao.getByteArrayProperties());
		properties.putAll(sdao.getBigDecimalProperties());
		properties.putAll(sdao.getBigIntegerProperties());
		properties.putAll(sdao.getDaoProperties());
		properties.putAll(sdao.getDaoArrayProperties());
		DAO dao = new DAO(sdao.getModelName());
		dao.setProperties(properties);
		return dao;
	}
	
	public static SerializableDAO cast(DAO dao){
		if(dao == null){
			return null;
		}
		HashMap<String, Object> properties = (HashMap<String, Object>) dao.getProperties();
		SerializableDAO sdao = new SerializableDAO(dao.getModelName());
		for (Entry<String, Object> entry: properties.entrySet()) {
			Object value = entry.getValue();
			String attribute = entry.getKey();
			if(value == null){
				;//don't do anything
			}
			else if(value.getClass().equals(java.lang.String.class)){
				sdao.set_Value(attribute, (String)value);
			}
			else if(value.getClass().equals(java.util.UUID.class)){
				sdao.set_Value(attribute, (UUID)value);
			}
			else if(value.getClass().equals(java.lang.Integer.class)){
				sdao.set_Value(attribute, (Integer)value);
			}
			else if(value.getClass().equals(java.lang.Boolean.class)){
				sdao.set_Value(attribute, (Boolean)value);
			}
			else if(value.getClass().equals(java.lang.Double.class)){
				sdao.set_Value(attribute, (Double)value);
			}
			else if(value.getClass().equals(java.lang.Float.class)){
				sdao.set_Value(attribute, (Float)value);
			}
			else if(value.getClass().equals(java.util.Date.class)){
				sdao.set_Value(attribute, (Date)value);
			}
			else if(value.getClass().equals(java.lang.Byte[].class)){
				sdao.set_Value(attribute, (Byte[])value);
			}
			else if(value.getClass().equals(java.math.BigDecimal.class)){
				sdao.set_Value(attribute, (BigDecimal)value);
			}
			else if(value.getClass().equals(java.math.BigInteger.class)){
				sdao.set_Value(attribute, (BigInteger)value);
			}
			else if(value instanceof DAO){
				sdao.set_Value(attribute, cast((DAO)value));
			}
			else if(value instanceof DAO[]){
				sdao.set_Value(attribute, cast((DAO[])value));
			}
		}
		return sdao;
	}
	public static SerializableDAO[] cast(DAO[] daoList){
		if(daoList == null){
			return null;
		}
		SerializableDAO[] sdaoList = new SerializableDAO[daoList.length];
		for(int i = 0; i < daoList.length; i++){
			sdaoList[i] = cast(daoList[i]);
		}
		return sdaoList;
	}
	public static DAO[] cast(SerializableDAO[] sdaoList){
		if(sdaoList == null){
			return null;
		}
		DAO[] daoList = new DAO[sdaoList.length];
		for(int i = 0; i < daoList.length; i++){
			daoList[i] = cast(sdaoList[i]);
		}
		return daoList;
	}
	
	@Override
	public String toString(){
		return dateProperties.toString();
	}
}
