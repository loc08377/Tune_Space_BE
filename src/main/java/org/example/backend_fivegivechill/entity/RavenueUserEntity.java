package org.example.backend_fivegivechill.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "revenue_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RavenueUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)@JsonIgnore
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    @Column(name = "amount", nullable = false)
    private long amount;

    @Column(name = "type", nullable = false)
    private Boolean type;

    @Column(name = "create_date", nullable = false)
    private Date createDate;

    @Column(name = "status")
    private int status;
}
