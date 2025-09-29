package org.example.backend_fivegivechill.repository;

import org.example.backend_fivegivechill.entity.HistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistoryRepository extends JpaRepository<HistoryEntity, Integer> {

    List<HistoryEntity> findByUserEntityId(int userId);

    // @Modifying
    // @Query("DELETE FROM HistoryEntity plI WHERE plI.id IN :historyIds AND plI.userEntity.id = :idUser")
    // int deleteByHistoryIdsAndUserId(@Param("historyIds") List<Integer> historyIds, @Param("idUser") int idUser);
    
    @Query("DELETE FROM HistoryEntity plI WHERE plI.userEntity.id = :idUser")
    void deleteByHistoryEntityId(@Param("idUser") int idUser);


    // Kiểm tra tồn tại
    boolean existsByUserEntityIdAndSongEntityId(int userId, int songId);

    // Tìm bản ghi để cập nhật
    Optional<HistoryEntity> findByUserEntityIdAndSongEntityId(int userId, int songId);


    @Modifying
    @Transactional
    @Query("DELETE FROM HistoryEntity h WHERE h.id IN :historyIds AND h.userEntity.id = :userId")
    int deleteByHistoryIdsAndUserId(@Param("historyIds") List<Integer> historyIds, @Param("userId") int userId);

    // cái dưới này lấy theo songId, cái lên là hisId
    @Modifying
    @Query("DELETE FROM HistoryEntity h WHERE h.userEntity.id = :userId AND h.songEntity.id IN :songIds")
    int deleteByUserIdAndSongIds(@Param("userId") int userId, @Param("songIds") List<Integer> songIds);

}
