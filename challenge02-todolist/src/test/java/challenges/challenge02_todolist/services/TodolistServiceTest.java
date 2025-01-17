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
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    private Todolist createTestTask(Long id) {
        return new Todolist(id, "Tarefa", "Iniciando Tarefa Teste "+ id, TodoStatus.PENDENTE,
                LocalDateTime.of(2023, 1, 15, 10, 0),
                LocalDateTime.of(2023, 1, 20, 18, 0));
    }


    @Test
    void findAll_WhenDataExists_ShouldReturnPageWithTasks() {

        List<Todolist> tasks = Arrays.asList(createTestTask(1L), createTestTask(2L));

        Pageable pageable = PageRequest.of(0, 5, Sort.by("title").ascending());
        Page<Todolist> pageMock = new PageImpl<>(tasks, pageable, tasks.size());

        when(repository.findAll(pageable)).thenReturn(pageMock);

        Page<Todolist> result = service.findAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());

        assertEquals(tasks.get(0).getTitle(), result.getContent().get(0).getTitle());
        assertEquals(tasks.get(0).getDescription(), result.getContent().get(0).getDescription());
        assertEquals(tasks.get(0).getStatus(), result.getContent().get(0).getStatus());
        assertEquals(tasks.get(0).getCreationDate(), result.getContent().get(0).getCreationDate());
        assertEquals(tasks.get(0).getConclusionDate(), result.getContent().get(0).getConclusionDate());

        assertEquals(tasks.get(1).getTitle(), result.getContent().get(1).getTitle());
        assertEquals(tasks.get(1).getDescription(), result.getContent().get(1).getDescription());
        assertEquals(tasks.get(1).getStatus(), result.getContent().get(1).getStatus());
        assertEquals(tasks.get(1).getCreationDate(), result.getContent().get(1).getCreationDate());
        assertEquals(tasks.get(1).getConclusionDate(), result.getContent().get(1).getConclusionDate());

        verify(repository, times(1)).findAll(pageable);

    }


    @Test
    void findAll_WhenNoDataExists_ShouldReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by("title").ascending());

        when(repository.findAll(pageable)).thenReturn(Page.empty(pageable));

        Page<Todolist> result = service.findAll(pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());

        verify(repository, times(1)).findAll(pageable);
    }


    @Test
    void findById_WhenIdExists_ShouldReturnCorrectTask() {
        Todolist task = createTestTask(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(task));

        Todolist result = service.findById(1L);

        assertNotNull(result);
        assertEquals(task.getTitle(), result.getTitle());
        assertEquals(task.getDescription(), result.getDescription());
        assertEquals(task.getStatus(), result.getStatus());
        assertEquals(task.getCreationDate(), result.getCreationDate());
        assertEquals(task.getConclusionDate(), result.getConclusionDate());
        verify(repository, times(1)).findById(1L);
    }


    @Test
    void findById_WhenIdDoesNotExist_ShouldThrowException() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        Exception excecao = assertThrows(RuntimeException.class, () -> {
            service.findById(id);
        });

        assertEquals("Tarefa nao encontrado", excecao.getMessage());
        verify(repository, times(1)).findById(id);
    }

    @Test
    void saveTest(){
        Todolist task = createTestTask(1L);

        when(repository.save(task)).thenReturn(task);

        Todolist result = service.insert(task);


        assertNotNull(result);
        assertEquals(task.getTitle(), result.getTitle());
        assertEquals(task.getDescription(), result.getDescription());
        assertEquals(task.getStatus(), result.getStatus());
        assertEquals(task.getCreationDate(), result.getCreationDate());
        assertEquals(task.getConclusionDate(), result.getConclusionDate());

        verify(repository, times(1)).save(createTestTask(1L));
    }

    @Test
    void saveTest_throwsExcepiton(){
        Todolist task = createTestTask(1L);
        when(repository.save(task)).thenThrow(new RuntimeException("Erro ao inserir uma tarefa"));

        RuntimeException exception = assertThrows(RuntimeException.class, ()->{
            service.insert(task);
        });

        assertEquals("Erro ao inserir uma tarefa", exception.getMessage());
    }

    @Test
    void updateTest(){
        Long taskId = 1L;

        //Todolist originalTask = createTestTask(taskId);

        Todolist updatedTask = createTestTask(1L);
        updatedTask.setTitle("Tarefa test atualizada");
        updatedTask.setDescription("Atualizando tarefa teste");
        updatedTask.setStatus(TodoStatus.EM_ANDAMENTO);
        updatedTask.setCreationDate(LocalDateTime.now());
        updatedTask.setConclusionDate(LocalDateTime.now().plusDays(1));

        when(repository.existsById(taskId)).thenReturn(true);
        when(repository.save(updatedTask)).thenReturn(updatedTask);

        Todolist result = service.update(taskId, updatedTask);

        assertNotNull(result);
        assertEquals(updatedTask.getTitle(), result.getTitle());
        assertEquals(updatedTask.getDescription(), result.getDescription());
        assertEquals(updatedTask.getStatus(), result.getStatus());
        assertEquals(updatedTask.getCreationDate(), result.getCreationDate());
        assertEquals(updatedTask.getConclusionDate(), result.getConclusionDate());

        verify(repository, times(1)).existsById(taskId);
        verify(repository, times(1)).save(updatedTask);
    }

    
    @Test
    void updateTest_TaskNotFound(){
        Long taskId = 1L;

        Todolist updatedTask = createTestTask(1L);
        updatedTask.setTitle("Tarefa test atualizada");
        updatedTask.setDescription("Atualizando tarefa teste");
        updatedTask.setStatus(TodoStatus.EM_ANDAMENTO);
        updatedTask.setCreationDate(LocalDateTime.now());
        updatedTask.setConclusionDate(LocalDateTime.now().plusDays(1));

        when(repository.existsById(taskId)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, ()->{
            service.update(taskId, updatedTask);
        });

        assertEquals("Tarefa nao encontrada", exception.getMessage());

        verify(repository, times(0)).save(updatedTask);
        verify(repository, times(1)).existsById(taskId);

    }

    @Test
    void deleteTest(){
        Long taskId = 1L;

        when(repository.existsById(taskId)).thenReturn(true);

        service.delete(taskId);

        verify(repository, times(1)).deleteById(taskId);

        verify(repository, times(1)).existsById(taskId);
    }

    @Test
    void deleteTest_TaskNotFound(){
        Long taskId = 1L;

        when(repository.existsById(taskId)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, ()->{
            service.delete(taskId);
        });

        assertEquals("Tarefa nao encontrada", exception.getMessage());

        verify(repository, times(0)).deleteById(taskId);
        verify(repository, times(1)).existsById(taskId);

    }

}
