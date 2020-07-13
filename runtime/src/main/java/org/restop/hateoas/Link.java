package org.restop.hateoas;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import java.net.URI;

public class Link extends PanacheEntity {
    public URI href;
    public boolean templated = false;
}
