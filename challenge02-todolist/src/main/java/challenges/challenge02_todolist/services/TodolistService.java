package challenges.challenge02_todolist.services;

import challenges.challenge02_todolist.assemblers.TodolistAssembler;
import challenges.challenge02_todolist.controllers.TodolistController;
import challenges.challenge02_todolist.models.Todolist;
import challenges.challenge02_todolist.models.enums.TodoStatus;
import challenges.challenge02_todolist.repositories.TodolistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

    @Autowired
    private TodolistAssembler todolistAssembler;

    public PagedModel<Todolist> findAll(Pageable pageable){
        if(pageable == null){
            pageable = PageRequest.of(0, 10, Sort.by("title").ascending());
        }
        Page<Todolist> page = repository.findAll(pageable);

        //Converte o page<Todolist> para PagedModel<Todolist>
        return todolistAssembler.toPagedModel(page);
    }

    public PagedModel<Todolist> findByTitle(String title, Pageable pageable){
        Page<Todolist> page =  repository.findByTitleContaining(title, pageable);
        return todolistAssembler.toPagedModel(page);
    }

    public PagedModel<Todolist> findByStatus(TodoStatus status, Pageable pageable ){
        Page<Todolist> page =  repository.findByStatus(status, pageable);
        return todolistAssembler.toPagedModel(page);
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
