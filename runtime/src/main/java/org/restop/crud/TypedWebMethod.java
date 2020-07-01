package org.restop.crud;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

public interface TypedWebMethod<E extends PanacheEntity>
{
    Class<E> getPanacheEntityType();
}
