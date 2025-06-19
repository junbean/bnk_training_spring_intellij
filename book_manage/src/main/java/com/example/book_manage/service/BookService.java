package com.example.book_manage.service;

import com.example.book_manage.entity.Book;
import com.example.book_manage.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<Book> searchBooksByTitle(String title) {
        return bookRepository.findByTitleContaining(title);
    }

    public Book getBookById(Long bid) {
        return bookRepository.findById(bid).orElseThrow();
    }

}
