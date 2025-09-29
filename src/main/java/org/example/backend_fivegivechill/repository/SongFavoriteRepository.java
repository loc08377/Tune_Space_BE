package org.example.backend_fivegivechill.repository;

import org.example.backend_fivegivechill.entity.SongFavoriteEntity;
import org.example.backend_fivegivechill.entity.SongEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SongFavoriteRepository extends JpaRepository<SongFavoriteEntity, Integer> {

    @Query("SELECT f.song FROM SongFavoriteEntity f WHERE f.user.id = ?1")
    List<SongEntity> findByUserId(int userId);

    @Query("SELECT f FROM SongFavoriteEntity f WHERE f.user.id = ?1 AND f.song.id = ?2")
    Optional<SongFavoriteEntity> findByUserIdAndSongId(int userId, int songId);

    //aaaaaaaaaaaaaaaaaaaaaaaaaaa
    @Query("""
            SELECT DISTINCT s, a
            FROM SongEntity s 
            JOIN ArtistSongEntity ats ON ats.songEntity = s
            JOIN ats.artistEntity a
            JOIN SongFavoriteEntity sf ON sf.song = s
            WHERE sf.user.id = :userId
            """)
    List<Object[]> favoriteOfSongEndArtists(int userId);

    @Query("SELECT f.song FROM SongFavoriteEntity f WHERE f.user.id = :userId")
    List<SongEntity> findSongFavoriteByUser(int userId);
}
