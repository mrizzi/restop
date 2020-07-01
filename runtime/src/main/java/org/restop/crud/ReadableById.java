package org.restop.crud;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

public interface ReadableById<E extends PanacheEntity> extends ReadableByIdWithDto<E, E> //TypedWebMethod<E>
{
/*    @GET
    @Path("{id}")
    @Transactional(SUPPORTS)
    default Response readById(@PathParam("id") Long id) throws Exception
    {
        E entity = getPanacheEntityType().cast(getPanacheEntityType().getMethod("findById", Object.class).invoke(null, id));
        if (entity == null) {
            throw new NotFoundException("Entity with id of " + id + " does not exist.");
        }
        return Response.ok(entity).build();
    }*/
}
