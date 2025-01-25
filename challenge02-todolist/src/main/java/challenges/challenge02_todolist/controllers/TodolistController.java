package challenges.challenge02_todolist.controllers;


import challenges.challenge02_todolist.models.Todolist;
import challenges.challenge02_todolist.models.enums.TodoStatus;
import challenges.challenge02_todolist.services.TodolistService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequestMapping("/tarefas")
public class TodolistController {

    @Autowired
    private TodolistService service;

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Todolist>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "id"));
        return ResponseEntity.ok(service.findAll(pageable));

    }


    @GetMapping("/busca")
    public ResponseEntity<PagedModel<EntityModel<Todolist>>> findByTitle(
            @RequestParam(value = "title") String title,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction

    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "id"));

        return ResponseEntity.ok(service.findByTitle(title, pageable));
    }

    @GetMapping("/status")
    public ResponseEntity<PagedModel<EntityModel<Todolist>>> findByStatus(
            @RequestParam(value = "status") TodoStatus status,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "id"));

        return ResponseEntity.ok(service.findByStatus(status, pageable));
    }


    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Todolist>> findById(@PathVariable Long id) {
        Todolist task = service.findById(id);
        EntityModel<Todolist> model = EntityModel.of(task);
        model.add(linkTo(methodOn(TodolistController.class).findById(task.getId())).withSelfRel());
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

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(model);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }


}
