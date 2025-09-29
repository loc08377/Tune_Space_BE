package org.example.backend_fivegivechill.repository;

import org.example.backend_fivegivechill.entity.CateSongEntity;
import org.example.backend_fivegivechill.entity.SongEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CateSongRepository extends JpaRepository<CateSongEntity, Integer> {
    // Lấy danh mục của một bài hát
    @Query("SELECT cs FROM CateSongEntity cs WHERE cs.songEntity.id = :songId")
    List<CateSongEntity> findBySongId(@Param("songId") int songId);

    // Xóa tất cả danh mục của một bài hát
    @Modifying
    @Transactional
    @Query("DELETE FROM CateSongEntity cs WHERE cs.songEntity = :songEntity")
    void deleteBySongEntity(@Param("songEntity") SongEntity songEntity);
}
