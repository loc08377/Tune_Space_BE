package org.example.backend_fivegivechill.repository;

import org.example.backend_fivegivechill.RepositoryCustom.SongRepositoryCustom;
import org.example.backend_fivegivechill.RepositoryCustom.SongSearchRepositoryCustom;
import org.example.backend_fivegivechill.entity.AlbumEntity;
import org.example.backend_fivegivechill.entity.PlaylistEntity;
import org.example.backend_fivegivechill.entity.SongEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SongRepository extends JpaRepository<SongEntity, Integer>, SongRepositoryCustom, SongSearchRepositoryCustom {
    @Query("SELECT s FROM SongEntity s " +
            "WHERE s.status = ?1 " +
            "AND (s.name LIKE ?2 " +
            "OR s.user.fullName LIKE ?2) " +
            "ORDER BY s.id DESC")
    Page<SongEntity> findAllByStatus(int status ,String search ,Pageable pageable);


    @Query(value = "SELECT * FROM songs WHERE status = 3 ORDER BY id DESC", nativeQuery = true)
    List<SongEntity> finSongReportStatus3();

    @Query(value = "SELECT * FROM songs WHERE status = ?1 AND user_id = ?2 AND name LIKE ?3", nativeQuery = true)
    Page<SongEntity> findAllByStatusAndUser(int status, int id, String search, Pageable pageable);

    @Query("SELECT s FROM SongEntity s WHERE s.status = 0 ORDER BY s.createDate DESC LIMIT 7")
    List<SongEntity> findTop3ByStatusTrueOrderByCreateDateDesc();

    // cho album
    List<SongEntity> findByAlbumId(Integer albumId);

    @Query(value = "SELECT * FROM songs WHERE status = ?1 AND user_id = ?2 AND (album_id = ?3 OR album_id IS NULL)", nativeQuery = true)
    List<SongEntity> findSongsByUserAndAlbumMatchOrNull(int status, int userId, Long albumId);

    @Query("SELECT s FROM SongEntity s WHERE s.album.id = :albumId")
    List<SongEntity> findAllByAlbumId(@Param("albumId") Long albumId);
    // cho album đóng

    @Query("SELECT pl.songEntity FROM PlaylistItemEntity pl WHERE pl.id = :playListId")
    List<SongEntity> findAllByPlayListId(@Param("playListId") int playListId);


    @Query("SELECT s FROM SongEntity s WHERE s.user.id = :idCreator")
    List<SongEntity> findAllByCreator(@Param("idCreator") int idCreator);

    @Query("SELECT s FROM SongEntity s WHERE s.status = 1")
    List<SongEntity> get3Music();

    @Query(value = "SELECT TOP 12 * FROM songs ORDER BY NEWID()", nativeQuery = true)
    List<SongEntity> findRandom12Songs();

    @Query(value = "SELECT s.album FROM SongEntity s WHERE s.id = :songId")
    AlbumEntity findAlbumBySongId(@Param("songId") int songId);

    @Query(value = "SELECT s.id FROM SongEntity s WHERE s.album.id = :albumId")
    List<Integer> findIdsSongByAlbumId(@Param("albumId") int albumId);

    @Query(value = "SELECT pl.songEntity.id FROM PlaylistItemEntity pl WHERE pl.playlistEntity.id = :playListId")
    List<Integer> findIdsSongByplaylistId(@Param("playListId") int playListId);

    @Query("""
            SELECT s
            FROM  SongEntity s
            WHERE s.status = 0
              AND FUNCTION('MONTH', s.createDate) = FUNCTION('MONTH', CURRENT_DATE)
              AND FUNCTION('YEAR',  s.createDate) = FUNCTION('YEAR',  CURRENT_DATE)
            ORDER BY s.createDate DESC
        """)
    Page<SongEntity> musicNewReleased(Pageable pageable);

    @Query(value = "EXEC SP_TopTrending", nativeQuery = true)
    List<SongEntity> callProcedureTopTrending();

    @Query(value = "EXEC sp_Recommend :id", nativeQuery = true)
    List<Object[]> callProcedureRecommend(@Param("id") int id);

    @Query(value = "EXEC sp_GetRelatedSongs :ids", nativeQuery = true)
    List<SongEntity> callProcedure(@Param("ids") String ids);

    @Query("SELECT s FROM SongEntity s WHERE s.status = 0 ORDER BY s.createDate DESC FETCH FIRST 50 ROWS ONLY")
    List<SongEntity> top50SongHaveMuchCountListens();

    @Query("SELECT s.id, COUNT(c) FROM SongEntity s " + //aaaaaaaaaaaaaaaaaaaaaaaaaaa
            "JOIN CountListenEntity c ON s.id = c.songEntity.id " +
            "WHERE s.id IN :songIds AND c.createDate BETWEEN :startTime AND :endTime " +
            "GROUP BY s.id")
    List<Object[]> countListBySongId(
            @Param("songIds") List<Integer> songIds,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    ); //aaaaaaaaaaaaaaaaaaaaaaaaaaa

    @Query("SELECT s FROM SongEntity s WHERE s.id IN :ids")
    List<SongEntity> test(@Param("ids") List<Integer> ids);

    @Query("SELECT s.mp3File FROM SongEntity s WHERE s.id IN :id")
    String testaaa(@Param("id") int id);

    @Query("SELECT s.vipSong FROM SongEntity s WHERE s.id IN :id")
    boolean isVip(@Param("id") int id);

//    @Query("SELECT s " +
//            "FROM SongEntity s " +
//            "JOIN UserEntity u ON s.userEntity.id = u.id " +
//            "WHERE u.id = :userId")
//    List<SongEntity> findByUserId(@Param("userId") int userId);

//    Page<SongEntity> findByUserId(int userId, Pageable pageable);

    //aaaaaaaaaaaaaaaaaaaaaaaaaaa
    @Query(""" 
                SELECT DISTINCT s, a, h.createDate
                FROM HistoryEntity h
                JOIN h.songEntity s
                JOIN ArtistSongEntity ats ON ats.songEntity = s
                JOIN ats.artistEntity a
                JOIN PlaylistItemEntity pi ON pi.songEntity = s
                JOIN pi.playlistEntity p
                WHERE h.userEntity.id = :userId
                AND p.userEntity.id = :userId
                ORDER BY h.createDate DESC
            """)
    List<Object[]> getSongsAndArtistsUserHeardAndInTheirPlaylists(
            @Param("userId") int userId, Pageable pageable

    ); //aaaaaaaaaaaaaaaaaaaaaaaaaaa

    //aaaaaaaaaaaaaaaaaaaaaaaaaaa

    @Query("""
            SELECT s FROM SongEntity s WHERE s.user.id IN :userId
            """)
    Page<SongEntity> findByUser(@Param("userId") int userId, Pageable pageable);

    @Query("SELECT e.duration FROM SongEntity e WHERE e.id = :songId")
    long getDuration(@Param("songId") int songId);

    @Query(value = "select * from songs where id = ?1 and status not in (2, 3)", nativeQuery = true)
    SongEntity findByIdAndStatus3(int id);

    @Query(value = """
    SELECT TOP 100
        s.*,
        ISNULL(month_listens.month_count, 0) AS total_month_listens,
        ISNULL(month_favorites.favorite_count, 0) AS total_favorites
    FROM songs s
    LEFT JOIN (
        SELECT song_id, COUNT(*) AS month_count
        FROM count_listen
        WHERE YEAR(create_date) = YEAR(GETDATE()) AND MONTH(create_date) = MONTH(GETDATE())
        GROUP BY song_id
    ) AS month_listens ON s.id = month_listens.song_id
    LEFT JOIN (
        SELECT song_id, COUNT(*) AS favorite_count
        FROM song_favorites
        GROUP BY song_id
    ) AS month_favorites ON s.id = month_favorites.song_id
    WHERE s.status = 0
    ORDER BY (ISNULL(month_listens.month_count, 0) + ISNULL(month_favorites.favorite_count, 0)) DESC
    """, nativeQuery = true)
    List<Object[]> findTop100SongsWithMonthlyListensAndFavorites();

    @Query(value = """
    SELECT 
        COUNT(CASE WHEN s.create_date >= DATEADD(DAY, -30, GETDATE()) THEN 1 END) AS recentSongCount,
        (SELECT MAX(s2.count_listens)
         FROM songs s2
         WHERE s2.user_id = :userId
           AND s2.create_date >= DATEADD(DAY, -90, GETDATE())
        ) AS topListenCount
    FROM songs s
    WHERE s.user_id = :userId
    """, nativeQuery = true)
    Object findSongStatsByUser(@Param("userId") int userId);


    @Query("SELECT pl.id FROM PlaylistItemEntity pl WHERE pl.playlistEntity.id = :playListId")
    List<Integer> findIdsPlaylistItemByPlaylistId(@Param("playListId") int playListId);





}
