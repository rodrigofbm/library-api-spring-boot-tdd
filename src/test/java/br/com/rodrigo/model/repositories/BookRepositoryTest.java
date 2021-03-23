package br.com.rodrigo.model.repositories;

import br.com.rodrigo.model.entity.Book;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;
    @Autowired
    BookRepository bookRepository;

    @Test
    @DisplayName("Should return true when book with given isbn exists")
    public void shouldReturnTrueWhenIsbnExists() {
        // cenario
        String isbn = "123456";
        Book book = Book.builder().author("Frank Miller").title("The Dark Knight").isbn(isbn).build();
        entityManager.persist(book);

        // acao
        boolean existsByIsbn = bookRepository.existsByIsbn(isbn);

        //verificacao
        Assertions.assertThat(existsByIsbn).isTrue();
    }

    @Test
    @DisplayName("Should return true when book with given isbn exists")
    public void shouldReturnFalseWhenIsbnDoesNotExists() {
        // cenario
        String isbn = "123456";

        // acao
        boolean existsByIsbn = bookRepository.existsByIsbn(isbn);

        //verificacao
        Assertions.assertThat(existsByIsbn).isFalse();
    }

    @Test
    @DisplayName("Should return a empty list of books")
    public void shouldReturnEmptyListOfBooks() {
        // acao
        List<Book> books  = bookRepository.findAll();

        // verificacao
        Assertions.assertThat(books).isEmpty();
    }

    @Test
    @DisplayName("Should return a list of books")
    public void shouldReturnListOfBooks() {
        // cenario
        Book book1 = Book.builder().author("Frank Miller").isbn("123456").title("The Dark Knight").build();
        Book book2 = Book.builder().author("Frank Miller").isbn("123458").title("The Dark Knight Rises").build();
        entityManager.persist(book1);
        entityManager.persist(book2);

        // acao
        List<Book> books  = bookRepository.findAll();

        // verificacao
        Assertions.assertThat(books).hasSize(2);
        Assertions.assertThat(books.get(0).getId()).isNotNull();
        Assertions.assertThat(books.get(0).getTitle()).isEqualTo("The Dark Knight");
        Assertions.assertThat(books.get(0).getAuthor()).isEqualTo("Frank Miller");
        Assertions.assertThat(books.get(0).getIsbn()).isEqualTo("123456");

        Assertions.assertThat(books.get(1).getId()).isNotNull();
        Assertions.assertThat(books.get(1).getTitle()).isEqualTo("The Dark Knight Rises");
        Assertions.assertThat(books.get(1).getAuthor()).isEqualTo("Frank Miller");
        Assertions.assertThat(books.get(1).getIsbn()).isEqualTo("123458");
    }

    @Test
    @DisplayName("Should return not found book")
    public void shouldReturnNotFoundBook() {
        // acao
        Optional<Book> book = bookRepository.findById(Mockito.anyLong());

        // verificacao
        Assertions.assertThat(book).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("Should a book")
    public void shouldReturnBook() {
        // cenario
        Long id = 1L;
        Book book = Book.builder().author("Frank Miller").title("The Dark Knight").isbn("123456").build();
        entityManager.persist(book);

        // acao
        Optional<Book> response = bookRepository.findById(id);

        // verificacao
        Assertions.assertThat(response.isPresent()).isTrue();
        Assertions.assertThat(response.get().getId()).isEqualTo(1L);
        Assertions.assertThat(response.get().getTitle()).isEqualTo("The Dark Knight");
        Assertions.assertThat(response.get().getAuthor()).isEqualTo("Frank Miller");
        Assertions.assertThat(response.get().getIsbn()).isEqualTo("123456");
    }

    @Test
    @DisplayName("Should Delete a Book")
    public void shouldDeleteBook() {
        // cenario
        Book book = Book.builder().author("Frank Miller").title("The Dark Knight").isbn("123456").build();
        Long id = entityManager.persistAndGetId(book, Long.class);

        // acao
        bookRepository.deleteById(id);

        // verificacao
        entityManager.flush();
        Book after = entityManager.find(Book.class, id);
        Assertions.assertThat(after).isNull();
    }
}
