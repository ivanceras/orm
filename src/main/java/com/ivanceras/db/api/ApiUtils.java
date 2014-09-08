package com.ivanceras.db.api;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.ivanceras.commons.strings.CStringUtils;
import com.ivanceras.db.model.ModelMetaData;
import com.ivanceras.db.shared.DAO;
import com.ivanceras.db.shared.Filter;
import com.ivanceras.db.shared.datatype.GenericDataType;
import com.ivanceras.db.shared.exception.DatabaseException;
import com.ivanceras.db.shared.util.SpecialCase;

public class ApiUtils {

	public static String getDefaultAutoIncrementColumn(String[] columns, String[] dataTypes, String[] primaryKeys){

		for(int i = 0; i < columns.length; i++){
			String c = columns[i];
			String dt = dataTypes[i];
			if(c != null && c.equalsIgnoreCase("id") && (dt.equals(GenericDataType.INTEGER) || dt.equals(GenericDataType.LONG))){
				return c;
			}else{
				int index = CStringUtils.indexOf(primaryKeys, c);
				if(index >= 0 && c.toLowerCase().endsWith("id") && primaryKeys.length == 1 && (dt.equals(GenericDataType.INTEGER) || dt.equals(GenericDataType.LONG))){//if only one primary and column name ends with id, and data type is integer, then must be an autoincrement column
					return c;
				}
			}
		}
		return null;
	}


	public Filter[] getUniqueFilters(ModelDef model, DAO dao) {
		String[] uniqueAttributes = model.getUniqueAttributes();
		Filter[] filters = new Filter[uniqueAttributes.length];
		for(int i = 0; i < uniqueAttributes.length; i++ ){
			Filter f = new Filter(getDBElementName(model, uniqueAttributes[i]), Filter.EQUAL, dao.get_Value(uniqueAttributes[i]));
			filters[i] = f;
		}
		return filters;
	}

	public static String getPivotTable(ModelDef model1, ModelDef model2) {
		String[] model1HasOnes = model1.getHasOne();
		String[] model1HasMany = model1.getHasMany();

		String[] model2HasOnes = model2.getHasOne();
		String[] model2HasMany = model2.getHasMany();

		String[] model1RelatedModels = CStringUtils.mergeString(model1HasOnes, model1HasMany);
		String[] model2RelatedModels = CStringUtils.mergeString(model2HasOnes, model2HasMany);

		for(int i = 0; i < model1RelatedModels.length; i++){
			for(int j = 0; j < model2RelatedModels.length; j++){
				if(i!=j){
					if(model1RelatedModels[i].equals(model2RelatedModels[j])){
						return CStringUtils.capitalize(model1RelatedModels[i]);
					}
				}
			}
		}
		return null;
	}


	public static boolean hasDirectRelation(ModelDef model1, ModelDef model2){
		if(model1.getForeignKeyColumn(model2.getModelName()) != null){
			return true;
		}else if(model2.getForeignKeyColumn(model1.getModelName()) != null) {
			return true;
		}
		return false;
	}

	public static Filter[] getPrimaryFilters(ModelMetaData em, DAO dao) throws DatabaseException {
		return getFilters(em, dao, false);
	}


	//	public static Filter[] getFilters(ModelMetaData em, DAO dao) {
	//		return getFilters(em, dao, true);
	//	}

	public static Filter[] getFilters(ModelMetaData em, DAO dao, boolean includeModelName) {
		ModelDef model = em.getDefinition(dao.getModelName());
		return getFilters(model, dao, includeModelName);
	}


	//	public static Filter[] getAllFilters(ModelMetaData em, DAO dao, boolean includeModelName) {
	//		ModelDef model = em.getDefinition(dao.getModelName());
	//		return getFilters(model, dao, includeModelName);
	//	}

	public static Filter[] getAllFilters(ModelDef model, DAO dao, boolean includeModelName){
		String[] attributes = model.getAttributes();
		List<Filter> filters = new ArrayList<Filter>();
		if(attributes != null){
			for(int i = 0; i < attributes.length; i++ ){
				Object value = dao.get_Value(attributes[i]);
				StringBuffer attb = new StringBuffer();
				if(includeModelName){
					attb.append(getDBElementName(model, model.getModelName())+".");
				}
				attb.append(getDBElementName(model, attributes[i]));
				if(value != null){
					filters.add(new Filter(attb.toString(), Filter.EQUAL, value));
				}
				else{
					filters.add(new Filter(attb.toString(), Filter.NULL, value));
				}
			}
		}
		return filters.toArray(new Filter[filters.size()]);
	}

	public static Filter[] getPrimaryAndUniqueFilters(ModelDef model, DAO dao, boolean includeModelName){
		String[] primaryAttributes = model.getPrimaryAttributes();
		String[] uniqueAttributes = model.getUniqueAttributes();
		List<String> attributes = new ArrayList<String>();
		for(String pri : primaryAttributes){
			attributes.add(pri);
		}
		for(String uniq : uniqueAttributes){
			if(CStringUtils.indexOf(primaryAttributes, uniq) >=0){
				;
			}
			else{
				attributes.add(uniq);
			}
		}
		List<Filter> filters = new ArrayList<Filter>();
		for(int i = 0; i < attributes.size(); i++ ){
			Object value = dao.get_Value(attributes.get(i));
			StringBuffer attb = new StringBuffer();
			if(includeModelName){
				attb.append(getDBElementName(model, model.getModelName())+".");
			}
			attb.append(getDBElementName(model, attributes.get(i)));
			if(value != null){
				filters.add(new Filter(attb.toString(), Filter.EQUAL, value));
			}
			else{
				filters.add(new Filter(attb.toString(), Filter.NULL, value));
			}
		}
		return filters.toArray(new Filter[filters.size()]);
	}

	public static Filter[] getFilters(ModelDef model, DAO dao, boolean includeModelName){
		String[] primaryAttributes = model.getPrimaryAttributes();
		List<Filter> filters = new ArrayList<Filter>();
		if(primaryAttributes != null){
			for(int i = 0; i < primaryAttributes.length; i++ ){
				Object value = dao.get_Value(primaryAttributes[i]);
				if(value != null){
					StringBuffer attb = new StringBuffer();
					if(includeModelName){
						attb.append(getDBElementName(model, model.getTableName())+".");
					}
					attb.append(getDBElementName(model, primaryAttributes[i]));
					filters.add(new Filter(attb.toString(), Filter.EQUAL, value));
				}
			}
		}
		return filters.toArray(new Filter[filters.size()]);
	}


	public static Filter[] getFiltersForUnique(ModelDef model, DAO dao){
		String[] uniqueAttributes = model.getUniqueAttributes();
		if(uniqueAttributes != null){
			Filter[] filters = new Filter[uniqueAttributes.length];
			for(int i = 0; i < uniqueAttributes.length; i++){
				String uattr = uniqueAttributes[i];
				Object value = dao.get_Value(uattr);
				filters[i] = new Filter(uattr, Filter.EQUAL, value);
			}
			return filters;
		}
		return null;
	}

	public static Filter[] addToFilters(Filter[] filters1, Filter filters2){
		if(filters2 != null){
			Filter[] filters = new Filter[1];
			filters[0] = filters2;
			if(filters1 != null){
				return mergeFilters(filters1, filters);
			}
			else{
				return filters;
			}
		}else{
			return filters1;
		}
	}
	public static Filter[] mergeFilters(Filter[] filters1, Filter[] filters2){
		int filter1Cnt = (filters1 != null ? filters1.length : 0);
		int filter2Cnt = (filters2 != null ? filters2.length : 0);
		int totalFilters = filter1Cnt + filter2Cnt; 
		if(totalFilters == 0){
			return null;
		}
		Filter[] allFilters = new Filter[totalFilters];
		if(filters1 != null){
			for(int i = 0; i < filters1.length; i++){
				allFilters[i] = filters1[i];
			}
		}
		if(filters2!=null){
			for(int j = 0; j < filters2.length; j++){
				allFilters[filter1Cnt+j] = filters2[j];
			}
		}
		return allFilters;
	}
	/**
	 * applies to tableSchema and elementName
	 * @param model
	 * @return
	 */
	public static String getDBElementName(ModelDef model, String elementName){
		return getDBElementName(model, elementName, "\"");
	}

	public static String getDBElementName(ModelDef model, String elementName, String quote){
		if(model!=null && model.isCaseSensitive()){
			return quote+elementName+quote;
		}
		return SpecialCase.correctKeyWords(elementName, false, quote);
	}


	/**
	 * build a join statement based from the relationships of Tables in the joinPath, order of the array matters, should mention first the table that has to join on for the next join
	 * @param em
	 * @param joinPath
	 * @param joinType
	 * @return
	 * @throws DatabaseException
	 */
	public static JoinPair[] findJoinPair(EntityManager em, Class<? extends DAO>[] joinPath, String joinType) throws DatabaseException{

		if(joinPath == null){
			return null;
		}

		List<JoinPair> joinPairs = new ArrayList<JoinPair>();

		ModelDef[] modeldefs = new ModelDef[joinPath.length];

		for(int i = 0; i < joinPath.length; i++){
			modeldefs[i] = em.getDefinition(joinPath[i]);
		}

		for(int i = 0; i < joinPath.length; i++){
			if(i != 0){
				Class<? extends DAO> model1class = joinPath[i-1];
				Class<? extends DAO> model2class = joinPath[i];
				ModelDef model1 = em.getDefinition(model1class);
				ModelDef model2 = em.getDefinition(model2class);
				String[] model1columns = model1.getLocalColumns(model2.getModelName());
				String[] model2columns = model2.getLocalColumns(model1.getModelName());
				if(model1columns != null && model1columns.length > 0){
					String[] model1refferedColumns = model1.getReferencedColumns(model2.getModelName());
					ColumnPair columnPairs = new ColumnPair(model1columns, model1refferedColumns);
					JoinPair join = new JoinPair(em, model1class, model2class, columnPairs, joinType);
					joinPairs.add(join);
				}
				else if(model2columns != null && model2columns.length > 0){
					String[] model2refferedColumns = model2.getReferencedColumns(model1.getModelName());
					ColumnPair columnPairs = new ColumnPair(model2columns, model2refferedColumns);
					JoinPair join = new JoinPair(em, model2class, model1class, columnPairs, joinType);
					joinPairs.add(join);
				}
				else{
					int joinTo = getHasRelatedToAforeMentionedTable(em, joinPath, model2class);
					if(joinTo >= 0){
						Class<? extends DAO> joinToClass = joinPath[joinTo];
						ModelDef joinToModel = modeldefs[joinTo];
						String[] joinToColumns = joinToModel.getLocalColumns(model2.getModelName());
						String[] joinToReffererdColumns = joinToModel.getReferencedColumns(model2.getModelName());
						ColumnPair columnPairs = new ColumnPair(joinToColumns, joinToReffererdColumns);
						JoinPair join = new JoinPair(em, joinToClass, model2class, columnPairs, joinType);
						joinPairs.add(join);
					}
					else{
						throw new DatabaseException("Unable to find relation ship match from prior join paths for ["+model2.getModelName()+"]" +
								".\nYou may have to rearrange the order of the join path");
					}
				}
			}
		}
		return joinPairs.toArray(new JoinPair[joinPairs.size()]);
	}
	


	private static int getHasRelatedToAforeMentionedTable(EntityManager em, Class<? extends DAO>[] joinPath,
			Class<? extends DAO> class1) throws DatabaseException {
		int index = indexOf(joinPath, class1);
		for(int i = 0; i < index; i++){
			ModelDef model = em.getDefinition(joinPath[i]);
			String modelName = em.getModelName(class1).toLowerCase();
			String[] localColumns = model.getLocalColumns(modelName);
			if(localColumns != null){
				return i;
			}
		}
		return -1;
	}
	


	private static int indexOf(Class<? extends DAO>[] joinPath,
			Class<? extends DAO> class1){
		for(int i = 0; i < joinPath.length; i++){
			if(joinPath[i].equals(class1)){
				return i;
			}
		}
		return -1;
	}


	public static String createSensibleId(ModelDef model, DAO dao){
		return createSensibleId(model, dao, 16, false);
	}

	public static String createSensibleId(ModelDef model, DAO dao, int length, boolean doRandomAppend){
		String[] uattrs = model.getUniqueAttributes();
		StringBuffer senseId = new StringBuffer();
		if(uattrs != null){
			boolean doUnds = false;
			for(String uatt : uattrs){
				if(doUnds){
					senseId.append("_");
				}else{doUnds = true;}
				senseId.append(dao.get_Value(uatt));
			}
			String id = senseId.toString().replace("-","").replaceAll("[ -]", "").toLowerCase();
			String noVowels = CStringUtils.removeVowelsExceptFirst(id);
			String hash = CStringUtils.getHash(UUID.randomUUID().toString());
			if(id.length() == length){
				return "["+id+"]";
			}
			else if(id.length() > length){
				if(!doRandomAppend){//recreatable
					return "["+noVowels+"]";
				}
				else if(noVowels.length() < length){
					int gap = length - noVowels.length();
					String shortHash = hash.substring(1, gap);
					return "["+noVowels+shortHash+"]";
				}
			}
			else{//lesser than desirable length
				int gap = length - id.length();
				String shortHash = hash.substring(1, gap);
				if(doRandomAppend){
					return "["+id+shortHash+"]";
				}else{
					return "["+id+"]";
				}
			}
		}else{
			if(doRandomAppend){
				return UUID.randomUUID().toString();
			}
		}
		return null;
	}


}
