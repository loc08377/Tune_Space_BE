package org.example.backend_fivegivechill.repository;

import org.example.backend_fivegivechill.entity.UserEntity;
import org.example.backend_fivegivechill.entity.UserPinCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserPinCodeRepository extends JpaRepository<UserPinCodeEntity, Integer> {
    @Query("""
            SELECT upc FROM UserPinCodeEntity upc WHERE upc.user = :userEntity
            """)
    UserPinCodeEntity findByUser(@Param("userEntity") UserEntity userEntity);
}
