package com.example.challenges.challenge01.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.challenges.challenge01.models.Book;

public interface BookRepository extends JpaRepository<Book, Long> {

    
}