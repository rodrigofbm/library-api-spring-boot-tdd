package br.com.rodrigo.services;

import br.com.rodrigo.exceptions.BusinessRuleException;
import br.com.rodrigo.model.entity.Book;
import br.com.rodrigo.model.repositories.BookRepository;
import br.com.rodrigo.services.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.matchers.InstanceOf;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService bookService;
    @MockBean
    BookRepository bookRepository;

    @BeforeEach
    public void setup() {
        bookService = new BookServiceImpl(bookRepository);
    }

    @Test
    @DisplayName("Should save a book")
    public void shouldSaveBook() {
        // cenario
        Book book = Book.builder().isbn("123456").title("The Dark Knight").author("Frank Miller").build();
        Mockito.when(bookRepository.existsByIsbn(Mockito.anyString())).thenReturn(false);
        Mockito.when(bookService.save(book)).thenReturn(Book.builder().isbn("123456").title("The Dark Knight")
                .id(10L).author("Frank Miller").build());
        // acao
        Book savedBook = bookService.save(book);

        // verificacao
        Assertions.assertThat(savedBook.getId()).isNotNull();
        Assertions.assertThat(savedBook.getAuthor()).isEqualTo("Frank Miller");
        Assertions.assertThat(savedBook.getIsbn()).isEqualTo("123456");
        Assertions.assertThat(savedBook.getTitle()).isEqualTo("The Dark Knight");
    }

    @Test
    @DisplayName("Should Not Save Book With Duplicated ISBN")
    public void shouldNotSaveBookWithDuplicatedISBN() {
        // cenario
        Book book = Book.builder().isbn("123456").title("The Dark Knight").author("Frank Miller").build();
        Mockito.when(bookRepository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        // acao
        Throwable exception = Assertions.catchThrowable(() -> bookService.save(book));

        // verificacao
        Assertions.assertThat(exception)
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("ISBN already exists.");
        Mockito.verify(bookRepository, Mockito.never()).save(book);
    }

    @Test
    @DisplayName("Should return a empty list of books")
    public void shouldReturnEmptyListOfBooks() {
        // cenario
        Mockito.when(bookRepository.findAll()).thenReturn(Arrays.asList());

        // acao
        List<Book> books = bookService.findAll();

        // verificacao
        Assertions.assertThat(books).isEmpty();
    }

    @Test
    @DisplayName("Should return a list of books")
    public void shouldReturnListOfBooks() {
        // cenario
        Book book1 = Book.builder().id(1L).author("Frank Miller").isbn("123456")
                .title("The Dark Knight").build();
        Book book2 = Book.builder().id(2L).author("Frank Miller").isbn("123458")
                .title("The Dark Knight Rises").build();
        Mockito.when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2));

        // acao
        List<Book> books = bookService.findAll();

        // verificacao
        Assertions.assertThat(books).hasSize(2);
        Assertions.assertThat(books.get(0).getId()).isEqualTo(1L);
        Assertions.assertThat(books.get(0).getTitle()).isEqualTo("The Dark Knight");
        Assertions.assertThat(books.get(0).getAuthor()).isEqualTo("Frank Miller");
        Assertions.assertThat(books.get(0).getIsbn()).isEqualTo("123456");

        Assertions.assertThat(books.get(1).getId()).isEqualTo(2L);
        Assertions.assertThat(books.get(1).getTitle()).isEqualTo("The Dark Knight Rises");
        Assertions.assertThat(books.get(1).getAuthor()).isEqualTo("Frank Miller");
        Assertions.assertThat(books.get(1).getIsbn()).isEqualTo("123458");
    }

    @Test
    @DisplayName("Should Return BadRequest GetById")
    public void shouldReturnBadRequestGetById() {
        // cenario
        Mockito.when(bookRepository.findById(Mockito.anyLong()))
                .thenThrow(ResponseStatusException.class);

        // acao
        Throwable response = Assertions.catchThrowable(() -> {
            Book book = bookService.findById(null).get();
        });

        //verificacao
        Assertions.assertThat(response)
                .isInstanceOf(ResponseStatusException.class)
                .hasMessage("400 BAD_REQUEST \"id is required\"");
    }

    @Test
    @DisplayName("Should Return NotFound GetById")
    public void shouldReturnNotFoundGetById() {
        // cenario
        Mockito.when(bookRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        // acao
        Optional<Book> book = bookService.findById(1L);

        //verificacao
        Assertions.assertThat(book).isNotPresent();
    }

    @Test
    @DisplayName("Should Return Book ById")
    public void shouldReturnBookById() {
        // cenario
        Book book = Book.builder().id(1L).isbn("123456").title("The Dark Knight").author("Frank Miller")
                .build();
        Mockito.when(bookRepository.findById(1L))
                .thenReturn(Optional.of(book));

        // acao
        book = bookService.findById(1L).get();

        //verificacao
        Assertions.assertThat(book).isNotNull();
        Assertions.assertThat(book.getId()).isEqualTo(1L);
        Assertions.assertThat(book.getAuthor()).isEqualTo("Frank Miller");
        Assertions.assertThat(book.getTitle()).isEqualTo("The Dark Knight");
        Assertions.assertThat(book.getIsbn()).isEqualTo("123456");
    }

    @Test
    @DisplayName("Should throw BadRequest When No Book Or Id Is Provided To Delete")
    public void shouldReturnBadRequestWhenNoBookOrIdProvidedToDelete() {
        // Cenario
        Book book = new Book();

        // acao
        org.junit.jupiter.api.Assertions.assertThrows(ResponseStatusException.class, () -> bookService.delete(book));

        // verificacao
        Mockito.verify(bookRepository, Mockito.never()).delete(book);
    }

    @Test
    @DisplayName("Should Delete Book By Id")
    public void shouldDeleteById() {
        // cenario
        Book book = Book.builder().id(1l).build();

        // execucao
        org.junit.jupiter.api.Assertions.assertDoesNotThrow( () -> bookService.delete(book) );

        // verificacoes
        Mockito.verify(bookRepository, Mockito.times(1)).delete(book);
    }

    @Test
    @DisplayName("Should Return BadRequest For Update Book Without Param")
    public void shouldReturnBadRequestForUpdateBookWithoutParam() {
        // acao
        Throwable response = Assertions.catchThrowable(() -> {
            bookService.update(null);
        });

        // verificacao
        Assertions.assertThat(response)
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    @DisplayName("Should Update Book")
    public void shouldUpdateBook() {
        // cenario
        Book book = Book.builder().id(1L).isbn("123456").title("The Dark K").author("Frank M.")
                .build();
        Mockito.when(bookRepository.save(book)).thenReturn(book);

        // acao
        Book updatedBook = bookService.update(book);

        // verificacao
        Assertions.assertThat(updatedBook.getTitle()).isEqualTo("The Dark K");
        Assertions.assertThat(updatedBook.getAuthor()).isEqualTo("Frank M.");
    }
}
