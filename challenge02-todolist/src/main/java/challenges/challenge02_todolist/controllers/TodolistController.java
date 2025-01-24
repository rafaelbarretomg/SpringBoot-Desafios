package challenges.challenge02_todolist.controllers;


import challenges.challenge02_todolist.models.dtos.TodolistRequest;
import challenges.challenge02_todolist.models.Todolist;
import challenges.challenge02_todolist.models.enums.TodoStatus;
import challenges.challenge02_todolist.services.TodolistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.hibernate.query.SortDirection;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
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


@RestController
@RequestMapping("/tarefas")
@Tag(name = "Todolist", description = "Gerenciamente de Tarefas")
public class TodolistController {

    @Autowired
    private TodolistService service;

    @Operation(summary = "Endpoint findAll com paginação")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Todolist>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC: Sort.Direction.ASC;
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
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC: Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "id"));

        return ResponseEntity.ok(service.findByTitle(title,pageable));
    }

    @GetMapping("/status")
    public ResponseEntity<PagedModel<EntityModel<Todolist>>> findByStatus(
            @RequestParam(value = "status") TodoStatus status,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC: Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "id"));

        return ResponseEntity.ok(service.findByStatus(status,pageable));
    }



    @GetMapping("/{id}")
    public ResponseEntity<Todolist> findById(@PathVariable Long id) {
        Todolist task = service.findById(id);
      
        EntityModel<Todolist> model = EntityModel.of(task);
        model.add(linkTo(methodOn(TodolistController.class).findById(task.getId())).withSelfRel());
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

        EntityModel<Todolist> model = EntityModel.of(savedTask);
        model.add(linkTo(methodOn(TodolistController.class).findById(savedTask.getId())).withSelfRel());
        return ResponseEntity.status(HttpStatus.CREATED).body(model);

    }

    @Operation(summary = "Atualiza uma tarefa existente", description = "Atualiza os detalhes de uma tarefa")
    @PutMapping(value = "/{id}")
    public ResponseEntity<Todolist> update(@PathVariable Long id, @RequestBody @Valid TodolistRequest request, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        Todolist updatedTask = service.update(id, toDoList);
        EntityModel<Todolist> model = EntityModel.of(updatedTask);
        model.add(linkTo(methodOn(TodolistController.class).findById(updatedTask.getId())).withSelfRel());


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

  
}
