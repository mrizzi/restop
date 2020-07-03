package org.restop.crud;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

public interface ReadablePaginatedByRange<E extends PanacheEntity> extends ReadablePaginatedByRangeWithDto<E, E> {}
