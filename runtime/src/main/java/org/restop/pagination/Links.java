package org.restop.pagination;

public class Links {
    public final String first;
    public final String prev;
    public final String next;
    public final String last;

    protected Links(String first, String prev, String next, String last) {
        this.first = first;
        this.prev = prev;
        this.next = next;
        this.last = last;
    }
}
