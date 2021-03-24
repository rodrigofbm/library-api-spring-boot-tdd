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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;

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

    @Test
    @DisplayName("Should return not found book")
    public void shouldReturnNotFoundBook() throws Exception{
        // cenario
        BDDMockito.given(bookService.findById(BDDMockito.anyLong()))
                .willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(BOOK_API + "/1")
                .accept(MediaType.APPLICATION_JSON);

        // acao/verificacao
        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("Should return a book")
    public void shouldReturnBook() throws Exception{
        // cenario
        Book book = Book.builder().id(1L).author("Frank Miller").isbn("123456")
                .title("The Dark Knight").build();
        BDDMockito.given(bookService.findById(1L))
                .willReturn(Optional.of(book));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(BOOK_API + "/1")
                .accept(MediaType.APPLICATION_JSON);

        // acao/verificacao
        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Should Return BadRequest On Delete Passing No Id")
    public void shouldReturnBadRequestOnDeletePassingNoId() throws Exception {
        // acao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API)
                .accept(MediaType.APPLICATION_JSON);

        // verificacao
        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().is(405));
    }

    @Test
    @DisplayName("Should Return NotFound Book On Delete")
    public void shouldReturnNotFoundBookOnDelete() throws Exception {
        // cenario
        BDDMockito.given(bookService.findById(BDDMockito.anyLong()))
                .willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API + "/1");

        // acao/verificacao
        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("Should Return NoContent On Delete Success")
    public void shouldReturnNoContentOnDeleteSuccess() throws Exception {
        Book book = Book.builder().id(1L).author("Frank Miller").isbn("123456")
                .title("The Dark Knight").build();
        BDDMockito.given(bookService.findById(BDDMockito.anyLong()))
                .willReturn(Optional.of(book));

        // acao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API + "/1")
                .accept(MediaType.APPLICATION_JSON);

        // verificacao
        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("Should Return NotFound For Update Book")
    public void shouldReturnNotFoundUpdateBook() throws Exception {
        // cenario
        Book book = Book.builder().id(1L).author("Frank Miller").isbn("123456")
                .title("The Dark Knight").build();
        String json = new ObjectMapper().writeValueAsString(book);

        BDDMockito.given(bookService.findById(BDDMockito.anyLong()))
                .willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(BOOK_API.concat("/" + 1))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        // acao/verificacao
        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("Should Return NotFound For Update Book")
    public void shouldReturnUpdatedBookSuccess() throws Exception {
        // cenario
        Long id = 1L;
        BookDTO dto = BookDTO.builder().id(1L).author("Frank Miller").isbn("654321")
                .title("The Dark K").build();
        Book updatingBook = Book.builder().id(1L).author("Frank Miller").isbn("123456")
                .title("The Dark Knight").build();
        Book updatedBook = Book.builder().id(1L).author("Frank Miller").isbn("654321")
                .title("The Dark K").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(bookService.findById(id))
                .willReturn(Optional.of(updatingBook));
        BDDMockito.given(bookService.update(updatingBook))
                .willReturn(updatedBook);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(BOOK_API.concat("/" + 1))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        // acao/verificacao
        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("title").value("The Dark K"))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value("Frank Miller"))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value("654321"));
    }

    @Test
    @DisplayName("Should Return List Of Filtered Books")
    public void shouldReturnListOfFilteredBooks() throws Exception {
        // cenario
        Book bookToReturn = Book.builder().id(1L).author("Frank Miller").title("The Dark Knight").isbn("123").build();
        String queryParams = String.format(
                "?title=%s&author=%s&page=0&size=100", bookToReturn.getTitle(), bookToReturn.getAuthor());

        BDDMockito.given(bookService.find(BDDMockito.any(Book.class), BDDMockito.any(Pageable.class)))
                .willReturn(new PageImpl<Book>(
                        Arrays.asList(bookToReturn), PageRequest.of(0, 100), 1));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(BOOK_API.concat(queryParams))
                .accept(MediaType.APPLICATION_JSON);

        // acao/verificacao
        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("content", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("totalElements").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("pageable.pageSize").value(100))
                .andExpect(MockMvcResultMatchers.jsonPath("pageable.pageNumber").value(0));
    }
}
