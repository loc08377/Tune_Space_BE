package org.example.backend_fivegivechill.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "user_pin_code")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPinCodeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private UserEntity user;

    @Column(name = "pin_code", nullable = false)
    private String pinCode;

    @Column(name = "create_date")
    private Date createDate;
}
