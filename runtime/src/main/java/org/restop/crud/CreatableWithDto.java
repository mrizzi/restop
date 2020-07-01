package org.restop.crud;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.transaction.Transactional;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import static javax.transaction.Transactional.TxType.REQUIRED;

public interface CreatableWithDto<E extends PanacheEntity, D> extends WithMapperWebMethod<E, D>
{
    @POST
    @Transactional(REQUIRED)
    default Response create(D resourceDto) throws Exception {
        E resource = getMapper().map(resourceDto, null);
        resource.persist();
        return Response.ok(resource).status(Response.Status.CREATED).build();
    }
}
