package br.com.rodrigo.controllers;

import br.com.rodrigo.DTOs.BookDTO;
import br.com.rodrigo.exceptions.ApiErrors;
import br.com.rodrigo.model.entity.Book;
import br.com.rodrigo.services.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrors> handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();

        return new ResponseEntity<>(new ApiErrors(bindingResult), HttpStatus.BAD_REQUEST);
    }
}
