package challenges.challenge02_todolist.repositories;

import challenges.challenge02_todolist.models.Todolist;
import challenges.challenge02_todolist.models.enums.TodoStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TodolistRepositoryIntegrationTest {

    @Autowired
    private TodolistRepository todolistRepository;

    @BeforeEach
    void setUp() {
        todolistRepository.deleteAll();
    }

    @Test
    void shouldReturnAllTasks() {
        Todolist newTask = new Todolist(null, "Tarefa 1", "Descricao", TodoStatus.CONCLUIDA, null, null);
        Todolist newTask2 = new Todolist(null, "Tarefa 3", "Descricao 3", TodoStatus.PENDENTE, null, null);
        todolistRepository.saveAll(Arrays.asList(newTask, newTask2));

        //busca pelo status
        Pageable pageable = PageRequest.of(0, 10, Sort.by("title").ascending());
        Page<Todolist> tasks = todolistRepository.findAll(pageable);

        //verificacoes
        assertThat(tasks.getContent()).hasSize(2);
        assertThat(tasks.getContent().get(0).getStatus()).isEqualTo(TodoStatus.CONCLUIDA);
        assertThat(tasks.getContent().get(1).getTitle()).isEqualTo("Tarefa 3");
    }

    @Test
    void shouldReturnTasks_WhenStatusMatches() {
        Todolist newTask = new Todolist(null, "Tarefa 1", "Descricao", TodoStatus.CONCLUIDA, null, null);
        Todolist newTask2 = new Todolist(null, "Tarefa 3", "Descricao 3", TodoStatus.PENDENTE, null, null);
        todolistRepository.saveAll(Arrays.asList(newTask, newTask2));

        //busca pelo status
        Pageable pageable = PageRequest.of(0, 10, Sort.by("title").ascending());
        Page<Todolist> tasks = todolistRepository.findByStatus(TodoStatus.CONCLUIDA, pageable);

        //verificacoes
        assertThat(tasks.getContent()).hasSize(1);
        assertThat(tasks.getContent().get(0).getStatus()).isEqualTo(TodoStatus.CONCLUIDA);
        assertThat(tasks.getContent().get(0).getTitle()).isEqualTo("Tarefa 1");

    }

    @Test
    void shouldReturnTasks_WhenTitleMatches() {
        Todolist newTask = new Todolist(null, "Tarefa 1", "Descricao", TodoStatus.CONCLUIDA, null, null);
        Todolist newTask2 = new Todolist(null, "Tarefa 3", "Descricao 3", TodoStatus.PENDENTE, null, null);
        todolistRepository.saveAll(Arrays.asList(newTask, newTask2));

        Pageable pageable = PageRequest.of(0, 10, Sort.by("title").ascending());
        Page<Todolist> tasks = todolistRepository.findByTitleContaining("Tarefa", pageable);

        //valida os resultados
        assertThat(tasks.getContent()).hasSize(2);
        assertThat(tasks.getContent().get(0).getTitle()).contains("Tarefa 1");
        assertThat(tasks.getContent().get(1).getDescription()).contains("Descricao 3");
        assertThat(tasks.getContent().get(1).getStatus()).isEqualTo(TodoStatus.PENDENTE);
        assertThat(tasks.getContent().get(0).getStatus()).isEqualTo(TodoStatus.CONCLUIDA);

    }

    @Test
    void shouldReturnEmptyPageWhenNoTasksMatch() {
        //Cenário: Banco está vazio
        Pageable pageable = PageRequest.of(0, 10, Sort.by("title").ascending());


        //Execucao: Busca por titulo inexistente
        Page<Todolist> result = todolistRepository.findByTitleContaining("Inexistente", pageable);

        //Verificaçao
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void shouldHandlePaginationcorrectly() {
        //Cenário: Salva várias tarefas
        for (int i = 1; i <= 10; i++) {
            todolistRepository.save(new Todolist(null, "Tarefa " + i, "Descricao " + i, TodoStatus.PENDENTE, null, null));
        }

        Pageable pageable = PageRequest.of(0, 5, Sort.by("title").ascending());

        //Execução: Pagina 1 com 5 elementos por página
        Page<Todolist> page = todolistRepository.findByStatus(TodoStatus.PENDENTE, pageable);


        //Verificação
        assertThat(page).isNotNull();
        assertThat(page.getContent()).hasSize(5);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.getTotalElements()).isEqualTo(10);
        List<Todolist> allTasks = todolistRepository.findAll();
        assertThat(allTasks).hasSize(10);
        assertThat(page.getContent().get(0).getTitle()).isEqualTo("Tarefa 1");
        assertThat(page.getContent().get(4).getTitle()).isEqualTo("Tarefa 4");

    }

    @Test
    void shouldFindTaskById() {
        Todolist newTask = new Todolist(null, "Tarefa 1", "Descricao", TodoStatus.PENDENTE, null, null);
        Todolist savedTask = todolistRepository.save(newTask);
        Todolist foundTask = todolistRepository.findById(savedTask.getId()).orElse(null);

        //verificacoes
        assertThat(foundTask).isNotNull();
        assertThat(foundTask.getId()).isEqualTo(savedTask.getId());
        assertThat(foundTask.getTitle()).isEqualTo("Tarefa 1");
        assertThat(foundTask.getDescription()).isEqualTo("Descricao");
        assertThat(foundTask.getStatus()).isEqualTo(TodoStatus.PENDENTE);
    }

    @Test
    void shouldReturnEmptyOptionalForNonExistingId() {
        Todolist task = todolistRepository.findById(999L).orElse(null);
        assertThat(task).isNull();
    }
}
