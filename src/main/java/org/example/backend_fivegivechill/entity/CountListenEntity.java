package org.example.backend_fivegivechill.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "countListen")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountListenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "song_id", nullable = false)
    private SongEntity songEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @Column(name = "device_fingerprint")
    private String deviceFingerprint;

    @Column(name = "create_date", nullable = false)
    private Date createDate;
}
