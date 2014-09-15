package com.ivanceras.db.sample;

import java.util.Map;
import java.util.LinkedHashMap;
import com.ivanceras.db.shared.DAO;

import com.ivanceras.db.api.ModelDef;

public class BazaarModelMetaData extends com.ivanceras.db.model.ModelMetaData{


	public static Map<Class<? extends DAO>, ModelDef> modelDefList = new LinkedHashMap<Class<? extends DAO>, ModelDef>();
	public static Map<String, ModelDef> namedModelDefList = new LinkedHashMap<String, ModelDef>();
	public static Map<Class<? extends DAO>, String> modelNameListNdx = new LinkedHashMap<Class<? extends DAO>, String>();
	public static Map<String, Class<? extends DAO>> classListNdx = new LinkedHashMap<String, Class<? extends DAO>>();



	void init4(){

		String   namespace                               = "public";
		String   modelName                               = "Category";
		String[] attributes                              = {"organization_id", "client_id", "created", "createdby", "updated", "updatedby", "name", "description", "help", "active", "category_id"};
		String   generatedAttribute                      = null;
		String[] primaryAttributes                       = {"category_id"};
		String[] uniqueAttributes                        = {"name"};
		String[] dataTypes                               = {"UUID", "UUID", "Date", "UUID", "Date", "UUID", "String", "String", "String", "Boolean", "UUID"};

		String[] hasOneLocalColumn                       = {"name"};
		String[] hasOne                                  = {"category"};
		String[] hasOneReferencedColumn                  = {"name"};

		String[] hasMany                                 = {"category", "product"};
		String[] hasManyReferencedColumn                 = {"name", "product_id"};
		String[] hasManyLocalColumn                      = {"name", "null"};

		String parentClass                         		 = "record";

		boolean  caseSensitive								= false;
		
		ModelDef modelDef = new ModelDef(
			namespace, 
			modelName, 
			attributes,
			generatedAttribute,
			dataTypes, 
			primaryAttributes, 
			uniqueAttributes, 
			hasOne, 
			hasOneLocalColumn, 
			hasOneReferencedColumn, 
			hasMany, 
			hasManyReferencedColumn, 
			hasManyLocalColumn, 
			caseSensitive);
				
		modelDef.setPolymorphic(false);
		modelDef.setTableName("category");
		modelDef.setDescription("null");
		modelDef.setParentClass(parentClass);
		modelDefList.put(DAO_Category.class, modelDef);
		namedModelDefList.put("Category", modelDef);
		modelNameListNdx.put(DAO_Category.class, "Category");
		classListNdx.put("Category", DAO_Category.class);


	}



	void init11(){

		String   namespace                               = "public";
		String   modelName                               = "Product";
		String[] attributes                              = {"organization_id", "client_id", "created", "createdby", "updated", "updatedby", "name", "description", "help", "active", "product_id", "parent_product_id", "is_service", "price", "use_parent_price", "unit", "stocks", "tags", "info", "currency", "seq_no", "upfront_fee"};
		String   generatedAttribute                      = null;
		String[] primaryAttributes                       = {"product_id"};
		String[] uniqueAttributes                        = {};
		String[] dataTypes                               = {"UUID", "UUID", "Date", "UUID", "Date", "UUID", "String", "String", "String", "Boolean", "UUID", "UUID", "Boolean", "BigDecimal", "Boolean", "String", "BigDecimal", "String", "String", "String", "Integer", "BigDecimal"};

		String[] hasOneLocalColumn                       = {"product_id"};
		String[] hasOne                                  = {"product_availability"};
		String[] hasOneReferencedColumn                  = {"product_id"};

		String[] hasMany                                 = {"category"};
		String[] hasManyReferencedColumn                 = {"category_id"};
		String[] hasManyLocalColumn                      = {"null"};

		String parentClass                         		 = "record";

		boolean  caseSensitive								= false;
		
		ModelDef modelDef = new ModelDef(
			namespace, 
			modelName, 
			attributes,
			generatedAttribute,
			dataTypes, 
			primaryAttributes, 
			uniqueAttributes, 
			hasOne, 
			hasOneLocalColumn, 
			hasOneReferencedColumn, 
			hasMany, 
			hasManyReferencedColumn, 
			hasManyLocalColumn, 
			caseSensitive);
				
		modelDef.setPolymorphic(false);
		modelDef.setTableName("product");
		modelDef.setDescription("null");
		modelDef.setParentClass(parentClass);
		modelDefList.put(DAO_Product.class, modelDef);
		namedModelDefList.put("Product", modelDef);
		modelNameListNdx.put(DAO_Product.class, "Product");
		classListNdx.put("Product", DAO_Product.class);


	}

	void init12(){

		String   namespace                               = "public";
		String   modelName                               = "ProductAvailability";
		String[] attributes                              = {"organization_id", "client_id", "created", "createdby", "updated", "updatedby", "product_id", "available", "always_available", "stocks", "available_from", "available_until", "available_day", "open_time", "close_time"};
		String   generatedAttribute                      = null;
		String[] primaryAttributes                       = {"product_id"};
		String[] uniqueAttributes                        = {};
		String[] dataTypes                               = {"UUID", "UUID", "Date", "UUID", "Date", "UUID", "UUID", "Boolean", "Boolean", "BigDecimal", "Date", "Date", "String", "Date", "Date"};

		String[] hasOneLocalColumn                       = {"product_id"};
		String[] hasOne                                  = {"product"};
		String[] hasOneReferencedColumn                  = {"product_id"};

		String[] hasMany                                 = {};
		String[] hasManyReferencedColumn                 = {};
		String[] hasManyLocalColumn                      = {};

		String parentClass                         		 = "base";

		boolean  caseSensitive								= false;
		
		ModelDef modelDef = new ModelDef(
			namespace, 
			modelName, 
			attributes,
			generatedAttribute,
			dataTypes, 
			primaryAttributes, 
			uniqueAttributes, 
			hasOne, 
			hasOneLocalColumn, 
			hasOneReferencedColumn, 
			hasMany, 
			hasManyReferencedColumn, 
			hasManyLocalColumn, 
			caseSensitive);
				
		modelDef.setPolymorphic(false);
		modelDef.setTableName("product_availability");
		modelDef.setDescription("null");
		modelDef.setParentClass(parentClass);
		modelDefList.put(DAO_ProductAvailability.class, modelDef);
		namedModelDefList.put("ProductAvailability", modelDef);
		modelNameListNdx.put(DAO_ProductAvailability.class, "ProductAvailability");
		classListNdx.put("ProductAvailability", DAO_ProductAvailability.class);


	}

	void init13(){

		String   namespace                               = "public";
		String   modelName                               = "ProductCategory";
		String[] attributes                              = {"organization_id", "client_id", "created", "createdby", "updated", "updatedby", "product_id", "category_id"};
		String   generatedAttribute                      = null;
		String[] primaryAttributes                       = {"product_id", "category_id"};
		String[] uniqueAttributes                        = {};
		String[] dataTypes                               = {"UUID", "UUID", "Date", "UUID", "Date", "UUID", "UUID", "UUID"};

		String[] hasOneLocalColumn                       = {"category_id", "product_id", "category_id"};
		String[] hasOne                                  = {"category", "product", "product_category"};
		String[] hasOneReferencedColumn                  = {"category_id", "product_id", "product_id"};

		String[] hasMany                                 = {"product_category"};
		String[] hasManyReferencedColumn                 = {"category_id"};
		String[] hasManyLocalColumn                      = {"product_id"};

		String parentClass                         		 = "base";

		boolean  caseSensitive								= false;
		
		ModelDef modelDef = new ModelDef(
			namespace, 
			modelName, 
			attributes,
			generatedAttribute,
			dataTypes, 
			primaryAttributes, 
			uniqueAttributes, 
			hasOne, 
			hasOneLocalColumn, 
			hasOneReferencedColumn, 
			hasMany, 
			hasManyReferencedColumn, 
			hasManyLocalColumn, 
			caseSensitive);
				
		modelDef.setPolymorphic(false);
		modelDef.setTableName("product_category");
		modelDef.setDescription("null");
		modelDef.setParentClass(parentClass);
		modelDefList.put(DAO_ProductCategory.class, modelDef);
		namedModelDefList.put("ProductCategory", modelDef);
		modelNameListNdx.put(DAO_ProductCategory.class, "ProductCategory");
		classListNdx.put("ProductCategory", DAO_ProductCategory.class);


	}








	public BazaarModelMetaData(){
		super(modelDefList, namedModelDefList, modelNameListNdx, classListNdx);
		init4();
		init11();
		init12();
		init13();
	}
}
