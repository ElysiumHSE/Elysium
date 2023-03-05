package hse.elysium.entities;

import jakarta.persistence.*;
import lombok.Setter;
import lombok.Getter;

import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "Track", schema = "u1950683_elysium")
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
