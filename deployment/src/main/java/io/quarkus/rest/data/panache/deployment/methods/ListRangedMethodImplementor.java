package io.quarkus.rest.data.panache.deployment.methods;

import io.quarkus.gizmo.AnnotatedElement;
import io.quarkus.gizmo.AnnotationCreator;
import io.quarkus.gizmo.AssignableResultHandle;
import io.quarkus.gizmo.ClassCreator;
import io.quarkus.gizmo.MethodCreator;
import io.quarkus.gizmo.MethodDescriptor;
import io.quarkus.gizmo.ResultHandle;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import io.quarkus.rest.data.panache.deployment.RestDataResourceInfo;
import io.quarkus.rest.data.panache.deployment.properties.MethodPropertiesAccessor;
import org.jboss.jandex.IndexView;
import org.restop.pagination.Filter;
import org.restop.pagination.FilterBuilder;
import org.restop.pagination.Links;
import org.restop.pagination.LinksBuilder;
import org.restop.pagination.Meta;
import org.restop.pagination.Pagination;
import org.restop.pagination.SortBuilder;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Map;

import static io.quarkus.gizmo.MethodDescriptor.ofMethod;

public final class ListRangedMethodImplementor extends StandardMethodImplementor {

    public static final String NAME = "readPaginatedByRange";

    private static final String REL = "list";

    /**
     * Generated code looks more or less like this:
     *
     * <pre>
     *     @GET
     *     @Transactional
     *     Pagination readPaginatedByRange(@QueryParam(QUERY_PARAM_LIMIT) @DefaultValue(DEFAULT_VALUE_LIMIT) int limit,
     *                                           @QueryParam(QUERY_PARAM_OFFSET) @DefaultValue(DEFAULT_VALUE_OFFSET) int offset,
     *                                           @QueryParam(QUERY_PARAM_SORT) @DefaultValue(DEFAULT_VALUE_SORT) String sortBy,
     *                                           @QueryParam(QUERY_PARAM_WHERE) @DefaultValue(DEFAULT_VALUE_WHERE) String where,
     *                                           @Context UriInfo uriInfo) throws Exception
     *     {
     *         Sort sort = SortBuilder.build(sortBy);
     *         Filter filter = FilterBuilder.withUriInfo(uriInfo).andWhere(where).build();
     *         @SuppressWarnings("unchecked")
     *         PanacheQuery<E> query = (PanacheQuery<E>) getPanacheEntityType().getMethod("find", String.class, Sort.class, Map.class)
     *                 .invoke(null, filter.getQuery(), sort, filter.getParameters());
     *         long count = query.count();
     *         Meta meta = Meta.withCount(count).andLimit(limit).andOffset(offset).andSort(sortBy);
     *         Links links = LinksBuilder.withBasePath(uriInfo).andLimit(limit).andOffset(offset).andCount(count).build();
     *         return new Pagination<>(meta, links, query.range(offset, offset + limit - 1).list());
     *     }
     * </pre>
     */
    @Override
    protected void implementInternal(ClassCreator classCreator, IndexView index, MethodPropertiesAccessor propertiesAccessor,
            RestDataResourceInfo resourceInfo) {
        MethodMetadata methodMetadata = getMethodMetadata(resourceInfo);
        MethodCreator methodCreator = classCreator.getMethodCreator(methodMetadata.getName(), Pagination.class.getName(), 
                methodMetadata.getParameterTypes());
        addGetAnnotation(methodCreator);
        addQueryParamAnnotation(methodCreator.getParameterAnnotations(0), "limit");
        addDefaultValueAnnotation(methodCreator.getParameterAnnotations(0), "25");
        addQueryParamAnnotation(methodCreator.getParameterAnnotations(1), "offset");
        addDefaultValueAnnotation(methodCreator.getParameterAnnotations(1), "0");
        addQueryParamAnnotation(methodCreator.getParameterAnnotations(2), "sort");
        addDefaultValueAnnotation(methodCreator.getParameterAnnotations(2), "id:Ascending");
        addQueryParamAnnotation(methodCreator.getParameterAnnotations(3), "where");
        addDefaultValueAnnotation(methodCreator.getParameterAnnotations(3), "");
        methodCreator.getParameterAnnotations(4).addAnnotation(Context.class);
        addPathAnnotation(methodCreator, propertiesAccessor.getPath(resourceInfo.getClassInfo(), methodMetadata));
        addProducesAnnotation(methodCreator, APPLICATION_JSON);
        //addLinksAnnotation(methodCreator, resourceInfo.getEntityClassName(), REL);

        AssignableResultHandle sort = methodCreator.createVariable(Sort.class);
        methodCreator.assign(sort, methodCreator.invokeStaticMethod(ofMethod(SortBuilder.class, "build", Sort.class, String.class), methodCreator.getMethodParam(2)));

        AssignableResultHandle filter = createFilter(methodCreator);

        ResultHandle panacheQuery = methodCreator.invokeStaticMethod(
                ofMethod(resourceInfo.getEntityClassName(), "find", PanacheQuery.class, String.class, Sort.class, Map.class),
                methodCreator.invokeVirtualMethod(ofMethod(Filter.class, "getQuery", String.class), filter),
                sort,
                methodCreator.invokeVirtualMethod(ofMethod(Filter.class, "getParameters", Map.class), filter));

        AssignableResultHandle count = methodCreator.createVariable(long.class);
        methodCreator.assign(count, methodCreator.invokeInterfaceMethod(ofMethod(PanacheQuery.class, "count", long.class), panacheQuery));

        AssignableResultHandle meta = createMeta(methodCreator, count);

        AssignableResultHandle links = createLinks(methodCreator, count);

        AssignableResultHandle lastIndexInRange = createLastIndex(methodCreator, methodCreator.getMethodParam(0), methodCreator.getMethodParam(1));
        ResultHandle panacheQueryWithRange = methodCreator.invokeInterfaceMethod(ofMethod(PanacheQuery.class, "range", PanacheQuery.class, int.class, int.class),
                panacheQuery, methodCreator.getMethodParam(1), lastIndexInRange);
        ResultHandle pagination = methodCreator.newInstance(MethodDescriptor.ofConstructor(Pagination.class, Meta.class, Links.class, List.class),
                meta, links, 
                methodCreator.invokeInterfaceMethod(ofMethod(PanacheQuery.class, "list", List.class), panacheQueryWithRange));

        methodCreator.returnValue(pagination);
        methodCreator.close();
    }

    @Override
    protected MethodMetadata getMethodMetadata(RestDataResourceInfo resourceInfo) {
        return new MethodMetadata(NAME, int.class.getName(), int.class.getName(), String.class.getName(), String.class.getName(), UriInfo.class.getName());
    }

    void addQueryParamAnnotation(AnnotatedElement element, String value) {
        element.addAnnotation(QueryParam.class).addValue("value", value);
    }

    void addDefaultValueAnnotation(AnnotatedElement element, String value) {
        element.addAnnotation(DefaultValue.class).addValue("value", value);
    }

    AssignableResultHandle createFilter(MethodCreator methodCreator) {
        AssignableResultHandle filter = methodCreator.createVariable(Filter.class);
        ResultHandle filterBuilder = methodCreator.invokeStaticMethod(ofMethod(FilterBuilder.class, "withUriInfo", FilterBuilder.class, UriInfo.class), methodCreator.getMethodParam(4));
        filterBuilder = methodCreator.invokeVirtualMethod(ofMethod(FilterBuilder.class, "andWhere", FilterBuilder.class, String.class), filterBuilder, methodCreator.getMethodParam(3));
        methodCreator.assign(filter, methodCreator.invokeVirtualMethod(ofMethod(FilterBuilder.class, "build", Filter.class), filterBuilder));
        return filter;
    }

    AssignableResultHandle createMeta(MethodCreator methodCreator, AssignableResultHandle count) {
        AssignableResultHandle meta = methodCreator.createVariable(Meta.class);
        ResultHandle metaBuilder = methodCreator.invokeStaticMethod(ofMethod(Meta.class, "withCount", Meta.class, long.class), count);
        metaBuilder = methodCreator.invokeVirtualMethod(ofMethod(Meta.class, "andLimit", Meta.class, int.class), metaBuilder, methodCreator.getMethodParam(0));
        metaBuilder = methodCreator.invokeVirtualMethod(ofMethod(Meta.class, "andOffset", Meta.class, int.class), metaBuilder, methodCreator.getMethodParam(1));
        methodCreator.assign(meta, methodCreator.invokeVirtualMethod(ofMethod(Meta.class, "andSort", Meta.class, String.class), metaBuilder, methodCreator.getMethodParam(2)));
        return meta;
    }

    AssignableResultHandle createLinks(MethodCreator methodCreator, AssignableResultHandle count) {
        AssignableResultHandle links = methodCreator.createVariable(Links.class);
        ResultHandle linksBuilder = methodCreator.invokeStaticMethod(ofMethod(LinksBuilder.class, "withBasePath", LinksBuilder.class, UriInfo.class), methodCreator.getMethodParam(4));
        linksBuilder = methodCreator.invokeVirtualMethod(ofMethod(LinksBuilder.class, "andLimit", LinksBuilder.class, int.class), linksBuilder, methodCreator.getMethodParam(0));
        linksBuilder = methodCreator.invokeVirtualMethod(ofMethod(LinksBuilder.class, "andOffset", LinksBuilder.class, int.class), linksBuilder, methodCreator.getMethodParam(1));
        linksBuilder = methodCreator.invokeVirtualMethod(ofMethod(LinksBuilder.class, "andCount", LinksBuilder.class, long.class), linksBuilder, count);
        methodCreator.assign(links, methodCreator.invokeVirtualMethod(ofMethod(LinksBuilder.class, "build", Links.class), linksBuilder));
        return links;
    }

    AssignableResultHandle createLastIndex(MethodCreator methodCreator, ResultHandle limit, ResultHandle offset) {
        AssignableResultHandle lastIndex = methodCreator.createVariable(int.class);
        ResultHandle sum = methodCreator.invokeStaticMethod(ofMethod(Math.class, "addExact", int.class, int.class, int.class), limit, offset);
        methodCreator.assign(lastIndex, methodCreator.invokeStaticMethod(ofMethod(Math.class, "subtractExact", int.class, int.class, int.class), sum, methodCreator.load(1)));
        return lastIndex;
    }
}
