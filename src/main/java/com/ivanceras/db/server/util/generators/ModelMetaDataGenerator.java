package com.ivanceras.db.server.util.generators;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringEscapeUtils;

import com.ivanceras.commons.conf.Configuration;
import com.ivanceras.commons.strings.CStringUtils;
import com.ivanceras.commons.writer.FileUtil;
import com.ivanceras.commons.writer.SourceWriter;
import com.ivanceras.commons.writer.StringSourceWriter;
import com.ivanceras.db.api.ModelDef;
import com.ivanceras.db.shared.DAO;

public class ModelMetaDataGenerator {

	public void start(ModelDef[] modelListChilded, Configuration conf) {
		generateModelMetaData(modelListChilded, conf);
	}

	private boolean generateModelMetaData(ModelDef[] modeldefList, Configuration conf) {

		Set<String> columnSet = new HashSet<String>();
		Map<String, Set<String>> usedInTable = new HashMap<String, Set<String>>();
		Set<String> uniquetableSet = new HashSet<String>();
		boolean useCamelCase = conf.useCamelCase;

		SourceWriter sw = new StringSourceWriter();
		sw.lnprint("package "+conf.metaDataPackageName+";");
		sw.lnprint();
		sw.lnprint("import java.util.Map;");
		sw.lnprint("import java.util.LinkedHashMap;");
		sw.lnprint("import "+DAO.class.getCanonicalName()+";");
		sw.lnprint("import "+conf.daopackageName+".*;");
		sw.lnprint("");
		sw.lnprint("import "+com.ivanceras.db.api.ModelDef.class.getCanonicalName()+";");
		sw.lnprint("");
		sw.lnprint("public class "+conf.metaDataClassName+" extends "+com.ivanceras.db.model.ModelMetaData.class.getCanonicalName()+"{");
		sw.lnprint("");
		sw.lnprint("");
		sw.lnTabPrint("public static Map<Class<? extends DAO>, ModelDef> modelDefList = new LinkedHashMap<Class<? extends DAO>, ModelDef>();");
		sw.lnTabPrint("public static Map<String, ModelDef> namedModelDefList = new LinkedHashMap<String, ModelDef>();");
		sw.lnTabPrint("public static Map<Class<? extends DAO>, String> modelNameListNdx = new LinkedHashMap<Class<? extends DAO>, String>();");
		sw.lnTabPrint("public static Map<String, Class<? extends DAO>> classListNdx = new LinkedHashMap<String, Class<? extends DAO>>();");
		sw.lnprint("");
		for(int i = 0; i < modeldefList.length; i++){
			ModelDef modeldef = modeldefList[i];
			String className = CStringUtils.capitalize(modeldef.getModelName(), useCamelCase);
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
			sw.lnprint("");
			sw.lnTabPrint("void init"+i+"(){");
			sw.lnprint("");
			sw.lnTabPrint("    String   namespace                               = \""+modeldef.getNamespace()+"\";");
			sw.lnTabPrint("    String   modelName                               = \""+className+"\";");
			sw.lnTabPrint("    String[] attributes                              = "+getStringListRepresentation(modeldef.getAttributes())+";");
			sw.lnTabPrint("    String   generatedAttribute                      = "+(modeldef.getGeneratedAttribute()!=null?("\""+modeldef.getGeneratedAttribute()+"\""):null)+";");
			sw.lnTabPrint("    String[] primaryAttributes                       = "+getStringListRepresentation(modeldef.getPrimaryAttributes())+";");
			sw.lnTabPrint("    String[] uniqueAttributes                        = "+getStringListRepresentation(modeldef.getUniqueAttributes())+";");
			sw.lnTabPrint("    String[] dataTypes                               = "+getStringListRepresentation(modeldef.getDataTypes())+";");
			//			buff.lnprint("        String[] attributeComments                       = "+getStringListRepresentation(modeldef.getAttributeComments())+";");
			sw.lnprint("");
			sw.lnTabPrint("    String[] hasOneLocalColumn                       = "+getStringListRepresentation(modeldef.getHasOneLocalColumn())+";");
			sw.lnTabPrint("    String[] hasOne                                  = "+getStringListRepresentation(modeldef.getHasOne())+";");
			sw.lnTabPrint("    String[] hasOneReferencedColumn                  = "+getStringListRepresentation(modeldef.getHasOneReferencedColumn())+";");
			sw.lnprint("");

			sw.lnTabPrint("    String[] hasMany                                 = "+getStringListRepresentation(modeldef.getHasMany())+";");
			sw.lnTabPrint("    String[] hasManyReferencedColumn                 = "+getStringListRepresentation(modeldef.getHasManyReferencedColumn())+";");
			sw.lnTabPrint("    String[] hasManyLocalColumn                      = "+getStringListRepresentation(modeldef.getHasManyLocalColumn())+";");
			sw.lnprint("");
			if(modeldef.getOwners() != null){
				sw.lnTabPrint("    String[] owners                                  = "+getStringListRepresentation(modeldef.getOwners())+";");
			}
			if(modeldef.getPrimaryOwner() != null){
				sw.lnTabPrint("    String primaryOwner                              =  \""+modeldef.getPrimaryOwner()+"\";");
			}
			if(modeldef.getDirectChildren() != null){
				sw.lnTabPrint("    String[] directChildren                          = "+getStringListRepresentation(modeldef.getDirectChildren())+";");
			}
			if(modeldef.getParentClass() != null){
				sw.lnTabPrint("    String parentClass                         		 = \""+modeldef.getParentClass()+"\";");
			}
			if(modeldef.getSubClass() != null && modeldef.getSubClass().length > 0){
				sw.lnTabPrint("    String[] subClasses								 = "+getStringListRepresentation(modeldef.getSubClass())+";");
			}
			sw.lnprint("");

			sw.lnTabPrint("    boolean  caseSensitive								= "+modeldef.isCaseSensitive()+";");
			sw.lnTabPrint("    ");

			sw.lnTabPrint("    ModelDef modelDef = new ModelDef(");
			sw.lnTabPrint("        namespace, ");
			sw.lnTabPrint("        modelName, ");
			sw.lnTabPrint("        attributes,");
			sw.lnTabPrint("        generatedAttribute,");
			sw.lnTabPrint("        dataTypes, ");
			sw.lnTabPrint("        primaryAttributes, ");
			sw.lnTabPrint("        uniqueAttributes, ");
			sw.lnTabPrint("        hasOne, ");
			sw.lnTabPrint("        hasOneLocalColumn, ");
			sw.lnTabPrint("        hasOneReferencedColumn, ");
			sw.lnTabPrint("        hasMany, ");
			sw.lnTabPrint("        hasManyReferencedColumn, ");
			sw.lnTabPrint("        hasManyLocalColumn, ");
			sw.lnTabPrint("        caseSensitive);");
			sw.lnTabPrint("            ");
			sw.lnTabPrint("    modelDef.setPolymorphic("+modeldef.isPolymorphic()+");");
			sw.lnTabPrint("    modelDef.setTableName(\""+modeldef.getTableName()+"\");");
			sw.lnTabPrint("    modelDef.setDescription("+ (modeldef.getDescription() != null ? ("\""+StringEscapeUtils.escapeJava(modeldef.getDescription())+"\"") : "null")+");");
			sw.lnTabPrint("    modelDef.setAttributeComments(new String[]"+getEscapedListRepresentation(modeldef.getAttributeComments())+");");
			if(modeldef.getOwners() != null){
				sw.lnTabPrint("    modelDef.setOwners(owners);");
			}
			if(modeldef.getPrimaryOwner() != null){
				sw.lnTabPrint("    modelDef.setPrimaryOwner(primaryOwner);");
			}
			if(modeldef.getDirectChildren() != null){
				sw.lnTabPrint("    modelDef.setDirectChildren(directChildren);");
			}
			if(modeldef.getParentClass() != null){
				sw.lnTabPrint("    modelDef.setParentClass(parentClass);");
			}
			if(modeldef.getSubClass() != null && modeldef.getSubClass().length > 0){
				sw.lnTabPrint("    modelDef.setSubClass(subClasses);");
			}
			sw.lnTabPrint("    modelDefList.put(DAO_"+className+".class, modelDef);");
			sw.lnTabPrint("    namedModelDefList.put(\""+className+"\", modelDef);");
			sw.lnTabPrint("    modelNameListNdx.put(DAO_"+className+".class, \""+className+"\");");
			sw.lnTabPrint("    classListNdx.put(\""+className+"\", DAO_"+className+".class);");
			sw.lnprint("");
			sw.lnprint("");
			sw.lnTabPrint("}");
		}
		sw.lnprint("");
		sw.lnprint("");
		sw.lnTabPrint("public "+conf.metaDataClassName+"(){");
		sw.lnTabPrint("    super(modelDefList, namedModelDefList, modelNameListNdx, classListNdx);");
		for(int i = 0; i < modeldefList.length; i++){
			sw.lnTabPrint("    init"+i+"();");
		}
		sw.lnTabPrint("}");
		sw.lnprint("}");
		FileUtil.writeToFile(sw.toString(), conf.metaDataDirectory, conf.metaDataClassName+".java");
		return true;
	}

	private String getStringListRepresentation(String[] list){
		if(list == null){
			return null;
		}
		StringBuffer buff = new StringBuffer();
		buff.append("{");
		boolean doComma = false;
		for(String l : list){
			if(doComma){buff.append(", ");}else{doComma=true;}
			buff.append("\""+l+"\"");
		}
		buff.append("}");
		return buff.toString();
	}
	private String getEscapedListRepresentation(String[] list){
		if(list == null){
			return null;
		}
		StringBuffer buff = new StringBuffer();
		buff.append("{");
		boolean doComma = false;
		for(String l : list){
			if(doComma){buff.append(", ");}else{doComma=true;}
			buff.append(l != null ? ("\""+StringEscapeUtils.escapeJava(l)+"\"") : null);
		}
		buff.append("}");
		return buff.toString();
	}
}
