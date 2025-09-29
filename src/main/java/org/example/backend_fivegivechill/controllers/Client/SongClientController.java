package org.example.backend_fivegivechill.controllers.Client;

import lombok.RequiredArgsConstructor;
import org.example.backend_fivegivechill.response.PlaylistResponse;
import org.example.backend_fivegivechill.response.Response;
import org.example.backend_fivegivechill.response.SongResponese;
import org.example.backend_fivegivechill.services.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/")
@RequiredArgsConstructor
public class SongClientController {


    @Autowired
    SongService songService;


    @GetMapping("/songs/heard-in-playlists/{userId}")
    public ResponseEntity<Response> getSongsUserHeardAndInTheirPlaylists(
            @PathVariable("userId") int userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            List<SongResponese> songResponses = songService.getSongsUserHeardAndInTheirPlaylists(userId, pageable);
            Response response = new Response(0, "Success", songResponses);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new Response(1, "Lỗi: " + e.getMessage(), null));
        }
    }

    @GetMapping("/songs/personalized/{userId}")
    public ResponseEntity<Response> getPersonalizedSongs(@PathVariable("userId") int userId) {
        try {
            List<SongResponese> songResponses = songService.getPersonalizedSongs(userId);
            Response response = new Response(0, "Success", songResponses);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new Response(1, "Lỗi: " + e.getMessage(), null));
        }
    }




}
