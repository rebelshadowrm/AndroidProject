package com.example.project1;

public class CalendarCard {

    private final String title, episode, airing, image, id;


    public CalendarCard(String title, String episode, String airing, String image, String id) {
        this.title = title;
        this.episode = episode;
        this.airing = airing;
        this.image = image;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getEpisode() {
        return episode;
    }

    public String getAiring() {
        return airing;
    }

    public String getImage() {
        return image;
    }

    public String getId() { return id; }
}
