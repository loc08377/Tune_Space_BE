package org.example.backend_fivegivechill.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "id_card")
@Data
public class IdCardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "number_id", nullable = false, unique = true, length = 20)
    private String numberId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @Column(name = "create_date", nullable = false, updatable = false)
    private LocalDateTime createDate;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    @Column(name = "similarity")
    private Float similarity;

    @PrePersist
    protected void onCreate() {
        this.createDate = LocalDateTime.now();
    }

    public enum Status { PENDING, APPROVED, REJECTED }
}
