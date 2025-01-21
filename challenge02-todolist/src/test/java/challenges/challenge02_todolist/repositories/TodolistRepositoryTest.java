package challenges.challenge02_todolist.repositories;

import challenges.challenge02_todolist.models.Todolist;
import challenges.challenge02_todolist.models.enums.TodoStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.*;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class TodolistRepositoryTest {

    @Mock
    private TodolistRepository todolistRepository;

    @BeforeEach
    void setUp(){
        todolistRepository.deleteAll();
    }

    @Test
    void shouldFindByTitleContaining(){
        //Mock de dados
        List<Todolist> mockTasks = List.of(new Todolist(1L, "Comprar pao", "Ir a padaria", TodoStatus.PENDENTE, null, null));
        Pageable pageable = PageRequest.of(0, 10, Sort.by("title").ascending());

        when(todolistRepository.findByTitleContaining("pao", pageable)).thenReturn(new PageImpl<>(mockTasks, pageable, mockTasks.size()));

        //Execucao
        Page<Todolist> result = todolistRepository.findByTitleContaining("pao", pageable);

        //verificacao
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Comprar pao");

        //garantir que o mock foi chamado
        verify(todolistRepository).findByTitleContaining("pao", pageable);
    }

    @Test
    void shouldFindByStatus(){
        //Mock de dados
        List<Todolist> mockTasks = List.of(new Todolist(1L, "Comprar pao", "Ir a padaria", TodoStatus.PENDENTE, null, null));
        Pageable pageable = PageRequest.of(0, 10, Sort.by("title").ascending());

        when(todolistRepository.findByStatus(TodoStatus.PENDENTE, pageable)).thenReturn(new PageImpl<>(mockTasks, pageable, mockTasks.size()));

        //Execucao
        Page<Todolist> result = todolistRepository.findByStatus(TodoStatus.PENDENTE, pageable);

        //verificacao
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getStatus()).isEqualTo(TodoStatus.PENDENTE);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Comprar pao");

        //garantir que o mock foi chamado
        verify(todolistRepository).findByStatus(TodoStatus.PENDENTE, pageable);
    }

}
