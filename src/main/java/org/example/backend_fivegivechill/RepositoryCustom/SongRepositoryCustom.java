package org.example.backend_fivegivechill.RepositoryCustom;

import java.util.List;


public interface SongRepositoryCustom {
    List<Object[]> getPersonalizedSongs(int userId);
}
