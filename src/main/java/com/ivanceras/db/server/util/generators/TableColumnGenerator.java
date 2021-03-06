package com.ivanceras.db.server.util.generators;

import com.ivanceras.db.api.ModelDef;
import com.ivanceras.commons.conf.Configuration;
import com.ivanceras.commons.writer.FileUtil;
import com.ivanceras.commons.writer.SourceWriter;
import com.ivanceras.commons.writer.StringSourceWriter;
public class TableColumnGenerator {
	
	
	public void start(ModelDef[] modelList, Configuration conf){
		generateTableColumnNames(modelList, conf.metaDataPackageName,  conf.metaDataDirectory, "TableColumns");
	}
	
	private void generateTableColumnNames(ModelDef[] modelList, String packageName, String dir, String className){
		SourceWriter sw = new StringSourceWriter();
		sw.lnprint("package "+packageName+";");
		sw.lnprint();
		sw.lnprint("public class "+className+"{");
		sw.lnprint();
		for(ModelDef model : modelList){
			sw.lnTabPrint("public class "+model.getTableName()+"{");
			sw.lnTabPrint("");
			String[] attributes = model.getAttributes();
			for(String att : attributes){
				sw.lnTabPrint("\tpublic static final String "+att+" = \""+model.getTableName()+"."+att+"\";");
			}
			sw.lnprint("");
			sw.lnTabPrint("}");
			sw.lnprint("");
		}
		sw.lnprint("}");
		FileUtil.writeToFile(sw.toString(), dir, className+".java");
	}
}
