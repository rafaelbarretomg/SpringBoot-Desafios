package challenges.challenge02_todolist.controllers;


import challenges.challenge02_todolist.models.Todolist;
import challenges.challenge02_todolist.models.enums.TodoStatus;
import challenges.challenge02_todolist.services.TodolistService;
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

    @PostMapping
    public ResponseEntity<EntityModel<Todolist>> insert(@RequestBody @Valid Todolist toDoList, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }
        Todolist savedTask = service.insert(toDoList);
        EntityModel<Todolist> model = EntityModel.of(savedTask);
        model.add(linkTo(methodOn(TodolistController.class).findById(savedTask.getId())).withSelfRel());
        model.add(linkTo(methodOn(TodolistController.class).findAll(Pageable.unpaged())).withSelfRel());
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<EntityModel<Todolist>> update(@PathVariable Long id, @RequestBody @Valid Todolist toDoList, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }
        Todolist updatedTask = service.update(id, toDoList);
        EntityModel<Todolist> model = EntityModel.of(updatedTask);
        model.add(linkTo(methodOn(TodolistController.class).findById(updatedTask.getId())).withSelfRel());
        model.add(linkTo(methodOn(TodolistController.class).findAll(Pageable.unpaged())).withSelfRel());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(model);
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
