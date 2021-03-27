package br.com.rodrigo.services;

import br.com.rodrigo.model.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BookService {
    Book save(Book any);

    List<Book> findAll();

    Optional<Book> findById(Long any);

    void delete(Book book);

    Book update(Book book);

    Page<Book> find(Book book, Pageable any1);

    Optional<Book> findBookByIsbn(String isbn);
}
