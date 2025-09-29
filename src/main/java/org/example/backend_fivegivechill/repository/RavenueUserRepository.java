package org.example.backend_fivegivechill.repository;

import org.example.backend_fivegivechill.entity.RavenueUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;


public interface RavenueUserRepository extends JpaRepository<RavenueUserEntity, Integer> {

    @Query("SELECT r FROM RavenueUserEntity r " +
            "WHERE r.userEntity.id IN :userIds " +
            "AND r.createDate BETWEEN :startDate AND :endDate")
    List<RavenueUserEntity> findByUserEntityIdInAndCreateDateBetween(@Param("userIds") List<Integer> userIds, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT r.userEntity.id, r.createDate " +
            "FROM RavenueUserEntity r " +
            "WHERE r.userEntity.id IN :userIds")
    List<Object[]> findDistinctDatesByUserIds(@Param("userIds") List<Integer> userIds);

    @Query("""
                SELECT 
                    SUM(CASE WHEN r.type = true THEN r.amount ELSE 0 END), 
                    SUM(CASE WHEN r.type = false THEN r.amount ELSE 0 END)
                FROM RavenueUserEntity r
                WHERE r.userEntity.id = :userId
            """)
    Object sumAmountsByType(@Param("userId") int userId);


    @Query("""
                SELECT SUM(r.amount) 
                FROM RavenueUserEntity r 
                WHERE r.type = true 
                AND r.userEntity.id = :userId
                  AND r.createDate BETWEEN :startDate AND :endDate
            """)
    Long getTotalAmountByDateRangeAndTypeTrue(
            @Param("userId") int userId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );

    @Query("""
                SELECT 
                    COALESCE(SUM(CASE WHEN r.type = true THEN r.amount ELSE 0 END), 0) - 
                    COALESCE(SUM(CASE WHEN r.type = false THEN r.amount ELSE 0 END), 0)
                FROM RavenueUserEntity r
                WHERE r.userEntity.id = :userId
                AND r.status = 1 or r.status = 0
            """)
    Long getTotalAmountDifferenceByUser(@Param("userId") int userId);

    @Query("""
                SELECT SUM(r.amount) 
                FROM RavenueUserEntity r 
                WHERE r.type = false 
                AND r.userEntity.id = :userId
                AND r.status = 0
            """)
    Long getTotalAmountByDateRangeAndTypeTrueAndStatus0(
            @Param("userId") int userId
    );

}
