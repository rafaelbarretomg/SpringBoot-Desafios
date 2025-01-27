package com.example.challenges.challenge01.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.challenges.challenge01.models.Book;
import com.example.challenges.challenge01.services.BookService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/livros")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public ResponseEntity<List<Book>> findAll(){
        List<Book> list = bookService.findAll();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Book> findById(@PathVariable Long id){
        Book book = bookService.findById(id);
        return ResponseEntity.ok().body(book);
    }
    
    @PostMapping
    public ResponseEntity<Book> insert(@RequestBody @Valid Book book, BindingResult result){
        if(result.hasErrors()){
            return ResponseEntity.badRequest().build();
        }
        bookService.insert(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(book);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Book> update(@PathVariable Long id, @RequestBody @Valid Book book, BindingResult result){
        if(result.hasErrors()){
            return ResponseEntity.badRequest().build();
        }
        bookService.update(id, book);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(book);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
