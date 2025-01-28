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
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(TodolistController.class)
@WithMockUser(username = "user", password = "1", roles = {"USER"})
public class TodolistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TodolistService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);}

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
    void shouldReturnAllTasks() throws Exception {
        // Criando um PagedModel fictício
        PagedModel<EntityModel<Todolist>> mockPagedModel = PagedModel.of(
                List.of(
                        EntityModel.of(createTestTask(1L)),
                        EntityModel.of(createTestTask(2L))
                ),
                new PagedModel.PageMetadata(10, 0, 2)
        );

        // Mockando o comportamento do serviço
        when(service.findAll(any(Pageable.class))).thenReturn(mockPagedModel);

        // Realizando a requisição ao controlador e verificando o resultado
        mockMvc.perform(get("/tarefas")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.todolistList[0].id").value(1))
                .andExpect(jsonPath("$._embedded.todolistList[1].id").value(2));
    }

    @Test
    void shouldReturnTasks_WhenTitleMatches() throws Exception {
        PagedModel<EntityModel<Todolist>> mockPagedModel = PagedModel.of(
                List.of(
                        EntityModel.of(createTestTask(1L)),
                        EntityModel.of(createTestTask(2L))
                ),
                new PagedModel.PageMetadata(10, 0, 2)
        );

        when(service.findByTitle(eq("Tarefa"), any(Pageable.class))).thenReturn(mockPagedModel);

        mockMvc.perform(get("/tarefas/busca?title=Tarefa&size=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.todolistList[0].title").value("Tarefa 1"));
    }

    @Test
    void shouldReturnTask_WhenStatusMatches() throws Exception {
        PagedModel<EntityModel<Todolist>> mockPagedModel = PagedModel.of(
                List.of(
                        EntityModel.of(createTestTask(1L)),
                        EntityModel.of(createTestTask(2L))
                ),
                new PagedModel.PageMetadata(10, 0, 2)
        );

        when(service.findByStatus(eq(TodoStatus.PENDENTE), any(Pageable.class))).thenReturn(mockPagedModel);

        mockMvc.perform(get("/tarefas/status?status=PENDENTE&size=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.todolistList[0].title").value("Tarefa 1"))
                .andExpect(jsonPath("$._embedded.todolistList[1].title").value("Tarefa 2"));
    }


    @Test
    void shouldReturnTaskById() throws Exception {
        Todolist task = createTestTask(1L);

        when(service.findById(anyLong())).thenReturn(task);

        mockMvc.perform(get("/tarefas/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Tarefa 1"))
                .andExpect(jsonPath("$.description").value("Descricao da tarefa 1"));
    }


    @Test
    void shouldInsertTask() throws Exception {
        Todolist task = createTestTask(1L);

        when(service.insert(any(Todolist.class))).thenReturn(task);

        mockMvc.perform(post("/tarefas")
                        .with(csrf())
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

        // Verifica se o metodo insert foi chamado corretamente no mock
        verify(service, times(1)).insert(any(Todolist.class));
    }


    @Test
    void shouldUpdateTask() throws Exception {
        Todolist task = createTestTask(1L);

        when(service.update(anyLong(), any(Todolist.class))).thenReturn(task);

        mockMvc.perform(put("/tarefas/{id}", 1)
                        .with(csrf())
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
    void shouldDeleteTask() throws Exception {
        Mockito.doNothing().when(service).delete(anyLong());

        mockMvc.perform(delete("/tarefas/{id}", 1) .with(csrf()))
                .andExpect(status().isNoContent());
    }

}
