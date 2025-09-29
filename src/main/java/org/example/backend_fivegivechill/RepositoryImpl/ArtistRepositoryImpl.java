package org.example.backend_fivegivechill.RepositoryImpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.backend_fivegivechill.RepositoryCustom.ArtistRepositoryCustom;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class ArtistRepositoryImpl implements ArtistRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> getPersonalizedArtists(int userId) {
        Query query = entityManager.createNativeQuery("EXEC sp_GetPersonalizedArtists :user_id");
        query.setParameter("user_id", userId);
        return query.getResultList();
    }
}
