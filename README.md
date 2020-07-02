# restop

## Introduction

Restop is an REST OPinionated Quarkus extension.  
It's meant to avoid keep rewriting over and over (almost) the same code to create resource REST endpoints when wrinting Quarkus applications leveraging Hibernate with Panache.  

The opinions that drives the opinionated approach are:

* a "list all" endpoint should be always paginated to provide a stable and predictable impact of each endpoint
* a "list all" endpoint should let the user to be able to filter data 
* every endpoint should allow the usage of DTOs to not expose "internal" entities
* it relies on "active record pattern" for Hibernate with Panache Quarkus extension
* multiple inheritance of behavior leveraging Java interfaces default methods

These are opinions (and as such are debatable) so this is not supposed to be the solution for everything but a solution that works on quite a lot of use cases (more below).

## Usage

### Add Dependency

[JitPack](https://jitpack.io) can be used to add restop dependency:

1. add the JitPack repository

    ```xml
    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>
    ```

1. add the dependency
    ```xml
        <dependency>
            <groupId>com.github.mrizzi</groupId>
            <artifactId>restop</artifactId>
            <version>master-SNAPSHOT</version>
        </dependency>
    ```

### Read endpoints

Let's start with the `Fruit` entity referenced in many Quarkus guides.  

It **must be a `PanacheEntity`** like:

```java
@Entity
public class Fruit extends PanacheEntity {

    @Column(length = 40, unique = true)
    public String name;
    public String description;

    public Fruit() {
    }

    public Fruit(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
```

Now let's move to leverage Restop to create the "read" endpoints:

```java
@Path("fruit")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FruitResource implements ReadableById<Fruit>, ReadablePaginatedByRange<Fruit> {
    @Override
    public Class<Fruit> getType() {return Fruit.class;}
}
```

done!  
The `/fruit` endpoint will be able to provide responses to calls:

* *read one*: `GET` request to `/fruit/{id}` endpoint with a single `Fruit` result
* *read many "first page"*: `GET` request to `/fruit` endpoint with an ordered (by ID) list of `Fruit` results using default (and opinionated) values for `limit` (i.e. `25`) and `offset` (i.e. `0`) parameters
* *read many "n-th page"*:`GET` request to `/fruit?limit=10&offset=20` endpoint with an ordered (by ID) list of (up to 10) `Fruit` results starting for the 20th element 
* *read many with sorting*:`GET` request to `/fruit?sort_by=name:Ascending` endpoint with an ordered by Fruit's `name` field ascending list of (up to 25) `Fruit` results starting for first element 
* *read many with "equals" filter*:`GET` request to `/fruit?name=Banana` endpoint with an ordered (by ID) list of (up to 25) `Fruit` results whose `name` field value is `Banana`
* *read many with "in" filter*:`GET` request to `/fruit?name=Banana&name=Apple&name=Kiwi` endpoint with an ordered (by ID) list of (up to 25) `Fruit` results whose `name` field value is `Banana` or `Apple` or `Kiwi`

Obviously the "query" parameters for the "read many" operations work together so they can be combined in the request.

Here is an example of a "paginated" response (for the `GET` request to `/fruit?sort_by=name:Ascending` endpoint):
```json
{
    "data": [
        {
            "id": 2,
            "description": "Winter fruit",
            "name": "Apple"
        },
        {
            "id": 3,
            "description": "Tropical fruit",
            "name": "Banana"
        },
        {
            "id": 1,
            "description": "Sweet fruit available on mid-spring.",
            "name": "Cherry"
        }
    ],
    "links": {
        "first": "/fruits?limit=25&offset=0&sort_by=name:Ascending",
        "last": "/fruits?limit=25&offset=0&sort_by=name:Ascending"
    },
    "meta": {
        "count": 3,
        "limit": 25,
        "offset": 0,
        "sortBy": "name:Ascending"
    }
}
```
where:

* `data` contains the response data array
* `links` set of links to easily move to other set of results consistently with the request's `limit` and `offset`
  * `first` link to the first page
  * `prev` link to the previous page (if available)
  * `next` link to the next page (if available)
  * `last` link to the last page
* `meta` contains metadata about the resource
  * `count` is the total number of entities corresponding to the filters
  * `limit` is the limit applied to the data retrieved (useful in case of default values)
  * `offset` is the offset applied to the data retrieved (useful in case of default values)
  * `sort` is the sorting applied to the data retrieved (useful in case of default values)

To recap what has been done so far:

1. created `Fruit extends PanacheEntity` entity (something that should have been done anyway)
1. created `FruitResource implements ReadableById<Fruit>, ReadablePaginatedByRange<Fruit>` providing the `getType()` method implementation (requested from Restop)
1. got for free all the "read" endpoints about with pagination, sorting, filtering and links

### Create endpoint

How to add the endpoint to create a `Fruit` entity?  
Following the "multiple inheritance of behavior" approach, it's a matter of adding to `FruitResource` class that it implements the `Creatable<E extends PanacheEntity>` interface.  
The class will look like:

```java
@Path("fruit")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FruitResource implements ReadableById<Fruit>, ReadablePaginatedByRange<Fruit>,
                                      Creatable<Fruit> {
    @Override
    public Class<Fruit> getType() {return Fruit.class;}
}
```

Now a `POST` request to `/fruit` endpoint will create a new `Fruit` resource.  
 
### Delete endpoint

For adding the deletion feature to an endpoint the approach will be the same as above.  
Change the `FruitResource` class to implement the `Deletable<E extends PanacheEntity>` interface.  

With this further change, the `FruitResource` class will be:

```java
@Path("fruit")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FruitResource implements ReadableById<Fruit>, ReadablePaginatedByRange<Fruit>,
                                      Creatable<Fruit>, Deletable<Fruit> {
    @Override
    public Class<Fruit> getType() {return Fruit.class;}
}
```
Now a request `DELETE` request to the `/fruit/{id}` endpoint will delete the `Fruit` with provided `id`.

### Update endpoint

No interface available for adding the update feature yet.  
Let me clarify the update use case.  
The "sample" implementation of the update endpoint is something like:

```java
@PUT
@Path("{id}")
@Transactional
public Fruit update(@PathParam Long id, Fruit fruit) {
    if (fruit.name == null) {
        throw new WebApplicationException("Fruit Name was not set on request.", 422);
    }

    Fruit entity = Fruit.findById(id);

    if (entity == null) {
        throw new WebApplicationException("Fruit with id of " + id + " does not exist.", 404);
    }

    entity.name = fruit.name;

    return entity;
}
```

as you can see, in this case there a need for a "knowledge" about the bean to move the information from the `fruit` input bean to the `entity` bean to get persisted into the database.  
The interfaces introduces so far are not taking into account any kind of knowledge about the bean and to keep this approach, there's no `Updatable` interface.  

But, no worries, this takes us to the next set of features: DTO.

## Usage with DTO

The last paragraph about being able to reflect changes from an input bean into the persisted bean is close to the DTO (data transfer object) approach.  
More generally speaking, when working with REST endpoints is common to have the need to use DTO for the endpoints avoiding to use the entities bean in the REST APIs.

restop provides a way to create quickly and easily REST endpoints with DTO.  
The main change to be introduced to use DTO is the need to implement the `getMapper()` method from the `WithDtoWebMethod` interface.
The aim of this method is to provide the implementation of a mapper that takes case of "translating" values from DTO to Entity beans.
  
DTO, as everything in restop, are opinionated as well and they must (right now, maybe this will change in the future) accomplish one requirement:  
**DTO's fields must be a subset of Entity's fields**.

Let's see how it works starting from where we left,  the update method.

###  Update with DTO endpoint

To add the endpoint for updating entities the `FruitResource` class must implement also the `UpdatableWithDto<E extends PanacheEntity, D>` interface.
In this example, the declaration will use `UpdatableWithDto<Fruit, Fruit>` since our DTO corresponds with the entity: this is an edge case obviously but for the sake of the example it makes sense.  

So `FruitResource` becomes:

```java
@Path("fruit")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FruitResource implements ReadableById<Fruit>, ReadablePaginatedByRange<Fruit>,
                                      Creatable<Fruit>, Deletable<Fruit>, UpdatableWithDto<Fruit, Fruit>{
    @Override
    public Class<Fruit> getType() {return Fruit.class;}

    @Override
    public Mapper<Fruit, Fruit> getMapper() {
        return new Mapper<Fruit, Fruit>() {
            @Override
            public Fruit map(Fruit source, Fruit target) {
                if (target == null) target = new Fruit();
                target.name = source.name;
                target.description = source.description;
                return target;
            }
        };
    }}
```

So, besides adding the interface, also the implementation for the `getMapper()` method has been added.  
The implementation of the method is basic but it does what we expect: it copies values from DTO (a.k.a. `source`) into entity (a.k.a. `target`).  

Now a `PUT` request to the `/fruit/{id}` endpoint will update the `Fruit` with provided `id` using the values in the DTO.  

It's also clear this is an edge case of having DTO because the entity and the DTO are the same (compliant with the "subset" requirement): in the next paragraph we will see how to use a "traditional" (and obviously opinionated) DTO.

### Reads with DTO endpoint

When dealing with read operations, it happens that some entity bean's fields are not meant to be sent out to the client.  This can happen for different reasons: some fields are just internal fields (e.g. audit fields) or maybe you want to create a response with just the field shown in the UI in order to maximize the perfomances reading from the DB only the needed fields and so keeping the response payload as small as possible.  
 
Going back to our example, let's say (and really just for the sake of the explanation) we just want to send out the `name` of the `Fruit` entities and not their `id` and `description`.
The DTO will look like:  

```java
@RegisterForReflection
public class FruitDto {

    public String name;

    public FruitDto(String name) {
        this.name = name;
    }
}
```

The annotation `@RegisterForReflection` is mandatory to register manually the projection class for reflection, if you plan to deploy your application as a native executable (ref. [Simplified Hibernate ORM with Panache](https://quarkus.io/guides/hibernate-orm-panache#query-projection))

For the example to use the "DTO-ed" operations, we can create a new resource class `FruitWithDtoResource` like this:

```java
@Path("fruits-dto")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FruitWithDtoResource implements ReadableByIdWithDto<Fruit, FruitDto>, ReadablePaginatedByRangeWithDto<Fruit, FruitDto> {

    @Override
    public Class<Fruit> getPanacheEntityType() {return Fruit.class;}

    @Override
    public Class<FruitDto> getDtoType() {return FruitDto.class;}
}
```

so, comparing quickly with the above `FruitResource` class:

* `ReadableById<Fruit>` has been replaced by `ReadableByIdWithDto<Fruit, FruitDto>`
* `ReadablePaginatedByRange<Fruit>` has been replaced by `ReadablePaginatedByRangeWithDto<Fruit, FruitDto>`
* `getDtoType()` method has been added and implemented

With just this code, we have all the same read endpoint described in the above [Reads endpoint](#read-endpoints) paragraph.  

### Create with DTO endpoint

As above, we can add a method to create an entity using a DTO implementing the `CreatableWithDto<E extends PanacheEntity, D>` interface and hence providing the requested implementation of the `getMapper()` method.

The `FruitWithDtoResource` will become:

```java
@Path("fruits-dto")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FruitWithDtoResource implements ReadableByIdWithDto<Fruit, FruitDto>, ReadablePaginatedByRangeWithDto<Fruit, FruitDto>,
        CreatableWithDto<Fruit, FruitDto> {

    @Override
    public Class<Fruit> getPanacheEntityType() {return Fruit.class;}

    @Override
    public Class<FruitDto> getDtoType() {return FruitDto.class;}

    @Override
    public Mapper<Fruit, FruitDto> getMapper() {
        return new Mapper<Fruit, FruitDto>() {
            @Override
            public Fruit map(FruitDto source, Fruit target) {
                if (target == null) target = new Fruit();
                target.name = source.name;
                return target;
            }
        };
    }
}
```

`FruitDto` needs a change as well to annotate the only available constructor (with one input parameter) with `@JsonbCreator` annotation since otherwise during deserialization the no-arg constructor is searched and since it's not available, the create endpoints will fail.  
If you're wondering why not just adding the no-arg constructor, the reason is that to use the same bean in the read operations as a projection, the bean must have just one single constructor will all the fields to have Hibernate to create the right select statement for the "projected" query (ref. [Simplified Hibernate ORM with Panache](https://quarkus.io/guides/hibernate-orm-panache#query-projection)).  
So `FruitDto` is:

```java
@RegisterForReflection
public class FruitDto {

    public String name;

    @JsonbCreator
    public FruitDto(String name) {
        this.name = name;
    }
}
```

Let's say that in this case using the `FruitDto` doesn't make a lot of sense because it will add `Fruits` without description but this example is provided just to show how to use DTO and then it's left to the user when it makes sense to use `Creatable` or `CreatableWithDto` interfaces.

### Delete with DTO endpoint

There's nothing about this since there's no DTO involved in deleting an entity: make a `DELETE` request to the `/fruit/{id}` endpoint will delete the `Fruit` with provided `id`.
So use `Deletable<E extends PanacheEntity>` interface and let the `FruitWithDtoResource` become:

```java
@Path("fruits-dto")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FruitWithDtoResource implements ReadableByIdWithDto<Fruit, FruitDto>, ReadablePaginatedByRangeWithDto<Fruit, FruitDto>,
        CreatableWithDto<Fruit, FruitDto>, Deletable<Fruit> {

    @Override
    public Class<Fruit> getPanacheEntityType() {return Fruit.class;}

    @Override
    public Class<FruitDto> getDtoType() {return FruitDto.class;}

    @Override
    public Mapper<Fruit, FruitDto> getMapper() {
        return new Mapper<Fruit, FruitDto>() {
            @Override
            public Fruit map(FruitDto source, Fruit target) {
                if (target == null) target = new Fruit();
                target.name = source.name;
                return target;
            }
        };
    }
}
```
