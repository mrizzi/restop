package org.restop.sample.resource;

import org.restop.crud.Creatable;
import org.restop.crud.Deletable;
import org.restop.crud.ReadableById;
import org.restop.crud.ReadablePaginatedByRange;
import org.restop.crud.UpdatableWithDto;
import org.restop.mapper.Mapper;
import org.restop.sample.pojo.Fruit;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("fruits")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class FruitResource implements Creatable<Fruit>, ReadableById<Fruit>, ReadablePaginatedByRange<Fruit>, UpdatableWithDto<Fruit, Fruit>, Deletable<Fruit> {

    @Override
    public Class<Fruit> getPanacheEntityType() {
        return Fruit.class;
    }
    
    @Override
    public Class<Fruit> getDtoType() {
        return Fruit.class;
    }

    @Override
    public Mapper<Fruit, Fruit> getMapper() {
        return new Mapper<Fruit, Fruit>() {
            @Override
            public Fruit map(Fruit source, Fruit target) {
                if (target == null) target = new Fruit();
                target.name = source.name;
                return target;
            }
        };
    }
}
