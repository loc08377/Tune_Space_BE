package org.example.backend_fivegivechill.repository;

import org.example.backend_fivegivechill.entity.CountListenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface CountListenRepository extends JpaRepository<CountListenEntity, Integer> {
    // hàm này quy ước sẳn giống findAll hay findBy á
    // nó sẽ đếm số dòng dựa vào điều kiện là id giô
    long countBySongEntity_IdAndCreateDateBetween(int songId, Date start, Date end); //aaaaaaaaaaaaaaaaaaaaaaaaaaa

    @Query("SELECT DISTINCT c.createDate FROM CountListenEntity c " + //aaaaaaaaaaaaaaaaaaaaaaaaaaa
            "WHERE c.songEntity.id IN :songIds AND c.createDate < CURRENT_DATE")
    List<Date> findDistinctDatesBySongIdsBeforeToday(@Param("songIds") List<Integer> songIds);

    @Query("SELECT s.id, COUNT(c) FROM CountListenEntity c JOIN c.songEntity s " + //aaaaaaaaaaaaaaaaaaaaaaaaaaa
            "WHERE s.id IN :songIds AND CAST(c.createDate AS DATE) BETWEEN :startDate AND :endDate " +
            "GROUP BY s.id")
    List<Object[]> countBySongEntityIdsAndCreateDateBetween(@Param("songIds") List<Integer> songIds,
                                                            @Param("startDate") LocalDateTime startDate,
                                                            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT CAST(c.createDate AS DATE), COUNT(c) FROM CountListenEntity c " + //aaaaaaaaaaaaaaaaaaaaaaaaaaa
            "WHERE c.songEntity.id = :songId AND CAST(c.createDate AS DATE) IN :dates " +
            "GROUP BY CAST(c.createDate AS DATE)")
    List<Object[]> countBySongEntityIdAndCreateDatesIn(@Param("songId") Integer songId, @Param("dates") List<LocalDate> dates);

    @Query(
            value = "SELECT COUNT(*) FROM count_listen WHERE user_id = :userId AND song_id = :songId AND CAST(create_date AS DATE) = CAST(:today AS DATE)",
            nativeQuery = true
    )
    int countByUserAndSongAndDate(@Param("userId") int userId, @Param("songId") int songId, @Param("today") Date today);


    @Query(
            value = "SELECT COUNT(*) FROM count_listen WHERE ip_address = :ip AND song_id = :songId AND CAST(create_date AS DATE) = CAST(:today AS DATE)",
            nativeQuery = true
    )
    int countByIpAndSongAndDate(@Param("ip") String ip, @Param("songId") int songId, @Param("today") Date today);


    @Query("""
                SELECT COUNT(c)
                FROM CountListenEntity c
                WHERE c.songEntity.id = :songId
                  AND c.createDate >= :startOfDay
                  AND c.createDate <= :endOfDay
            """)
    int calculator(
            @Param("songId") int songId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );

    void deleteByCreateDateBetween(LocalDateTime startTime, LocalDateTime endTime);

    @Query(
            value = "SELECT COUNT(*) FROM count_listen WHERE device_fingerprint = :fingerprint AND song_id = :songId AND CAST(create_date AS DATE) = CAST(:today AS DATE)",
            nativeQuery = true
    )
    int countByDeviceFingerprintAndSongAndDate(@Param("fingerprint") String fingerprint, @Param("songId") int songId, @Param("today") Date today);
}
