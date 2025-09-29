package org.example.backend_fivegivechill.repository;

import jakarta.transaction.Transactional;
import org.example.backend_fivegivechill.entity.FollowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FollowRepository extends JpaRepository<FollowEntity, Integer> {

    @Query("SELECT f FROM FollowEntity f WHERE f.follower.id = :followerId")
    List<FollowEntity> findByFollowerId(@Param("followerId") int followerId);

    @Query("SELECT f FROM FollowEntity f WHERE f.following.id = :userId")
    List<FollowEntity> findByFollowingId(@Param("userId") int userId);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END " +
            "FROM FollowEntity f WHERE f.follower.id = :followerId AND f.following.id = :userId")
    boolean existsByFollowerIdAndUserId(@Param("followerId") int followerId, @Param("userId") int userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM FollowEntity f WHERE f.follower.id = :followerId AND f.following.id = :userId")
    void deleteByFollowerIdAndUserId(@Param("followerId") int followerId, @Param("userId") int userId);

    @Query("SELECT COUNT(f) FROM FollowEntity f WHERE f.following.id = :userId")
    int countFollowersByUserId(@Param("userId") int userId);

}
//jfjifđjjfkjfkdkfjkd
