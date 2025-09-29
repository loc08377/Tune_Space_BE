package org.example.backend_fivegivechill.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend_fivegivechill.beans.SongBean;
import org.example.backend_fivegivechill.entity.ReportEntity;
import org.example.backend_fivegivechill.entity.SongEntity;
import org.example.backend_fivegivechill.repository.ReportRepository;
import org.example.backend_fivegivechill.response.ReportResponse;
import org.example.backend_fivegivechill.response.Response;
import org.example.backend_fivegivechill.response.SongResponese;
import org.example.backend_fivegivechill.services.CountListenService;
import org.example.backend_fivegivechill.services.SongService;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequiredArgsConstructor
@CrossOrigin("*")
public class SongController {

    private final SongService songService;
    private final HttpServletRequest request;

    @Autowired
    private CountListenService countListenService;
    @Autowired
    private ReportRepository reportRepository;

    // Lấy danh sách bài hát (đã xoá hoặc chưa xoá dựa vào URL)
    @GetMapping({"/songs", "/songs/trash", "/songs/review", "/songs/violate"})
    public ResponseEntity<Response> getSongs(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            String url = request.getRequestURI();
            Page<SongEntity> songPage;

            if (url.equals("/admin/songs/trash")) {
                songPage = songService.getAllSongs(1, search, pageable);
            } else if (url.equals("/admin/songs/review")) {
                songPage = songService.getAllSongs(2, search, pageable);
            } else if (url.equals("/admin/songs/violate")) {
                songPage = songService.getAllSongs(3, search, pageable);

            } else {
                songPage = songService.getAllSongs(0, search, pageable);
            }

            List<SongResponese> songResponseList = songPage.getContent().stream()
                    .map(song -> new SongResponese(
                            song.getId(),
                            song.getName(),
                            song.getMp3File(),
                            song.isVipSong(),
                            song.getAvatar(),
                            song.getCreateDate(),
                            song.getStatus(),
                            countListenService.calculatorCountListenToDay(song.getId()) + song.getCountListens(),
                            song.getDuration(),
                            song.getUser().getFullName(),
                            song.getLyrics(),
                            songService.getCateSongs(song.getId()),
                            songService.getArtistSongs(song.getId()),
                            songService.getArtistSongss(song.getId()),
                            song.isVipSong()
                                    ? (countListenService.calculatorCountListenToDay(song.getId()) + song.getCountListens()) * 20
                                    : (countListenService.calculatorCountListenToDay(song.getId()) + song.getCountListens()) * 10
                    )).collect(Collectors.toList());

            Response response = new Response(0, "Success!", songResponseList);
            response.setTotalPages(songPage.getTotalPages());
            response.setTotalElements(page);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }

    // Lấy thông tin chi tiết bài hát
    @GetMapping("/songs/{id}")
    public ResponseEntity<Response> getSongById(@PathVariable int id) {
        try {
            SongEntity songEntity = songService.getSongById(id);
            if (songEntity == null) {
                return ResponseEntity.badRequest().body(new Response(1, "Song not found", null));
            }

            SongResponese songResponse = new SongResponese(
                    songEntity.getId(),
                    songEntity.getName(),
                    songEntity.getMp3File(),
                    songEntity.isVipSong(),
                    songEntity.getAvatar(),
                    songEntity.getCreateDate(),
                    songEntity.getStatus(),
                    countListenService.calculatorCountListenToDay(songEntity.getId()) + songEntity.getCountListens(),
                    songEntity.getDuration(),
                    songEntity.getUser().getFullName(),
                    songEntity.getLyrics(),
                    songService.getCateSongs(songEntity.getId()),
                    songService.getArtistSongs(songEntity.getId()),
                    songService.getArtistSongss(songEntity.getId())
            );

            return ResponseEntity.ok(new Response(0, "Success!", songResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }

    // Thêm bài hát mới
    @PostMapping("/songs")
    public ResponseEntity<Response> addSong(@Valid @RequestBody SongBean songBean, Errors errors) {
        if (errors.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            errors.getFieldErrors().forEach(fieldError -> {
                errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(new Response(1, "Validation failed", errorMap));
        }

        try {
            SongEntity savedSong = songService.addSong(songBean);
            if (savedSong == null) {
                return ResponseEntity.badRequest().body(new Response(1, "Không tìm thấy người dùng!", null));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(new Response(0, "Success!", savedSong));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }

    // Cập nhật bài hát
    @PutMapping("/songs/{id}")
    public ResponseEntity<Response> updateSong(@PathVariable int id, @Valid @RequestBody SongBean songBean, Errors errors) {
        if (errors.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            errors.getFieldErrors().forEach(fieldError -> {
                errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(new Response(1, "Validation failed", errorMap));
        }

        try {
            SongEntity updatedSong = songService.updateSong(id, songBean);
            if (updatedSong == null) {
                return ResponseEntity.badRequest().body(new Response(1, "Song not found", null));
            }
            return ResponseEntity.ok(new Response(0, "Success!", updatedSong));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }
    @GetMapping("/ReportConfirm")
    public ResponseEntity<Response> getAllReportByStatus3() {
        try {

            List<ReportEntity> reportEntities = reportRepository.findAll();

            List<ReportResponse> songResponses = reportEntities.stream().map(item -> new ReportResponse(
                    item.getId(), item.getSong().getId(), item.getSong().getName(),
                    item.getSong().getAvatar(), item.getSong().isVipSong(), item.getSong().getStatus(), item.getContent()
            )).collect(Collectors.toList());

            return ResponseEntity.ok(new Response(0, "Success!", songResponses));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .badRequest()
                    .body(new Response(1, "Đã xảy ra lỗi khi lấy danh sách báo cáo", null));
        }
    }


}
