package org.example.backend_fivegivechill.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "follow")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Người theo dõi
    @ManyToOne
    @JoinColumn(name = "follower", referencedColumnName = "id")
    private UserEntity follower;

    // Người được theo dõi
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity following;

    private LocalDate createDate;
}
//jifjinvuirt
