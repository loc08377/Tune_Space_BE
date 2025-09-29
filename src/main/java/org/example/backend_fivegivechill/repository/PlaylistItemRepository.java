package org.example.backend_fivegivechill.repository;

import org.example.backend_fivegivechill.entity.AlbumEntity;
import org.example.backend_fivegivechill.entity.PlaylistEntity;
import org.example.backend_fivegivechill.entity.PlaylistItemEntity;
import org.example.backend_fivegivechill.entity.SongEntity;
import org.example.backend_fivegivechill.response.PlaylistResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;

public interface PlaylistItemRepository extends JpaRepository<PlaylistItemEntity, Integer> {
    @Modifying
    @Query("DELETE FROM PlaylistItemEntity plI WHERE plI.playlistEntity.id = :playlistId")
    void deleteByPlaylistEntityId(@Param("playlistId") int playlistId);

    //Check xem bài có tồn tại không
    boolean existsByPlaylistEntityAndSongEntity(PlaylistEntity playlist, SongEntity song);

    @Query("SELECT p FROM PlaylistItemEntity p WHERE p.playlistEntity.id = :id")
    List<PlaylistItemEntity> findPlayListItemByIdPlaylist(@Param("id") int id);

    @Modifying
    @Query("DELETE FROM PlaylistItemEntity p WHERE p.id = :id")
    int deleteByIdCustom(@Param("id") int id);

    @Modifying
    @Transactional
    @Query("DELETE FROM PlaylistItemEntity plI WHERE plI.id IN :idPlaylistItems ")
    int deletePlaylistItems(@Param("idPlaylistItems") List<Integer> idPlaylistItems);

    @Query("SELECT p FROM PlaylistItemEntity p WHERE p.songEntity.id = :id")
    List<PlaylistItemEntity> findPlayListItemByIdSongs(@Param("id") int id);

    @Query("SELECT p FROM PlaylistItemEntity p WHERE p.songEntity.id = :songId AND p.playlistEntity.id = :playlistId")
    List<PlaylistItemEntity> findPlaylistItemBySongAndPlaylist(
            @Param("songId") int songId,
            @Param("playlistId") int playlistId
    );





}