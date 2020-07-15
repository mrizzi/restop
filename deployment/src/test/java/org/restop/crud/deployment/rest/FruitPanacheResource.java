package org.restop.crud.deployment.rest;

import io.quarkus.hibernate.orm.rest.data.panache.PanacheEntityResource;
import io.quarkus.rest.data.panache.MethodProperties;
import io.quarkus.rest.data.panache.ResourceProperties;
import org.restop.crud.deployment.pojo.Fruit;

import java.util.List;

@ResourceProperties(path = "fruit-panache")
public interface FruitPanacheResource extends PanacheEntityResource<Fruit, Long> {
    @MethodProperties(exposed = false)
    List<Fruit> list();
}
