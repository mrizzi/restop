package org.restop.crud;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.transaction.Transactional;
import javax.ws.rs.POST;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import static javax.transaction.Transactional.TxType.REQUIRED;

public interface Creatable<E extends PanacheEntity> extends TypedWebMethod<E>
{
    @POST
    @Transactional(REQUIRED)
    default Response create(E resource) {
        if (resource.id != null) {
            // 422 Unprocessable Entity
            throw new WebApplicationException("Id was invalidly set on request.", 422);
        }

        resource.persist();
        return Response.ok(resource).status(Response.Status.CREATED).build();
    }
}
