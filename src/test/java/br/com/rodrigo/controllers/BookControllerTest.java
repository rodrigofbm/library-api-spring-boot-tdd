package br.com.rodrigo.controllers;

import br.com.rodrigo.DTOs.BookDTO;
import br.com.rodrigo.exceptions.BusinessRuleException;
import br.com.rodrigo.model.entity.Book;
import br.com.rodrigo.services.BookService;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {
    private static String BOOK_API = "/api/books";
    @Autowired
    MockMvc mvc; // simula requisicoes para a API

    @MockBean // criar mock no contexto de DI do spring
    BookService bookService;

    @Test
    @DisplayName("Should create a Book")
    public void shouldCreateBook() throws Exception {
        BookDTO bookDTO = BookDTO.builder().author("Frank Miller").isbn("123456")
                .title("The Dark Knight").build();
        Book savedBook = Book.builder().id(10L).author("Frank Miller").isbn("123456").title("The Dark Knight").build();

        BDDMockito.given(bookService.save(Mockito.any(Book.class))).willReturn(savedBook);
        // transforma um objeto em string-json
        String json = new ObjectMapper().writeValueAsString(bookDTO);
        // cenario
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);
        // Book book = new Book();

        // acao/verificacao
        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(10L))
                .andExpect(MockMvcResultMatchers.jsonPath("title").value("The Dark Knight"))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value("Frank Miller"))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value("123456"));
    }

    @Test
    @DisplayName("Should Throws Error On Create Book Without Required Data")
    public void shouldThrowsRequiredDataError() throws Exception {
        // cenario
        String json = new ObjectMapper().writeValueAsString(new BookDTO());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        // acao/verificacao
        mvc.perform(request)
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(3)));
    }

    @Test
    @DisplayName("Should Not Create Book With Duplicated ISBN")
    public void shouldNotCreateBookWithDuplicatedISBN() throws Exception {
        // cenario
        BookDTO bookDTO = BookDTO.builder().author("Frank Miller").isbn("123456")
                .title("The Dark Knight").build();
        String json = new ObjectMapper().writeValueAsString(bookDTO);

        BDDMockito.given(bookService.save(Mockito.any(Book.class))).
                willThrow(new BusinessRuleException("ISBN already exists"));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0]")
                        .value("ISBN already exists"));
    }
}
