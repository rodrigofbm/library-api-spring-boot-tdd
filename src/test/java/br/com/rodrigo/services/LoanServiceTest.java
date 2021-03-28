package br.com.rodrigo.services;

import br.com.rodrigo.exceptions.BusinessRuleException;
import br.com.rodrigo.model.entity.Book;
import br.com.rodrigo.model.entity.Loan;
import br.com.rodrigo.model.repositories.LoanRepository;
import br.com.rodrigo.services.impl.LoanServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

    LoanService loanService;
    @MockBean
    LoanRepository loanRepository;

    @BeforeEach
    public void setup() {
        loanService = new LoanServiceImpl(loanRepository);
    }

    @Test
    @DisplayName("Should Create A Loan")
    public void shouldCreateLoan() {
        // cenario
        Book book = Book.builder().id(1L).isbn("1230").author("").build();
        Loan savingLoan = Loan.builder().book(book).loanDate(LocalDate.now()).customer("Fulano").build();
        Loan savedLoan = Loan.builder().id(1L).book(book).loanDate(savingLoan.getLoanDate()).customer("Fulano").build();

        Mockito.when(loanRepository.save(savingLoan)).thenReturn(savedLoan);
        Mockito.when(loanRepository.existsByBookLoanNotReturned(book)).thenReturn(false);

        // acao
        Loan loan = loanService.save(savingLoan);

        // verificacao
        Assertions.assertThat(loan).isNotNull();
        Assertions.assertThat(loan.getId()).isEqualTo(1L);
        Assertions.assertThat(loan.getBook().getId()).isEqualTo(savedLoan.getBook().getId());
        Assertions.assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());
        Assertions.assertThat(loan.getCustomer()).isEqualTo("Fulano");

        Mockito.verify(loanRepository, Mockito.times(1)).save(savingLoan);
    }

    @Test
    @DisplayName("Should Return Error When Creating A Loan")
    public void shouldReturnBadRequestCreateLoan() {
        // cenario
        Book book = Book.builder().id(1L).isbn("1230").author("").build();
        Loan savingLoan = Loan.builder().book(book).loanDate(LocalDate.now()).customer("Fulano").build();

        Mockito.when(loanRepository.existsByBookLoanNotReturned(book)).thenReturn(true);

        // acao
        Throwable error = Assertions.catchThrowable(() -> loanService.save(savingLoan));

        // verificacao
        Assertions.assertThat(error).isInstanceOf(BusinessRuleException.class).hasMessage("Book already loaned");
        Mockito.verify(loanRepository, Mockito.never()).save(savingLoan);
    }

    @Test
    @DisplayName("Should Return Loan FindById")
    public void shouldReturnLoanFindById() {
        Long id = 1L;
        Loan loan = Loan.builder().id(id).build();

        Mockito.when(loanRepository.findById(id)).thenReturn(Optional.of(loan));

        // acao
        Optional<Loan> foundedLoan = loanService.findById(1L);

        // verificacao
        Assertions.assertThat(foundedLoan).isPresent();
    }

    @Test
    @DisplayName("Should Update Loan")
    public void shouldUpdateLoan() {
        Long id = 1L;
        Loan updatingLoan = Loan.builder().id(id).build();
        Loan updatedLoan = Loan.builder().id(id).isReturned(true).build();

        Mockito.when(loanRepository.save(updatingLoan)).thenReturn(updatedLoan);

        // acao
        Loan loan = loanService.update(updatingLoan);

        // verificacao
        Assertions.assertThat(loan.getId()).isEqualTo(1L);
        Assertions.assertThat(loan.isReturned()).isTrue();
    }

    @Test
    @DisplayName("Should Return Filtered List Of Loans")
    public void shouldReturnFilteredListOfLoans() {
        // cenario
        Book book = Book.builder().id(1L).isbn("1230").build();
        Loan loan = Loan.builder().id(1L).customer("Fulano").book(book).loanDate(LocalDate.now()).build();
        PageRequest params = PageRequest.of(0, 100);

        Mockito.when(loanRepository
                .findByBookOrCustomer(Mockito.anyString(), Mockito.anyString(), Mockito.any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(loan),  params, 1));

        // acao
        Page<Loan> loans = loanService.find(loan, params);

        // verificacao
        Assertions.assertThat(loans.getContent()).hasSize(1);
        Assertions.assertThat(loans.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(loans.getContent()).isEqualTo(Arrays.asList(loan));
        Assertions.assertThat(loans.getPageable().getPageNumber()).isEqualTo(0);
        Assertions.assertThat(loans.getPageable().getPageSize()).isEqualTo(100);
    }
}
