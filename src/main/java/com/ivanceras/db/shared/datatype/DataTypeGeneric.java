package com.ivanceras.db.shared.datatype;

import com.ivanceras.db.shared.exception.DataTypeException;

public class DataTypeGeneric {

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
	public static final String HASHMAP = "HashMap";

	public static String javaDataTypeFromDBDataType(String dbDataType) throws DataTypeException{
		return DataTypeJava.fromGenericDataType(fromDBDataType(dbDataType));
	}

	public static String fromDBDataType(String dbDataType){
		if(dbDataType.startsWith(DataTypeDB.JSON)){//include limitized datatype such as character varying(255)
			return HASHMAP;
		}
		else if(dbDataType.startsWith(DataTypeDB.TIMESTAMP_WITH_TIME_ZONE)){//include limitized datatype such as character varying(255)
			return TIMESTAMP;
		}
		else if(dbDataType.startsWith(DataTypeDB.TIME_WITH_TIME_ZONE)){//include limitized datatype such as character varying(255)
			return TIME;
		}
		else if(dbDataType.startsWith(DataTypeDB.NUMERIC)){//include limitized datatype such as character varying(255)
			return BIGDECIMAL;
		}
		else if(dbDataType.startsWith(DataTypeDB.CHARACTER_VARYING)){//include limitized datatype such as character varying(255)
			return STRING;
		}
		else if(dbDataType.startsWith(DataTypeDB.NAME)){
			return BIGDECIMAL;
		}
		else if(dbDataType.startsWith(DataTypeDB.INTEGER)){
			return INTEGER;
		}
		else if(dbDataType.startsWith(DataTypeDB.INT)){
			return INTEGER;
		}
		else if(dbDataType.startsWith(DataTypeDB.BIGINT)){
			return LONG;
		}
		else if(dbDataType.startsWith(DataTypeDB.CHARACTER)){
			return STRING;
		}
		else if(dbDataType.equals(DataTypeDB.BOOLEAN)){
			return BOOLEAN;
		}
		else if(dbDataType.equals(DataTypeDB.DATE)){
			return DATE;
		}
		else if(dbDataType.equals(DataTypeDB.TEXT)){
			return STRING;
		}
		else if(dbDataType.equals(DataTypeDB.NAME)){
			return STRING;
		}
		else if(dbDataType.startsWith(DataTypeDB.TIMESTAMP)){
			return TIMESTAMP;
		}
		else if(dbDataType.startsWith(DataTypeDB.TIME)){
			return TIME;
		}

		else if(dbDataType.equals(DataTypeDB.BYTEA)){
			return BYTE_ARRAY;
		}
		else if(dbDataType.equals(DataTypeDB.UUID)){
			return UUID;
		}
		else if(dbDataType.equals(DataTypeDB.REAL)){
			return FLOAT;
		}
		else if(dbDataType.equals(DataTypeDB.OID)){
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
