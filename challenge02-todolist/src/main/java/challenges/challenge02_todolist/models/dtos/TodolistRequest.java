package challenges.challenge02_todolist.models.dtos;

import challenges.challenge02_todolist.models.enums.TodoStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Schema(description = "TodoListRequest")
public class TodolistRequest {

    @NotEmpty(message = "Titulo é obrigatório")
    @NotBlank(message = "Titulo nao pode conter apenas espaços!")
    @Size(min = 3, max = 100, message = "Titulo deve ter entre 3 e 100 caracteres")
    private String title;
    private String description;
    private TodoStatus status;

    @CreationTimestamp
    private LocalDateTime creationDate;

    private LocalDateTime conclusionDate;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TodoStatus getStatus() {
        return status;
    }

    public void setStatus(TodoStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getConclusionDate() {
        return conclusionDate;
    }

    public void setConclusionDate(LocalDateTime conclusionDate) {
        this.conclusionDate = conclusionDate;
    }
}
