package org.restop.crud;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

public interface WithDtoWebMethod<E extends PanacheEntity, DTO> extends TypedWebMethod<E>
{
    default Class<DTO> getDtoType() {
        return null;
    }
}
