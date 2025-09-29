package org.example.backend_fivegivechill.repository;

import org.example.backend_fivegivechill.entity.AlbumEntity;
import org.example.backend_fivegivechill.entity.SongEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

// cái mới nè
public interface AlbumRepository extends JpaRepository<AlbumEntity, Integer> {
    @Query(value = "SELECT * FROM album WHERE status = ?1 AND user_id = ?2 AND name LIKE ?3 ORDER BY id DESC", nativeQuery = true)
    Page<AlbumEntity> findByUserId(int status, Integer userId, String search, Pageable pageable);

    @Query(value = "SELECT * FROM album WHERE status = 1 AND user_id = :userId ORDER BY id DESC", nativeQuery = true)
    List<AlbumEntity> findAlbumByUserId(int userId) ;
}
