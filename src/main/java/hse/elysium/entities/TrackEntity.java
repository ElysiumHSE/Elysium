package hse.elysium.entities;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "Track", schema = "u1950683_elysium", catalog = "")
public class TrackEntity {
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
        TrackEntity that = (TrackEntity) o;
        return trackId == that.trackId && Objects.equals(name, that.name) && Objects.equals(author, that.author) && Objects.equals(genre, that.genre) && Objects.equals(mood, that.mood) && Objects.equals(musicUrl, that.musicUrl) && Objects.equals(coverUrl, that.coverUrl) && Objects.equals(streams, that.streams);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trackId, name, author, genre, mood, musicUrl, coverUrl, streams);
    }
}
