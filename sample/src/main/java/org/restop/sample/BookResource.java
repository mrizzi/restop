package org.restop.sample;

import org.restop.crud.Deletable;
import org.restop.crud.ReadableById;
import org.restop.crud.ReadablePaginatedByRange;
import org.restop.crud.UpdatableWithDto;
import org.restop.mapper.Mapper;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("library/book")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class BookResource implements ReadableById<Book>, ReadablePaginatedByRange<Book>, UpdatableWithDto<Book, Book>, Deletable<Book> {

    @Override
    public Class<Book> getPanacheEntityType() {
        return Book.class;
    }

    @Override
    public Class<Book> getDtoType() {
        return Book.class;
    }

    @Override
    public Mapper<Book, Book> getMapper() {
        return new Mapper<Book, Book>() {
            @Override
            public Book map(Book source, Book target) {
                if (target == null) target = new Book();
                target.title = source.title;
                return target;
            }
        };
    }
}
