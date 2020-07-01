package org.restop.crud.deployment.rest;

import org.restop.crud.Creatable;
import org.restop.crud.Deletable;
import org.restop.crud.ReadableById;
import org.restop.crud.ReadablePaginatedByRange;
import org.restop.crud.UpdatableWithDto;
import org.restop.crud.deployment.pojo.Fruit;
import org.restop.mapper.Mapper;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("fruits")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FruitResource implements Creatable<Fruit>, ReadableById<Fruit>, ReadablePaginatedByRange<Fruit>, Deletable<Fruit>, UpdatableWithDto<Fruit, Fruit> {
    @Override
    public Class<Fruit> getPanacheEntityType() {return Fruit.class;}

    @Override
    public Class<Fruit> getDtoType() {return Fruit.class;}

    @Override
    public Mapper<Fruit, Fruit> getMapper() {
        return new Mapper<Fruit, Fruit>() {
            @Override
            public Fruit map(Fruit source, Fruit target) {
                if (target == null) target = new Fruit();
                target.setName(source.name);
                target.setDescription(source.description);
                return target;
            }
        };
    }
}
