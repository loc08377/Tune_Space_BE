package org.example.backend_fivegivechill.repository;

import org.example.backend_fivegivechill.entity.SongEntity;
import org.example.backend_fivegivechill.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    @Query("SELECT c FROM UserEntity c WHERE c.status = :status AND c.fullName LIKE :search AND c.role != 0 ORDER BY c.id DESC")
    Page<UserEntity> findAllByStatusTrue(@Param("status") boolean status, @Param("search") String search, Pageable pageable);

    Optional<UserEntity> findByEmail(String email);

    @Query(value = "SELECT DISTINCT * FROM users where full_name LIKE ?1", nativeQuery = true)
    List<UserEntity> findByNamee(String search);

    @Query("SELECT u FROM UserEntity u WHERE u.email = :email")
    Optional<UserEntity> findByEmailUnique(@Param("email") String email);

    @Query("SELECT s.user FROM SongEntity s WHERE s.id = :songId")
    UserEntity findUserBySongId(@Param("songId") int songId);

    @Query("SELECT u FROM UserEntity u WHERE u.role = :role")
    Page<UserEntity> findByRole(@Param("role") Integer role, Pageable pageable);

    @Query("""
                SELECT u 
                FROM UserEntity u
                JOIN FollowEntity f ON f.following.id = u.id
                WHERE f.follower.id = :user_id
                  AND u.role = 2
            """)
    List<UserEntity> findFollowedArtistsByUserId(@Param("user_id") int userId);

    @Query("""
                SELECT DISTINCT a
                FROM HistoryEntity h
                JOIN SongEntity s ON h.songEntity.id = s.id
                JOIN ArtistSongEntity  ats ON  ats.songEntity.id = s.id
                JOIN ArtistEntity at ON at.id = ats.artistEntity.id
                JOIN UserEntity a ON ats.artistEntity.id = a.id
                WHERE h.userEntity.id = :user_id
                  AND a.role = 2
                ORDER BY h.createDate DESC
            """)
    List<UserEntity> suggestArtistsFromListeningHistory(@Param("user_id") int userId);

    @Query("SELECT u.status FROM UserEntity u WHERE u.id = :userID")
    boolean checkUserVip(@Param("userID") int userID);


    @Query(value = "SELECT * " +
            "FROM users " +
            "WHERE id IN ( " +
            "    SELECT TOP 5 s.id " +
            "    FROM users s " +
            "    JOIN follow f ON s.id = f.user_id " +
            "    WHERE s.role = 2 " +
            "    GROUP BY s.id " +
            "    ORDER BY COUNT(f.id) DESC" +
            ")",
            nativeQuery = true)
    List<UserEntity> getTop5Creator();

}
