package br.com.rodrigo.model.repositories;

import br.com.rodrigo.model.entity.Book;
import br.com.rodrigo.model.entity.Loan;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private LoanRepository loanRepository;

    @Test
    @DisplayName("Should Return True If Exists Loan Not Returned With Book")
    public void shouldReturnTrueIfExistsLoanNotReturnedWithBook() {
        // cenario
        Book book = Book.builder().isbn("1230").author("Frank Miller").title("The Dark K.").build();
        Loan loan = Loan.builder().loanDate(LocalDate.now()).customer("Fulano").book(book).build();
        entityManager.persist(book);
        entityManager.persist(loan);

        // acao
        Boolean exists = loanRepository.existsByBookLoanNotReturned(book);

        // verificacao
        Assertions.assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should Find By Book Or Customer")
    public void shouldFindByBookOrCustomer() {
        // cenario
        Book book = Book.builder().isbn("1230").title("The Dark knigh").author("Frank Miller").build();
        Loan loan = Loan.builder().loanDate(LocalDate.now()).book(book).customer("Fulano").build();
        PageRequest pageRequest = PageRequest.of(0, 100);
        entityManager.persist(book);
        entityManager.persist(loan);

        // acao
        Page<Loan> result = loanRepository.findByBookOrCustomer("1230", "Fulano", pageRequest);

        // verificacao
        Assertions.assertThat(result.getContent()).hasSize(1);
        Assertions.assertThat(result.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(result.getPageable().getPageSize()).isEqualTo(100);
        Assertions.assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
    }
}
