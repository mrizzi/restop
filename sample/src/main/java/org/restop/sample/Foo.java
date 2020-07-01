package org.restop.sample;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Foo extends PanacheEntity {

    @Column(unique = true)
    public String name;
    public String description;
    public String owner;
    @OneToMany(
            cascade = CascadeType.DETACH,
            orphanRemoval = true
    )
    public List<Label> labels = new ArrayList<>();

    public Foo() {
    }
}
