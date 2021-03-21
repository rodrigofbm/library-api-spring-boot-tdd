package br.com.rodrigo.DTOs;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    Long id;
    @NotEmpty
    String title;
    @NotEmpty
    String author;
    @NotEmpty
    String isbn;
}
