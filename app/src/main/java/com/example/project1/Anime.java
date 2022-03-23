package com.example.project1;

import java.util.List;

public class Anime {

    String image, title, description, episodes, studio,
           producer, director, original;
    List<Character> characters;

    public Anime(String image, String title, String description, String episodes, String studio, String producer, String director, String original, List<Character> characters) {
        this.image = image;
        this.title = title;
        String strRegEx = "<[^>]*>";
        this.description = description.replaceAll(strRegEx, "");
        if (description.equalsIgnoreCase("null")) {
            this.description = "no description";
        }
        this.episodes = nullCheck(episodes);
        this.studio = nullCheck(studio);
        this.producer = nullCheck(producer);
        this.director = nullCheck(director);
        this.original = nullCheck(original);
        this.characters = characters;
    }
    public String nullCheck(String str) {
        if ( str == null || str.equals("null")) {
            return "------";
        }
        return str;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getEpisodes() {
        return episodes;
    }

    public String getStudio() {
        return studio;
    }

    public String getProducer() {
        return producer;
    }

    public String getDirector() {
        return director;
    }

    public String getOriginal() {
        return original;
    }

    public List<Character> getCharacters() {
        return characters;
    }
}
