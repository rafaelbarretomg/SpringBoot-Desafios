package  challenges.challenge02_todolist.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import  challenges.challenge02_todolist.models.Todolist;
import challenges.challenge02_todolist.models.enums.TodoStatus;

import java.util.List;


public interface TodolistRepository extends JpaRepository<Todolist, Long>{
    
    List<Todolist> findByTitleContaining(String title);
    List<Todolist> findByStatus(TodoStatus status);
    
}
