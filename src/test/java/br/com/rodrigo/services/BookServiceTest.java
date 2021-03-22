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
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

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
}
