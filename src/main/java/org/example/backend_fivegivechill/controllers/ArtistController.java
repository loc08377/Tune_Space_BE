package org.example.backend_fivegivechill.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend_fivegivechill.beans.ArtistBean;
import org.example.backend_fivegivechill.entity.ArtistEntity;
import org.example.backend_fivegivechill.response.ArtistResponse;
import org.example.backend_fivegivechill.response.Response;
import org.example.backend_fivegivechill.services.ArtistService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@CrossOrigin("*")
@RequiredArgsConstructor // Tự động tạo constructor cho các biến final
public class ArtistController {

    private final ArtistService artistService;
    private final HttpServletRequest request;

    @GetMapping({"/artists", "/artists/trash", "/artists/review"})
    public ResponseEntity<Response> getArtists(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            String url = request.getRequestURI();

            Page<ArtistEntity> artistPage;
            if (url.equals("/admin/artists/trash")) {
                artistPage = artistService.getAllArtists(1, search, pageable);
            } else if (url.equals("/admin/artists/review")) {
                artistPage = artistService.getAllArtists(2, search, pageable);
            } else {
                artistPage = artistService.getAllArtists(0, search, pageable);
            }

            List<ArtistResponse> artistResponseList = artistPage.getContent().stream()
                    .map(artist -> new ArtistResponse(artist.getId(), artist.getFullName(), artist.getAvatar(),
                            artist.getHometown(), artist.getBiography(), artist.getStatus()))
                    .collect(Collectors.toList());

            Response response = new Response(0, "Success!", artistResponseList);
            response.setTotalPages(artistPage.getTotalPages());
            response.setTotalElements(page);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }

    //
    @GetMapping("/artists/{id}")
    public ResponseEntity<Response> getArtistById(@PathVariable int id) {
        try {
            ArtistEntity artistEntity = artistService.getArtistById(id);
            if (artistEntity == null) {
                return ResponseEntity.badRequest().body(new Response(1, "Artist not found", null));
            }

            ArtistResponse artistResponse = new ArtistResponse(
                    artistEntity.getId(), artistEntity.getFullName(),
                    artistEntity.getAvatar(), artistEntity.getHometown(),
                    artistEntity.getBiography(), artistEntity.getStatus());

            return ResponseEntity.ok(new Response(0, "Success!", artistResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }

    @PostMapping("/artists")
    public ResponseEntity<Response> addArtist(@Valid @RequestBody ArtistBean artistBean, Errors errors) {
        try {
            if (errors.hasErrors()) {
                Map<String, String> errorMap = new HashMap<>();
                errors.getFieldErrors().forEach(fieldError -> {
                    errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
                });
                return ResponseEntity.badRequest().body(new Response(1, "Validation failed", errorMap));
            }

            ArtistEntity existArtist = artistService.existArtistAdd(artistBean);
            if (existArtist != null) {
                return ResponseEntity.badRequest().body(new Response(1, "Đã tồn tại một nghệ sĩ giống vậy!", null));
            }

            ArtistEntity savedArtist = artistService.addArtist(artistBean);
            return ResponseEntity.status(HttpStatus.CREATED).body(new Response(0, "Success!", savedArtist));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }

    @PutMapping("/artists/{id}")
    public ResponseEntity<Response> updateArtist(@PathVariable int id, @Valid @RequestBody ArtistBean artistBean, Errors errors) {
        try {
            if (errors.hasErrors()) {
                Map<String, String> errorMap = new HashMap<>();
                errors.getFieldErrors().forEach(fieldError -> {
                    errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
                });
                return ResponseEntity.badRequest().body(new Response(1, "Validation failed", errorMap));
            }

            ArtistEntity existArtist = artistService.existArtistUpdate(id, artistBean);
            if (existArtist != null) {
                return ResponseEntity.badRequest().body(new Response(1, "Đã tồn tại một nghệ sĩ giống vậy!", null));
            }

            ArtistEntity updatedArtist = artistService.updateArtist(id, artistBean);
            if (updatedArtist == null) {
                return ResponseEntity.badRequest().body(new Response(1, "Artist not found", null));
            }

            return ResponseEntity.ok(new Response(0, "Success!", updatedArtist));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }
}

