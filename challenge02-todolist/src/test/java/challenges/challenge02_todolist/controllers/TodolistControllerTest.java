package challenges.challenge02_todolist.controllers;

import challenges.challenge02_todolist.models.Todolist;
import challenges.challenge02_todolist.models.enums.TodoStatus;
import challenges.challenge02_todolist.services.TodolistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(TodolistController.class)
public class TodolistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TodolistService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Todolist createTestTask(Long id) {
        return new Todolist(
                id,
                "Tarefa " + id,
                "Descricao da tarefa " + id,
                TodoStatus.PENDENTE,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(5)
        );
    }

    @Test
    void shouldReturnAllTodoLists() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Todolist> tasks = new PageImpl<>(Arrays.asList(createTestTask(1L), createTestTask(2L)));
        when(service.findAll(pageable)).thenReturn(tasks);

        mockMvc.perform(get("/tarefas")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[1].id").value(2));
    }

    @Test
    void shouldReturnTodolistById() throws Exception {
        Todolist task = createTestTask(1L);

        when(service.findById(anyLong())).thenReturn(task);

        mockMvc.perform(get("/tarefas/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Tarefa 1"))
                .andExpect(jsonPath("$.description").value("Descricao da tarefa 1"));
    }

    @Test
    void shouldInsertTodolist() throws Exception {
        Todolist task = createTestTask(1L);

        when(service.insert(any(Todolist.class))).thenReturn(task);

        mockMvc.perform(post("/tarefas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Tarefa 1",
                                    "description": "Descricao da tarefa 1",
                                    "status": "PENDENTE"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1)) // Verifica o ID
                .andExpect(jsonPath("$.title").value("Tarefa 1"))
                .andExpect(jsonPath("$.description").value("Descricao da tarefa 1"))
                .andExpect(jsonPath("$.status").value("PENDENTE"));

        // Verifica se o método insert foi chamado corretamente no mock
        verify(service, times(1)).insert(any(Todolist.class));
    }


    @Test
    void shouldUpdateTodolist() throws Exception {
        Todolist task = createTestTask(1L);

        when(service.update(anyLong(), any(Todolist.class))).thenReturn(task);

        mockMvc.perform(put("/tarefas/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                         {
                                         "title": "Tarefa atualizada",
                                         "description": "Descricao atualizada",
                                         "status": "CONCLUIDA"
                                         }
                                        """
                        ))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Tarefa 1"))
                .andExpect(jsonPath("$.description").value("Descricao da tarefa 1"))
                .andExpect(jsonPath("$.status").value("PENDENTE"));
    }

    @Test
    void shouldDeleteTodolist() throws Exception {
        Mockito.doNothing().when(service).delete(anyLong());

        mockMvc.perform(delete("/tarefas/{id}", 1))
                .andExpect(status().isNoContent());
    }

}
