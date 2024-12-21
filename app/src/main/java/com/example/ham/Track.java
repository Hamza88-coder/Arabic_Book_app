package com.example.ham;

public class Track {

    String title;
    String artiste;
    private int image;

    public Track(String title, String artiste, int image) {
        this.title = title;
        this.artiste = artiste;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtiste() {
        return artiste;
    }

    public void setArtiste(String artiste) {
        this.artiste = artiste;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
