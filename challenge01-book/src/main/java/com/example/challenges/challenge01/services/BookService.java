package com.example.challenges.challenge01.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.challenges.challenge01.models.Book;
import com.example.challenges.challenge01.repositories.BookRepository;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public Book findById(Long id){
        return bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Livro não encontrado"));

    }

    public Book insert(Book book){
        try{
            return bookRepository.save(book);
        }catch(Exception e){
            throw new RuntimeException("Erro ao inserir um livro.", e);
        }
       
    }

    public void delete(Long id){
        bookRepository.deleteById(id);
    }

    public Book update(Long id, Book book){
        try{
            Book entity = bookRepository.getReferenceById(id);
            updateData(entity, book);
            return bookRepository.save(entity);
        }catch(Exception e){
            throw new RuntimeException("Erro ao atualizar um livro.", e);
        }
       
    }

    private void updateData(Book entity, Book obj){
        entity.setTitle(obj.getTitle());
        entity.setAuthor(obj.getAuthor());
        entity.setYearOfPublication(obj.getYearOfPublication());

    }

}
