package org.example.backend_fivegivechill.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "artist")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtistEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "full_name", columnDefinition = "nvarchar(255)", nullable = false)
    private String fullName;

    @Column(name = "avatar", length = 250)
    private String avatar;

    @Column(name = "hometown", columnDefinition = "nvarchar(255)")
    private String hometown;

    @Column(name = "biography", columnDefinition = "nvarchar(max)")
    private String biography;

    @Column(name = "status")
    private int status;

    @OneToMany(mappedBy = "artistEntity")
    @JsonIgnore
    private List<ArtistSongEntity> artistSongs;
}
