package challenges.challenge02_todolist.repositories;

import challenges.challenge02_todolist.models.Todolist;
import challenges.challenge02_todolist.models.enums.TodoStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TodolistRepositoryTest {

    @Mock
    private TodolistRepository todolistRepository;


    @Test
    void shouldFindTasksByPartialTitle() {
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
        assertThat(result.getContent().get(0).getDescription()).isEqualTo("Ir a padaria");
        assertThat(result.getContent().get(0).getStatus()).isEqualTo(TodoStatus.PENDENTE);

        //garantir que o mock foi chamado
        verify(todolistRepository).findByTitleContaining("pao", pageable);
    }

    @Test
    void shouldReturnTasksByStatus() {
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
        assertThat(result.getContent().get(0).getDescription()).isEqualTo("Ir a padaria");
        assertThat(result.getContent().get(0).getStatus()).isEqualTo(TodoStatus.PENDENTE);

        //garantir que o mock foi chamado
        verify(todolistRepository).findByStatus(TodoStatus.PENDENTE, pageable);
    }

    @Test
    void shouldReturnEmptyPageWhenNoTasksMatch() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("title").ascending());
        when(todolistRepository.findByTitleContaining("Inexistente", pageable)).thenReturn(Page.empty(pageable));

        Page<Todolist> result = todolistRepository.findByTitleContaining("Inexistente", pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        verify(todolistRepository).findByTitleContaining("Inexistente", pageable);
    }


}
