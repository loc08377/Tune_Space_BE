package org.example.backend_fivegivechill.repository;


import org.example.backend_fivegivechill.entity.IdCardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IdCardRepository extends JpaRepository<IdCardEntity, Integer> {

    List<IdCardEntity> findAllByStatus(IdCardEntity.Status status);

    Page<IdCardEntity> findByStatusAndNumberIdContainingIgnoreCase(IdCardEntity.Status status, String search, Pageable pageable);

}

