package org.restop.crud;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

public interface ReadablePaginatedByRange<E extends PanacheEntity> extends ReadablePaginatedByRangeWithDto<E, E> //TypedWebMethod<E>
{
/*    String QUERY_PARAM_LIMIT = "limit";
    String DEFAULT_VALUE_LIMIT = "25";
    String QUERY_PARAM_OFFSET = "offset";
    String DEFAULT_VALUE_OFFSET = "0";
    String QUERY_PARAM_SORT_BY = "sort_by";
    String DEFAULT_VALUE_SORT_BY = "id:Ascending";
    String QUERY_PARAM_WHERE = "where";
    String DEFAULT_VALUE_WHERE = "";

    @GET
    @Operation(summary = "Retrieve a paginated list of entities",
            operationId = "readPaginatedByRange")
    @Transactional(SUPPORTS)
    default Response readPaginatedByRange(@QueryParam(QUERY_PARAM_LIMIT) @DefaultValue(DEFAULT_VALUE_LIMIT) int limit,
                                          @QueryParam(QUERY_PARAM_OFFSET) @DefaultValue(DEFAULT_VALUE_OFFSET) int offset,
                                          @QueryParam(QUERY_PARAM_SORT_BY) @DefaultValue(DEFAULT_VALUE_SORT_BY) String sortBy,
                                          @QueryParam(QUERY_PARAM_WHERE) @DefaultValue(DEFAULT_VALUE_WHERE) String where,
                                          @Context UriInfo uriInfo) throws Exception
    {
        Sort sort = SortBuilder.sortBy(sortBy);
        Filter filter = FilterBuilder.withUriInfo(uriInfo).andWhere(where).build();
        @SuppressWarnings("unchecked")
        PanacheQuery<E> query = (PanacheQuery<E>) getPanacheEntityType().getMethod("find", String.class, Sort.class, Map.class)
                .invoke(null, filter.getQuery(), sort, filter.getParameters());
        long count = query.count();
        Meta meta = Meta.withCount(count).andLimit(limit).andOffset(offset).andSortBy(sortBy);
        Links links = LinksBuilder.withBasePath(uriInfo).andLimit(limit).andOffset(offset).andCount(count).build();
        Pagination<E> pagination = new Pagination<>(meta, links, query.range(offset, offset + limit - 1).list());
        return Response.ok(pagination).build();
    }*/
}
