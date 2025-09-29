package org.example.backend_fivegivechill.repository;

import org.example.backend_fivegivechill.RepositoryCustom.ArtistRepositoryCustom;
import org.example.backend_fivegivechill.entity.ArtistEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArtistRepository extends JpaRepository<ArtistEntity, Integer>, ArtistRepositoryCustom {

    @Query("SELECT a FROM ArtistEntity a WHERE a.status = :status AND a.fullName LIKE :search ORDER BY a.id DESC")
    Page<ArtistEntity> findAllByStatus(@Param("status") int status, @Param("search") String search, Pageable pageable);

    @Query("SELECT a FROM ArtistEntity a WHERE a.status IN (0, 2)")
    List<ArtistEntity> getAll();

    @Query(value = "SELECT * FROM artist WHERE full_name = ?1 AND hometown = ?2 AND biography = ?3", nativeQuery = true)
    ArtistEntity existByFullName(String fullName, String country, String biography);

    @Query(value = "SELECT * FROM artist WHERE full_name = ?1 AND hometown = ?2 AND biography = ?3 AND id != ?4", nativeQuery = true)
    ArtistEntity existByFullNameAndId(String fullName, String country, String biography, int id);

    @Query(value = "SELECT id, full_name FROM artist WHERE status = 0", nativeQuery = true)
    ArtistEntity getAllArtistTrue();

    @Query("SELECT a FROM ArtistEntity a ORDER BY a.id ASC LIMIT 5")
    List<ArtistEntity> fingTop5Artist();


}
