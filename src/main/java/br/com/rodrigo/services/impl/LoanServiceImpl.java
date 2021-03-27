package br.com.rodrigo.services.impl;

import br.com.rodrigo.model.entity.Loan;
import br.com.rodrigo.services.LoanService;

public class LoanServiceImpl implements LoanService {
    @Override
    public Loan save(Loan loan) {
        return loan;
    }
}
