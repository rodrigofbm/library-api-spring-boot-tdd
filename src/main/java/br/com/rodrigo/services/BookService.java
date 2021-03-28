package br.com.rodrigo.services;

import br.com.rodrigo.model.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface BookService {
    Book save(Book any);

    List<Book> findAll();

    Optional<Book> findById(Long any);

    void delete(Book book);

    Book update(Book book);

    Page<Book> find(Book book, Pageable params);

    Optional<Book> findBookByIsbn(String isbn);
}
