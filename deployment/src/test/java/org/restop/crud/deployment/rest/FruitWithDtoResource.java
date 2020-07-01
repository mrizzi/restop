package org.restop.crud.deployment.rest;

import org.restop.crud.CreatableWithDto;
import org.restop.crud.Deletable;
import org.restop.crud.ReadableByIdWithDto;
import org.restop.crud.ReadablePaginatedByRangeWithDto;
import org.restop.crud.UpdatableWithDto;
import org.restop.crud.deployment.pojo.Fruit;
import org.restop.crud.deployment.pojo.FruitDto;
import org.restop.mapper.Mapper;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("fruits-dto")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FruitWithDtoResource implements ReadableByIdWithDto<Fruit, FruitDto>, ReadablePaginatedByRangeWithDto<Fruit, FruitDto>,
        CreatableWithDto<Fruit, FruitDto>, Deletable<Fruit>, UpdatableWithDto<Fruit, FruitDto> {

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
                target.setName(source.name);
//                target.name = source.name;
                return target;
            }
        };
    }
}
