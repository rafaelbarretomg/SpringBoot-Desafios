package challenges.challenge02_todolist.services;

import challenges.challenge02_todolist.models.Todolist;
import challenges.challenge02_todolist.models.enums.TodoStatus;
import challenges.challenge02_todolist.repositories.TodolistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TodolistServiceIntegrationTest {

    @Autowired
    private TodolistService todolistService;

    @Autowired
    private TodolistRepository todolistRepository;

    private Pageable pageable;
    private Page<Todolist> page;

    @BeforeEach
    void setUp() {
        // Criando um Pageable fictício para os testes
        pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        todolistRepository.deleteAll();
    }

    @Test
    void shouldReturnAllTodolist() {
        Todolist task1 = new Todolist(null, "Tarefa 1", "Descricao", TodoStatus.PENDENTE, null, null);
        Todolist task2 = new Todolist(null, "Tarefa 2", "Descricao 2", TodoStatus.EM_ANDAMENTO, null, null);
        todolistService.insert(task1);
        todolistService.insert(task2);

        PagedModel<EntityModel<Todolist>> result = todolistService.findAll(pageable);

        assertThat(result).hasSize(2);
        assertThat(result).isNotNull();
        result.getContent().forEach(taskModel -> {
            Todolist task = taskModel.getContent();
            if (task.getTitle().equals("Tarefa 1")) {
                assertThat(task.getTitle()).isEqualTo("Tarefa 1");
                assertThat(task.getDescription()).isEqualTo("Descricao");
                assertThat(task.getStatus()).isEqualTo(TodoStatus.PENDENTE);
            } else if (task.getTitle().equals("Tarefa 2")) {
                assertThat(task.getTitle()).isEqualTo("Tarefa 2");
                assertThat(task.getDescription()).isEqualTo("Descricao 2");
                assertThat(task.getStatus()).isEqualTo(TodoStatus.EM_ANDAMENTO);
            }

        });

    }

    @Test
    void shouldFindTodolistById() {
        Todolist newTask = new Todolist(null, "Tarefa 1", "Descricao", TodoStatus.PENDENTE, null, null);

        Todolist savedTask = todolistService.insert(newTask);

        Todolist foundTask = todolistService.findById(savedTask.getId());


        assertThat(foundTask).isNotNull();
        assertThat(foundTask.getId()).isEqualTo(savedTask.getId());
    }

    @Test
    void shouldInsertTodolist() {
        Todolist newTask = new Todolist(null, "Tarefa 1", "Descricao", TodoStatus.PENDENTE, null, null);
        Todolist savedTask = todolistService.insert(newTask);

        assertThat(savedTask).isNotNull();
        assertThat(savedTask.getId()).isNotNull();
        assertThat(savedTask.getTitle()).isEqualTo("Tarefa 1");
        assertThat(savedTask.getDescription()).isEqualTo("Descricao");
        assertThat(savedTask.getStatus()).isEqualTo(TodoStatus.PENDENTE);
    }

    @Test
    void shouldUpdateTodolist() {
        Todolist newTask = new Todolist(null, "Tarefa 1", "Descricao", TodoStatus.PENDENTE, null, null);
        Todolist savedTask = todolistService.insert(newTask);

        Todolist updatedTask = new Todolist(savedTask.getId(), "Tarefa atualizada", "Descricao atualizada", TodoStatus.CONCLUIDA, null, null);
        todolistService.update(savedTask.getId(), updatedTask);

        //Buscar novamente o savedTask atualizado
        Todolist foundTask = todolistService.findById(savedTask.getId());

        assertThat(foundTask.getId()).isNotNull();
        assertThat(foundTask.getId()).isEqualTo(savedTask.getId());
        assertThat(foundTask.getTitle()).isEqualTo("Tarefa atualizada");
        assertThat(foundTask.getDescription()).isEqualTo("Descricao atualizada");
        assertThat(foundTask.getStatus()).isEqualTo(TodoStatus.CONCLUIDA);
    }

    @Test
    void shouldDeleteTodolist() {
        Todolist newTask = new Todolist(null, "Tarefa 1", "Descricao", TodoStatus.PENDENTE, null, null);
        Todolist savedTask = todolistService.insert(newTask);

        //Verifica se a tarefa foi salva no banco
        assertThat(todolistRepository.findById(savedTask.getId())).isPresent();

        todolistService.delete(savedTask.getId());

        assertThat(todolistRepository.findById(savedTask.getId())).isEmpty();
    }
}
