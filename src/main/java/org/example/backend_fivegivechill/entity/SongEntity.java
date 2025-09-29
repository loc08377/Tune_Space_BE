package org.example.backend_fivegivechill.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "songs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SongEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JsonIgnore
//    @JoinColumn(name = "user_id", nullable = false)
//    private UserEntity userEntity;

    @Column(name = "name", columnDefinition = "nvarchar(255)", nullable = false)
    private String name;

    @Column(name = "mp3_file", columnDefinition = "nvarchar(255)", nullable = false)
    private String mp3File;

    @Column(name = "vip_song")
    private boolean vipSong;

    @Column(name = "avatar", length = 250)
    private String avatar;

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "lyrics", columnDefinition = "nvarchar(max)")
    private String lyrics;

    @Column(name = "duration")
    private int duration;

    @Column(name = "count_listens")
    private int countListens;

    @Column(name = "status")
    private int status;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "album_id")
    private AlbumEntity album;

    @OneToMany(mappedBy = "song", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<CommentEntity> comments;

}
