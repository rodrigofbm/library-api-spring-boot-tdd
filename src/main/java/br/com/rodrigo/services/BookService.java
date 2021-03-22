package br.com.rodrigo.services;

import br.com.rodrigo.DTOs.BookDTO;
import br.com.rodrigo.model.entity.Book;

import java.util.List;

public interface BookService {
    Book save(Book any);

    List<Book> findAll();
}
