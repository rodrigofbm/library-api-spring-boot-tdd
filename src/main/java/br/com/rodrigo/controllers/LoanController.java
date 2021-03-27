package br.com.rodrigo.controllers;

import br.com.rodrigo.model.entity.Book;
import br.com.rodrigo.DTOs.LoanDTO;
import br.com.rodrigo.model.entity.Loan;
import br.com.rodrigo.services.BookService;
import br.com.rodrigo.services.LoanService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private BookService bookService;
    private LoanService loanService;
    private ModelMapper mapper;

    public LoanController(BookService bookService, LoanService loanService, ModelMapper mapper) {
        this.bookService = bookService;
        this.loanService = loanService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<LoanDTO> create(@RequestBody LoanDTO dto) {
        return bookService.findBookByIsbn(dto.getIsbn())
                .map(entity -> {
                    Loan loan = Loan.builder().customer(dto.getCustomer()).book(entity).build();
                    LoanDTO response = mapper.map(loanService.save(loan), LoanDTO.class);
                    response.setIsbn(entity.getIsbn());

                    return new ResponseEntity(response, HttpStatus.CREATED);
                }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
    }
}
