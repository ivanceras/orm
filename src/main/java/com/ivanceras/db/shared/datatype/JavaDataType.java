package com.ivanceras.db.shared.datatype;

import com.ivanceras.db.shared.exception.DataTypeException;

public class JavaDataType {
	
	public static final String JAVA_STRING = "String";
	public static final String JAVA_BIGDECIMAL = "java.math.BigDecimal";
	public static final String JAVA_INTEGER = "Integer";
	public static final String JAVA_LONG = "Long";
	public static final String JAVA_CHAR = "char";
	public static final String JAVA_BOOLEAN = "Boolean";
	public static final String JAVA_DATE = "java.util.Date";
	public static final String JAVA_TIMESTAMP = "java.util.Timestamp";
	public static final String JAVA_BYTE_ARRAY = "Byte[]";
	public static final String JAVA_BYTE = "Byte";
	public static final String JAVA_UUID = "java.util.UUID";
	public static final String JAVA_FLOAT = "Float";
	public static final String JAVA_DOUBLE = "Double";
	public static final String JAVA_BIGINTEGER = "java.math.BigInteger";
	
	public static String fromGenericDataType(String genType) throws DataTypeException{
		if(genType.equals(GenericDataType.STRING)){
			return JAVA_STRING;
		}
		else if(genType.equals(GenericDataType.BIGDECIMAL)){
			return JAVA_BIGDECIMAL;
		}
		else if(genType.equals(GenericDataType.INTEGER)){
			return JAVA_INTEGER;
		}
		else if(genType.equals(GenericDataType.LONG)){
			return JAVA_LONG;
		}
		else if(genType.equals(GenericDataType.CHAR)){
			return JAVA_CHAR;
		}
		else if(genType.equals(GenericDataType.BOOLEAN)){
			return JAVA_BOOLEAN;
		}
		else if(genType.equals(GenericDataType.DATE)){
			return JAVA_DATE;
		}
		else if(genType.equals(GenericDataType.TIMESTAMP)){
			return JAVA_DATE;
		}
		else if(genType.equals(GenericDataType.BYTE_ARRAY)){
			return JAVA_BYTE_ARRAY;
		}
		else if(genType.equals(GenericDataType.BYTE)){
			return JAVA_BYTE;
		}
		else if(genType.equals(GenericDataType.UUID)){
			return JAVA_UUID;
		}
		else if(genType.equals(GenericDataType.FLOAT)){
			return JAVA_FLOAT;
		}
		else if(genType.equals(GenericDataType.DOUBLE)){
			return JAVA_DOUBLE;
		}
		else if(genType.equals(GenericDataType.BIGINTEGER)){
			return JAVA_BIGINTEGER;
		}
		else{
			throw new DataTypeException("No equivalent Java data type for general data type "+genType);
		}
	}

	public static String[] fromGenericDataType(String[] dataTypes) {
		String[] javaDts = new String[dataTypes.length];
		for(int i = 0; i < dataTypes.length; i++){
			try {
				javaDts[i] = fromGenericDataType(dataTypes[i]);
			} catch (DataTypeException e) {
				e.printStackTrace();
			}
		}
		return javaDts;
	}
	
}
