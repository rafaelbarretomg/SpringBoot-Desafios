package challenges.challenge02_todolist.controllers;


import challenges.challenge02_todolist.dtos.TodolistRequest;
import challenges.challenge02_todolist.models.Todolist;
import challenges.challenge02_todolist.models.enums.TodoStatus;
import challenges.challenge02_todolist.services.TodolistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.parser.Entity;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequestMapping("/tarefas")
@Tag(name = "Todolist", description = "Gerenciamente de Tarefas")
public class TodolistController {

    @Autowired
    private TodolistService service;

    @Operation(summary = "Endpoint findAll com paginação")
    @GetMapping
    public ResponseEntity<List<Todolist>> findAll() {
            List<Todolist> tasks = service.findAll();
            return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "Endpoint findById")
    @GetMapping("/{id}")
    public ResponseEntity<Todolist> findById(@PathVariable Long id) {
        Todolist task = service.findById(id);
        return ResponseEntity.ok(task);
    }

    @Operation(summary = "Cria uma nova tarefa", description = "Cria uma nova tarefa no sistema")
    @PostMapping
    public ResponseEntity<Todolist> insert(@RequestBody @Valid TodolistRequest request, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }
        //convertendo DTO para entidade
        Todolist toDoList = new Todolist();
        toDoList.setTitle(request.getTitle());
        toDoList.setDescription(request.getDescription());
        toDoList.setStatus(request.getStatus());

        Todolist savedTask = service.insert(toDoList);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTask);
    }

    @Operation(summary = "Atualiza uma tarefa existente", description = "Atualiza os detalhes de uma tarefa")
    @PutMapping(value = "/{id}")
    public ResponseEntity<Todolist> update(@PathVariable Long id, @RequestBody @Valid TodolistRequest request, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        //Convertendo DTO para entidade
        Todolist toDoList = new Todolist();
        toDoList.setTitle(request.getTitle());
        toDoList.setDescription(request.getDescription());
        toDoList.setStatus(request.getStatus());

        Todolist updatedTask = service.update(id, toDoList);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(updatedTask);
    }

    @Operation(summary = "Endpoint Delete")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Endpoint de busca com paginação")
    @GetMapping("/busca")
    public ResponseEntity<List<Todolist>> findByTitle(@RequestParam String title) {
       List<Todolist> tasks = service.findByTitle(title);
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "Endpoint busca por status com paginação")
    @GetMapping("/status")
    public ResponseEntity<List<Todolist>> findByStatus(@RequestParam TodoStatus status) {
       List<Todolist> tasks = service.findByStatus(status);
        return ResponseEntity.ok(tasks);
    }


}
