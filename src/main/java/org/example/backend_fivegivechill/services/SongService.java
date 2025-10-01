package org.example.backend_fivegivechill.services;

import org.example.backend_fivegivechill.beans.SongBean;
import org.example.backend_fivegivechill.entity.*;
import org.example.backend_fivegivechill.repository.*;
import org.example.backend_fivegivechill.response.ArtistSongResponse;
import org.example.backend_fivegivechill.response.CateSongResponse;
import org.example.backend_fivegivechill.response.SongResponese;
import org.example.backend_fivegivechill.response.SongSearchResponse;
import org.example.backend_fivegivechill.utils.Encryption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SongService {
    @Autowired
    private SongRepository songRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CateSongRepository cateSongRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ArtistSongRepository artistSongRepository;

    @Value("${encryption}")
    private String encryptionKey;

    @Value("ip")
    private String ip;

    @Autowired
    Encryption encryption;

    public Page<SongEntity> getAllSongs(int status, String search, Pageable pageable) {
        return songRepository.findAllByStatus(status, "%"+search+"%", pageable);
    }

    public Page<SongEntity> getAllSongsByUser(int status, int userId, String search, Pageable pageable) {
        return songRepository.findAllByStatusAndUser(status, userId, "%"+search+"%", pageable);
    }

    // Lấy bài hát theo ID
    public SongEntity getSongById(int id) {
        return songRepository.findById(id).orElse(null);
    }

    // Thêm bài hát mới
    public SongEntity addSong(SongBean songBean) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return null;
        }

        SongEntity songEntity = new SongEntity();
        songEntity.setName(songBean.getName());
        songEntity.setMp3File(songBean.getMp3File());
        songEntity.setVipSong(songBean.isVipSong());
        songEntity.setAvatar(songBean.getAvatar());
        songEntity.setCreateDate(new Date()); // Set ngày tạo
        songEntity.setStatus(songBean.getStatus());
        songEntity.setDuration(songBean.getDuration());
        songEntity.setLyrics(songBean.getLyrics());
        songEntity.setUser(userOpt.get());

        // Lưu bài hát trước để có ID
        SongEntity savedSong = songRepository.save(songEntity); // bài hát

        // Lưu danh mục bài hát
        List<CateSongEntity> cateSongs = songBean.getCategoryIds().stream().map(categoryId -> {
            CategoryEntity category = categoryRepository.findById(categoryId).orElse(null);
            return category != null ? new CateSongEntity(0, category, savedSong) : null;
        }).filter(cateSong -> cateSong != null).collect(Collectors.toList());

        cateSongRepository.saveAll(cateSongs); // danh mục

        // Lưu ca sĩ bài hát (1 chữ s)
        List<ArtistSongEntity> artistSongs = songBean.getArtistIds().stream().map(artistId -> {
            ArtistEntity artist = artistRepository.findById(artistId).orElse(null);
            return artist != null ? new ArtistSongEntity(0, true, artist, savedSong ) : null;
        }).filter(artistSong -> artistSong != null).collect(Collectors.toList());

        // Lưu tác giả bài hát (2 chữ s)
        List<ArtistSongEntity> artistSongss = songBean.getArtistIdss().stream().map(artistId -> {
            ArtistEntity artist = artistRepository.findById(artistId).orElse(null);
            return artist != null ? new ArtistSongEntity(0, false, artist, savedSong ) : null;
        }).filter(artistSong -> artistSong != null).collect(Collectors.toList());

        artistSongRepository.saveAll(artistSongs); // ca sĩ
        artistSongRepository.saveAll(artistSongss); // tác giả

        return savedSong;
    }

    // Cập nhật bài hát
    public SongEntity updateSong(int id, SongBean songBean) {
        SongEntity exist = songRepository.findById(id).orElse(null);
        if (exist != null) {
            exist.setName(songBean.getName());
            exist.setMp3File(songBean.getMp3File());
            exist.setVipSong(songBean.isVipSong());
            exist.setAvatar(songBean.getAvatar());
            exist.setStatus(songBean.getStatus());
            exist.setDuration(songBean.getDuration());
            exist.setLyrics(songBean.getLyrics());

            // Cập nhật danh mục bài hát
            cateSongRepository.deleteBySongEntity(exist);
            List<CateSongEntity> cateSongs = songBean.getCategoryIds().stream().map(categoryId -> {
                CategoryEntity category = categoryRepository.findById(categoryId).orElse(null);
                return category != null ? new CateSongEntity(0, category, exist) : null;
            }).filter(cateSong -> cateSong != null).collect(Collectors.toList());

            cateSongRepository.saveAll(cateSongs); // danh mục

            // Cập nhật ca sĩ bài hát (1 chữ s)
            artistSongRepository.deleteBySongEntity(exist);
            List<ArtistSongEntity> artistSongs = songBean.getArtistIds().stream().map(artistId -> {
                ArtistEntity artist = artistRepository.findById(artistId).orElse(null);
                return artist != null ? new ArtistSongEntity(0, true,artist, exist) : null;
            }).filter(artistSong -> artistSong != null).collect(Collectors.toList());

            // Cập nhật tác giả bài hát (2 chữ s)
            List<ArtistSongEntity> artistSongss = songBean.getArtistIdss().stream().map(artistId -> {
                ArtistEntity artist = artistRepository.findById(artistId).orElse(null);
                return artist != null ? new ArtistSongEntity(0, false,artist, exist) : null;
            }).filter(artistSong -> artistSong != null).collect(Collectors.toList());

            artistSongRepository.saveAll(artistSongs); // ca sĩ
            artistSongRepository.saveAll(artistSongss); // tác giả

            return songRepository.save(exist); // bài hát
        }
        return null;
    }

    // này tìm danh mục của bài hát
    public List<CateSongResponse> getCateSongs(int id) {
        // Lấy danh sách CateSongEntity dựa trên songId
        List<CateSongEntity> cateSongEntities = cateSongRepository.findBySongId(id);

        // Ánh xạ CateSongEntity thành CateSongResponse cho từng phần tử trong danh sách
        List<CateSongResponse> cateSongResponses = cateSongEntities.stream()
                .map(cateSongEntity -> {
                    return new CateSongResponse(cateSongEntity.getId(), cateSongEntity.getCategoryEntity());
                })
                .collect(Collectors.toList());

        // Trả về danh sách CateSongResponse
        return cateSongResponses;
    }

    // này tìm ca sĩ của bài hát
    public List<ArtistSongResponse> getArtistSongs(int id) {
        // Lấy danh sách ArtistSongEntity từ repository
        List<ArtistSongEntity> artistSongEntities = artistSongRepository.findBySongId(id); //(1 chữ d)

        // Ánh xạ từng ArtistSongEntity thành ArtistSongResponse
        List<ArtistSongResponse> artistSongResponses = artistSongEntities.stream()
                .map(artistSongEntity -> {
                    // Chỉ lấy id và artistEntity từ mỗi ArtistSongEntity
                    return new ArtistSongResponse(artistSongEntity.getId(), artistSongEntity.getArtistEntity());
                })
                .collect(Collectors.toList());

        return artistSongResponses;
    }

    // này tìm tác giả của bài hát
    public List<ArtistSongResponse> getArtistSongss(int id) {
        // Lấy danh sách ArtistSongEntity từ repository
        List<ArtistSongEntity> artistSongEntities = artistSongRepository.findBySongIdd(id); //(2 chữ d)

        // Ánh xạ từng ArtistSongEntity thành ArtistSongResponse
        List<ArtistSongResponse> artistSongResponses = artistSongEntities.stream()
                .map(artistSongEntity -> {
                    // Chỉ lấy id và artistEntity từ mỗi ArtistSongEntity
                    return new ArtistSongResponse(artistSongEntity.getId(), artistSongEntity.getArtistEntity());
                })
                .collect(Collectors.toList());

        return artistSongResponses;
    }

//    public List<SongResponese> getSongsByIdArtistId(int artistId) {
//        // Lấy danh sách ArtistSongEntity
//        List<SongEntity> artistSongEntities = artistSongRepository.getSongsByIdArtistId(artistId);
//
//        List<SongResponese> songResponses = artistSongEntities.stream()
//                .map(artistSongEntity -> {
//
//                    SongEntity songEntity = artistSongEntity.getSongEntity();
//
//                    SongResponese songResponse = new SongResponese();
//
//                    songResponse.setId(songEntity.getId());
//                    songResponse.setName(songEntity.getName());
//                    songResponse.setMp3File(songEntity.getMp3File());
//                    songResponse.setVipSong(songEntity.isVipSong());
//                    songResponse.setAvatar(songEntity.getAvatar());
//                    songResponse.setCreateDate(songEntity.getCreateDate());
//                    songResponse.setStatus(songEntity.getStatus());
//                    songResponse.setDuration(songEntity.getDuration());
//
//                    return songResponse;
//                })
//                .collect(Collectors.toList());
//
//        return songResponses;
//    }

    public String getArtistNameBySongId(int id) {
        List<ArtistSongResponse> listArtistSongResponses = getArtistSongs(id);
        String artistNames = listArtistSongResponses.stream().map(a -> a.getArtistSong().getFullName()).collect(Collectors.joining(", "));
        return artistNames;
    }

    public String getCateNameBySongId(int id) {
        List<CateSongResponse> listCateSongRespone = getCateSongs(id);
        String categoryNames = listCateSongRespone.stream()
                .map(c -> c.getCateSong().getName())
                .collect(Collectors.joining(", "));
        return categoryNames;
    }

    public List<Integer> getIdsSongByAlbumId(int id) {
        List<Integer> idsSong = songRepository.findIdsSongByAlbumId(id);
        return idsSong;
    }

    public List<Integer> getIdsSongByPlaylistId(int id) {
        List<Integer> idsSong = songRepository.findIdsSongByplaylistId(id);
        return idsSong;
    }

//    public boolean increaseListens(int songId) {
//        Optional<SongEntity> songOpt = songRepository.findById(songId);
//        if (songOpt.isPresent()) {
//            SongEntity song = songOpt.get();
//            song.setCountListens(song.getCountListens() + 1); // Tăng lượt +1
//            songRepository.save(song);
//            return true;
//        }
//        return false;
//    }
    public boolean increaseListens(int songId) {
        Optional<SongEntity> songOpt = songRepository.findById(songId);
        if (songOpt.isPresent()) {
            SongEntity song = songOpt.get();
//            song.setCountListens(song.getCountListens() + 1); // Tăng lượt +1
            songRepository.save(song);
            return true;
        }
        return false;
    }

    public List<SongResponese> getSongsUserHeardAndInTheirPlaylists(int userId, Pageable pageable) {
        List<Object[]> rawResults = songRepository.getSongsAndArtistsUserHeardAndInTheirPlaylists(userId, pageable);

        Map<Integer, SongResponese> songMap = new LinkedHashMap<>();

        for (Object[] row : rawResults) {
            SongEntity song = (SongEntity) row[0];
            ArtistEntity artist = (ArtistEntity) row[1];

            // Nếu song chưa có trong map, tạo mới SongResponese
            if (!songMap.containsKey(song.getId())) {
                SongResponese sr = new SongResponese();
                sr.setId(song.getId());
                sr.setName(song.getName());
                sr.setMp3File(song.getMp3File());
                sr.setVipSong(song.isVipSong());
                sr.setAvatar(song.getAvatar());
                sr.setCreateDate(song.getCreateDate());
                sr.setStatus(song.getStatus());
                sr.setCountListens(song.getCountListens());
                sr.setDuration(song.getDuration());

                sr.setCateSongs(null);
                sr.setArtistSongs(new ArrayList<>());

                songMap.put(song.getId(), sr);
            }

            // Tạo ArtistSongResponse từ ArtistEntity
            ArtistSongResponse asr = new ArtistSongResponse();
            asr.setId(artist.getId());
            asr.setArtistSong(artist);

            // Thêm vào danh sách nghệ sĩ của bài hát tương ứng
            songMap.get(song.getId()).getArtistSongs().add(asr);
        }
        return new ArrayList<>(songMap.values());
    }

    public List<SongResponese> getPersonalizedSongs(int userId) {
        List<Object[]> rawResults = songRepository.getPersonalizedSongs(userId);
        Map<Integer, SongResponese> songMap = new LinkedHashMap<>();

        for (Object[] row : rawResults) {
            Integer songId = (Integer) row[0];

            if (!songMap.containsKey(songId)) {
                SongResponese sr = new SongResponese();
                sr.setId(songId);
                sr.setName((String) row[1]);
                sr.setAvatar((String) row[2]);
                sr.setMp3File((String) row[3]);
                sr.setCountListens((Integer) row[4]);
                sr.setVipSong((Boolean) row[5]);
                sr.setCreateDate((Date) row[6]); // java.util.Date
                sr.setStatus((int) row[7]);
                sr.setDuration((Integer) row[8]); // là int chứ không phải String
                sr.setCateSongs(null); // Bỏ thể loại như bạn yêu cầu
                sr.setArtistSongs(new ArrayList<>()); // Khởi tạo danh sách nghệ sĩ

                songMap.put(songId, sr);
            }

            // Tạo artist entity
            ArtistEntity ae = new ArtistEntity();
            ae.setId((Integer) row[9]);
            ae.setFullName((String) row[10]);
            ae.setAvatar((String) row[11]);
            ae.setHometown((String) row[12]);
            ae.setBiography((String) row[13]);
            ae.setStatus((int) row[14]);

            // Tạo response wrapper cho artist
            ArtistSongResponse asr = new ArtistSongResponse();
            asr.setId(ae.getId());
            asr.setArtistSong(ae);

            // Thêm nghệ sĩ vào bài hát
            songMap.get(songId).getArtistSongs().add(asr);
        }

        return new ArrayList<>(songMap.values());
    }

    public String generateSignedUrl(int songId, String userId) {
        long expires = Instant.now().getEpochSecond() + 1800;

        String dataToSign = songId + ":" + userId + ":" + expires;

        String signature = encryption.sign(dataToSign, encryptionKey);

        String signedUrl = String.format("http://103.101.163.203/:8080/stream?songId=%s&userId=%s&expires=%d&signature=%s", songId, userId, expires, signature);

        return signedUrl;
    }


    public String getStreamUrl(int songId, boolean isVipUser, boolean isVipSong) {

        String songUrl = songRepository.testaaa(songId);

        if (songUrl == null) {
            throw new RuntimeException("Không tìm thấy URL bài hát.");
        }

        if (isVipSong && !isVipUser) {
            return getPreviewUrl(songUrl);
        }

        return songUrl;
    }


    private String getPreviewUrl(String originalUrl) {
        return originalUrl.replace("/upload/", "/upload/so_0,eo_15/");
    }

    public Page<SongSearchResponse> searchSongs(String keyword, Pageable pageable, List<Integer> type) {
        return songRepository.searchSongs(keyword, pageable, type);
    }

}
