package br.com.rodrigo.controllers;

import br.com.rodrigo.model.entity.Book;
import br.com.rodrigo.DTOs.LoanDTO;
import br.com.rodrigo.model.entity.Loan;
import br.com.rodrigo.services.BookService;
import br.com.rodrigo.services.LoanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(LoanController.class)
@AutoConfigureMockMvc
public class LoanControllerTest {

    private static String BOOK_API = "/api/loans";
    @Autowired
    MockMvc mvc;

    @MockBean
    BookService bookService;
    @MockBean
    LoanService loanService;

    @Test
    @DisplayName("Should Return Book NotFound When Creating Loan")
    public void shouldReturnBookNotFoundWhenCreatingLoan() throws Exception  {
        // cenario
        LoanDTO dto = LoanDTO.builder().customer("Fulano").isbn("1234").build();
        String json = new  ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(bookService.findBookByIsbn(BDDMockito.anyString())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        //acao verificacao
        mvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0]").value("Book not found"));
    }

    @Test
    @DisplayName("Should Create A Loan")
    public void shouldCreateLoan() throws Exception  {
        // cenario
        LoanDTO dto = LoanDTO.builder().customer("Fulano").isbn("1234").build();
        String json = new  ObjectMapper().writeValueAsString(dto);

        Book book = Book.builder().author("Frank Miller").isbn("1234").title("The Dark Knight").build();
        BDDMockito.given(bookService.findBookByIsbn(dto.getIsbn())).willReturn(Optional.of(book));

        Loan loan = Loan.builder().id(1L).book(book).isAvailable(false).loanDate(LocalDate.now()).customer("Fulano")
                .build();
        BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willReturn(loan);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        //acao verificacao
        mvc.perform(request)
            .andExpect(status().isCreated())
            .andExpect(MockMvcResultMatchers.content()
                    .string("{\"isbn\":\"1234\",\"customer\":\"Fulano\"}"));
    }
}
