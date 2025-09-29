package org.example.backend_fivegivechill.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "subscription_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)@JsonIgnore
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    @ManyToOne(fetch = FetchType.LAZY)@JsonIgnore
    @JoinColumn(name = "sub_id", nullable = false)
    private SubscriptionPackageEntity subscriptionPackageEntity;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "create_date", nullable = false)
    private Date createDate;

    @Column(name = "first_day", nullable = false)
    private Date firstDay;

    @Column(name = "last_day", nullable = false)
    private Date lastDay;

    @Column(name = "status")
    private boolean status;
}
