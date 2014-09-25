package com.ivanceras.db.shared.datatype;

import com.ivanceras.db.shared.exception.DataTypeException;

public class DataTypeDB {
	public static String JSON = "json";
	public static String TIME = "time";
	public static String TIME_WITH_TIME_ZONE = "time with time zone";
	public static String CHARACTER_VARYING = "character varying";
	public static String NUMERIC = "numeric";
	public static String INTEGER = "integer";
	public static String INT = "int";
	public static String BIGINT = "bigint";
	public static String CHARACTER = "character";
	public static String BOOLEAN = "boolean";
	public static String DATE = "date";
	public static String TEXT = "text";
	public static String NAME = "name";
	public static String TIMESTAMP = "timestamp";
	public static String BYTEA = "bytea";
	public static String UUID = "uuid";
	public static String REAL = "real";
	public static String OID = "oid";
	public static String DOUBLE_PRECISION = "double precision";
	public static final String TIMESTAMP_WITH_TIME_ZONE = "timestamp with time zone";
	public static final String TINY_INT = "tinyint";
	public static final String VARCHAR = "varchar";
	public static final String CHAR = "char";
	
	public static String fromGenericType(String genType) {
		if(genType.equals(DataTypeGeneric.JSON)){
			return JSON;
		}
		else if(genType.equals(DataTypeGeneric.STRING)){
			return CHARACTER_VARYING;
		}
		else if(genType.equals(DataTypeGeneric.BIGDECIMAL)){
			return NUMERIC;
		}
		else if(genType.equals(DataTypeGeneric.INTEGER)){
			return INTEGER;
		}
		else if(genType.equals(DataTypeGeneric.LONG)){
			return BIGINT;
		}
		else if(genType.equals(DataTypeGeneric.CHAR)){
			return CHARACTER;
		}
		else if(genType.equals(DataTypeGeneric.BOOLEAN)){
			return BOOLEAN;
		}
		else if(genType.equals(DataTypeGeneric.DATE)){
			return TIMESTAMP_WITH_TIME_ZONE;
		}
		else if(genType.equals(DataTypeGeneric.TIME)){
			return TIME_WITH_TIME_ZONE;
		}
		else if(genType.equals(DataTypeGeneric.TIMESTAMP)){
			return TIMESTAMP_WITH_TIME_ZONE;
		}
		else if(genType.equals(DataTypeGeneric.BYTE_ARRAY)){
			return BYTEA;
		}
		else if(genType.equals(DataTypeGeneric.BYTE)){
			return OID;
		}
		else if(genType.equals(DataTypeGeneric.UUID)){
			return UUID;
		}
		else if(genType.equals(DataTypeGeneric.FLOAT)){
			return NUMERIC;
		}
		else if(genType.equals(DataTypeGeneric.DOUBLE)){
			return NUMERIC;
		}
		else if(genType.equals(DataTypeGeneric.BIGINTEGER)){
			return BIGINT;
		}
		else{
			try {
				throw new DataTypeException("No equivalent Java data type for general data type "+genType);
			} catch (DataTypeException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}
