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
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
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
    void shouldReturnAllTodolist() throws Exception {
        Todolist task1 = new Todolist(null, "Tarefa 1", "Descricao", TodoStatus.PENDENTE, null, null);
        Todolist task2 = new Todolist(null, "Tarefa 2", "Descricao", TodoStatus.CONCLUIDA, null, null);


        //Inserindo tarefas no banco
        todolistRepository.save(task1);
        todolistRepository.save(task2);

        //Requisicao GET
        mockMvc.perform(get("/tarefas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())// O status deve ser 200 OK
                .andExpect(jsonPath("$.content.length()").value(2)); // verifica se retorna 2 tarefas

    }

    @Test
    void shouldReturnTodolistFindById() throws Exception{
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
    void shouldInsertTodolistAndReturnCreatedStatus() throws Exception {
        //dados de entrada
        Todolist newTask = new Todolist(null, "Tarefa 1", "Descricao", TodoStatus.PENDENTE, null, null);


        //Enviando a requisicao POST
        mockMvc.perform(
                        post("/tarefas")
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
    void shouldUpdateTodoListAndReturnAcceptedStatus() throws Exception {
        Todolist newTask = new Todolist(null, "Tarefa 1", "Descricao", TodoStatus.PENDENTE, null, null);
        Todolist savedTask = todolistRepository.save(newTask);

        //Requisicao UPDATE
        mockMvc.perform(
                        put("/tarefas/{id}", savedTask.getId())
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
    void shouldDeleteTodolist() throws Exception {

        Todolist newTask = new Todolist(null, "Tarefa 1", "Descricao", TodoStatus.PENDENTE, null, null);

        Todolist savedTask = todolistRepository.save(newTask);

        //Requisicao DELETE
        mockMvc.perform(delete("/tarefas/{id}", savedTask.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        //Verifica se a tarefa foi excluida
        assertThat(todolistRepository.existsById(savedTask.getId())).isFalse();


    }


}
