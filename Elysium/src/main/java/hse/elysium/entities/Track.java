package hse.elysium.entities;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Track {
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
    @Basic
    @Column(name = "comments")
    private String comments;

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

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Track track = (Track) o;
        return trackId == track.trackId && Objects.equals(name, track.name) && Objects.equals(author, track.author) && Objects.equals(genre, track.genre) && Objects.equals(mood, track.mood) && Objects.equals(musicUrl, track.musicUrl) && Objects.equals(coverUrl, track.coverUrl) && Objects.equals(streams, track.streams) && Objects.equals(comments, track.comments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trackId, name, author, genre, mood, musicUrl, coverUrl, streams, comments);
    }
}