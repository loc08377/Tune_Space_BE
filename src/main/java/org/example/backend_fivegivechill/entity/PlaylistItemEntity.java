package org.example.backend_fivegivechill.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "playlist_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "play_id", nullable = false)
    @JsonIgnore
    private PlaylistEntity playlistEntity;

    @ManyToOne
    @JoinColumn(name = "song_id", nullable = false)
    private SongEntity songEntity;
}
