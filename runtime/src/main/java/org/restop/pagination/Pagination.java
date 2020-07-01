package org.restop.pagination;

import java.util.List;

public class Pagination<E> {

    public Meta meta;
    public Links links;
    public List<E> data;

    public Pagination(Meta meta, Links links, List<E> data) {
        this.meta = meta;
        this.links = links;
        this.data = data;
    }
}
