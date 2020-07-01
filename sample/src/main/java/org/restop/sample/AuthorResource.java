package org.restop.sample;

import org.restop.crud.Creatable;
import org.restop.crud.Deletable;
import org.restop.crud.ReadableById;
import org.restop.crud.ReadablePaginatedByRange;
import org.restop.crud.UpdatableWithDto;
import org.restop.mapper.Mapper;

import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("library/author")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class AuthorResource implements Creatable<Author>, ReadableById<Author>, ReadablePaginatedByRange<Author>, UpdatableWithDto<Author, Author>, Deletable<Author> {

    @Override
    public Class<Author> getPanacheEntityType() {
        return Author.class;
    }

    @Override
    public Class<Author> getDtoType() {
        return Author.class;
    }

    @Override
    public Mapper<Author, Author> getMapper() {
        return new Mapper<Author, Author>() {
            @Override
            public Author map(Author source, Author target) {
                if (target == null) target = new Author();
                target.firstName = source.firstName;
                target.lastName = source.lastName;
                return target;
            }
        };
    }
    
    @POST
    @Transactional
    @Path("{id}/book")
    public Response createBook(@PathParam("id") Long id, Book book) {
        Author author = Author.findById(id);
        if (author == null) {
            throw new NotFoundException("Author not found with id " + id);
        }

        Book newBook = new Book();
        newBook.title = book.title;
        newBook.author = author;
        newBook.persist();

        author.books.add(newBook);
        author.persist();
        return Response.ok(newBook).status(Response.Status.CREATED).build();
    }
}
