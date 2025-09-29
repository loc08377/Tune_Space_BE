package org.example.backend_fivegivechill.repository;

import org.example.backend_fivegivechill.RepositoryCustom.ArtistRepositoryCustom;
import org.example.backend_fivegivechill.entity.ArtistSongEntity;
import org.example.backend_fivegivechill.entity.CateSongEntity;
import org.example.backend_fivegivechill.entity.SongEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ArtistSongRepository extends JpaRepository<ArtistSongEntity, Integer> {
    // Lấy danh sách nghệ sĩ của một bài hát
    @Query("SELECT asg FROM ArtistSongEntity asg WHERE asg.songEntity.id = :songId AND asg.type = true")
    List<ArtistSongEntity> findBySongId(@Param("songId") int songId); // này tìm ca sĩ

    @Query("SELECT asg FROM ArtistSongEntity asg WHERE asg.songEntity.id = :songId AND asg.type = true")
    List<ArtistSongEntity> findBySongIdd(@Param("songId") int songId); // này tìm tác giả

    // Xóa tất cả nghệ sĩ của một bài hát
    @Modifying
    @Transactional
    @Query("DELETE FROM ArtistSongEntity asg WHERE asg.songEntity = :songEntity")
    void deleteBySongEntity(@Param("songEntity") SongEntity songEntity);
    //aaaaaaaaaaaaaaaaaaaaaaaaaaa

    @Query(value = "SELECT DISTINCT s.songEntity FROM ArtistSongEntity s WHERE s.artistEntity.id =:artistId")//aaaaaaaaaaaaaaaaaaaaaaaaaaa
    List<SongEntity> getSongsByIdArtistId(@Param("artistId") int artistId);//aaaaaaaaaaaaaaaaaaaaaaaaaaa

    //aaaaaaaaaaaaaaaaaaaaaaaaaaa
    @Query(value = "SELECT cs FROM CateSongEntity cs WHERE cs.songEntity =: songId")
    List<CateSongEntity> getCatesBySongId(@Param("songId") int songId);
}
