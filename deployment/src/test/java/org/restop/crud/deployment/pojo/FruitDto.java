package org.restop.crud.deployment.pojo;

import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.json.bind.annotation.JsonbCreator;

@RegisterForReflection
public class FruitDto {

    public String name;

    @JsonbCreator
    public FruitDto(String name) {
        this.name = name;
    }
}
