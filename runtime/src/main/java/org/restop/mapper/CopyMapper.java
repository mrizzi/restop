package org.restop.mapper;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import org.apache.commons.beanutils.BeanUtils;

import java.util.Objects;

public class CopyMapper<E extends PanacheEntity, D> implements Mapper <E,D> {
    @Override
    public E map(D entityDto, E entity) throws Exception {
        Objects.requireNonNull(entity, "Target entity can not be null");
        Objects.requireNonNull(entityDto, "DTO bean can not be null");
        BeanUtils.copyProperties(entity, entityDto);
        return entity;
    }
}
