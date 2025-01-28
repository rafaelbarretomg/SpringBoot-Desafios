package challenges.challenge02_todolist.controllers;

import challenges.challenge02_todolist.models.Todolist;
import challenges.challenge02_todolist.models.enums.TodoStatus;
import challenges.challenge02_todolist.repositories.TodolistRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "user", password = "1", roles = {"USER"})
public class TodolistControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TodolistRepository todolistRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        //limpar base de dados antes de cada teste
        todolistRepository.deleteAll();
    }


    @Test
    void shouldReturnAllTasks() throws Exception {
        Todolist task1 = new Todolist(null, "Tarefa 1", "Descricao", TodoStatus.PENDENTE, null, null);
        Todolist task2 = new Todolist(null, "Tarefa 2", "Descricao", TodoStatus.CONCLUIDA, null, null);


        //Inserindo tarefas no banco
        todolistRepository.save(task1);
        todolistRepository.save(task2);

        //Requisicao GET
        mockMvc.perform(get("/tarefas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())// O status deve ser 200 OK
                .andExpect(jsonPath("$._embedded.todolistList[0].title").value("Tarefa 1"))
                .andExpect(jsonPath("$._embedded.todolistList[1].title").value("Tarefa 2"));

    }

    @Test
    void shouldReturnTasks_WhenTitleMatches() throws Exception {
        Todolist task1 = new Todolist(null, "Tarefa 1", "Descricao", TodoStatus.PENDENTE, null, null);
        Todolist task2 = new Todolist(null, "Acordar cedo", "Acordando", TodoStatus.EM_ANDAMENTO, null, null);

        todolistRepository.save(task1);
        todolistRepository.save(task2);

        mockMvc.perform(get("/tarefas/busca?title=Tarefa&Size=5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.todolistList[0].title").value("Tarefa 1"));


        mockMvc.perform(get("/tarefas/busca?title=Acordar&Size=5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.todolistList[0].title").value("Acordar cedo"));

    }

    @Test
    void shouldReturnTasks_WhenStatusMatches() throws Exception {
        Todolist task1 = new Todolist(null, "Tarefa 1", "Descricao", TodoStatus.PENDENTE, null, null);
        Todolist task2 = new Todolist(null, "Acordar cedo", "Acordando", TodoStatus.EM_ANDAMENTO, null, null);

        todolistRepository.save(task1);
        todolistRepository.save(task2);


        mockMvc.perform(get("/tarefas/status?status=PENDENTE&size=3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.todolistList[0].title").value("Tarefa 1"))
                .andExpect(jsonPath("$._embedded.todolistList[0].description").value("Descricao"))
                .andExpect(jsonPath("$._embedded.todolistList[0].status").value("PENDENTE"));

        mockMvc.perform(get("/tarefas/status?status=EM_ANDAMENTO&size=3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.todolistList[0].description").value("Acordando"));
    }

    @Test
    void shouldReturnTaskFindById() throws Exception {
        Todolist newTask = new Todolist(null, "Tarefa 1", "Descricao", TodoStatus.PENDENTE, null, null);
        Todolist savedTask = todolistRepository.save(newTask);

        //Requisicao GET findByID
        mockMvc.perform(get("/tarefas/{id}", savedTask.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())// O status deve ser 200 OK
                .andExpect(
                        result -> {
                            Todolist task = objectMapper.readValue(result.getResponse().getContentAsString(), Todolist.class);
                            assertThat(task.getId()).isNotNull();
                            assertThat(task.getId()).isEqualTo(savedTask.getId());
                            assertThat(task.getTitle()).isEqualTo("Tarefa 1");
                            assertThat(task.getDescription()).isEqualTo("Descricao");
                            assertThat(task.getStatus()).isEqualTo(TodoStatus.PENDENTE);
                        }
                );
    }

    @Test
    void shouldInsertTaskAndReturnCreatedStatus() throws Exception {
        //dados de entrada
        Todolist newTask = new Todolist(null, "Tarefa 1", "Descricao", TodoStatus.PENDENTE, null, null);


        //Enviando a requisicao POST
        mockMvc.perform(
                        post("/tarefas")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(newTask))) // Converte o objeto para JSON
                .andExpect(status().isCreated()) // verifica se o status da resposta eh 201
                .andExpect(result -> {
                    //verifica se o ID da tarefa foi gerado
                    Todolist createdTask = objectMapper.readValue(result.getResponse().getContentAsString(), Todolist.class);
                    assertThat(createdTask.getId()).isNotNull();
                    assertThat(createdTask.getTitle()).isEqualTo("Tarefa 1");
                    assertThat(createdTask.getDescription()).isEqualTo("Descricao");
                    assertThat(createdTask.getStatus()).isEqualTo(TodoStatus.PENDENTE);

                });
    }

    @Test
    void shouldUpdateTaskAndReturnAcceptedStatus() throws Exception {
        Todolist newTask = new Todolist(null, "Tarefa 1", "Descricao", TodoStatus.PENDENTE, null, null);
        Todolist savedTask = todolistRepository.save(newTask);

        //Requisicao UPDATE
        mockMvc.perform(
                        put("/tarefas/{id}", savedTask.getId())
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
                                )
                )
                .andExpect(status().isAccepted())//verifica se retournou 202
                .andExpect(result -> {
                    //verifica se foi atualizado
                    Todolist updatedTask = objectMapper.readValue(result.getResponse().getContentAsString(), Todolist.class);
                    assertThat(updatedTask.getId()).isNotNull();
                    assertThat(updatedTask.getTitle()).isEqualTo("Tarefa atualizada");
                    assertThat(updatedTask.getDescription()).isEqualTo("Descricao atualizada");
                    assertThat(updatedTask.getStatus()).isEqualTo(TodoStatus.CONCLUIDA);

                });


    }

    @Test
    void shouldDeleteTask() throws Exception {

        Todolist newTask = new Todolist(null, "Tarefa 1", "Descricao", TodoStatus.PENDENTE, null, null);

        Todolist savedTask = todolistRepository.save(newTask);

        //Requisicao DELETE
        mockMvc.perform(delete("/tarefas/{id}", savedTask.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        //Verifica se a tarefa foi excluida
        assertThat(todolistRepository.existsById(savedTask.getId())).isFalse();


    }


}
