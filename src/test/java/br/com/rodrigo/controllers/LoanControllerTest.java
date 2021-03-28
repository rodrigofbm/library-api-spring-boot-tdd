package br.com.rodrigo.controllers;

import br.com.rodrigo.DTOs.LoanReturnedDTO;
import br.com.rodrigo.exceptions.BusinessRuleException;
import br.com.rodrigo.model.entity.Book;
import br.com.rodrigo.DTOs.LoanDTO;
import br.com.rodrigo.model.entity.Loan;
import br.com.rodrigo.services.BookService;
import br.com.rodrigo.services.LoanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.Arrays;
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

        Loan loan = Loan.builder().id(1L).book(book).isReturned(false).loanDate(LocalDate.now()).customer("Fulano")
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
                    .string("{\"id\":1,\"isbn\":\"1234\",\"customer\":\"Fulano\",\"bookDTO\":null}"));
    }

    @Test
    @DisplayName("Should Return Book Already Loaned Error")
    public void shouldReturnBookAlreadyLoanedError() throws Exception{
        LoanDTO loanDTO = LoanDTO.builder().customer("Fulano").isbn("1230").build();
        String json = new ObjectMapper().writeValueAsString(loanDTO);

        Book book = Book.builder().id(1L).isbn(loanDTO.getIsbn()).build();

        BDDMockito.given(bookService.findBookByIsbn("1230")).willReturn(Optional.of(book));
        BDDMockito.given(loanService.save(Mockito.any(Loan.class)))
                .willThrow(new BusinessRuleException("Book already loaned"));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0]")
                        .value("Book already loaned"));
    }

    @Test
    @DisplayName("Should Finish Loan And Make Book Available Again")
    public void shouldFinishLoan() throws Exception {
        // cenario
        LoanReturnedDTO dto = LoanReturnedDTO.builder().isReturned(true).build();
        String json = new ObjectMapper().writeValueAsString(dto);
        Loan loan = Loan.builder().id(1L).build();

        BDDMockito.given(loanService.findById(1L)).willReturn(Optional.of(loan));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch(BOOK_API.concat("/1"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        // acao/verificacao;
        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk());
        Mockito.verify(loanService, Mockito.times(1)).update(loan);
    }

    @Test
    @DisplayName("S hould Return NotFound When Finishing Loan")
    public void shouldReturnNotFoundWhenFinishingLoan() throws Exception {
        // cenario
        LoanReturnedDTO dto = LoanReturnedDTO.builder().isReturned(true).build();
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(loanService.findById(Mockito.anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch(BOOK_API.concat("/1"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        // acao/verificacao;
        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        Mockito.verify(loanService, Mockito.never()).update(Mockito.any(Loan.class));
    }

    @Test
    @DisplayName("Should GetList Of Loans")
    public void shouldGetListOfLoans() throws Exception{
        String isbn = "1230";
        String customer = "Fulano";
        String queryString = String.format("?isbn=%s&customer=%s&page=0&size=100", isbn, customer);
        Book  book = Book.builder().id(1L).author("Frank M").isbn("1230").title("TDK").build();
        Loan loan = Loan.builder().loanDate(LocalDate.now()).id(1L).book(book).customer("Fulano").build();

        BDDMockito.given(loanService.find(Mockito.any(Loan.class),Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<>(Arrays.asList(loan), PageRequest.of(0, 100), 1));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(BOOK_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        // acao/verificacao
        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("content", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("totalElements").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("pageable.pageSize").value(100))
                .andExpect(MockMvcResultMatchers.jsonPath("pageable.pageNumber").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("content[0].bookDTO.isbn")
                        .value("1230"));
    }
}
