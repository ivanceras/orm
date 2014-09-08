# Ivanceras ORM


Yes, I know this is yet another ORM library like many others.

But this exists because all other ORM i came across does not fit all my requirements, one of which is to be able to do database operations in the browser using the same controller codes in your server side.




### Supported Platforms:

 * PostgreSQL - FULL Support
 * SQLite  - Under construction
 * Hive - Partial Support


### Features:

 * Code Generators.
    You can use the library to generate source code for you.
    
    You can use the library to generate all the necessary DAO,models and mappers which you will use in manipulating the data. 
    * DAO - (data access objects), resembles the table object in the database.
    * Model - models will be used in your API's exposed to some other clients.
    * Mappers - mappers is used to convert Models to DAO. This is very useful when you are creating a new version of your API, which the database has changed drastically that the mapping from Model to DAO requires non-trivial work.
    




### FAQ
 * Well, you can do database operation in the browser using WebSQL+javascript.

     Yeah, but I don't want to rewrite my java server side controller into javascript.
I don't want to maintain different code base that has the same logic.

  * Why not use Hibernate?

     On the server side, hibernate works really well, but GWT can't do reflection so hibernate is useless there.



[![Click here to lend your support to: Ivanceras ORM and make a donation at pledgie.com !](https://pledgie.com/campaigns/26665.png?skin_name=chrome)](https://pledgie.com/campaigns/26665)