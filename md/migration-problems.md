#Introduction

You've implemented a new feature, which involved refactoring some existing data formats or server handlers. Everything looks good, the tests all pass, and (if applicable) the latest clients (frontend and/or backend) all seem to work with the latest servers. Ship it?!

Not so fast.

If your changes involve formatting changes to data at rest, and/or interface changes to servers with clients running in other processes, you also have to worry about (and test) how your changes will interact with old data or code.




##The problem is real and we can do something about it

No matter how much thought and time spent on design decisions, later you will have to change something in the sytem, be it database schema changes and or new features. We can not avoid change.


Supposed we created a very simple user api which just deals with the user management.

Say, in our `/v1` we have a very simple model of User object.

In version 1:

`/v1`

```java
User {
    int id;
	String username;
	String password;
	String name;
	String address;
	String company;
}

```
Assuming you haven't thought about it beforehand.
Later on, you realize that you need to email, firstname, lastname, middlename, prefix and add Company and Address of the user.
Aside from that you feel that you don't need the 'name' already, so you drop it.
Also, you renamed id to userId and use UUID as its data type to avoid collision of id's when having multiple database.

In version 2:

`/v2`

```java
User {
	UUID userId;
	String username;
	String password;
	String firstname;
	String lastname;
	String midlename;
	String prefix;
	String suffix;
	String email;
	Company company;
	Address address;
}

Company{
	UUID companyId;
	String name;
	String tagLine;
	Address address;
}

Address{
	UUID addressId;
	String street;
	Integer postalCode;
	String city;
	String region;
	String country;
}
```


In version 1:

`GET /v1/users/jsmtith`

```javascript
{
    id : 1000,
	username:"jsmith",
	name:"John D. Smith",
	address:"2301 M Street Northwest Washington, DC 20037",
	company : "Acme Corporation"
}
```

In verions 2:

`GET /v2/users/jsmith`
```javascript
{
	userId:"00000000-0000-0000-0000-0000000003e8",
	username:"jsmith",
	firstname:"John",
	lastname: "Smith",
	middlename: "Doe",
	prefix:"Mr.",
	suffix:"II",
	address:{
		street:"2301 M Street Northwest",
		city:"Washington, DC",
		postalCode:20037
	}
	company: {
	 	name: "Acme",
	 	tagline:"Powering fictitious Companies since 1922"
	}
}
```

Now, when you go live with `/v2`, think what would happen to the clients that is using `/v1`.


### Definitely not YAGNI - You ain't gonna need it

Unless we DON'T need to support previous versions of the API, right after we deploy an updated version, we ARE gonna need it.


### Let the client Update right away

> "Let the client update right away, just right after we deploy `/v2`"

**NO.**  We can not force the consumer of the API to update right away, case scenario is that the maintainer of the client that consumes your API is working on something different as of the moment, and he needs his client to be keep working without downtime.

### Well, we won't be touching `/v1`
> "Just leave the war file as it was deployed."

**NO.**  The old war file won't be able to do query on the new database.

> "Just leave the old database and deploy a new one for `/v2`"

**NO.**  This double the requirement of our server resources, besides, how are we gonna sync `/v1` and `/v2` databases?
If we don't sync the database, does that mean, that the new users created using the client that uses `/v1` will not be accessible by `/v2` API.


The problem is real, and if we don't deal with this now. It would be a huge technical debt later.

### How are we gonna solve it?

Using Mappers to map `DAO`'s to `Model`

`/v1`

```java
UserMapper{

	User map(DAO_User daoUser){
		User user = new User();
		user.setId(daoUser.getId());
		user.setUsername(daoUser.getUsername());
		user.setPassword(daoUser.getPassword());
		user.setName(daoUser.getName());
		user.setAddress(daoUser.getAddress());
		user.setCompany(daoUser.getCompany());
		return user;
	}

	DAO_User map(User user){
		DAO_User daoUser = new DAO_User();
		daoUser.setId(user.getId());
		daoUser.setUsername(user.getUsername());
		daoUser.setPassword(user.getPassword());
		daoUser.setName(user.getName());
		daoUser.setAddress(user.getAddress());
		daoUser.setCompany(user.getCompany());
		return daoUser;
	}

}

```



`/v2`

```java

UserMapper{

	User map(DAO_User daoUser){
		User user = new User();
		user.setUserId(daoUser.getUserId());
		user.setUsername(daoUser.getUsername());
		user.setPassword(daoUser.getPassword());
		user.setFirstname(daoUser.getFirstname());
		user.setLastname(daoUser.getLastname());
		user.setMiddlename(daoUser.getMiddlename());
		user.setPrefix(daoUser.getPrefix());
		user.setSuffix(daoUser.getSuffix());
		user.setEmail(daoUser.getEmail());
		user.setCompany(CompanyMapper.map(daoUser.getCompany));
		user.setAddress(AddressMapper.map(daoUser.getAddress());
	}

	DAO_User map(User user){
		DAO_User daoUser = new DAO_User();
		daoUser.setUserId(user.getUserId());
		daoUser.setUsername(user.getUsername());
		daoUser.setPassword(user.getPassword());
		daoUser.setFirstname(user.getFirstname());
		daoUser.setLastname(user.getLastname());
		daoUser.setMiddlename(user.getMiddlename());
		daoUser.setPrefix(user.getPrefix());
		daoUser.setSuffix(user.getSuffix());
		daoUser.setEmail(user.getEmail());
		daoUser.setCompany(CompanyMapper.map(user.getCompany));
		daoUser.setAddress(AddressMapper.map(user.getAddress());
	}

}
```

###Mappers what?

`DAO` - Data Access Object, is a class which has a direct mapping of to your database tables. This is what you manipulate in your controller.

`Model` - Models are objects that are exposed to your API, this is what you manipulate in the Services/APIs

`Mappers` - Transform DAO to Model and vice versa. You use this in both the controllers and the Services/APIs


### Meh, What's so special about it, I can do it with reflection

> "I only see a passing of fields from one object to another which I could whip out my favorite library and do it's thing."

**True.** It is indeed a plain passing of values to and fro DAO and Model, and in most cases, you don't have to change anything there, not just yet.


Now, when you are ready to deploy `/v2` to production, look for the UserMapper in `/v1` and try to come up with a best possible `/v1` User Model representation by transforming `/v2` DAO_User.


`/v1.1`

```java
UserMapper{

	User map(DAO_User daoUser){
		User user = new User();
		UUID userId = daoUser.getUserId();
		long leastSigBit = userId.getLeastSignificantBits()
		user.setId(leastSigBit);

		user.setUsername(daoUser.getUsername());
		user.setPassword(daoUser.getPassword());
		String name = daoUser.getPrefix()+" "+daoUser.getFirstname()+" "+daoUser.getMiddlename()+" "+
					  daoUser.getLastname()+" "+daoUser.getSuffix();
		user.setName(name);
		String companyName = daoUser.getCompany().getName();
		user.setCompany(companyName);
		DAO_Address daoAddress = daoUser.getAddress();
		String addressString = daoAddress.getStreet()+" "+daoAddress.getRegion()+" "+daoAddress.getCity()+" "+daoAddress.getCountry()++daoAddress.getPostalCode();
		user.setAddress(addressString);
	}

	DAO_User map(User user){
		DAO_User daoUser = new DAO_User();
		daoUser.setUsername(user.getUsername());
		daoUser.setPassword(user.getPassword());

		String name = user.getName();

		String[] splinters = name.split(" ");
		String prefix = tryMatch(["Mr.","Mrs.", "Engr"], splinters[0]);//[*]
		int offset = prefix != null ? 1 : 0
		String firstName = splinters[offset];
		String middleName = tryMatch(".",splinter[++offset]);
		String lastName = splinters[++offset]
		String suffix = tryMatch(["Jr.", "Sr.", "I", "II"], splinters[++offset])

		daoUser.setFirstname(firstName);
		daoUser.setLastname(lastName);
		daoUser.setMiddlename(middleName);
		daoUser.setPrefix(prefix);
		daoUser.setSuffix(suffix);
		daoUser.setEmail("placeholder@company.com");

		DAO_Company daoCompany = new DAO_Company();
		daoCompany.setName(user.getCompany());
		daoUser.setCompany(daoCompany);

		DAO_Address daoAddress = new DAO_Addres();
		String addrString = user.getAdress();
		//..regex algorithm to split and match address parts...
		daoUser.setAddress(daoAddress);
	}

}
```


In version 1.1 which still maps to `/v1`

Notice, We modified the UserMapper in `/v1` to try to come with with the best representation of `/v1` User model using DAO_User of `/v2`

*Other code are ommitted.*.

### I'm lost, where/when do you change?

You need to do the changes on the `/v1` mappers when you are about to deploy `/v2`, while you still need to support `/v1` for a period of time.


Code requirement in order for your deployment to succeed without breaking old code and old data.

 * `DAO`
 * `Model`
 * `Mapper`

For User Object. You will need to have:
 * `DAO_User.java`
 * `User.java`
 * `UserMapper.java`

There are 3 class files required for each table on your database. So if you have 30 tables, you will have 90 classes for the `DAOs`, `Models` and `Mappers` alone.

> "That's a lot of code to write and maintain."


### DRY - Don't repeat yourself.


Well, it turns out, that the three classes share a common pattern, so we can generate those 3 files based on the database schema.


##Taming Complexity

While I agree, that we will not be able to build a perfect system, I believe that if we can identify the problem, we can minize better yet eliminate it.



