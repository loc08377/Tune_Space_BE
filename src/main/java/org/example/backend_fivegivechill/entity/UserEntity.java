package org.example.backend_fivegivechill.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "email", length = 250, nullable = false)
    private String email;

    @Column(name = "full_name", columnDefinition = "nvarchar(255)", nullable = false)
    private String fullName;

    @Column(name = "password", length = 250, nullable = false)
    private String password;

    @Column(name = "phone", length = 15, nullable = false)
    private String phone;

    @Column(name = "avatar", columnDefinition = "NVARCHAR(MAX)")
    private String avatar;

    @Column(name = "status")
    private boolean status;

    @Column(name = "role")
    private int role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)            // user có thể chưa có CCCD
    @JsonManagedReference
    private IdCardEntity idCard;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<SongEntity> songs;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<AlbumEntity> albums;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<CommentEntity> comments;

    public enum Role {
        ADMIN(0),
        USER(1),
        CREATOR(2);

        private final int value;

        Role(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static Role fromValue(int value) {
            for (Role role : Role.values()) {
                if (role.getValue() == value) {
                    return role;
                }
            }
            throw new IllegalArgumentException("Invalid role value: " + value);
        }
    }

    public Role getRoleEnum() {
        return Role.fromValue(this.role);
    }
}
