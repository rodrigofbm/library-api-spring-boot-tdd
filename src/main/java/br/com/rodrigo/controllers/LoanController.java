package br.com.rodrigo.controllers;

import br.com.rodrigo.DTOs.BookDTO;
import br.com.rodrigo.DTOs.LoanReturnedDTO;
import br.com.rodrigo.model.entity.Book;
import br.com.rodrigo.DTOs.LoanDTO;
import br.com.rodrigo.model.entity.Loan;
import br.com.rodrigo.services.BookService;
import br.com.rodrigo.services.LoanService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    @PatchMapping("{id}")
    public ResponseEntity update(@PathVariable Long id, @RequestBody LoanReturnedDTO dto) {
        Loan loan = loanService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
        loan.setReturned(dto.getIsReturned());

        return new ResponseEntity(loanService.update(loan), HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<Page<LoanDTO>> update(LoanDTO dto, Pageable params) {
        Page<Loan> loans = loanService.find(mapper.map(dto, Loan.class), params);
        List<LoanDTO> dtos = loans.getContent().stream().map(entity -> {
            LoanDTO loanDTO = mapper.map(entity, LoanDTO.class);
            loanDTO.setBookDTO(mapper.map(entity.getBook(), BookDTO.class));

            return loanDTO;
        }).collect(Collectors.toList());
        Page<LoanDTO> reponse = new PageImpl<>(dtos,PageRequest.of(params.getPageNumber(), params.getPageSize()),loans
                .getTotalElements());

        return new ResponseEntity<>(reponse, HttpStatus.OK);
    }
}
