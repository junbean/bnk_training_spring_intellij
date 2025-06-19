package com.example.book_manage.controller;

import com.example.book_manage.entity.Book;
import com.example.book_manage.service.BookService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private BookService bookService;

    @GetMapping("/books")
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> list = bookService.getAllBooks();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/books/search")
    public ResponseEntity<List<Book>> searchBooksByTitle(@RequestParam("title") String title) {
        List<Book> list = bookService.searchBooksByTitle(title);
        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/books/{bid}")
    public ResponseEntity<Book> getBookById(@PathVariable("bid") Long bid) {
        Book result = bookService.getBookById(bid);
        return ResponseEntity.ok().body(result);
    }

}
