package org.example.backend_fivegivechill.RepositoryCustom;

import java.util.List;


public interface ArtistRepositoryCustom {

    List<Object[]> getPersonalizedArtists(int userId);
}
