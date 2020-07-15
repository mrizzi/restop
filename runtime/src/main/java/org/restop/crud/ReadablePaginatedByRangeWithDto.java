package org.restop.crud;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.restop.pagination.Filter;
import org.restop.pagination.FilterBuilder;
import org.restop.pagination.Links;
import org.restop.pagination.LinksBuilder;
import org.restop.pagination.Meta;
import org.restop.pagination.Pagination;
import org.restop.pagination.SortBuilder;

import javax.transaction.Transactional;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Map;

import static javax.transaction.Transactional.TxType.SUPPORTS;

public interface ReadablePaginatedByRangeWithDto<E extends PanacheEntity, DTO> extends WithDtoWebMethod<E, DTO>
{
    String QUERY_PARAM_LIMIT = "limit";
    String DEFAULT_VALUE_LIMIT = "25";
    String QUERY_PARAM_OFFSET = "offset";
    String DEFAULT_VALUE_OFFSET = "0";
    String QUERY_PARAM_SORT = "sort";
    String DEFAULT_VALUE_SORT = "id:Ascending";
    String QUERY_PARAM_WHERE = "where";
    String DEFAULT_VALUE_WHERE = "";

    @GET
    @Operation(summary = "Retrieve a paginated list of entities",
            operationId = "readPaginatedByRange")
    @Transactional(SUPPORTS)
    default Response readPaginatedByRange(@QueryParam(QUERY_PARAM_LIMIT) @DefaultValue(DEFAULT_VALUE_LIMIT) int limit,
                                          @QueryParam(QUERY_PARAM_OFFSET) @DefaultValue(DEFAULT_VALUE_OFFSET) int offset,
                                          @QueryParam(QUERY_PARAM_SORT) @DefaultValue(DEFAULT_VALUE_SORT) String sortBy,
                                          @QueryParam(QUERY_PARAM_WHERE) @DefaultValue(DEFAULT_VALUE_WHERE) String where,
                                          @Context UriInfo uriInfo) throws Exception
    {
        Sort sort = SortBuilder.build(sortBy);
        Filter filter = FilterBuilder.withUriInfo(uriInfo).andWhere(where).build();
        @SuppressWarnings("unchecked")
        PanacheQuery<E> query = (PanacheQuery<E>) getPanacheEntityType().getMethod("find", String.class, Sort.class, Map.class)
                .invoke(null, filter.getQuery(), sort, filter.getParameters());
        long count = query.count();
        Meta meta = Meta.withCount(count).andLimit(limit).andOffset(offset).andSort(sortBy);
        Links links = LinksBuilder.withBasePath(uriInfo).andLimit(limit).andOffset(offset).andCount(count).build();
        Class<DTO> dtoClass = getDtoType();
        if (dtoClass == null || dtoClass.equals(getPanacheEntityType())) {
            Pagination<E> pagination = new Pagination<>(meta, links, query.range(offset, offset + limit - 1).list());
            return Response.ok(pagination).build();
        } else {
            Pagination<DTO> pagination = new Pagination<>(meta, links, query.range(offset, offset + limit - 1).project(getDtoType()).list());
            return Response.ok(pagination).build();
        }
    }
}
