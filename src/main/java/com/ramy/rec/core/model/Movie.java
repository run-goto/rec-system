package com.ramy.rec.core.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
@JsonPropertyOrder({"movieId", "title", "genres"})
public class Movie implements Serializable {
    private int id;
    private String title;
    private List<String> genres;

    @JsonProperty("movieId")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("genres")
    public List<String> getGenres() {
        return Arrays.asList(genres.toArray(new String[0]));
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", genres=" + genres +
                '}';
    }
}