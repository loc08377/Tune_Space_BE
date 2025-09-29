package org.example.backend_fivegivechill.repository;

import org.example.backend_fivegivechill.entity.BankEntity;
import org.example.backend_fivegivechill.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;



public interface BankRepository extends JpaRepository<BankEntity, Integer> {
    @Query("""
            SELECT b
            FROM BankEntity b
            WHERE b.numberAccount = :numberAccount
            AND b.nameAccount = :nameAccount
            """)
    BankEntity checkInformation(String numberAccount, String nameAccount);

    @Query("""
            SELECT b
                                     FROM BankEntity b
                                     WHERE b.user.id = :userId
                                     AND b.status = true
                                     ORDER BY b.id DESC
            """)
    List<BankEntity> findByUserId(int userId);

    @Modifying
    @Transactional
    @Query("UPDATE BankEntity b SET b.status = false WHERE b.id IN :ids")
    int disableBanks(@Param("ids") List<Integer> ids);

}
