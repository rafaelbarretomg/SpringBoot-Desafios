package  challenges.challenge02_todolist.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import  challenges.challenge02_todolist.models.Todolist;
import challenges.challenge02_todolist.models.enums.TodoStatus;
import  challenges.challenge02_todolist.services.TodolistService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/tarefas")
public class TodolistController {
    
    @Autowired
    private TodolistService service;

    @GetMapping
    public Page<Todolist> findAll(Pageable pageable) {
        return service.findAll(pageable);
    }
    
    @GetMapping("/busca")
    public Page<Todolist> findByTitle(@RequestParam String title, Pageable pageable){
        return service.findByTitle(title, pageable);
    }

    @GetMapping("/status")
    public Page<Todolist> findByStatus(@RequestParam TodoStatus status, Pageable pageable){
        return service.findByStatus(status, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Todolist> findById(@PathVariable Long id) {
        Todolist toDoList = service.findById(id);
        return ResponseEntity.ok().body(toDoList);
    }

    @PostMapping
    public ResponseEntity<Todolist> insert(@RequestBody @Valid Todolist toDoList, BindingResult result) {
        if(result.hasErrors()){
            return ResponseEntity.badRequest().build();
        }
        Todolist savedTask = service.insert(toDoList);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTask);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Todolist> update(@PathVariable Long id, @RequestBody @Valid Todolist toDoList, BindingResult result){
        if(result.hasErrors()){
            return ResponseEntity.badRequest().build();
        }
        Todolist updatedTask = service.update(id, toDoList);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(updatedTask);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    
}
