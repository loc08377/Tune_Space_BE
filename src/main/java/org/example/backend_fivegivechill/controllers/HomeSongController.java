package org.example.backend_fivegivechill.controllers;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.example.backend_fivegivechill.beans.ArtistBean;
import org.example.backend_fivegivechill.beans.CategoryBean;
import org.example.backend_fivegivechill.entity.*;
import org.example.backend_fivegivechill.repository.*;
import org.example.backend_fivegivechill.response.*;
import org.example.backend_fivegivechill.services.*;
import org.example.backend_fivegivechill.utils.Encryption;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpRange;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
@CrossOrigin("*")
public class HomeSongController {

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private SongService songService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ArtistService artistService;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private Encryption encryption;

    @Value("${encryption}")
    private String encryptionKey;

    @Autowired
    private PlaylistService playlistService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private SubscriptionPackage_userService subscriptionPackage_userService;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private SubscriptionUserRepository subscriptionUserRepository;

    @GetMapping("/musicNewReleased")
    public ResponseEntity<Response> Songs(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "9") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<SongEntity> songs = songRepository.musicNewReleased(pageable);
            List<TestURL> songResponses = songs.stream().map(song -> new TestURL(
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

    @GetMapping("/bxhMusicNewReleased")
    public ResponseEntity<Response> top50SongsHaveMuchCountListens() {
        try {
            List<SongEntity> bxhSong = songRepository.callProcedureTopTrending();
            List<TestURL> songResponses = bxhSong.stream().map(song -> new TestURL(
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

    @GetMapping("/songs/{id}")
    public ResponseEntity<Response> getSongById(@PathVariable int id) {
        try {
            AlbumEntity album = songRepository.findAlbumBySongId(id);
            SongEntity song = songRepository.findById(id).orElse(null);

            if (album != null) {
                List<SongEntity> songEntities = songRepository.findAllByAlbumId(Long.valueOf(album.getId()));
                List<TestURL> songResponses = songEntities.stream().map(item -> new TestURL(
                        item.getId(), item.getName(), item.isVipSong(),
                        item.getAvatar(), item.getCreateDate(), item.getDuration(), item.getStatus(),
                        songService.getCateNameBySongId(item.getId()), songService.getArtistNameBySongId(item.getId()),item.getAlbum() != null ? item.getAlbum().getName() : null, item.getLyrics()
                )).collect(Collectors.toList());

                AlbumResponse albumResponse = new AlbumResponse(
                        album.getId(), album.getName(), album.getCoverImage(),
                        album.isStatus(), album.getUser().getFullName(), songResponses, null
                );
                Response response = new Response(0, "Album", albumResponse);
                return ResponseEntity.ok(response);
            }

            TestURL songRespone = new TestURL(
                    song.getId(), song.getName(), song.isVipSong(),
                    song.getAvatar(), song.getCreateDate(), song.getDuration(), song.getStatus(),
                    songService.getCateNameBySongId(song.getId()), songService.getArtistNameBySongId(song.getId()), song.getAlbum() != null ? song.getAlbum().getName() : null, song.getLyrics()
            );

            Response response = new Response(0, "Single", songRespone);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }

    @GetMapping("/playlist")
    public ResponseEntity<Response> getPlaylist(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<UserEntity> userOpt = userRepository.findByEmail(email);

            Pageable pageable = PageRequest.of(page, size);
            Page<PlaylistResponse> packagePage;

            packagePage = playlistService.getPlaylistByUserId(pageable, userOpt.get().getId());

            List<PlaylistResponse> playlists = packagePage.getContent();

            Response response = new Response(0, "success", playlists);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }


    @GetMapping("/streaming")
    public ResponseEntity<Response> generateSignedUrl(@RequestParam int songId) {
        try {
            String userID = request.getHeader("userID");
            String anonymousID = request.getHeader("anonymousID");
            String actualUserId = (userID != null) ? userID : anonymousID;
            String url = songService.generateSignedUrl(songId, actualUserId);

            boolean check = false;

            if(userID != null){
                int userDecode = Integer.parseInt(encryption.decrypt(userID));
                SubscriptionStatusResponse subSubscriptionStatusResponse = subscriptionPackage_userService.getSubscriptionStatus(userDecode);
                check = subSubscriptionStatusResponse.isVip();
            }

            GenerateSignedUrlRespone signedUrlRespone = new GenerateSignedUrlRespone(url, check);
            Response response = new Response(0, "Success!", signedUrlRespone);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }

    @GetMapping("/stream")
    public ResponseEntity<Resource> streamSong(
            @RequestParam int songId,
            @RequestParam String userId,

            @RequestParam Long expires,
            @RequestParam String signature,
            @RequestHeader HttpHeaders headers) throws IOException {
        try {
            String referer = headers.getFirst("Referer");
            String origin = headers.getFirst("Origin");

            boolean validReferer = referer != null &&
                    (referer.contains("http://localhost:3000/") || referer.contains("http://103.101.163.203/:3000/"));
            boolean validOrigin = origin != null &&
                    (origin.contains("http://localhost:3000/") || origin.contains("http://103.101.163.203/:3000/"));

            if (!validReferer && !validOrigin) {
                System.out.println("Lỗi nè");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); // 403: Invalid origin
            }

            String dataToVerify = songId + ":" + userId + ":" + expires;

            String computedSignature = encryption.sign(dataToVerify, encryptionKey);

            if (!computedSignature.equals(signature)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); // 403: Invalid signature
            }

            // check thời gian sống của url, hết hạn thì chặn
            long currentTime = Instant.now().getEpochSecond();
            if (currentTime > expires) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); // 403: URL expired
            }

            boolean checkUserVip = false;

            if (userId != null) {
                try {
                    int userID = Integer.parseInt(encryption.decrypt(userId));
                    SubscriptionStatusResponse subSubscriptionStatusResponse = subscriptionPackage_userService.getSubscriptionStatus(userID);
                    checkUserVip = subSubscriptionStatusResponse.isVip();
                } catch (Exception e) {
                    checkUserVip = false;
                }
            }

            boolean checkSongVip = songRepository.isVip(songId);

            URL url = new URL(songService.getStreamUrl(songId, checkUserVip, checkSongVip));

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            long contentLength = connection.getContentLengthLong();
            InputStream inputStream;

            // kiểm tra coi ngta có tua không nếu tua thì xử lý theo tua không thì trả về nguyên đoạn
            HttpRange range = headers.getRange().isEmpty() ? null : headers.getRange().get(0);

            if (range != null && range.getRangeStart(contentLength) > 0) {
                long start = range.getRangeStart(contentLength);
                long end = range.getRangeEnd(contentLength);
                long rangeLength = end - start + 1;
                connection.disconnect();
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Range", "bytes=" + start + "-" + end);
                inputStream = connection.getInputStream();
                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                        .header("Content-Range", "bytes " + start + "-" + end + "/" + contentLength)
                        .contentType(MediaType.parseMediaType("audio/mpeg"))
                        .contentLength(rangeLength)
                        .body(new InputStreamResource(inputStream));
            }

            inputStream = connection.getInputStream();
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("audio/mpeg"))
                    .contentLength(contentLength)
                    .header("Accept-Ranges", "bytes")
                    .body(new InputStreamResource(inputStream));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/listSongs/{ids}")
    public ResponseEntity<Response> getListId(@PathVariable("ids") List<Integer> ids) {
        try {

            List<SongEntity> songs = songRepository.test(ids);
            songs.sort(Comparator.comparingInt(song -> ids.indexOf(song.getId())));
            List<TestURL> songResponses = songs.stream().map(song -> new TestURL(
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

    @GetMapping("/historyUser")
    public ResponseEntity<Response> getHistory() {
        try {

            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<UserEntity> userOpt = userRepository.findByEmail(email);
            List<HistoryEntity> history = historyRepository.findByUserEntityId(userOpt.get().getId());
            List<TestURL> songResponses = history.stream().map(song -> new TestURL(
                    song.getSongEntity().getId(), song.getSongEntity().getName(), song.getSongEntity().isVipSong(),
                    song.getSongEntity().getAvatar(), song.getCreateDate(), song.getSongEntity().getDuration(), song.getSongEntity().getStatus(),
                    songService.getCateNameBySongId(song.getSongEntity().getId()), songService.getArtistNameBySongId(song.getSongEntity().getId()), song.getSongEntity().getAlbum() != null ? song.getSongEntity().getAlbum().getName() : null, song.getSongEntity().getLyrics()
            )).collect(Collectors.toList());
            return ResponseEntity.ok(new Response(0, "Lấy lịch sử thành công", songResponses));
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new Response(1, "Lấy lịch sử thất bại: " + e.getMessage(), null));
        }
    }

    @GetMapping("/recommendByPlaylist")
    public ResponseEntity<Response> recommendByPlaylist(@RequestParam List<Integer> ids) {
        try {

            String joinedIds = ids.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));

            List<SongEntity> top50Songs = songRepository.callProcedure(joinedIds);

            List<TestURL> songResponses = top50Songs.stream().map(song -> new TestURL(
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

    @GetMapping("/recommendUser")
    public ResponseEntity<Response> recommendUser() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<UserEntity> userOpt = userRepository.findByEmail(email);

            int id = 0;
            if(userOpt.isPresent()) {
                id = userOpt.get().getId();
            }

            List<Object[]> recommend = songRepository.callProcedureRecommend(id);

            List<TestURL> songResponses = recommend.stream().map(song -> new TestURL(
                    (Integer) song[0],(String) song[7], (Boolean) song[9],
                    (String) song[1], (Date) song[3],(Integer) song[4],(Integer) song[8],
                    songService.getCateNameBySongId((Integer) song[0]), songService.getArtistNameBySongId((Integer) song[0]), null, songService.getSongById((Integer) song[0]).getLyrics()

            )).collect(Collectors.toList());

            Response response = new Response(0, "Success!", songResponses);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }

    @GetMapping("/top5new")
    public ResponseEntity<Response> top3NewSongs() {
        try {
            List<SongEntity> top3Songs = songRepository.findTop3ByStatusTrueOrderByCreateDateDesc();


            List<TestURL> songResponses = top3Songs.stream().map(song -> new TestURL(
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

    @GetMapping("/top5artist")
    public ResponseEntity<Response> top5Artists() {
        try {
            // Lấy 5 nghệ sĩ từ database (có thể là mới nhất, nổi bật nhất tùy logic bạn chọn)
            List<ArtistEntity> top5Artists = artistRepository.fingTop5Artist(); // native query LIMIT 5
            // Chuyển sang danh sách phản hồi dạng ArtistResponse (tạo class nếu chưa có)
            List<ArtistResponse> artistResponses = top5Artists.stream().map(artist -> new ArtistResponse(
                    artist.getId(),
                    artist.getFullName(),
                    artist.getAvatar(),
                    artist.getHometown(),
                    artist.getBiography(),
                    artist.getStatus()
            )).collect(Collectors.toList());
            // Trả về phản hồi thành công
            Response response = new Response(0, "Success!", artistResponses);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }

    @GetMapping("/top5Creator")
    public ResponseEntity<Response> top5Creator() {
        try {
            List<UserEntity> top5Creator = userRepository.getTop5Creator();
            List<UserResponse> UserResponse = top5Creator.stream().map(item -> new UserResponse(
                    item.getId(),
                    item.getEmail(),
                    item.getFullName(),
                    item.getPhone(),
                    item.getAvatar(),
                    item.isStatus(),
                    item.getRole()
            )).collect(Collectors.toList());
            Response response = new Response(0, "Success!", UserResponse);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }

    @GetMapping("/albumAll")
    public ResponseEntity<Response> albumAll() {
        try {
            List<AlbumEntity> albumAll = albumRepository.findAll();

            List<AlbumResponse> albumResponse = albumAll.stream().map(album -> new AlbumResponse(
                            album.getId(), album.getName(), album.getCoverImage(), album.isStatus(), album.getUser().getFullName(), null, songService.getIdsSongByAlbumId(album.getId())
                    )).collect(Collectors.toList());
            Response response = new Response(0, "Success!", albumResponse);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }

    @GetMapping("/album/{id}")
    public ResponseEntity<Response> getAlbumByIDs(@PathVariable int id) {
        try {

            AlbumEntity album = albumRepository.findById(id).orElse(null);
            List<SongEntity> songEntities = songRepository.findAllByAlbumId(Long.valueOf(album.getId()));

            List<TestURL> songResponses = songEntities.stream().map(item -> new TestURL(
                    item.getId(), item.getName(), item.isVipSong(),
                    item.getAvatar(), item.getCreateDate(), item.getDuration(), item.getStatus(),
                    songService.getCateNameBySongId(item.getId()), songService.getArtistNameBySongId(item.getId()), item.getAlbum() != null ? item.getAlbum().getName() : null, item.getLyrics()
            )).collect(Collectors.toList());

            AlbumResponse albumResponse = new AlbumResponse(
                    album.getId(), album.getName(), album.getCoverImage(),
                    album.isStatus(), album.getUser().getFullName(), songResponses, null
            );

            Response response = new Response(0, "Album", albumResponse);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }

    @GetMapping("/getCreatorById/{id}")
    public ResponseEntity<Response> getCreatorById(@PathVariable int id) {
        try {
            UserEntity userEntity = userRepository.findById(id).orElse(null);

            int numberFollow = subscriptionUserRepository.statisticsSumUsersCreator(id);
            int numberSong = subscriptionUserRepository.statisticsSumSongsCreator(id);
            int numberAlbum = subscriptionUserRepository.statisticsSumAlbumCreator(id);

            UserResponse userResponse = new UserResponse(
                userEntity.getId(), userEntity.getEmail(), userEntity.getFullName(), userEntity.getPhone(),
                    userEntity.getAvatar(), userEntity.isStatus(), userEntity.getRole()
            );

            List<AlbumEntity> albumEntities = albumRepository.findAlbumByUserId(id);

            List<AlbumResponse> albumResponse = albumEntities.stream().map(album -> new AlbumResponse(
                    album.getId(), album.getName(), album.getCoverImage(), album.isStatus(), album.getUser().getFullName(), null, songService.getIdsSongByAlbumId(album.getId())
            )).collect(Collectors.toList());

            List<SongEntity> songEntity = songRepository.findAllByCreator(id);

            List<TestURL> songResponses = songEntity.stream().map(item -> new TestURL(
                    item.getId(), item.getName(), item.isVipSong(),
                    item.getAvatar(), item.getCreateDate(), item.getDuration(), item.getStatus(),
                    songService.getCateNameBySongId(item.getId()), songService.getArtistNameBySongId(item.getId()), item.getAlbum() != null ? item.getAlbum().getName() : null, item.getLyrics()
            )).collect(Collectors.toList());

            ArtistSongReponsneNew artistSongResponseNew = new ArtistSongReponsneNew(userResponse, songResponses, numberSong, numberFollow, numberAlbum, albumResponse);

            Response response = new Response(0, "Artist", artistSongResponseNew);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }


    // của pthanh
    // lấy danh sách danh mục trong admin page
    @GetMapping("/categories_all")
    public ResponseEntity<Response> getAllCategories() {
        try{
            List<CategoryEntity> categories = categoryService.getAllCategory();
            List<CategoryResponse> categoryResponseList = categories.stream()
                    .map(cate -> new CategoryResponse(cate.getId(), cate.getName(), cate.getStatus()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new Response(0, "Success!", categoryResponseList));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }

    // lấy danh sách nghệ sĩ trong admin page
    @GetMapping("/artist_all")
    public ResponseEntity<Response> getAllArtists() {
        try{
            List<ArtistEntity> artistEntityList = artistService.getAllArtist();
            List<ArtistResponse> artistResponseList = artistEntityList.stream()
                    .map(artist -> new ArtistResponse(artist.getId(), artist.getFullName(), artist.getAvatar(),
                            artist.getHometown(), artist.getBiography(), artist.getStatus()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new Response(0, "Success!", artistResponseList));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }

    // Chức năng yêu cầu thêm mới danh mục
    @PostMapping("/categories")
    public ResponseEntity<Response> addCategory(@Valid @RequestBody CategoryBean categoryBean, Errors errors) {
        try {
            if (errors.hasErrors()) {
                Map<String, String> errorMap = new HashMap<>();
                errors.getFieldErrors().forEach(fieldError -> {
                    errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
                });
                return ResponseEntity.badRequest().body(new Response(1, "Validation failed", errorMap));
            }

            // kiểm tra trùng tên khi thêm
            CategoryEntity existCategoryAdd = categoryService.existCategoryAdd(categoryBean);
            if (existCategoryAdd != null) {
                return ResponseEntity.badRequest().body(new Response(1, "Đã tồn tại một danh mục giống vậy!", null));
            }

            CategoryEntity savedCategory = categoryService.addCategory(categoryBean);
            if (savedCategory == null) {
                return ResponseEntity.badRequest().body(new Response(1, "Category existed", null));
            }

            Response response = new Response(0, "Success!", savedCategory);
            return ResponseEntity.status(HttpStatus.CREATED).body(response); // 201
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null)); // 400
        }
    }

    // Chức năng yêu cầu thêm mới nghệ sĩ
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
    // hết của pthanh

    @GetMapping("/top100MusicMonth")
    public ResponseEntity<Response> top100MusicMonth() {
        try {
            List<Object[]> bxhSong = songRepository.findTop100SongsWithMonthlyListensAndFavorites();

            List<TestURL> songResponses = bxhSong.stream().map(song -> new TestURL(
                    (Integer) song[0],(String) song[7], (Boolean) song[9],
                    (String) song[1], (Date) song[3],(Integer) song[4],(Integer) song[8],
                    songService.getCateNameBySongId((Integer) song[0]), songService.getArtistNameBySongId((Integer) song[0]), null, songService.getSongById((Integer) song[0]).getLyrics()

            )).collect(Collectors.toList());

            Response response = new Response(0, "Success!", songResponses);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }





    }
}
