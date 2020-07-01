package org.restop.crud;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.transaction.Transactional;
import javax.ws.rs.DELETE;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import static javax.transaction.Transactional.TxType.REQUIRED;

public interface Deletable<E extends PanacheEntity> extends TypedWebMethod<E>
{
    @DELETE
    @Path("{id}")
    @Transactional(REQUIRED)
    default Response delete(@PathParam("id") Long id) throws Exception {
        E entity = getPanacheEntityType().cast(getPanacheEntityType().getMethod("findById", Object.class).invoke(null, id));
        if (entity == null) {
            throw new NotFoundException(getPanacheEntityType().getCanonicalName() + " with id " + id + " does not exist.");
        }
        entity.delete();
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
