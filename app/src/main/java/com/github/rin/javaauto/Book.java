package com.github.rin.javaauto;

import java.util.Objects;
import java.util.UUID;

@StorageService()
public class Book implements Identifiable {

    UUID id;

    @NotEmpty
    String title;

    @NotEmpty
    String publishYear;

    @NotEmpty
    String author;
    String description;

    public Book() {
        id = UUID.randomUUID();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPublishYear(String publishYear) {
        this.publishYear = publishYear;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getPublishYear() {
        return publishYear;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(id, book.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
