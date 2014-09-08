package com.ivanceras.db.server.util.generators;

import com.ivanceras.db.api.ModelDef;
import com.ivanceras.commons.conf.Configuration;
import com.ivanceras.commons.strings.CStringUtils;
import com.ivanceras.commons.writer.FileUtil;
import com.ivanceras.commons.writer.SourceWriter;
import com.ivanceras.commons.writer.StringSourceWriter;
public class TableColumnGenerator {
	
	
	public void start(ModelDef[] modelList, Configuration conf){
		generateTableColumnNames(modelList, conf.metaDataPackageName,  conf.metaDataDirectory, "TableColumns");
	}
	
	private void generateTableColumnNames(ModelDef[] modelList, String packageName, String dir, String className){
		SourceWriter sw = new StringSourceWriter();
		sw.println("package "+packageName+";");
		sw.println();
		sw.println("public class "+className+"{");
		sw.println();
		for(ModelDef model : modelList){
			sw.println("\tpublic class "+model.getTableName()+"{");
			sw.println("\t");
			String[] attributes = model.getAttributes();
			for(String att : attributes){
				sw.println("\t\tpublic static final String "+att+" = \""+model.getTableName()+"."+att+"\";");
			}
			sw.println("");
			sw.println("\t}");
			sw.println("");
		}
		sw.println("}");
		FileUtil.writeToFile(sw.toString(), dir, className+".java");
	}
}
