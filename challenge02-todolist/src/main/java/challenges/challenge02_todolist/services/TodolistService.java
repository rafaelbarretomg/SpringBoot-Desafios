package challenges.challenge02_todolist.services;

import challenges.challenge02_todolist.controllers.TodolistController;
import challenges.challenge02_todolist.models.Todolist;
import challenges.challenge02_todolist.models.enums.TodoStatus;
import challenges.challenge02_todolist.repositories.TodolistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class TodolistService {
    
    @Autowired
    private TodolistRepository repository;

    public List<Todolist> findAll(){
        List<Todolist> tasks = repository.findAll();

        tasks.forEach(t -> t.add(linkTo(methodOn(TodolistController.class).findById(t.getId())).withSelfRel()));
        return tasks;
    }

    public List<Todolist> findByTitle(String title){
        List<Todolist> tasks =  repository.findByTitleContaining(title);
        tasks.forEach(t -> t.add(linkTo(methodOn(TodolistController.class).findById(t.getId())).withSelfRel()));
        return tasks;
    }

    public List<Todolist> findByStatus(TodoStatus status  ){
        List<Todolist> tasks =  repository.findByStatus(status);
        tasks.forEach(t -> t.add(linkTo(methodOn(TodolistController.class).findById(t.getId())).withSelfRel()));
        return tasks;
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
