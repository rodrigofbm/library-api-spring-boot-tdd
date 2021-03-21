package br.com.rodrigo.DTOs;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    Long id;

    @NotEmpty
    String title;
    @NotEmpty
    String author;
    @NotEmpty
    String isbn;

}
