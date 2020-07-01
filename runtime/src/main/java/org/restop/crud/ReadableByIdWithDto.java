package org.restop.crud;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;

import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import static javax.transaction.Transactional.TxType.SUPPORTS;

public interface ReadableByIdWithDto<E extends PanacheEntity, DTO> extends WithDtoWebMethod<E, DTO>
{
    @GET
    @Path("{id}")
    @Transactional(SUPPORTS)
    default Response readById(@PathParam("id") Long id) throws Exception
    {
        Class<DTO> dtoClass = getDtoType();
        if (dtoClass == null || dtoClass.equals(getPanacheEntityType())) {
            E entity = getPanacheEntityType().cast(getPanacheEntityType().getMethod("findById", Object.class).invoke(null, id));
            if (entity == null) {
                throw new NotFoundException("Entity with id of " + id + " does not exist.");
            }
            return Response.ok(entity).build();
        } else {
            Object[] idParam = new Object[1];
            idParam[0] = id;
            @SuppressWarnings("unchecked")
            PanacheQuery<E> query = (PanacheQuery<E>) getPanacheEntityType().getMethod("find", String.class, Object[].class).invoke(null, "id", idParam);
            DTO dto = query.project(dtoClass).firstResult();
            if (dto == null) {
                throw new NotFoundException("Entity with id of " + id + " does not exist.");
            }
            return Response.ok(dto).build();
        }
    }
}
