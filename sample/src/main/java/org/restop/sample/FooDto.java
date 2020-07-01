package org.restop.sample;

import javax.json.bind.annotation.JsonbCreator;

public class FooDto {
    public Long id;
    public String name;
    public String description;

    @JsonbCreator
    public FooDto(Long id, String name, String description, String owner) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
    
}
