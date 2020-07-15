package org.restop.sample.resource;

import org.restop.crud.CreatableWithDto;
import org.restop.mapper.Mapper;
import org.restop.sample.dto.BookDto;
import org.restop.sample.pojo.Book;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("library/book")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class BookResourceWithDto implements CreatableWithDto<Book, BookDto> {

    @Override
    public Class<Book> getPanacheEntityType() {
        return Book.class;
    }

    @Override
    public Class<BookDto> getDtoType() {
        return BookDto.class;
    }

    @Override
    public Mapper<Book, BookDto> getMapper() {
        return new Mapper<Book, BookDto>() {
            @Override
            public Book map(BookDto source, Book target) {
                if (target == null) target = new Book();
                target.title = source.title;
                target.author = source.author;
                return target;
            }
        };
    }
}
