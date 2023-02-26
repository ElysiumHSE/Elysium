package ru.hse.java.entities;

import jakarta.persistence.*;

@Entity
public class Tracks {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "track_id")
    private int trackId;
    @Basic
    @Column(name = "name")
    private String name;
    @Basic
    @Column(name = "author")
    private String author;
    @Basic
    @Column(name = "genre")
    private Object genre;
    @Basic
    @Column(name = "mood")
    private Object mood;
    @Basic
    @Column(name = "music_url")
    private String musicUrl;
    @Basic
    @Column(name = "cover_url")
    private String coverUrl;
    @Basic
    @Column(name = "streams")
    private Integer streams;

    public int getTrackId() {
        return trackId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Object getGenre() {
        return genre;
    }

    public void setGenre(Object genre) {
        this.genre = genre;
    }

    public Object getMood() {
        return mood;
    }

    public void setMood(Object mood) {
        this.mood = mood;
    }

    public String getMusicUrl() {
        return musicUrl;
    }

    public void setMusicUrl(String musicUrl) {
        this.musicUrl = musicUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public Integer getStreams() {
        return streams;
    }

    public void setStreams(Integer streams) {
        this.streams = streams;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tracks tracks = (Tracks) o;

        if (trackId != tracks.trackId) return false;
        if (name != null ? !name.equals(tracks.name) : tracks.name != null) return false;
        if (author != null ? !author.equals(tracks.author) : tracks.author != null) return false;
        if (genre != null ? !genre.equals(tracks.genre) : tracks.genre != null) return false;
        if (mood != null ? !mood.equals(tracks.mood) : tracks.mood != null) return false;
        if (musicUrl != null ? !musicUrl.equals(tracks.musicUrl) : tracks.musicUrl != null) return false;
        if (coverUrl != null ? !coverUrl.equals(tracks.coverUrl) : tracks.coverUrl != null) return false;
        if (streams != null ? !streams.equals(tracks.streams) : tracks.streams != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = trackId;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + (genre != null ? genre.hashCode() : 0);
        result = 31 * result + (mood != null ? mood.hashCode() : 0);
        result = 31 * result + (musicUrl != null ? musicUrl.hashCode() : 0);
        result = 31 * result + (coverUrl != null ? coverUrl.hashCode() : 0);
        result = 31 * result + (streams != null ? streams.hashCode() : 0);
        return result;
    }
}
