package br.com.rodrigo.model.repositories;

import br.com.rodrigo.model.entity.Book;
import br.com.rodrigo.model.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    @Query(value = "SELECT case when ( count(l.id) > 0 ) THEN TRUE ELSE FALSE END FROM Loan l " +
            "WHERE l.book = :book AND (l.isReturned IS NULL OR l.isReturned IS NOT TRUE)")
    boolean existsByBookLoanNotReturned(@Param("book") Book book);

    @Query( value = " select l from Loan as l join l.book as b where b.isbn = :isbn or l.customer =:customer ")
    Page<Loan> findByBookOrCustomer(@Param("isbn") String isbn, @Param("customer") String customer, Pageable pageable);
}
