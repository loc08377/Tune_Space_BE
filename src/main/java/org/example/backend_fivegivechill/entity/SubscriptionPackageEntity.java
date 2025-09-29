package org.example.backend_fivegivechill.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "subscription_package")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPackageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", columnDefinition = "nvarchar(255)", nullable = false)
    private String name;

    @Column(name = "duration", nullable = false)
    private int duration;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "status")
    private boolean status;

//    @Column(name = "description", nullable = false)
//    private String description;
}
