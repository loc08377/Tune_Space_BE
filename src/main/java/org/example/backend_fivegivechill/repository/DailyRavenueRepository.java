package org.example.backend_fivegivechill.repository;

import org.example.backend_fivegivechill.entity.CountListenEntity;
import org.example.backend_fivegivechill.entity.DailyRavenueEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;


public interface DailyRavenueRepository extends JpaRepository<DailyRavenueEntity, Integer> {
    @Query("SELECT DISTINCT dr.createDate " +
            "FROM DailyRavenueEntity dr " +
            "WHERE dr.songEntity.id = :songId")
    List<Date> findDistinctDatesBySongId(@Param("songId") Integer songId);

    @Query("SELECT dr FROM DailyRavenueEntity dr WHERE dr.songEntity.user.id = :userId")
    List<DailyRavenueEntity> findByUserId(Integer userId, Pageable pageable);

    //chat vieet
    @Query(value = "SELECT dr.id, dr.create_date, dr.total_amount, dr.song_id, COUNT(cl.id) AS listenCount " +
            "FROM daily_ravenue dr " +
            "INNER JOIN songs s ON dr.song_id = s.id " +
            "LEFT JOIN count_listen cl ON dr.song_id = cl.song_id AND CONVERT(date, cl.create_date) = CONVERT(date, dr.create_date) " +
            "WHERE s.user_id = :userId " +
            "GROUP BY dr.id, dr.create_date, dr.total_amount, dr.song_id " +
            "ORDER BY dr.create_date DESC",
            nativeQuery = true)
    List<Object[]> findByUserIdWithListenCount(@Param("userId") Integer userId, Pageable pageable);

    @Query("SELECT d FROM DailyRavenueEntity d WHERE d.songEntity.id = :songId " +
            "AND d.createDate BETWEEN :startDate AND :endDate")
    Optional<DailyRavenueEntity> findBySongEntityIdAndCreateDateBetween(int songId, Date startDate, Date endDate);

    List<DailyRavenueEntity> findAllByCreateDateBetweenAndSongEntityUserId(Date startDate, Date endDate, Integer userId);

    List<DailyRavenueEntity> findBySongEntityIdInAndCreateDateBetween(List<Integer> songIds, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT d.songEntity.id, d.createDate FROM DailyRavenueEntity d WHERE d.songEntity.id IN :songIds")
    List<Object[]> findDistinctDatesBySongIds(List<Integer> songIds);

    @Query("SELECT s.user.id, SUM(dr.totalAmount) " +
            "FROM DailyRavenueEntity dr " +
            "JOIN dr.songEntity s " +
            "WHERE s.user.id IN :userIds " +
            "AND dr.createDate BETWEEN :startDate AND :endDate " +
            "GROUP BY s.user.id")
    List<Object[]> sumTotalAmountByUserIdInAndCreateDateBetween(@Param("userIds") List<Integer> userIds, @Param("startDate") Date startDate, @Param("endDate") Date endDate);


    @Query("SELECT s.user.id, MIN(dr.createDate) " +
            "FROM DailyRavenueEntity dr " +
            "JOIN dr.songEntity s " +
            "WHERE s.user.id IN :userIds " +
            "GROUP BY s.user.id")
    List<Object[]> findEarliestDatesByUserIds(@Param("userIds") List<Integer> userIds);

    @Query("SELECT dr.songEntity.user.id, SUM(dr.totalAmount) " +
            "FROM DailyRavenueEntity dr " +
            "WHERE dr.songEntity.user.id = :userId " +
            "AND dr.createDate IN :startDates " +
            "GROUP BY dr.songEntity.user.id, dr.createDate")
    List<Object[]> sumTotalAmountByUserIdAndCreateDatesBetween(@Param("userId") Integer userId,
                                                               @Param("startDates") List<Date> startDates);
}
