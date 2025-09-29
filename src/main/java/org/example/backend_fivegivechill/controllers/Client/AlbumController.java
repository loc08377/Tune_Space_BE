package org.example.backend_fivegivechill.controllers.Client;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend_fivegivechill.beans.SongsAlbumBean;
import org.example.backend_fivegivechill.beans.AlbumBean;
import org.example.backend_fivegivechill.entity.AlbumEntity;
import org.example.backend_fivegivechill.entity.SongEntity;
import org.example.backend_fivegivechill.response.AlbumResponse;
import org.example.backend_fivegivechill.response.Response;
import org.example.backend_fivegivechill.services.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/creator/albums")
public class AlbumController {

    @Autowired
    private AlbumService albumService;

    private final HttpServletRequest request;

    // Endpoint để tạo album mới cho nghệ sĩ
    @PostMapping("/create")
    public ResponseEntity<Response> createAlbum(@Valid @RequestBody AlbumBean albumBean, Errors errors) {
        try {
            if (errors.hasErrors()) {
                Map<String, String> errorMap = new HashMap<>();
                errors.getFieldErrors().forEach(fieldError -> {
                    errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
                });
                return ResponseEntity.badRequest().body(new Response(1, "Validation failed" + errorMap, errorMap));
            }

            AlbumEntity albumEntity = albumService.createAlbum(albumBean);
            if (albumEntity == null) {
                return ResponseEntity.badRequest().body(new Response(1, "Error!", null));
            }
            return ResponseEntity.ok(new Response(0, "Success!", albumEntity));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new Response(1, "lỗi khi thêm album!", null));
        }
    }

    // Endpoint để tạo album mới cho nghệ sĩ
    @PutMapping("/update/{id}")
    public ResponseEntity<Response> updateAlbum(@Valid @RequestBody AlbumBean albumBean,
                                                @PathVariable int id,
                                                Errors errors) {
        try {
            if (errors.hasErrors()) {
                Map<String, String> errorMap = new HashMap<>();
                errors.getFieldErrors().forEach(fieldError -> {
                    errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
                });
                return ResponseEntity.badRequest().body(new Response(1, "Validation failed", errorMap));
            }

            AlbumEntity albumEntity = albumService.updateAlbum(id, albumBean);
            if (albumEntity == null) {
                return ResponseEntity.badRequest().body(new Response(1, "Error!", null));
            }
            return ResponseEntity.ok(new Response(0, "Success!", albumEntity));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new Response(1, "lỗi khi cập nhật album!", null));
        }
    }

    //  lấy tất cả album của user
    @GetMapping({"/all", "/all-trash"})
    public ResponseEntity<Response> getAllAlbumsByUserId(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            String url = request.getRequestURI();
            Page<AlbumEntity> albumPage;
            if (url.equals("/creator/albums/all")) {
                albumPage = albumService.getAllAlbumsByUserId(1, search, pageable);
            } else {
                albumPage = albumService.getAllAlbumsByUserId(0, search, pageable);
            }

            List<AlbumResponse> albumResponse = albumPage.getContent().stream()
                    .map(album -> new AlbumResponse(
                            album.getId(), album.getName(), album.getCoverImage(), album.isStatus(), album.getUser().getFullName(), null, null
                    )).collect(Collectors.toList());


            
            Response response = new Response(0, "Success!", albumResponse);
            response.setTotalPages(albumPage.getTotalPages());
            response.setTotalElements(page);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new Response(1, "Lỗi khi tải danh sách album!", null));
        }
    }

    // lấy tất cả bài hát cảu user
    @GetMapping("/songs/{albumId}")
    public ResponseEntity<Response> getSongByCreator(@PathVariable Long albumId) {
        try {
            return ResponseEntity.ok(new Response(0, "Success!", albumService.getSongsByUserId(albumId)));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new Response(1, "Lỗi khi lấy danh sách bài hát!", null));
        }
    }

    // thêm hoặc cập nhật bài hát vào album
    @PutMapping("/update-songs")
    public ResponseEntity<Response> updateSongsInAlbum(@RequestBody SongsAlbumBean albumBean) {
        try {
            boolean success = albumService.updateSongsInAlbum(albumBean);
            if (success) {
                return ResponseEntity.ok(new Response(0, "Success!", null));
            } else {
                return ResponseEntity.badRequest().body(new Response(1, "Album không tồn tại!", null));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new Response(1, "Lỗi khi cập nhật bài hát trong album!", null));
        }
    }

    @GetMapping("/{albumId}/songs")
    public ResponseEntity<Response> getSongsInAlbum(@PathVariable Long albumId) {
        List<SongEntity> songs = albumService.getSongsInAlbum(albumId);
        return ResponseEntity.ok(new Response(0, "Success!", songs));
    }

}
