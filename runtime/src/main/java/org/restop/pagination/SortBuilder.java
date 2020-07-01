package org.restop.pagination;

import io.quarkus.panache.common.Sort;
import io.quarkus.panache.common.Sort.Direction;

import javax.validation.constraints.NotNull;

public class SortBuilder {

    public static Sort sortBy(@NotNull String sortBy) {
        final String[] sortingColumnsAndDirections = sortBy.split(",");
        Sort sort = Sort.by();
        for (String sortingColumnsAndDirection : sortingColumnsAndDirections) {
            String[] columnAndDirection = sortingColumnsAndDirection.split(":");
            String column = columnAndDirection[0].trim();
            String direction = columnAndDirection[1].trim();
            if (!column.isEmpty()) {
                sort = sort.and(column, SortBuilder.getDirection(direction));
            }
        }
        return sort;
    }
    
    private static Direction getDirection(String direction) {
/*        if ("a".equalsIgnoreCase(direction) ||
                "asc".equalsIgnoreCase(direction) ||
                "ascending".equalsIgnoreCase(direction)) return Direction.Ascending;
        else*/ if ("d".equalsIgnoreCase(direction) ||
                "desc".equalsIgnoreCase(direction) ||
                "descending".equalsIgnoreCase(direction)) return Direction.Descending;
        else return Direction.Ascending;
    }

}
