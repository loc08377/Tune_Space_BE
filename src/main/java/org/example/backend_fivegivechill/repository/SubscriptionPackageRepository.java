package org.example.backend_fivegivechill.repository;

import org.example.backend_fivegivechill.entity.SubscriptionPackageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubscriptionPackageRepository extends JpaRepository<SubscriptionPackageEntity, Integer> {

    @Query("SELECT s FROM SubscriptionPackageEntity s WHERE s.status = :status AND s.name LIKE :search ORDER BY s.id DESC")
    Page<SubscriptionPackageEntity> findAllByStatus(@Param("status") boolean status, @Param("search") String search, Pageable pageable);

    @Query("SELECT s FROM SubscriptionPackageEntity s WHERE s.status = :status ORDER BY s.id DESC")
    Page<SubscriptionPackageEntity> findAllByStatusClient(@Param("status") boolean status, Pageable pageable);

    @Query(value = "SELECT * FROM subscription_package WHERE name = ?1 AND duration = ?2 AND price = ?3", nativeQuery = true)
    SubscriptionPackageEntity existByName(String name, int duration, int price);

    @Query(value = "SELECT * FROM subscription_package WHERE name = ?1 AND duration = ?2 AND price = ?3 AND id != ?4", nativeQuery = true)
    SubscriptionPackageEntity existByNameAndId(String name, int duration, int price, int id);
}
