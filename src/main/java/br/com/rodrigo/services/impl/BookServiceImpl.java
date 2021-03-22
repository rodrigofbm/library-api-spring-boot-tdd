package br.com.rodrigo.services.impl;

import br.com.rodrigo.exceptions.BusinessRuleException;
import br.com.rodrigo.model.entity.Book;
import br.com.rodrigo.services.BookService;
import br.com.rodrigo.model.repositories.BookRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book save(Book book) {
        if(bookRepository.existsByIsbn(book.getIsbn())){
            throw new BusinessRuleException("ISBN already exists.");
        }
        return bookRepository.save(book);
    }

    @Override
    public List<Book> findAll() {
        List<Book> books = bookRepository.findAll();

        return books;
    }

    @Override
    public Optional<Book> findById(Long id) {
        if(id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id is required");
        }

        return bookRepository.findById(id).map(book -> Optional.of(book))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
    }
}
