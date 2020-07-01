package org.restop.pagination;

public class Meta {
    public long count;
    public int limit;
    public int offset;
    public String sortBy;

    private Meta() {}

    public static Meta withCount(long count) {
        Meta meta = new Meta();
        meta.count = count;
        return meta;
    }

    public Meta andLimit(int limit) {
        this.limit = limit;
        return this;
    }

    public Meta andOffset(int offset) {
        this.offset = offset;
        return this;
    }

    public Meta andSortBy(String sortBy) {
        this.sortBy = sortBy;
        return this;
    }
}
