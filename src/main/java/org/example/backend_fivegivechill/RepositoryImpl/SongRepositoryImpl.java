package org.example.backend_fivegivechill.RepositoryImpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.example.backend_fivegivechill.RepositoryCustom.SongRepositoryCustom;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class SongRepositoryImpl implements SongRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> getPersonalizedSongs(int userId) {
        Query query = entityManager.createNativeQuery("EXEC sp_GetPersonalizedSongs :user_id");
        query.setParameter("user_id", userId);
        return query.getResultList();

    }
}
