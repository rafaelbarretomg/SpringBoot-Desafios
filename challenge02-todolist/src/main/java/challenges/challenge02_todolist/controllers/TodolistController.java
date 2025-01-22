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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequestMapping("/tarefas")
@Tag(name = "Todolist", description = "Gerenciamente de Tarefas")
public class TodolistController {

    @Autowired
    private TodolistService service;

    @GetMapping
    public ResponseEntity<Page<EntityModel<Todolist>>> findAll(Pageable pageable) {
        Page<Todolist> tasks = service.findAll(pageable);
        Page<EntityModel<Todolist>> tasksWithLinks = tasks.map(task ->{
            EntityModel<Todolist> model = EntityModel.of(task);
            model.add(linkTo(methodOn(TodolistController.class).findById(task.getId())).withSelfRel());
            model.add(linkTo(methodOn(TodolistController.class).findAll(pageable)).withSelfRel());
            return model;
        });

        return ResponseEntity.ok(tasksWithLinks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Todolist>> findById(@PathVariable Long id) {
        Todolist task = service.findById(id);
        EntityModel<Todolist> model = EntityModel.of(task);
        model.add(linkTo(methodOn(TodolistController.class).findById(task.getId())).withSelfRel());
        model.add(linkTo(methodOn(TodolistController.class).findAll(Pageable.unpaged())).withSelfRel());
        return ResponseEntity.ok(model);
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

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/busca")
    public ResponseEntity<Page<EntityModel<Todolist>>> findByTitle(@RequestParam String title, Pageable pageable) {
        Page<Todolist> tasks = service.findByTitle(title, pageable);
        Page<EntityModel<Todolist>> tasksWithLinks = tasks.map(task ->{
            EntityModel<Todolist> model = EntityModel.of(task);
            model.add(linkTo(methodOn(TodolistController.class).findById(task.getId())).withSelfRel());
            model.add(linkTo(methodOn(TodolistController.class).findAll(pageable)).withSelfRel());
            return model;
        });
        return ResponseEntity.ok(tasksWithLinks);
    }

    @GetMapping("/status")
    public ResponseEntity<Page<EntityModel<Todolist>>> findByStatus(@RequestParam TodoStatus status, Pageable pageable) {
        Page<Todolist> tasks = service.findByStatus(status, pageable);
        Page<EntityModel<Todolist>> tasksWithLinks = tasks.map(task ->{
            EntityModel<Todolist> model = EntityModel.of(task);
            model.add(linkTo(methodOn(TodolistController.class).findById(task.getId())).withSelfRel());
            model.add(linkTo(methodOn(TodolistController.class).findAll(pageable)).withSelfRel());
            return model;
        });
        return ResponseEntity.ok(tasksWithLinks);
    }


}
