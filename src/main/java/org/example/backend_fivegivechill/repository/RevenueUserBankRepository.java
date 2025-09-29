package org.example.backend_fivegivechill.repository;

import org.example.backend_fivegivechill.entity.RavenueUserEntity;
import org.example.backend_fivegivechill.entity.RevenueUserBankEntity;
import org.example.backend_fivegivechill.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RevenueUserBankRepository extends JpaRepository<RevenueUserBankEntity, Integer> {

    @Query("""
       SELECT rub 
       FROM RevenueUserBankEntity rub
       WHERE rub.revenueUser.userEntity = :userEntity
       AND rub.revenueUser.type = false
       ORDER BY rub.id DESC
       """)
    List<RevenueUserBankEntity> findByUserAndTypedraw(UserEntity userEntity);


    @Query("""
                 SELECT rub 
                 FROM RevenueUserBankEntity rub
                 WHERE rub.revenueUser.type = false
                 ORDER BY rub.id DESC
            
            """)
    List<RevenueUserBankEntity> findByTypedraw();

    @Query("""
                 SELECT rub 
                 FROM RevenueUserBankEntity rub
                 WHERE rub.revenueUser.id = :revenueUserId
            """)
    RevenueUserBankEntity findByRevenueUserId(int  revenueUserId);

    @Query("""
            SELECT rub 
            FROM RavenueUserEntity rub
            WHERE rub.userEntity = :userEntity
            AND rub.type = true
            AND rub.status = 1
            """)
    List<RavenueUserEntity> findByUserfindByUserAndTypePlus(UserEntity userEntity);

}
