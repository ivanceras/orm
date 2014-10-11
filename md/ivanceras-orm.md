# Ivanceras ORM


An ORM aimed to be migration and version upgrade friendly.

[![Build Status](https://api.travis-ci.org/ivanceras/orm.svg)](https://travis-ci.org/ivanceras/orm)

Yes, I know this is yet another ORM library like many others.

But this exists because all other ORM i came across does not fit all my requirements, one of which is to be able to do database operations in the browser using the same controller codes in your server side.




#### Features:


 * Code Generators.
    You can use the library to generate source code for you.

    You can use the library to generate all the necessary DAO,models and mappers which you will use in manipulating the data.
    * DAO - (data access objects), resembles the table object in the database.
    * Model - models will be used in your API's exposed to some other clients.
    * Mappers - mappers is used to convert Models to DAO. This is very useful when you are creating a new version of your API, which the database has changed drastically that the mapping from Model to DAO requires non-trivial work.

    * The code generator can figure out which of the Models has 1:1 or 1:M relations based on the primary keys and foreign keys of the table schemas
    * a Model which has 1:1 relationship with another model, will have each other's model as a field.
    * a Model which has 1:M relationship with another model, will have a field arry of the other model

####    Example:
    	Product, ProductAvailability, ProductCategory, Category
    	* Product has 1 ProductAvailabilty
    	* ProductCategory is a linker table for Product and Category, so Product has many Categories

```sql
    CREATE TABLE product
    (
      product_id uuid NOT NULL DEFAULT uuid_generate_v4(),
      parent_product_id uuid,
      name character varying,
      description character varying,
      price numeric,
      unit character varying,
      stocks numeric,
      tags json,
      info json,
      currency character varying,
      seq_no integer,
      CONSTRAINT product_pkey PRIMARY KEY (product_id)
    );


    CREATE TABLE product_availability
    (
      product_id uuid NOT NULL,
      available boolean,
      always_available boolean,
      stocks numeric,
      available_from timestamp with time zone,
      available_until timestamp with time zone,
      available_day json,
      open_time time with time zone,
      close_time time with time zone,
      CONSTRAINT product_availability_pkey PRIMARY KEY (product_id),
      CONSTRAINT product_availability_product_id_fkey FOREIGN KEY (product_id)
          REFERENCES product (product_id) MATCH SIMPLE
          ON UPDATE NO ACTION ON DELETE NO ACTION
    );

     CREATE TABLE category
    (
      category_id uuid NOT NULL DEFAULT uuid_generate_v4(),
      name character varying,
      description character varying,
      CONSTRAINT category_pkey PRIMARY KEY (category_id),
      CONSTRAINT category_name_key UNIQUE (name)
    );

    CREATE TABLE product_category
    (
      product_id uuid NOT NULL,
      category_id uuid NOT NULL,
      CONSTRAINT product_category_pkey PRIMARY KEY (product_id, category_id),
      CONSTRAINT product_category_category_id_fkey FOREIGN KEY (category_id)
          REFERENCES category (category_id) MATCH SIMPLE
          ON UPDATE NO ACTION ON DELETE NO ACTION,
      CONSTRAINT product_category_product_id_fkey FOREIGN KEY (product_id)
          REFERENCES product (product_id) MATCH SIMPLE
          ON UPDATE NO ACTION ON DELETE NO ACTION
    );

    CREATE TABLE users
    (
      user_id uuid NOT NULL DEFAULT uuid_generate_v4(),
      username character varying,
      password character varying,
      email character varying,
      CONSTRAINT users_pkey PRIMARY KEY (user_id)
    );


```
#### Note
    * Some fields are omitted to provide more readability
      * organization_id, client_id, created, createdby, updated, updatedby, description, help, active

#### The generated Models


```java

    public class Product{

        	private Date created;
        	private UUID createdby;
        	private String name;
        	private String description;
        	private Boolean active;
        	private UUID productId;
        	private UUID parentProductId;
        	private Boolean isService;
        	private BigDecimal price;
        	private Boolean useParentPrice;
        	private String unit;
        	private BigDecimal stocks;
        	private String tags;
        	private String info;
        	private String currency;
        	private Integer seqNo;
        	private BigDecimal upfrontFee;

        	private ProductAvailability productAvailability;

        	private Category[] categoryList;

        	//setters and getters
            .....
    }



    public class ProductAvailability{

        	private UUID productId;
        	private Boolean available;
        	private Boolean alwaysAvailable;
        	private BigDecimal stocks;
        	private Date availableFrom;
        	private Date availableUntil;
        	private String availableDay;
        	private Date openTime;
        	private Date closeTime;

        	private Product product;
        	//setters and getters
            .....
    }



    public class Category{

        	private String name;
        	private String description;
        	private Boolean active;
        	private UUID categoryId;

        	private Category category;

        	private Category[] categoryList;

        	private Product[] productList;

    		//setters and getters
            .....
    }

    public class ProductCategory{

        	private Date created;
        	private UUID createdby;
        	private UUID productId;
        	private UUID categoryId;

        	private Category category;

        	private Product product;

        	//setters and getters
        .....
    }


    public class Users{

        	private String name;
        	private Boolean active;
        	private UUID userId;
        	private String username;
        	private String password;
        	private String email;

        	//setters and getters
        .....
    }

```

### Usage:
In your controller, you can use the generated classes like this:

```java

    /**
    * Getting the userId using username or Email
    */
    public UUID getUserId(EntityManager em, String usernameOrEmail) throws DatabaseException{
		DAO_Users user = em.getOne(DAO_Users.class,
				            new Filter(users.username, Filter.EQUAL, usernameOrEmail)
				           .or(new Filter(users.email, Filter.EQUAL, usernameOrEmail))
				        );
		if(user != null){
			return user.getUserId();
		}
		return null;
	}
```

If you prefer to use the fluentsql API, you can do so by:

```java

	import static com.ivanceras.fluent.sql.SQL.Statics.*;
    ...

	public UUID getUserId(EntityManager em, String usernameOrEmail) throws DatabaseException{
		SQL sql = SELECT(users.username, users.user_id, users.email)
					.FROM(Table.users)
					.WHERE(users.username).EQUAL(userNameOrEmail)
					.OR(users.email).EQUAL(userNameOrEmail);

		DAO_Users[] users = em.execute(sql);
		if(users.length > 0){
		DAO_Users user0 = users[0];
			return user0.getUserId();
		}
		return null;
	}

```

Note: Though writing in fluentsql API is a lot more readable and flexible, this should be used sparingly in order not to loose portability of your app to any other database platform that is very different from traditional RDBMS database (such as Google BigTable, DynamoDB)



```java
    /**
    * Getting the list of Products created by a certain user
    */

    public DAO_Product[] listUserProducts(EntityManager em, String username, int page, int itemsPerPage) throws DatabaseException{
		UUID userId = getUserId(em, username);
		System.err.println("UserId: "+userId);
		Query query = new Query(em, DAO_Product.class);
		query.setPage(page);
		query.setItemsPerPage(itemsPerPage);
		query.addFilter(new Filter(product.active, Filter.EQUAL, true));
		query.addFilter(new Filter(product.createdby, Filter.EQUAL, userId));

		DAO_Product[] daoProducts = em.retrieveRecords(query);

		for(DAO_Product prod : daoProducts){
			DAO_ProductAvailability availability = em.getOne(DAO_ProductAvailability.class,
					new Filter(product_availability.product_id, Filter.EQUAL, prod.getProductId()));
			prod.setProductAvailability(availability);

			Query query2 = new Query(em, DAO_Category.class);
			query2.addJoinPair(new JoinPair(em,
					DAO_Category.class, DAO_ProductCategory.class,
					new ColumnPair(product_category.category_id, category.category_id)));
			query2.addFilter(new Filter(product_category.product_id, Filter.EQUAL, prod.getProductId()));

			DAO_Category[] categoryList = em.retrieveRecords(query2);
			prod.setCategoryList(categoryList);
		}

		return daoProducts;
	}

```

 * EntityManager takes care of the inserting, updating and retrieving of records.
 * Most commonly used methods are:
     * em.getOne
     * em.getAll
     * em.insert
     * em.update
     * em.retrieveRecords(Query)
 * Query class is used to cleanly express a more complex arguments for retrieving records which would pollute EntityManager API when using an exhausetive list of comma seperated method arguments

##### Note

   * DAO_Product.class is a generated class based on table product
   * product.createdby -> is a generated field, of generated class product(meta classes) which contains field createdby = "product.createdby", so you won't be able to misspell tables and column names

#### How many classes are generated?

   * There are 4 generates sources for each table
      * DAO_Product.java   (com.company.dao)
      * Product.java       (com.company.model)
      * ProductMapper.java (com.company.mapper)
      * product.java       (com.company.meta)
   * Then there is ModelMetaData which contains the definition of each DAO classes
   * There is also Column.java (in com.company.meta) which list down all the column names(distinct) used in your database tables, very useful for autotyping in the IDE and also compile time error checking.
   * There is also Table.java (in com.company.meta) which list down the table names in your database.

Using as a service.
Exposing the Product to API/services, you need to map the DAO to the model

```java


    @Override
	public Product[] listProducts(String username, int page, int itemsPerPage) throws ServiceException {
		EntityManager em = null;
		try {
			em = EMF.get("");
			em.startPrintSqls();
			DAO_Product[] daoPproducts = getController().listUserProducts(em, username, page, itemsPerPage);
			return ProductMapper.map(daoPproducts);
		} catch (DatabaseException e1) {
			e1.printStackTrace();
			throw new ServiceException(e1.getMessage());
		}
		finally{
			EMF.release(em);
		}
	}
```
##### Note

  * ProductMapper converts DAO_Product to and fro Product.
  * DAO_Product is a direct mapping to the database, and is regenerated when database schema changes
  * Product is the models to used when exposing API/services in your application, this corresponds exactly to the DAO_Product at version 1.0 of your application.
  * When you release version 2.0, Pretty sure a lot of database table schema already has changed, while you still need to support version 1.0 while using the new updated database, you will only have to change the ProductMapper of version 1.0 to map the model representation of Product version 1.0 to convert to and for DAO_Product 2.0.


### Supported Platforms:

 * PostgreSQL - FULL Support
 * SQLite  - Through SQL.js

#### Additional Platform
via [orm-extras](https://github.com/ivanceras/orm-extras)
Most are just stubs and not really polished as I am not using these platforms in my current development set-up.
It is just easy to port the PostgreSQL implementation into any of these platform.
If the platform won't be able to support anything in PostgreSQL, make a workaround, else throw a NotSupportedException.


 * Oracle
 * MySQL
 * Hive
 * Cassandra
 * DynamoDB
 * BigTable
 * Mongo




### You may Ask

 * Well, you can do database operation in the browser using WebSQL+javascript.

     Yeah, but I don't want to rewrite my java server side controller into javascript.
I don't want to maintain different code base that has the same logic.

  * Why not use Hibernate?

     On the server side, hibernate works, but GWT can't do reflection so hibernate is useless there.

  * Why SQL.js

        WebSQL and indexedb could pretty much do as a platform, but the limiting factor is the asynchronous implementation in the browser. SQL.js does not do asynchronous queries, this is in parallel with how we write code on the server. Just compile the server to javascript(Using GWT in these case), you will have an exact client-side/offline implementation of your controller.


