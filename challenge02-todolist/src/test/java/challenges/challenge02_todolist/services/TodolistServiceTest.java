package challenges.challenge02_todolist.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import challenges.challenge02_todolist.models.Todolist;
import challenges.challenge02_todolist.models.enums.TodoStatus;
import challenges.challenge02_todolist.repositories.TodolistRepository;

public class TodolistServiceTest {

    @Mock
    private TodolistRepository repository;

    @InjectMocks
    private TodolistService service;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void findAll_WhenDataExists_ShouldReturnPageWithTasks(){
        TodoStatus status = TodoStatus.PENDENTE;
        LocalDateTime creationDate = LocalDateTime.of(2023,1,15,10,0);
        LocalDateTime conclusionDate = LocalDateTime.of(2023,1,20,18,0);

        Todolist todolist1 = new Todolist(1L, "Tarefa Teste 1", "Iniciando Tarefa Teste 1", status, creationDate, conclusionDate);
        Todolist todolist2= new Todolist(2L, "Tarefa Teste 2", "Iniciando Tarefa Teste 2", status, creationDate, conclusionDate);
        List<Todolist> tasks = Arrays.asList(todolist1, todolist2);

        Pageable pageable = PageRequest.of(0,5, Sort.by("title").ascending());
        Page<Todolist> pageMock = new PageImpl<>(tasks, pageable, tasks.size());

        when (repository.findAll(pageable)).thenReturn(pageMock);

        Page<Todolist> result = service.findAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());

        assertEquals("Tarefa Teste 1", result.getContent().get(0).getTitle());
        assertEquals("Iniciando Tarefa Teste 1", result.getContent().get(0).getDescription());
        assertEquals(status, result.getContent().get(0).getStatus());
        assertEquals(creationDate, result.getContent().get(0).getCreationDate());
        assertEquals(conclusionDate, result.getContent().get(0).getConclusionDate());

        assertEquals("Tarefa Teste 2", result.getContent().get(1).getTitle());
        assertEquals("Iniciando Tarefa Teste 2", result.getContent().get(1).getDescription());
        assertEquals(status, result.getContent().get(1).getStatus());
        assertEquals(creationDate, result.getContent().get(1).getCreationDate());
        assertEquals(conclusionDate, result.getContent().get(1).getConclusionDate());

        verify(repository, times(1)).findAll(pageable);

    }


    @Test
    void findAll_WhenNoDataExists_ShouldReturnEmptyPage(){
        Pageable pageable = PageRequest.of(0, 5, Sort.by("title").ascending());

        when(repository.findAll(pageable)).thenReturn(Page.empty(pageable));

        Page<Todolist> result = service.findAll(pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0,result.getTotalElements());
        
        verify(repository, times(1)).findAll(pageable);
    }


    @Test
    void findById_WhenIdExists_ShouldReturnCorrectTask(){
        TodoStatus status = TodoStatus.PENDENTE;
        LocalDateTime creationDate = LocalDateTime.of(2023,1,15,10,0);
        LocalDateTime conclusionDate = LocalDateTime.of(2023,1,20,18,0);


        Todolist todolist = new Todolist(1L, "Tarefa Teste", "Iniciando Tarefa Teste", status, creationDate, conclusionDate);
        when(repository.findById(1L)).thenReturn(Optional.of(todolist));

        Todolist result = service.findById(1L);

        assertNotNull(result);
        assertEquals("Tarefa Teste", result.getTitle());
        assertEquals("Iniciando Tarefa Teste", result.getDescription());
        assertEquals(status, result.getStatus());
        assertEquals(creationDate, result.getCreationDate());
        assertEquals(conclusionDate, result.getConclusionDate());
        verify(repository, times(1)).findById(1L);
    }


    @Test
    void findById_WhenIdDoesNotExist_ShouldThrowException(){
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        Exception excecao = assertThrows(RuntimeException.class, ()->{
            service.findById(id);
        });

        assertEquals("Tarefa nao encontrado", excecao.getMessage());
        verify(repository, times(1)).findById(id);
    }


}
