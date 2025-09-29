package org.example.backend_fivegivechill.repository;

import org.example.backend_fivegivechill.entity.PlaylistEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlaylistRepository extends JpaRepository<PlaylistEntity, Integer> {
    @Query("SELECT pl FROM PlaylistEntity pl WHERE pl.userEntity.id = :userId ORDER BY pl.id DESC")
    Page<PlaylistEntity> findByUserId(@Param("userId") int userId, Pageable pageable);

    @Query("SELECT pl FROM PlaylistEntity pl WHERE pl.name = :name")
    PlaylistEntity findByName(@Param("name") String name);

    List<PlaylistEntity> findByUserEntityId(int userId);

}
