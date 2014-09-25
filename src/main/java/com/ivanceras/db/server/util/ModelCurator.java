package com.ivanceras.db.server.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.ivanceras.commons.strings.CStringUtils;
import com.ivanceras.db.api.ModelDef;

public class ModelCurator {


	private ModelDef model;
	private ModelDef[] modelList;

	public ModelCurator(ModelDef[] modelList){
		this.modelList = modelList;
	}

	public ModelDef[] correct(){
		for(ModelDef m : modelList){

			this.model = m;
			correct1to1Detail();
			removeUnnecessaryHasOneToSelf();
			removeUnnecessaryHasManyToSelf();
			crossOutLinkerTables();

		}
		return modelList;
	}



	/**
	 * If the localColum of that hasMany table is the primary attributes AND it refer to the primary column of this table
	 * @param model
	 * @param modelList
	 * @return
	 */
	private void correct1to1Detail(){
		String[] hasManyTables = model.getHasMany();
		for(String hasMany : hasManyTables){
			ModelDef hasManyModel = getModel(hasMany);
			if(hasManyModel != null  && !hasManyModel.equals(model)){
				String[] hasManyModelHasOne = hasManyModel.getHasOne();
				String[] hasManyModelHasOneLocalColumn = hasManyModel.getHasOneLocalColumn();
				String[] hasManyModelHasOneReferencedColumn = hasManyModel.getHasOneReferencedColumn();
				for(int i = 0; i < hasManyModelHasOne.length; i++){
					if(model.getTableName().equals(hasManyModelHasOne[i])){//if this table is being refered by the hasManyTable
						//check to see if the referenced colum if the primary key of this table
						boolean inModelsPrimary = CStringUtils.inArray(model.getPrimaryAttributes(), hasManyModelHasOneLocalColumn[i]);
						boolean inHasManyPrimary = CStringUtils.inArray(hasManyModel.getPrimaryAttributes(), hasManyModelHasOneReferencedColumn[i]);

						if (inModelsPrimary && model.getPrimaryAttributes().length == 1
								&& inHasManyPrimary && hasManyModel.getPrimaryAttributes().length == 1){

							model = moveFromHasManyToHasOne(model, hasMany);

						}

					}
				}
			}

		}

	}

	private static ModelDef moveFromHasManyToHasOne(ModelDef model, String hasMany) {
		List<String> newHasMany = new LinkedList<String>(Arrays.asList(model.getHasMany()));

		int index = CStringUtils.indexOf(model.getHasMany(), hasMany);
		String addedHasOne = newHasMany.remove(index);
		List<String> newHasManyLocalColumn = new LinkedList<String>(Arrays.asList(model.getHasManyLocalColumn()));
		String addedHasOneLocalColumn = newHasManyLocalColumn.remove(index);

		List<String> newHasManyReferencedColumn = new LinkedList<String>(Arrays.asList(model.getHasManyReferencedColumn()));
		String addedHasOneReferencedLocalColumn = newHasManyReferencedColumn.remove(index);

		model.setHasMany(newHasMany.toArray(new String[newHasMany.size()]));
		model.setHasManyReferredColumn(newHasManyReferencedColumn.toArray(new String[newHasManyReferencedColumn.size()]));
		model.setHasManyForeignColumn(newHasManyLocalColumn.toArray(new String[newHasManyLocalColumn.size()]));


		List<String> newHasOne = new ArrayList<String>(Arrays.asList(model.getHasOne()));
		newHasOne.add(addedHasOne);

		List<String> newHasOneLocalColumn = new ArrayList<String>(Arrays.asList(model.getHasOneLocalColumn()));
		newHasOneLocalColumn.add(addedHasOneLocalColumn);

		List<String> newHasOneReferencedLocalColumn =new ArrayList<String>( Arrays.asList(model.getHasOneReferencedColumn()));
		newHasOneReferencedLocalColumn.add(addedHasOneReferencedLocalColumn);

		model.setHasOne(newHasOne.toArray(new String[newHasOne.size()]));
		model.setHasOneLocalColumn(newHasOneLocalColumn.toArray(new String[newHasOneLocalColumn.size()]));
		model.setHasOneReferredColumn(newHasOneReferencedLocalColumn.toArray(new String[newHasOneReferencedLocalColumn.size()]));
		return model;
	}

	private ModelDef getModel(String hasMany) {
		for(ModelDef model : modelList){
			if(hasMany.equals(model.getTableName())){
				return model;
			}
		}
		return null;
	}


	/**
	 * When refering to self using the same local and reference_id remove it
	 * @param model
	 * @param modelList
	 * @return
	 */
	private void removeUnnecessaryHasOneToSelf() {
		model.getPrimaryAttributes();
		String[] hasOne = model.getHasOne();
		String[] hasOneLocalColumn = model.getHasOneLocalColumn();
		String[] hasOneReferencedColumn = model.getHasOneReferencedColumn();


		for(int i = 0; i < hasOne.length; i++){
			boolean hasOneLocalInPrimary = CStringUtils.inArray(model.getPrimaryAttributes(), hasOneLocalColumn[i]);
			boolean hasOneReferencedInPrimary = CStringUtils.inArray(model.getPrimaryAttributes(), hasOneReferencedColumn[i]);

			if(hasOne[i].equals(model.getTableName()) && hasOneLocalColumn[i].equals(hasOneReferencedColumn[i]) &&
					hasOneLocalInPrimary && hasOneReferencedInPrimary ){
				//				System.err.println("Removing unneccessary to self: "+model.getTableName()+" -> "+hasOne[i]);

				model = removeFromHasOne(model, hasOne[i]);
			}
		}

	}

	/**
	 * When refering to self using the same local and reference_id remove it
	 * @param model
	 * @param modelList
	 * @return
	 */
	private void removeUnnecessaryHasManyToSelf() {
		model.getPrimaryAttributes();
		String[] hasMany = model.getHasMany();
		String[] hasManyLocalColumn = model.getHasManyLocalColumn();
		String[] hasManyReferencedColumn = model.getHasManyReferencedColumn();


		for(int i = 0; i < hasMany.length; i++){
			boolean hasManyLocalInPrimary = CStringUtils.inArray(model.getPrimaryAttributes(), hasManyLocalColumn[i]);
			boolean hasManyReferencedInPrimary = CStringUtils.inArray(model.getPrimaryAttributes(), hasManyReferencedColumn[i]);

			if(hasMany[i].equals(model.getTableName()) && hasManyLocalColumn[i].equals(hasManyReferencedColumn[i]) &&
					hasManyLocalInPrimary && hasManyReferencedInPrimary ){
				model = removeFromHasMany(model, hasMany[i]);
			}
		}

	}

	private static ModelDef removeFromHasMany(ModelDef model2, String hasMany) {

		int index = CStringUtils.indexOf(model2.getHasMany(), hasMany);

		List<String> newHasMany = new ArrayList<String>(Arrays.asList(model2.getHasMany()));
		newHasMany.remove(index);

		List<String> newHasManyLocalColumn = new ArrayList<String>(Arrays.asList(model2.getHasManyLocalColumn()));
		newHasManyLocalColumn.remove(index);

		List<String> newHasManyReferencedLocalColumn =new ArrayList<String>( Arrays.asList(model2.getHasManyReferencedColumn()));
		newHasManyReferencedLocalColumn.remove(index);

		model2.setHasMany(newHasMany.toArray(new String[newHasMany.size()]));
		model2.setHasManyForeignColumn(newHasManyLocalColumn.toArray(new String[newHasManyLocalColumn.size()]));
		model2.setHasManyReferredColumn(newHasManyReferencedLocalColumn.toArray(new String[newHasManyReferencedLocalColumn.size()]));

		return model2;
	}

	private static ModelDef removeFromHasOne(ModelDef model2, String hasOne) {

		int index = CStringUtils.indexOf(model2.getHasOne(), hasOne);

		List<String> newHasOne = new ArrayList<String>(Arrays.asList(model2.getHasOne()));
		newHasOne.remove(index);

		List<String> newHasOneLocalColumn = new ArrayList<String>(Arrays.asList(model2.getHasOneLocalColumn()));
		newHasOneLocalColumn.remove(index);

		List<String> newHasOneReferencedLocalColumn =new ArrayList<String>( Arrays.asList(model2.getHasOneReferencedColumn()));
		newHasOneReferencedLocalColumn.remove(index);

		model2.setHasOne(newHasOne.toArray(new String[newHasOne.size()]));
		model2.setHasOneLocalColumn(newHasOneLocalColumn.toArray(new String[newHasOneLocalColumn.size()]));
		model2.setHasOneReferredColumn(newHasOneReferencedLocalColumn.toArray(new String[newHasOneReferencedLocalColumn.size()]));

		return model2;
	}

	/**
	 * Remove a linker table present in the hasMany, then short circuit right away to the linked table
	 * 
	 * Linker tables contains composite primary keys of two tables
	 * If each local column of the primary key is the primary of the table it is referring to
	 * the this is a lookup table
	 * 
	 * ie. product, product_category, category
	 *  Product will have many category
	 *  Category at the same time is used by many product
	 *  Changes should be apply on both tables right away
	 */
	private void crossOutLinkerTables(){
		String[] primaryKeys = model.getPrimaryAttributes();
		if(primaryKeys != null && primaryKeys.length == 2){//there are only 2 primary keys
			//if both primary keys look up to different table which is also a primary key
			String[] hasOne = model.getHasOne();
			String[] hasOneLocalColum = model.getHasOneLocalColumn();
			String[] hasOneReferencedColumn = model.getHasOneReferencedColumn();

			int indexP1 = CStringUtils.indexOf(hasOneLocalColum, primaryKeys[0]);
			int indexP2 = CStringUtils.indexOf(hasOneLocalColum, primaryKeys[1]);

			if(indexP1 >= 0 && indexP2 >= 0){
				String t1 = hasOne[indexP1];
				String t2 = hasOne[indexP2];

				String ref1 = hasOneReferencedColumn[indexP1];
				String ref2 = hasOneReferencedColumn[indexP2];

				ModelDef m1 = getModel(t1);
				boolean isRef1Primary = CStringUtils.inArray(m1.getPrimaryAttributes(), ref1);

				ModelDef m2 = getModel(t2);
				boolean isRef2Primary = CStringUtils.inArray(m2.getPrimaryAttributes(), ref2);

				if(model != m1 && model != m2
						&& isRef1Primary
						&& isRef2Primary
						){

					System.err.println("linker table "+model.getTableName());
					System.err.println("link directly "+m1.getTableName()+" and "+m2.getTableName());

					removeFromHasMany(m1, model.getTableName());//remove the hasMany of this table
					removeFromHasMany(m2, model.getTableName());//remove the hasMany of this table

					addToHasMany(m1, m2.getTableName(), ref2, null);
					addToHasMany(m2, m1.getTableName(), ref1, null);
				}
			}

		}
	}

	private void addToHasMany(ModelDef model2, String tableName,
			String m2primaryKey, String m1LocalColumn) {

		List<String> newHasMany = new ArrayList<String>(Arrays.asList(model2.getHasMany()));
		newHasMany.add(tableName);

		List<String> newHasManyLocalColumn = new ArrayList<String>(Arrays.asList(model2.getHasManyLocalColumn()));
		newHasManyLocalColumn.add(m1LocalColumn);

		List<String> newHasManyReferencedLocalColumn =new ArrayList<String>( Arrays.asList(model2.getHasManyReferencedColumn()));
		newHasManyReferencedLocalColumn.add(m2primaryKey);

		model2.setHasMany(newHasMany.toArray(new String[newHasMany.size()]));
		model2.setHasManyForeignColumn(newHasManyLocalColumn.toArray(new String[newHasManyLocalColumn.size()]));
		model2.setHasManyReferredColumn(newHasManyReferencedLocalColumn.toArray(new String[newHasManyReferencedLocalColumn.size()]));

	}



}
