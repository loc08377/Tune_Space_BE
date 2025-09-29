package org.example.backend_fivegivechill.RepositoryCustom;

import org.example.backend_fivegivechill.response.SongSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SongSearchRepositoryCustom {
    Page<SongSearchResponse> searchSongs(String keyword, Pageable pageable, List<Integer> types);
}
