package br.com.rodrigo.services.impl;

import br.com.rodrigo.exceptions.BusinessRuleException;
import br.com.rodrigo.model.entity.Loan;
import br.com.rodrigo.model.repositories.LoanRepository;
import br.com.rodrigo.services.LoanService;
import org.springframework.data.domain.*;

import java.util.Optional;

public class LoanServiceImpl implements LoanService {
    LoanRepository loanRepository;

    public LoanServiceImpl(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Override
    public Loan save(Loan loan) {
        if(loanRepository.existsByBookLoanNotReturned(loan.getBook())) {
            throw new BusinessRuleException("Book already loaned");
        }

        return loanRepository.save(loan);
    }

    @Override
    public Optional<Loan> findById(Long id) {
        return loanRepository.findById(id);
    }

    @Override
    public Loan update(Loan loan) {
        return loanRepository.save(loan);
    }

    @Override
    public Page<Loan> find(Loan loan, Pageable params) {
        Page<Loan> response = loanRepository.findByBookOrCustomer(loan.getBook().getIsbn(), loan.getCustomer(), params);

        return response;
    }
}
