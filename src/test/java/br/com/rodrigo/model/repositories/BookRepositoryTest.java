package br.com.rodrigo.model.repositories;

import br.com.rodrigo.model.entity.Book;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
}
