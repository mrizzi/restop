package org.restop.crud;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import static javax.transaction.Transactional.TxType.REQUIRED;

public interface UpdatableWithDto<E extends PanacheEntity, DTO> extends WithMapperWebMethod<E, DTO>
{
    @PUT
    @Path("{id}")
    @Transactional(REQUIRED)
    default Response update(@PathParam Long id, DTO resourceDto) throws Exception {
        if (PanacheEntity.class.isAssignableFrom(resourceDto.getClass()) &&
                ((PanacheEntity)resourceDto).id != null) {
            // 422 Unprocessable Entity
            throw new WebApplicationException("Id was invalidly set on request bean. Please do not set the Id since the only Id provided must be the one provided as PathParam.", 422);
        }
        E entity = getPanacheEntityType().cast(getPanacheEntityType().getMethod("findById", Object.class).invoke(null, id));
        if (entity == null) {
            throw new NotFoundException("Resource with id of " + id + " does not exist.");
        }
        getMapper().map(resourceDto, entity);
        return Response.ok(entity).build();
    }

    default Class<DTO> getUpdatableDtoType() {
        return getDtoType();
    }
}
