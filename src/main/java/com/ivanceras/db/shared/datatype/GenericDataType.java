package com.ivanceras.db.shared.datatype;

import com.ivanceras.db.shared.exception.DataTypeException;

public class GenericDataType {

	public static final String STRING = "String";
	public static final String BIGDECIMAL = "BigDecimal";
	public static final String INTEGER = "Integer";
	public static final String LONG = "Long";
	public static final String CHAR = "char";
	public static final String BOOLEAN = "Boolean";
	public static final String DATE = "Date";
	public static final String TIMESTAMP = "Timestamp";
	public static final String TIME = "Time";
	public static final String BYTE = "Byte";
	public static final String BYTE_ARRAY = "Byte[]";
	public static final String UUID = "UUID";
	public static final String FLOAT = "Float";
	public static final String DOUBLE = "Double";
	public static final String BIGINTEGER = "BigInteger";
	public static final String TEXT = "Text";

	public static String javaDataTypeFromDBDataType(String dbDataType) throws DataTypeException{
		return JavaDataType.fromGenericDataType(fromDBDataType(dbDataType));
	}

	public static String fromDBDataType(String dbDataType){
		if(dbDataType.startsWith(DBDataType.JSON)){//include limitized datatype such as character varying(255)
			return STRING;
		}
		else if(dbDataType.startsWith(DBDataType.TIMESTAMP_WITH_TIME_ZONE)){//include limitized datatype such as character varying(255)
			return TIMESTAMP;
		}
		else if(dbDataType.startsWith(DBDataType.TIME_WITH_TIME_ZONE)){//include limitized datatype such as character varying(255)
			return TIME;
		}
		else if(dbDataType.startsWith(DBDataType.NUMERIC)){//include limitized datatype such as character varying(255)
			return BIGDECIMAL;
		}
		else if(dbDataType.startsWith(DBDataType.CHARACTER_VARYING)){//include limitized datatype such as character varying(255)
			return STRING;
		}
		else if(dbDataType.startsWith(DBDataType.NAME)){
			return BIGDECIMAL;
		}
		else if(dbDataType.startsWith(DBDataType.INTEGER)){
			return INTEGER;
		}
		else if(dbDataType.startsWith(DBDataType.INT)){
			return INTEGER;
		}
		else if(dbDataType.startsWith(DBDataType.BIGINT)){
			return LONG;
		}
		else if(dbDataType.startsWith(DBDataType.CHARACTER)){
			return STRING;
		}
		else if(dbDataType.equals(DBDataType.BOOLEAN)){
			return BOOLEAN;
		}
		else if(dbDataType.equals(DBDataType.DATE)){
			return DATE;
		}
		else if(dbDataType.equals(DBDataType.TEXT)){
			return STRING;
		}
		else if(dbDataType.equals(DBDataType.NAME)){
			return STRING;
		}
		else if(dbDataType.startsWith(DBDataType.TIMESTAMP)){
			return TIMESTAMP;
		}
		else if(dbDataType.startsWith(DBDataType.TIME)){
			return TIME;
		}

		else if(dbDataType.equals(DBDataType.BYTEA)){
			return BYTE_ARRAY;
		}
		else if(dbDataType.equals(DBDataType.UUID)){
			return UUID;
		}
		else if(dbDataType.equals(DBDataType.REAL)){
			return FLOAT;
		}
		else if(dbDataType.equals(DBDataType.OID)){
			return LONG;
		}
		else{
			try {
				throw new DataTypeException("No Equivalent General Data type found for DB datatype["+dbDataType+"]");
			} catch (DataTypeException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}
