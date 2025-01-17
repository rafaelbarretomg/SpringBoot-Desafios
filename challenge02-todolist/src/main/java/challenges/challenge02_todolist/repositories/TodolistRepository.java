package  challenges.challenge02_todolist.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import  challenges.challenge02_todolist.models.Todolist;
import challenges.challenge02_todolist.models.enums.TodoStatus;




public interface TodolistRepository extends JpaRepository<Todolist, Long>{
    
    Page<Todolist> findByTitleContaining(String title, Pageable pageable);
    Page<Todolist> findByStatus(TodoStatus status, Pageable pageable);
    
}
