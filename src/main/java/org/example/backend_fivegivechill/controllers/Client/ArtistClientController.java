package org.example.backend_fivegivechill.controllers.Client;

import org.example.backend_fivegivechill.entity.ArtistEntity;
import org.example.backend_fivegivechill.entity.ArtistSongEntity;
import org.example.backend_fivegivechill.entity.SongEntity;
import org.example.backend_fivegivechill.repository.ArtistRepository;
import org.example.backend_fivegivechill.repository.ArtistSongRepository;
import org.example.backend_fivegivechill.response.ArtistResponse;
import org.example.backend_fivegivechill.response.Response;
import org.example.backend_fivegivechill.response.SongResponese;
import org.example.backend_fivegivechill.response.TestURL;
import org.example.backend_fivegivechill.services.ArtistService;
import org.example.backend_fivegivechill.services.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
@CrossOrigin("*")
public class ArtistClientController {

    @Autowired
    SongService songService;
    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ArtistSongRepository artistSongRepository;

    @Autowired
    ArtistService artistService;

    @GetMapping("/song/artists/{artistId}")
    public ResponseEntity<Response> getHistory(@PathVariable int artistId) {
        try {

            List<SongEntity> artistSongEntities = artistSongRepository.getSongsByIdArtistId(artistId);

            if (artistSongEntities.isEmpty()) {
                return ResponseEntity.ok(new Response(0, "Không tìm thấy bài nhạc của ca sĩ này: ", null));
            }

            List<TestURL> songResponses = artistSongEntities.stream().map(item -> new TestURL(
                    item.getId(), item.getName(), item.isVipSong(),
                    item.getAvatar(), item.getCreateDate(), item.getDuration(), item.getStatus(),
                    songService.getCateNameBySongId(item.getId()), songService.getArtistNameBySongId(item.getId()), item.getAlbum() != null ? item.getAlbum().getName() : null, item.getLyrics()
            )).collect(Collectors.toList());


            return ResponseEntity.ok(new Response(0, "Lấy lịch sử thành công", songResponses));
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new Response(1, "Lấy lịch sử thất bại: " + e.getMessage(), null));
        }
    }

    @GetMapping("/artists/getById/{artistId}")
    public ResponseEntity<Response> getArtists(@PathVariable int artistId) {
        try {
            System.out.println(artistId);
            // Giả sử artistRepository là một Spring Data JPA repository
            ArtistEntity artistEntity = artistRepository.findById(artistId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy nghệ sĩ với ID: " + artistId));

            // Chuyển đổi ArtistEntity sang ArtistResponse
            ArtistResponse artistResponse =
                    new ArtistResponse(
                    artistEntity.getId(),
                    artistEntity.getFullName(),
                    artistEntity.getAvatar(),
                    artistEntity.getHometown(),
                    artistEntity.getBiography(),
                    artistEntity.getStatus()
            );

            return ResponseEntity.ok(new Response(0, "Lấy thông tin nghệ sĩ thành công", artistResponse));
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new Response(1, "Lấy thông tin nghệ sĩ thất bại: " + e.getMessage(), null));
        }
    }

    @GetMapping("/artist/personalized/{userId}")
    public ResponseEntity<Response> getPersonalizedArtist(@PathVariable int userId) {
        try {
            List<ArtistResponse> artistResponses = artistService.getPersonalizedArtist(userId);
          return ResponseEntity.ok(new Response(0, "successs", artistResponses));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }
}

