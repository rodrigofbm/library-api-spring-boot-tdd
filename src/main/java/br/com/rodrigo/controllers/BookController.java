package br.com.rodrigo.controllers;

import br.com.rodrigo.DTOs.BookDTO;
import br.com.rodrigo.arguments.book.GetBookResponse;
import br.com.rodrigo.exceptions.ApiErrors;
import br.com.rodrigo.exceptions.BusinessRuleException;
import br.com.rodrigo.model.entity.Book;
import br.com.rodrigo.services.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

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

    @GetMapping
    public ResponseEntity<GetBookResponse> get() {
        List<Book> books = bookService.findAll();
        List<BookDTO> dtos = Arrays.asList(mapper.map(books, BookDTO[].class));

        return new ResponseEntity<>(new GetBookResponse(dtos), HttpStatus.OK);
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
