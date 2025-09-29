package org.example.backend_fivegivechill.repository;

import org.example.backend_fivegivechill.entity.CategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer> {
    @Query("SELECT c FROM CategoryEntity c WHERE c.status = :status AND c.name LIKE :search ORDER BY c.id DESC")
    Page<CategoryEntity> findAllByStatus(@Param("status") int status, @Param("search") String search, Pageable pageable);

    @Query("SELECT c FROM CategoryEntity c WHERE c.status IN (0, 2)")
    List<CategoryEntity> getAll();

    @Query(value = "SELECT * FROM categories WHERE name = ?1", nativeQuery = true)
    CategoryEntity existByName(String name);

    @Query(value = "SELECT * FROM categories WHERE name = ?1 AND id != ?2", nativeQuery = true)
    CategoryEntity existByNameAndId(String name, int id);
}
