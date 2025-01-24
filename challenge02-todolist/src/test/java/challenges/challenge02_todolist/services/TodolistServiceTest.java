package challenges.challenge02_todolist.services;

import challenges.challenge02_todolist.models.Todolist;
import challenges.challenge02_todolist.models.enums.TodoStatus;
import challenges.challenge02_todolist.repositories.TodolistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TodolistServiceTest {

    @Mock
    private TodolistRepository repository;

    @Mock
    PagedResourcesAssembler<Todolist> assembler;

    @InjectMocks
    private TodolistService service;

    private Pageable pageable;
    private Page<Todolist> page;


    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // Criando um Pageable fictício para os testes
        pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        page = new PageImpl<>(List.of(createTestTask(1L), createTestTask(2L)), pageable, 2);
    }

    private Todolist createTestTask(Long id) {
        return new Todolist(id, "Tarefa " + id, "Iniciando Tarefa Teste " + id, TodoStatus.PENDENTE,
                LocalDateTime.of(2023, 1, 15, 10, 0),
                LocalDateTime.of(2023, 1, 20, 18, 0));
    }


    @Test
    public void findAll_WhenDataExists_ShouldReturnPageWithTasks() {
        // Mockando o comportamento do repositório
        when(repository.findAll(pageable)).thenReturn(page);

        // Mockando o comportamento do PagedResourcesAssembler
        PagedModel<EntityModel<Todolist>> mockPagedModel = mock(PagedModel.class);
        when(assembler.toModel(any(Page.class), any(Link.class))).thenReturn(mockPagedModel);

        // Aqui estamos garantindo que o mockPagedModel tenha conteúdo
        when(mockPagedModel.getContent()).thenReturn(List.of(
                EntityModel.of(createTestTask(1L)),
                EntityModel.of(createTestTask(2L))
        ));

        //Chamando o metodo do serviço
        PagedModel<EntityModel<Todolist>> result = service.findAll(pageable);

        // Verificando se o repositório foi chamado corretamente
        verify(repository, times(1)).findAll(pageable);

        // Verificando se o metodo do assembler foi chamado corretamente
        verify(assembler, times(1)).toModel(any(Page.class), any(Link.class));

        // Verificando se o retorno não é nulo
        assertNotNull(result);

        // Verificando o conteúdo das tarefas retornadas
        assertTrue(result.getContent().size() > 0); // Verifica se há tarefas na lista

        // Verificando os campos das tarefas
        result.getContent().forEach(taskModel -> {
            Todolist task = taskModel.getContent();
            if (task.getTitle().equals("Tarefa 1")) {
                assertEquals("Tarefa 1", task.getTitle());
                assertEquals("Iniciando Tarefa Teste 1", task.getDescription());
                assertEquals(TodoStatus.PENDENTE, task.getStatus());
            } else if (task.getTitle().equals("Tarefa 2")) {
                assertEquals("Tarefa 2", task.getTitle());
                assertEquals("Iniciando Tarefa Teste 2", task.getDescription());
                assertEquals(TodoStatus.PENDENTE, task.getStatus());
            }
        });
    }


    @Test
    void findAll_WhenNoDataExists_ShouldReturnEmptyPage() {
        when(repository.findAll(pageable)).thenReturn(Page.empty(pageable));

        PagedModel<EntityModel<Todolist>> mockPagedModel = mock(PagedModel.class);
        when(assembler.toModel(any(Page.class), any(Link.class))).thenReturn(mockPagedModel);

        PagedModel<EntityModel<Todolist>> result = service.findAll(pageable);

        verify(repository, times(1)).findAll(pageable);

        verify(assembler, times(1)).toModel(any(Page.class), any(Link.class));

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getContent().size());

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
    void saveTest() {
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
    void saveTest_throwsExcepiton() {
        Todolist task = createTestTask(1L);
        when(repository.save(task)).thenThrow(new RuntimeException("Erro ao inserir uma tarefa"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.insert(task);
        });

        assertEquals("Erro ao inserir uma tarefa", exception.getMessage());
    }

    @Test
    void updateTest() {
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
    void updateTest_TaskNotFound() {
        Long taskId = 1L;

        Todolist updatedTask = createTestTask(1L);
        updatedTask.setTitle("Tarefa test atualizada");
        updatedTask.setDescription("Atualizando tarefa teste");
        updatedTask.setStatus(TodoStatus.EM_ANDAMENTO);
        updatedTask.setCreationDate(LocalDateTime.now());
        updatedTask.setConclusionDate(LocalDateTime.now().plusDays(1));

        when(repository.existsById(taskId)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.update(taskId, updatedTask);
        });

        assertEquals("Tarefa nao encontrada", exception.getMessage());

        verify(repository, times(0)).save(updatedTask);
        verify(repository, times(1)).existsById(taskId);

    }

    @Test
    void deleteTest() {
        Long taskId = 1L;

        when(repository.existsById(taskId)).thenReturn(true);

        service.delete(taskId);

        verify(repository, times(1)).deleteById(taskId);

        verify(repository, times(1)).existsById(taskId);
    }

    @Test
    void deleteTest_TaskNotFound() {
        Long taskId = 1L;

        when(repository.existsById(taskId)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.delete(taskId);
        });

        assertEquals("Tarefa nao encontrada", exception.getMessage());

        verify(repository, times(0)).deleteById(taskId);
        verify(repository, times(1)).existsById(taskId);

    }

}
