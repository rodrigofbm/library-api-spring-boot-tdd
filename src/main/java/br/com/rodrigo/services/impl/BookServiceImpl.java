package br.com.rodrigo.services.impl;

import br.com.rodrigo.model.entity.Book;
import br.com.rodrigo.services.BookService;
import br.com.rodrigo.model.repositories.BookRepository;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

    private BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book save(Book book) {
        return bookRepository.save(book);
    }
}
