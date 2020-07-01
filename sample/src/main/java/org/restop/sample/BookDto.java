package org.restop.sample;

import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.json.bind.annotation.JsonbCreator;

@RegisterForReflection
public class BookDto {

    public Long id;
    public String title;
    public Author author;

    @JsonbCreator
    public BookDto(Long id, String title, Author author) {
        this.id = id;
        this.title = title;
        this.author = author;
    }
}
