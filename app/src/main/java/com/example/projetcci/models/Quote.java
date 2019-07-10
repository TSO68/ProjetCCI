package com.example.projetcci.models;

/**
 * Class with informations needed from each quote
 */
public class Quote {
    private int idMovie;
    private String quote;
    private String character;

    public Quote(int idMovie, String quote, String character) {
        this.idMovie = idMovie;
        this.quote = quote;
        this.character = character;
    }

    public int getIdMovie() {
        return idMovie;
    }

    public void setIdMovie(int idMovie) {
        this.idMovie = idMovie;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }
}
