package org.example.backend_fivegivechill.repository;

import org.example.backend_fivegivechill.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findBySongIdOrderByCreateDateDesc(Long songId);
    List<CommentEntity> findByParentCommentId(Long cmtId);

    @Transactional
    @Modifying
    @Query(value = "delete from comment where id = ?1 or cmt_id = ?1", nativeQuery = true)
    int deleteByIdCmt(Long cmtId);

}
