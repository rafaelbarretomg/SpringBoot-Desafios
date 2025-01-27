package com.example.challenges.challenge01.models;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "books", uniqueConstraints = @UniqueConstraint(columnNames = "title"))
public class Book implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Titulo é obrigatório!")
    @NotBlank(message = "Titulo nao pode conter apenas espaços!")
    @Size(min = 3, max = 100, message = "Titulo deve ter entre 3 e 100 caracteres")
    private String title;

    @NotEmpty(message = "Autor é obrigatório!")
    @NotBlank(message = "Autor nao pode conter apenas espaços!")
    @Size(min = 3, max = 100, message = "Autor deve ter entre 3 e 100 caracteres")
    private String author;

    @NotNull(message = "Ano de publicação é obrigatorio!")
    @PastOrPresent(message = "Ano de publicação deve ser no passado ou presente")
    private Integer yearOfPublication;

    public Book(long id, String title, String author, Integer yearOfPublication) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.yearOfPublication = yearOfPublication;
    }

    public Book() {
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public Integer getYearOfPublication() {
        return yearOfPublication;
    }
    public void setYearOfPublication(Integer yearOfPublication) {
        this.yearOfPublication = yearOfPublication;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((author == null) ? 0 : author.hashCode());
        result = prime * result + ((yearOfPublication == null) ? 0 : yearOfPublication.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Book other = (Book) obj;
        if (id != other.id)
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        if (author == null) {
            if (other.author != null)
                return false;
        } else if (!author.equals(other.author))
            return false;
        if (yearOfPublication == null) {
            if (other.yearOfPublication != null)
                return false;
        } else if (!yearOfPublication.equals(other.yearOfPublication))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Book [id=" + id + ", title=" + title + ", author=" + author + ", yearOfPublication=" + yearOfPublication
                + "]";
    }

    
}
