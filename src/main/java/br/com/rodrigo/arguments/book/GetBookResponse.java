package br.com.rodrigo.arguments.book;

import br.com.rodrigo.DTOs.BookDTO;

import java.util.List;

public class GetBookResponse {
    List<BookDTO> books;

    public GetBookResponse(List<BookDTO> booksDTOs) {
        this.books = booksDTOs;
    }

    public List<BookDTO> getBooks() {
        return books;
    }
}
