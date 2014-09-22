package com.ivanceras.db.server.util.generators;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import com.ivanceras.commons.conf.Configuration;
import com.ivanceras.commons.writer.FileUtil;
import com.ivanceras.commons.writer.SourceWriter;
import com.ivanceras.commons.writer.StringSourceWriter;
import com.ivanceras.db.api.ModelDef;

public class SchemaTableGenerator {

	public void start(ModelDef[] modelList, Configuration conf){
		generateSchemaTableNames(modelList, conf.metaDataPackageName,  conf.metaDataDirectory, "SchemaTables");
	}


	private void generateSchemaTableNames(ModelDef[] modelList, String packageName, String dir, String className){
		LinkedHashMap<String, List<ModelDef>> schemaModels = groupModelsBySchame(modelList);
		SourceWriter sw = new StringSourceWriter();
		sw.lnprint("package "+packageName+";");
		sw.lnprint();
		sw.lnprint("public class "+className+"{");
		sw.lnprint();
		for(Entry<String, List<ModelDef>> entry : schemaModels.entrySet()){
			String schemaName = entry.getKey();
			if(schemaName.equals("public")){
				schemaName = "public_";
			}
			sw.lnprint("\tpublic class "+schemaName+"{");
			sw.lnprint("\t");
			List<ModelDef> list = entry.getValue();
			for(ModelDef model : list){
				sw.lnprint("\t\tpublic static final String "+model.getTableName()+" = \""+model.getNamespace()+"."+model.getTableName()+"\";");
			}
			sw.lnprint("");
			sw.lnprint("\t}");
			sw.lnprint("");
		}
		sw.lnprint("}");
		FileUtil.writeToFile(sw.toString(), dir, className+".java");
	}

	private LinkedHashMap<String, List<ModelDef>> groupModelsBySchame(ModelDef[] modelList){
		LinkedHashMap<String, List<ModelDef>> schemaModels = new LinkedHashMap<String, List<ModelDef>>();
		for(ModelDef model : modelList){
			String schema = model.getNamespace();
			List<ModelDef> list = schemaModels.get(schema);
			if(list == null){
				list = new LinkedList<ModelDef>();
				schemaModels.put(schema, list);
			}
			list.add(model);
		}
		return schemaModels;
	}

}
