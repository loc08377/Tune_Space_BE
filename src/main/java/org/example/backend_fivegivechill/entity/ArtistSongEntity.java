package org.example.backend_fivegivechill.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "artist_song")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtistSongEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JoinColumn(name = "type")
    private boolean type;  // true = ca sĩ / false = tác giả

    @ManyToOne
    @JoinColumn(name = "artist_id", nullable = false)
    private ArtistEntity artistEntity;

    @ManyToOne
    @JoinColumn(name = "song_id", nullable = false)
    private SongEntity songEntity;
}
