package org.restop.pagination;

import org.restop.crud.ReadablePaginatedByRange;

import javax.ws.rs.core.UriInfo;
import java.util.HashMap;
import java.util.Map;

public class FilterBuilder {

    private final UriInfo uriInfo;
    private String where;

    FilterBuilder(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    public static FilterBuilder withUriInfo(UriInfo uriInfo) {
        return new FilterBuilder(uriInfo);
    }

    public FilterBuilder andWhere(String where) {
        this.where = where;
        return this;
    }

    public Filter build() {
        final StringBuilder queryBuilder = new StringBuilder();
        final Map<String, Object> parameters = new HashMap<>();
        if (!where.isEmpty()) {
            String[] whereConditions = where.split(",");
            for (String whereCondition : whereConditions) {
                String[] nameAndValue = whereCondition.split(":");
                String name = nameAndValue[0].trim();
                String value = nameAndValue[1].trim();
                if (!name.isEmpty()) {
                    if (queryBuilder.length() != 0) queryBuilder.append(" and ");
                    queryBuilder.append(name);
                    queryBuilder.append(" = :");
                    queryBuilder.append(name);
                    parameters.put(name, value);
                }
            }
        } else {
            uriInfo.getQueryParameters(true).forEach((key, values) -> {
                if (!(ReadablePaginatedByRange.QUERY_PARAM_LIMIT.equals(key) || 
                        "offset".equals(key) || "sort_by".equals(key) || "where".equals(key))) {
                    if (queryBuilder.length() != 0) queryBuilder.append(" and ");
                    queryBuilder.append(key);
                    queryBuilder.append(" in :");
                    queryBuilder.append(key);
                    parameters.put(key, values);
                }
            });
        }
        return Filter.withQuery(queryBuilder.toString()).andParameters(parameters);
    }
}
