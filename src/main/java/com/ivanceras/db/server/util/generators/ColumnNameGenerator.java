package com.ivanceras.db.server.util.generators;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ivanceras.commons.conf.Configuration;
import com.ivanceras.commons.writer.FileUtil;
import com.ivanceras.commons.writer.SourceWriter;
import com.ivanceras.commons.writer.StringSourceWriter;
import com.ivanceras.db.api.DB_Rdbms;
import com.ivanceras.db.api.ModelDef;

public class ColumnNameGenerator {
	public void start(ModelDef[] modelList, Configuration conf){

		Set<String> columnSet = new HashSet<String>();
		//explicitly added (synthetic column for inherited tables)
		columnSet.add(DB_Rdbms.subclasstable);
		
		Map<String, Set<String>> usedInTable = new HashMap<String, Set<String>>();
		Set<String> uniquetableSet = new HashSet<String>();

		for(int i = 0; i < modelList.length; i++){
			ModelDef modeldef = modelList[i];
			uniquetableSet.add(modeldef.getTableName());
			List<String> attrList = Arrays.asList(modeldef.getAttributes());
			for(String att : attrList){
				columnSet.add(att);
				if(usedInTable.containsKey(att)){
					usedInTable.get(att).add(modeldef.getModelName());
				}
				else{
					HashSet<String> tableSet = new HashSet<String>();
					tableSet.add(modeldef.getModelName());
					usedInTable.put(att, tableSet);
				}
			}
		}
		createColumnNameList(columnSet, usedInTable , conf.metaDataPackageName,  conf.metaDataDirectory, "Column");
	}

	private void createColumnNameList(Set<String> columnSet, Map<String, Set<String>> usedInTable, String metaDataPackageName, String metaDataDirectory, String className) {
		SourceWriter sw = new StringSourceWriter();

		
		sw.lnprint("package "+metaDataPackageName+";");
		sw.lnprint("");
		sw.lnprint("/***");
		sw.lnprint("* This is automatically generated by DAOGenerator, based on the database table schema");
		sw.lnprint("* ");
		sw.lnprint("* ");
		sw.lnprint("*/");
		sw.lnprint("");
		sw.lnprint("public class "+className+"{");
		for(String col : columnSet){
			if(usedInTable.containsKey(col)){
				Set<String> tables = usedInTable.get(col);
				sw.lnprint();
				sw.lnprint("\t/**Used in table "+tables+"*/");
			}
			sw.lnprint("\tpublic final static String "+col+" = \""+col+"\";");
		}
		sw.lnprint("");
		sw.lnprint("}");
		FileUtil.writeToFile(sw.toString(), metaDataDirectory, className+".java");
	}
}