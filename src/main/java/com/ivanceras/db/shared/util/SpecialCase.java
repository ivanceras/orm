package com.ivanceras.db.shared.util;


public class SpecialCase {

	public static String getEquiv(String att) {
		if(att.equals("ad_language")){
			att = "ad_language_code";
			return att;
		}
		return att;
	}
	
	static String[] keywords = {"order", "limit", "select", "group", "by", "offset", "primary", "user", "schema"};
	
	public static String correctKeyWords(String column, boolean caseSensitive, String quote){
		if(isSpecial(column)){
//			System.err.println("MATCHED special case: "+column+" quote ["+quote+"]");
			if(caseSensitive){
				return quote + column + quote;
			}else{
				return quote + column.toLowerCase()+ quote;
			}
		}else{
			return column;
		}
	}
	
	public static boolean isSpecial(String column){
		if(column == null){
			return false;
		}
		for(String key : keywords){
			if(column.equalsIgnoreCase(key)){
				return true;
			}
		}
		return false;
	}
	

}
