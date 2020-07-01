package org.restop.crud;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import org.restop.mapper.Mapper;

public interface WithMapperWebMethod<E extends PanacheEntity, DTO> extends WithDtoWebMethod<E, DTO>
{
    Mapper<E, DTO> getMapper();
}
