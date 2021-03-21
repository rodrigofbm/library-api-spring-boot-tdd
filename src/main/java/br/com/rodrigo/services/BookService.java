package br.com.rodrigo.services;

import br.com.rodrigo.model.entity.Book;
import org.springframework.stereotype.Service;

public interface BookService {
    Book save(Book any);
}
