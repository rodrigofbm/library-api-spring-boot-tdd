package br.com.rodrigo.services;

import br.com.rodrigo.model.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface LoanService {
    Loan save(Loan loan);

    Optional<Loan> findById(Long id);

    Loan update(Loan loan);

    Page<Loan> find(Loan loan, Pageable params);
}
