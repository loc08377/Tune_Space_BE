package org.example.backend_fivegivechill.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Entity
@Table(name = "daily_revenue")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyRavenueEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "song_id", nullable = false)
    private SongEntity songEntity;

    @Column(name = "total_amount", nullable = false)
    private long totalAmount;

    @Column(name = "create_date", nullable = false)
    private Date createDate;

}
