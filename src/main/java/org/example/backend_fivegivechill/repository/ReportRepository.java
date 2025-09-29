package org.example.backend_fivegivechill.repository;

import org.example.backend_fivegivechill.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, Integer> {
    // 1. SELECT song_id FROM report GROUP BY song_id
    @Query ("SELECT r.song.id FROM ReportEntity r GROUP BY r.song.id")
    List<Integer> findDistinctSongIds();


    // 2. SELECT content FROM report WHERE song_id = 1
    @Query("SELECT r.content FROM ReportEntity r WHERE r.song.id = :songId")
    List<String> findContentsBySongId(@Param("songId") Integer songId);

    @Modifying
    @Transactional
    @Query(value = "delete from report where id = ?1", nativeQuery = true)
    void deleteBySongId(Integer report);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM report WHERE id <> ?1", nativeQuery = true)
    void deleteReportNotLike(int reportId);
    
}

