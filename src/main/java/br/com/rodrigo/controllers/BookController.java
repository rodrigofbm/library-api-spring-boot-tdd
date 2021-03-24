package br.com.rodrigo.controllers;

import br.com.rodrigo.DTOs.BookDTO;
import br.com.rodrigo.exceptions.ApiErrors;
import br.com.rodrigo.exceptions.BusinessRuleException;
import br.com.rodrigo.model.entity.Book;
import br.com.rodrigo.services.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private BookService bookService;
    private ModelMapper mapper;

    public BookController(BookService bookService, ModelMapper mapper) {
        this.bookService = bookService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<BookDTO> create(@RequestBody @Valid BookDTO dto) {
        Book entity = mapper.map(dto, Book.class);
        entity = bookService.save(entity);

        return new ResponseEntity(mapper.map(entity, BookDTO.class), HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<BookDTO> get(@PathVariable Long id) {
        return bookService.findById(id)
                .map(b ->  ResponseEntity.ok(mapper.map(b, BookDTO.class)))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<Page<BookDTO>> find(BookDTO book, Pageable params) {
        Page<Book> results = bookService.find(mapper.map(book, Book.class), params);
        List<BookDTO> books = results.getContent().stream()
                .map(entity -> mapper.map(entity, BookDTO.class))
                .collect(Collectors.toList());
        Page<BookDTO> response = new PageImpl<>(books, params, results.getTotalElements());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        Book foundedBook = bookService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        bookService.delete(foundedBook);

        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    @PutMapping("{id}")
    public ResponseEntity<BookDTO> update(@PathVariable Long id, @RequestBody @Valid BookDTO dto) {
        return bookService.findById(id)
                .map(book -> {
                    book.setAuthor(dto.getAuthor());
                    book.setTitle(dto.getTitle());
                    book = bookService.update(book);

                    return new ResponseEntity<>(mapper.map(book, BookDTO.class), HttpStatus.OK);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrors> handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();

        return new ResponseEntity<>(new ApiErrors(bindingResult), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ApiErrors> handleBusinessRuleException(BusinessRuleException ex) {

        return new ResponseEntity<>(
                new ApiErrors(new BusinessRuleException("ISBN already exists")),
                HttpStatus.BAD_REQUEST);
    }
}
