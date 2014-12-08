package com.ivanceras.db.server.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @refer http://code.google.com/appengine/docs/java/datastore/entities.html
 * @author jcesar
 *
 */
public class DataTypeUtils {
	
	public static Object convertFromString(String value, String dataType){
		if(value == null || value.equals("null")){
			return null;
		}
		else if(dataType.equals("java.lang.String")){
			return value;
		}
		else if(dataType.equals("java.lang.Integer")){
			return Integer.parseInt(value);
		}
		else if(dataType.equals("java.lang.Boolean")){
			return Boolean.parseBoolean(value);
		}
		else if(dataType.equals("java.lang.Double")){
			return Double.parseDouble(value);
		}
		else if(dataType.equals("java.lang.Float")){
			return Float.parseFloat(value);
		}
		else if(dataType.equals("java.util.Date")){
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
			Date date = null;
			try {
				date = df.parse(value);
			} catch (ParseException e) {
				DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
				try {
					date = df2.parse(value);
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
			return date;
		}
		else if(dataType.equals("java.lang.Byte[]")){
			return null;
		}
		else if(dataType.equals("java.math.BigDecimal")){
			return new BigDecimal(value);
		}
		else if(dataType.equals("java.math.BigInteger")){
			return new BigInteger(value);
		}
		return value;
	}
	

}
