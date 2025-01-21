package challenges.challenge02_todolist.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import challenges.challenge02_todolist.models.Todolist;
import challenges.challenge02_todolist.models.enums.TodoStatus;
import challenges.challenge02_todolist.repositories.TodolistRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TodolistService {
    
    @Autowired
    private TodolistRepository repository;

    public Page<Todolist> findAll(Pageable pageable){
        Sort sort = Sort.by("title").ascending();
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return repository.findAll(pageable);
    }

    public Page<Todolist> findByTitle(String title, Pageable pageable){
        return repository.findByTitleContaining(title, pageable);
    }

    public Page<Todolist> findByStatus(TodoStatus status, Pageable pageable){
        return repository.findByStatus(status, pageable);
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
