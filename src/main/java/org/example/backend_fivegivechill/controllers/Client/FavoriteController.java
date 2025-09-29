package org.example.backend_fivegivechill.controllers.Client;

import org.example.backend_fivegivechill.entity.SongEntity;
import org.example.backend_fivegivechill.entity.SongFavoriteEntity;
import org.example.backend_fivegivechill.entity.UserEntity;
import org.example.backend_fivegivechill.repository.SongFavoriteRepository;
import org.example.backend_fivegivechill.repository.SongRepository;
import org.example.backend_fivegivechill.repository.UserRepository;
import org.example.backend_fivegivechill.response.Response;
import org.example.backend_fivegivechill.response.SongResponese;
import org.example.backend_fivegivechill.response.TestURL;
import org.example.backend_fivegivechill.services.FavoriteService;
import org.example.backend_fivegivechill.services.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin("*")
@RestController
@RequestMapping("/user")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SongRepository songRepository;
    @Autowired
    private SongFavoriteRepository songFavoriteRepository;

    @Autowired
    private SongService songService;

    @GetMapping("/favorite")
    public ResponseEntity<Response> getFavorite() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<UserEntity> userOpt = userRepository.findByEmail(email);

            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(new Response(1, "Người dùng không tồn tại", null));
            }

            List<SongEntity> songEntities = songFavoriteRepository.findSongFavoriteByUser(userOpt.get().getId());

            List<TestURL> songResponses = songEntities.stream().map(song -> new TestURL(
                    song.getId(), song.getName(), song.isVipSong(),
                    song.getAvatar(), song.getCreateDate(), song.getDuration(), song.getStatus(),
                    songService.getCateNameBySongId(song.getId()), songService.getArtistNameBySongId(song.getId()), song.getAlbum() != null ? song.getAlbum().getName() : null, song.getLyrics()

            )).collect(Collectors.toList());

            Response response = new Response(0, "Success!", songResponses);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }

    @GetMapping("/favorite/active") //aaaaaaaaaaaaaaaaaaaaaaaaaaa
    public ResponseEntity<Response> favoriteOfSongEndArtists() {
        Response response = new Response();
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<UserEntity> userOpt = userRepository.findByEmail(email);

            if (userOpt.isEmpty()) {
                response.setStatus(1);
                response.setMessage("Người dùng không tồn tại");
                response.setData(null);
                return ResponseEntity.badRequest().body(response);
            }

            List<SongResponese> favoriteList = favoriteService.favoriteOfSongEndArtists(userOpt.get());

            response.setStatus(0);
            response.setMessage("Lấy danh sách yêu thích thành công");
            response.setData(favoriteList);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.setStatus(1);
            response.setMessage("Lấy danh sách yêu thích thất bại -> " + e.getMessage());
            response.setData(null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/favorite/save/{songId}")
    public ResponseEntity<Response> saveFavorite(@PathVariable Integer songId) {
        Response response = new Response();
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<UserEntity> userOpt = userRepository.findByEmail(email);
            Optional<SongEntity> songOpt = songRepository.findById(songId);

            if (userOpt.isEmpty() || songOpt.isEmpty()) {
                response.setStatus(1);
                response.setMessage("Người dùng hoặc bài hát không tồn tại");
                response.setData(null);
                return ResponseEntity.badRequest().body(response);
            }

            UserEntity user = userOpt.get();
            SongEntity song = songOpt.get();

            Optional<SongFavoriteEntity> exists = favoriteService.findByUserIdAndSongId(user.getId(), song.getId());

            if (exists.isPresent()) {
                response.setStatus(1);
                response.setMessage("Bài hát đã được yêu thích trước đó");
                response.setData(null);
                return ResponseEntity.ok(response);
            }

            SongFavoriteEntity songFavoriteEntity = new SongFavoriteEntity();
            songFavoriteEntity.setUser(user);
            songFavoriteEntity.setSong(song);
            favoriteService.save(songFavoriteEntity);

            response.setStatus(0);
            response.setMessage("Thêm yêu thích thành công");
            response.setData(songFavoriteEntity);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.setStatus(1);
            response.setMessage("Thêm yêu thích thất bại -> " + e.getMessage());
            response.setData(null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/favorite/delete/{songId}")
    public ResponseEntity<Response> deleteFavorite(@PathVariable Integer songId) {
        Response response = new Response();
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<UserEntity> userOpt = userRepository.findByEmail(email);

            if (userOpt.isEmpty()) {
                response.setStatus(1);
                response.setMessage("Người dùng không tồn tại");
                response.setData(null);
                return ResponseEntity.badRequest().body(response);
            }

            UserEntity user = userOpt.get();
            favoriteService.delete(user.getId(), songId);

            response.setStatus(0);
            response.setMessage("Xóa yêu thích thành công");
            response.setData(null);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.setStatus(1);
            response.setMessage("Xóa yêu thích thất bại -> " + e.getMessage());
            response.setData(null);
            return ResponseEntity.badRequest().body(response);
        }
    }
}
