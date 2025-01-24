package challenges.challenge02_todolist.services;


import challenges.challenge02_todolist.controllers.TodolistController;
import challenges.challenge02_todolist.mappers.ModelMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class TodolistService {
    
    @Autowired
    private TodolistRepository repository;

    @Autowired
    PagedResourcesAssembler<Todolist> assembler;

    public PagedModel<EntityModel<Todolist>> findAll(Pageable pageable){
       Page<Todolist> tasks = repository.findAll(pageable);

        Page<Todolist> listTasks = tasks.map(t -> ModelMapper.parseObject(t, Todolist.class));
        listTasks.map(
                t -> t.add(
                        linkTo(methodOn(TodolistController.class)
                                .findById(t.getId())).withSelfRel()
                )
        );

        Link link = linkTo(
                methodOn(TodolistController.class)
                        .findAll(pageable.getPageNumber(), pageable.getPageSize(), "asc")).withSelfRel();

        return assembler.toModel(listTasks, link);
    }

    public PagedModel<EntityModel<Todolist>> findByTitle(String title, Pageable pageable){
        Page<Todolist> tasks = repository.findByTitleContaining(title, pageable);
        Page<Todolist> listTasks = tasks.map(t -> ModelMapper.parseObject(t, Todolist.class));
        listTasks.map(
                t -> t.add(
                        linkTo(methodOn(TodolistController.class)
                                .findById(t.getId())).withSelfRel()
                )
        );

        Link link = linkTo(
                methodOn(TodolistController.class)
                        .findByTitle(title, pageable.getPageNumber(), pageable.getPageSize(), "asc")).withSelfRel();

        return assembler.toModel(listTasks, link);


    }

    public PagedModel<EntityModel<Todolist>> findByStatus(TodoStatus status, Pageable pageable){
        Page<Todolist> tasks = repository.findByStatus(status, pageable);
        Page<Todolist> listTasks = tasks.map(t -> ModelMapper.parseObject(t, Todolist.class));
        listTasks.map(
                t -> t.add(
                        linkTo(methodOn(TodolistController.class)
                                .findById(t.getId())).withSelfRel()
                )
        );

        Link link = linkTo(
                methodOn(TodolistController.class)
                        .findByStatus( status ,pageable.getPageNumber(), pageable.getPageSize(), "asc")).withSelfRel();

        return assembler.toModel(listTasks, link);

    }

    public Todolist findById(Long id){
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Tarefa nao encontrado"));
    }

    public Todolist insert(Todolist toDoList){
        try{
            if(toDoList.getStatus() == null){
                toDoList.setStatus(TodoStatus.PENDENTE);
            }
            return repository.save(toDoList);
        }catch(Exception e){
            throw new RuntimeException("Erro ao inserir uma tarefa", e);
        }
    }

    public void delete(Long id){
        if(!repository.existsById(id)){
            throw new RuntimeException("Tarefa nao encontrada");
        }
        repository.deleteById(id);
    }
    

    public Todolist update(Long id, Todolist todolist){
        if(!repository.existsById(id)){
            throw new RuntimeException("Tarefa nao encontrada");
        }
        todolist.setId(id);
        return repository.save(todolist);
    }

}
