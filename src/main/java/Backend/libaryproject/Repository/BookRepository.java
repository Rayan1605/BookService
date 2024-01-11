package Backend.libaryproject.Repository;

import Backend.libaryproject.Entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    //Since Jpa has no Search option we will create a method to search for books by title
    //Which will generate the method at runtime

    Page<Book> findByTitle(@Param("title") String title, Pageable pageable);
    //This method will search for books by title and return a page of books

    Page<Book> findByCategory(@Param("category") String category, Pageable pageable);

    //This method will search for books by category and return a page of books
//@Query - Defines the JPQL query to execute:
//JPQL is a query language for JPA that queries entity objects rather than tables.
//It offers a database independent way to query entities in Java.
//JPQL gets converted to optimized SQL queries for the underlying database.
//Provides an object-oriented approach for retrieving entities from a database.

    //Query("SELECT b FROM Book b WHERE b.id IN :book_ids")
    //
    //SELECT - This specifies to select data
    //b - This is an alias for the Book entity
    //FROM Book b - This specifies to select from the Book entity, using 'b' as the alias
    //WHERE - This provides a condition for the query
    //b.id - Refers to the 'id' property of the Book entity
    //IN - Checks if the id is IN the given list
    //:book_ids - This is the query parameter that will be populated with the bookIds list

    // So basically this query will select all the books where the id is in the
    // given list of bookIds
    @Query("SELECT b FROM Book b WHERE b.id IN :book_ids")
    List<Book> findBooksByBookIds(@Param("book_ids") List<Long> bookIds);
}
