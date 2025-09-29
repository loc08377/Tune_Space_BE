package org.example.backend_fivegivechill.controllers.Client;

import lombok.RequiredArgsConstructor;
import org.example.backend_fivegivechill.beans.PlaylistBean;
import org.example.backend_fivegivechill.entity.PlaylistEntity;
import org.example.backend_fivegivechill.entity.SubscriptionPackageEntity;
import org.example.backend_fivegivechill.entity.UserEntity;
import org.example.backend_fivegivechill.repository.PlaylistItemRepository;
import org.example.backend_fivegivechill.repository.PlaylistRepository;
import org.example.backend_fivegivechill.repository.UserRepository;
import org.example.backend_fivegivechill.response.PlaylistResponse;
import org.example.backend_fivegivechill.response.PlaylistResponseNew;
import org.example.backend_fivegivechill.response.Response;
import org.example.backend_fivegivechill.services.PlaylistItemService;
import org.example.backend_fivegivechill.services.PlaylistService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/")
@RequiredArgsConstructor
public class PlayListClientController {

    private final PlaylistService playlistService;
    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;

    @GetMapping("/playlist/users/{id}")
    public ResponseEntity<Response> getPlaylist(@PathVariable int id,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<PlaylistResponse> packagePage;
            packagePage = playlistService.getPlaylistByUserId(pageable, id);
            List<PlaylistResponse> playlists = packagePage.getContent();
            Response response = new Response(0, "success", playlists);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }

    @GetMapping("/playlist/{id}")
    public ResponseEntity<Response> getPlaylistWithSongs(@PathVariable int id) {
        try {
            PlaylistResponseNew playlistResponse = playlistService.getPlaylistResponseNew(id);

            Response response = new Response(0, "success", playlistResponse);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }

//    @GetMapping("/playlist/top-10/{id}")
//    public ResponseEntity<Response> getTop10PlaylistByUser(@PathVariable int id,
//                                                           @RequestParam(defaultValue = "0") int page,
//                                                           @RequestParam(defaultValue = "10") int size) {
//        try {
//            Pageable pageable = PageRequest.of(page, size);
//            Page<PlaylistResponse> packagePage;
//            List<PlaylistResponse> playlistResponse = playlistService.getSongsUserHeardAndInTheirPlaylists(id, pageable);
//            Response response = new Response(0, "success", playlistResponse);
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
//        }
//    }

    @DeleteMapping("/playlist/delete/{id}")
    public ResponseEntity<Response> deletePlaylist(@PathVariable int id) {
        try {

            boolean check = playlistService.deletePlaylist(id);
            if (check) {
                Response response = new Response(0, "Xóa playlist thành công!", true);
                return ResponseEntity.ok(response);
            } else {
                Response response = new Response(1, "Xóa playlist meo thanh cong!", false);
                return ResponseEntity.ok(response);
            }

        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new Response(1, "Xóa playlist thất bại: " + e.getMessage(), null));
        }
}

    @PostMapping("/playlist/add-playlist")
    public ResponseEntity<Response> addPlaylist(@RequestBody PlaylistBean playlistBean) {
        try {

            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<UserEntity> userOpt = userRepository.findByEmail(email);

            PlaylistEntity existing = playlistRepository.findByName(playlistBean.getName());
            if (existing != null) {
                Response response = new Response(0, "Tên playlist đã tồn tại!", null);
                return ResponseEntity.ok(response);
            }

            PlaylistEntity playlistEntity = new PlaylistEntity();

            playlistEntity.setName(playlistBean.getName());

            playlistEntity.setUserEntity(userOpt.get());

            PlaylistEntity savedPlaylist = playlistRepository.save(playlistEntity);


            Response response = new Response(0, "Tạo playlist thành công!", savedPlaylist.getId());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }

    @PutMapping("/playlist/update-playlist")
    public ResponseEntity<Response> updatePlaylist(@RequestBody PlaylistBean playlistBean) {
        try {
            // Tìm playlist theo playlistId từ PlaylistBean
            PlaylistEntity existing = playlistRepository.findByName(playlistBean.getName());
            if (existing != null) {
                Response response = new Response(0, "Tên playlist đã tồn tại!", null);
                return ResponseEntity.ok(response);
            }
            PlaylistEntity playlistEntity = playlistRepository.findById(playlistBean.getId()).orElse(null);
            // Cập nhật tên playlist
            playlistEntity.setName(playlistBean.getName());
            playlistRepository.save(playlistEntity);

            Response response = new Response(0, "Cập nhật playlist thành công!", playlistEntity);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }

}
