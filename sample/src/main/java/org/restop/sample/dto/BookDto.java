package org.restop.sample.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.restop.sample.pojo.Author;

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
