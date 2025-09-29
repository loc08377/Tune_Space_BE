package org.example.backend_fivegivechill.RepositoryCustom;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.example.backend_fivegivechill.response.SongSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class SongSearchRepositoryCustomImpl implements SongSearchRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<SongSearchResponse> searchSongs(String keyword, Pageable pageable, List<Integer> types) {
        String[] words = keyword.toLowerCase().split("\\s+");

        System.out.println(types);
        System.out.println("oooooooooooooooooooo");
        if (types == null || types.isEmpty()) {
            // nếu rỗng thì mặc định = [1,2,3,4]
            types = Arrays.asList(1, 2, 3, 4);
        }

        // Build search filter (WHERE ...)
        StringBuilder filterBuilder = new StringBuilder(" WHERE 1=1 ");
        for (int i = 0; i < words.length; i++) {
            String param = "word" + i;
            StringBuilder condition = new StringBuilder(" AND (");

            List<String> fields = new ArrayList<>();
            if (types.contains(1)) {
                fields.add("LOWER(A.lyrics) LIKE CONCAT('%', :" + param + ", '%')");
            }
            if (types.contains(2)) {
                fields.add("LOWER(N.name) LIKE CONCAT('%', :" + param + ", '%')");
            }
            if (types.contains(3)) {
                fields.add("LOWER(Q.full_name) LIKE CONCAT('%', :" + param + ", '%')");
            }
            if (types.contains(4)) {
                fields.add("LOWER(A.name) LIKE CONCAT('%', :" + param + ", '%')");
            }
            // Nối bằng OR
            condition.append(String.join(" OR ", fields));
            condition.append(")");

            filterBuilder.append(condition);
        }

        // Build main query
        StringBuilder queryBuilder = new StringBuilder(
                "SELECT " +
                        " A.id, " +
                        " A.name, " +
                        " A.avatar, " +
                        " A.vip_song, " +
                        " ISNULL(( " +
                        "   SELECT STRING_AGG(F.name, ', ') " +
                        "   FROM cate_song D " +
                        "   JOIN categories F ON F.id = D.cate_id " +
                        "   WHERE D.song_id = A.id " +
                        " ), '') AS category_names, " +
                        " ISNULL(( " +
                        "   SELECT STRING_AGG(G.full_name, ', ') " +
                        "   FROM artist_song E " +
                        "   JOIN artist G ON G.id = E.artist_id " +
                        "   WHERE E.song_id = A.id AND E.type = 0 " +
                        " ), '') AS artist_names, " +
                        " ISNULL(( " +
                        "   SELECT STRING_AGG(G.full_name, ', ') " +
                        "   FROM artist_song E " +
                        "   JOIN artist G ON G.id = E.artist_id " +
                        "   WHERE E.song_id = A.id AND E.type = 1 " +
                        " ), '') AS artist_name, " +
                        " C.full_name AS user_name " +
                        "FROM songs A " +
                        "LEFT JOIN users C ON C.id = A.user_id " +
                        "LEFT JOIN cate_song M ON M.song_id = A.id " +
                        "LEFT JOIN categories N ON N.id = M.cate_id " +
                        "LEFT JOIN artist_song P ON P.song_id = A.id " +
                        "LEFT JOIN artist Q ON Q.id = P.artist_id "
        ).append(filterBuilder).append("AND A.status = 0")
                .append(" ORDER BY A.id OFFSET :offset ROWS FETCH NEXT :limit ROWS ONLY");

        // Build count query
        StringBuilder countBuilder = new StringBuilder(
                "SELECT COUNT(DISTINCT A.id) FROM songs A " +
                        "LEFT JOIN users C ON C.id = A.user_id " +
                        "LEFT JOIN cate_song M ON M.song_id = A.id " +
                        "LEFT JOIN categories N ON N.id = M.cate_id " +
                        "LEFT JOIN artist_song P ON P.song_id = A.id " +
                        "LEFT JOIN artist Q ON Q.id = P.artist_id "
        ).append(filterBuilder);

        // Create native queries
        Query query = entityManager.createNativeQuery(queryBuilder.toString());
        Query countQuery = entityManager.createNativeQuery(countBuilder.toString());

        // Set parameters
        for (int i = 0; i < words.length; i++) {
            query.setParameter("word" + i, words[i]);
            countQuery.setParameter("word" + i, words[i]);
        }

        query.setParameter("offset", pageable.getOffset());
        query.setParameter("limit", pageable.getPageSize());

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.getResultList();

        // Convert rows to list of SongSearchResponse
        List<SongSearchResponse> rawResults = rows.stream()
                .map(row -> new SongSearchResponse(
                        (Integer) row[0],
                        (String) row[1],
                        (String) row[2],
                        row[3] != null && ((Boolean) row[3]),
                        (String) row[4],
                        (String) row[5],
                        (String) row[6],
                        (String) row[7]
                )).toList();

        // Remove duplicates by ID using LinkedHashMap
        Map<Integer, SongSearchResponse> deduplicatedMap = new LinkedHashMap<>();
        for (SongSearchResponse song : rawResults) {
            deduplicatedMap.putIfAbsent(song.getId(), song);
        }

        List<SongSearchResponse> results = List.copyOf(deduplicatedMap.values());

        long total = ((Number) countQuery.getSingleResult()).longValue();

        return new PageImpl<>(results, pageable, total);
    }
}
