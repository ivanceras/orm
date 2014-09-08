/*******************************************************************************
 * Copyright by CMIL
 ******************************************************************************/
package com.ivanceras.db.server.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.ivanceras.commons.conf.Configuration;
import com.ivanceras.commons.strings.CStringUtils;
import com.ivanceras.db.api.EntityManager;
import com.ivanceras.db.api.ModelDef;
import com.ivanceras.db.server.util.generators.BeanGenerator;
import com.ivanceras.db.server.util.generators.ColumnNameGenerator;
import com.ivanceras.db.server.util.generators.DAOClassGenerator;
import com.ivanceras.db.server.util.generators.DAOInstanceFactoryGenerator;
import com.ivanceras.db.server.util.generators.MapperGenerator;
import com.ivanceras.db.server.util.generators.ModelFactoryGenerator;
import com.ivanceras.db.server.util.generators.SchemaTableGenerator;
import com.ivanceras.db.server.util.generators.TableColumnGenerator;
import com.ivanceras.db.server.util.generators.TableNameGenerator;


/**
 * Creates a dao definition based on the database tables
 * @author jcesar
 *
 */
public class DAOGenerator{

	private EntityManager em; 
	private Configuration conf;
	private List<String[]> tableGroups;
	private boolean cleanupDirectory;
	
	public static final String Array = "List"; //Use word "List" instead of "Array"

	public DAOGenerator(EntityManager em, Configuration conf, List<String[]> tableGroups, boolean cleanupDirectory){
		this.em = em;
		this.conf = conf;
		this.tableGroups = tableGroups;
		this.cleanupDirectory = cleanupDirectory;
	}

	public void start() throws Exception{
		if(cleanupDirectory){
			CleanUp.start(conf);
		}
		ModelDefinitionProvider provider = new ModelDefinitionProvider(em.getDB(), conf.dbUser, null, conf.includeSchema);
		ModelDef[] modelList = correctModelList(provider.getTableDefinitions());
		
		new DAOClassGenerator().start(modelList, conf, true, false);
		new BeanGenerator().start(modelList, conf);
		new MapperGenerator().start(modelList, conf);

		ModelDef[] modelListOwned = setModelOwners(modelList, tableGroups);
		ModelDef[] modelListChilded = setDirectChildren(modelListOwned);

		new ColumnNameGenerator().start(modelListChilded, conf);
		new TableNameGenerator().start(modelListChilded, conf);
		new SchemaTableGenerator().start(modelListChilded, conf);
		new TableColumnGenerator().start(modelListChilded, conf);
		new ModelFactoryGenerator().start(modelListChilded, conf);
		new DAOInstanceFactoryGenerator().start(modelListChilded, conf);
	}




	/**
	 * Correctionals in model definition, such as
	 * when a primary column of a certain table is used as a foreign key of
	 * another table while at the same time it is the primary column of that table, then both table has 1:1 relationship only
	 * 
	 * For each model that hasMany table
	 * If the hasmany table is using a primary key that is a foreign key refering to the primary key of this table
	 * 
	 * @param tableDefinitions
	 * @return
	 */
	private ModelDef[] correctModelList(ModelDef[] modelList) {
		return new ModelCurator(modelList).correct();
	}






	/**
	 * 
	 * @param modelList
	 * @param tableGroups, gives a clue to the final/primary owner of this table, when grouping tables together
	 * @return
	 */
	private ModelDef[] setModelOwners(ModelDef[] modelList, List<String[]> tableGroups) {
		HashMap<String, ModelDef> modelHash = new LinkedHashMap<String, ModelDef>();//modelname, ModelDef
		HashMap<String, Set<String>> model_owners = new LinkedHashMap<String, Set<String>>();//ModelName, owners
		for(ModelDef model : modelList){
			String modelName = model.getModelName().toLowerCase();
			System.out.println("modelName: "+modelName);
			modelHash.put(model.getModelName().toLowerCase(), model);
		}
		for(ModelDef owner : modelList){
			String[] dependents = owner.getHasMany();
			for(String ownedDependent : dependents){
				if(model_owners.containsKey(ownedDependent)){
					Set<String> owners = model_owners.get(ownedDependent);
					owners.add(owner.getModelName().toLowerCase());
				}
				else{
					Set<String> set = new HashSet<String>();
					set.add(owner.getModelName().toLowerCase());
					model_owners.put(ownedDependent, set);
				}
			}
		}

		if(tableGroups != null){
			Map<String, Set<String[]>> tableInvolvedment = transformGroup(tableGroups);


			for(Entry<String, Set<String>> model_owner : model_owners.entrySet()){
				String modelName = model_owner.getKey();
				ModelDef model = modelHash.get(modelName);
				if(model == null){
					System.err.println("Warning: can not find model: "+modelName);
				}
				Set<String> ownerSet = model_owner.getValue();
				if(ownerSet != null && ownerSet.size() > 0){
					String[] owners = ownerSet.toArray(new String[ownerSet.size()]);
					if(model != null){
						model.setOwners(owners);
						if(owners.length == 1){
							model.setPrimaryOwner(owners[0]);
						}
						else if(owners.length > 1){
							String[] group = getTableInvolvement(model.getModelName().toLowerCase(), tableInvolvedment);
							String primaryOwner = chooseFirstOccurence(owners, group, model.getTableName());
							model.setPrimaryOwner(primaryOwner);
						}
						else{
						}
					}
				}
			}
		}
		List<ModelDef> finalModelList = new ArrayList<ModelDef>();
		for(Entry<String, ModelDef> modelEntry : modelHash.entrySet()){
			finalModelList.add(modelEntry.getValue());
		}
		return finalModelList.toArray(new ModelDef[finalModelList.size()]);
	}

	private ModelDef[] setDirectChildren(ModelDef[] modelListOwned) {

		Map<String, ModelDef> modelSearchList = new LinkedHashMap<String, ModelDef>();
		for(ModelDef model : modelListOwned){
			String modelName = model.getModelName().toLowerCase();
			modelSearchList.put(modelName, model);
		}

		Map<ModelDef, Set<String>> owner_childrenSet = new LinkedHashMap<ModelDef, Set<String>>();
		for(ModelDef model : modelListOwned){
			String modelName = model.getModelName().toLowerCase();
			String primaryOwner = model.getPrimaryOwner();
			if(primaryOwner != null){
				ModelDef owner = modelSearchList.get(primaryOwner);
				if(!owner.getModelName().equalsIgnoreCase(modelName)){
					if(owner_childrenSet.containsKey(owner)){
						Set<String> children = owner_childrenSet.get(owner);
						children.add(modelName);

					}
					else{
						Set<String> children = new HashSet<String>();
						children.add(modelName);
						owner_childrenSet.put(owner, children);
					}
				}
			}
		}

		List<ModelDef> childedModelList = new LinkedList<ModelDef>();
		for(ModelDef owner : modelListOwned){
			Set<String> children = owner_childrenSet.get(owner);
			if(children != null){
				owner.setDirectChildren(children.toArray(new String[children.size()]));
			}
			childedModelList.add(owner);
		}
		return childedModelList.toArray(new ModelDef[childedModelList.size()]);
	}


	/**
	 * Choose among the owners that are present within the list group that occurs before the tableName, and closest to it
	 * @param owners
	 * @param group
	 * @param tableName
	 */
	private String chooseFirstOccurence(String[] among_owners, String[] within_listgroup,
			String prior_tableName) {
		int index = CStringUtils.indexOf(within_listgroup, prior_tableName);
		for(int i = index-1; i >= 0; i-- ){
			String closest = within_listgroup[i];
			if(CStringUtils.indexOf(among_owners, closest) >= 0){
				return closest;
			}
		}
		return null;
	}


	private String[] getTableInvolvement(String table, Map<String, Set<String[]>> involvements){
		if(involvements == null){
			return null;
		}
		if(involvements.size() == 0){
			return null;
		}
		if(!involvements.containsKey(table)){
			return null;
		}
		else{
			Set<String[]> involvedList = involvements.get(table);
			String[] result = null;
			if(involvedList!=null && involvedList.size() > 0){
				result = involvedList.iterator().next();
			}
			else{
				return null;
			}
			for(String[] inv : involvedList){
				if(result.length < inv.length){
					result = inv;
				}
			}
			return result;
		}
	}


	//transform the listing of table groups, according to table
	private Map<String, Set<String[]>> transformGroup(List<String[]> tableGroups) {
		Map<String, Set<String[]>> model_tableGroup = new LinkedHashMap<String, Set<String[]>>();
		for(String[] list : tableGroups){
			for(String table : list){
				if(model_tableGroup.containsKey(table)){
					Set<String[]> tableGroupSet = model_tableGroup.get(table);
					tableGroupSet.add(list);
				}
				else{
					Set<String[]> tableGroupSet = new HashSet<String[]>();
					tableGroupSet.add(list);
					model_tableGroup.put(table, tableGroupSet);
				}
			}
		}
		return model_tableGroup;
	}

}
