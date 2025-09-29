package org.example.backend_fivegivechill.controllers.Client;

import org.example.backend_fivegivechill.entity.SongEntity;
import org.example.backend_fivegivechill.response.Response;
import org.example.backend_fivegivechill.response.SongSearchResponse;
import org.example.backend_fivegivechill.services.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/filter")
@CrossOrigin("*")
public class SearchController {
    @Autowired
    private SongService songService;

    @GetMapping("/search")
    public ResponseEntity<Response> searchSongs(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) List<Integer> types
    ) {
        try{
            Pageable pageable = PageRequest.of(page, size);
            Page<SongSearchResponse> result = songService.searchSongs(keyword, pageable, types);
            return ResponseEntity.ok(new Response(0, "Success", result, result.getTotalElements(), result.getTotalPages()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new Response(0, "Error", null));
        }
    }
}
