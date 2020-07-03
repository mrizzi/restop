package org.restop.crud;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

public interface ReadableById<E extends PanacheEntity> extends ReadableByIdWithDto<E, E> {}
