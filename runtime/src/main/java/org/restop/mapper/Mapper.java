package org.restop.mapper;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

public interface Mapper<ENTITY extends PanacheEntity, DTO> {
    ENTITY map(DTO source, ENTITY target) throws Exception;
}
