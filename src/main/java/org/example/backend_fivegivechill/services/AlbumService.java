package org.example.backend_fivegivechill.services;

import org.example.backend_fivegivechill.beans.SongsAlbumBean;
import org.example.backend_fivegivechill.beans.AlbumBean;
import org.example.backend_fivegivechill.entity.AlbumEntity;
import org.example.backend_fivegivechill.entity.ArtistSongEntity;
import org.example.backend_fivegivechill.entity.SongEntity;
import org.example.backend_fivegivechill.entity.UserEntity;
import org.example.backend_fivegivechill.repository.AlbumRepository;
import org.example.backend_fivegivechill.repository.SongRepository;
import org.example.backend_fivegivechill.repository.UserRepository;
import org.example.backend_fivegivechill.response.ArtistSongResponse;
import org.example.backend_fivegivechill.response.SongsAlbumResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AlbumService {
//cái mới nè
    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SongRepository songRepository;

    // Phương thức tạo album mới
    public AlbumEntity createAlbum(AlbumBean albumBean) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {return null;}

        AlbumEntity album = new AlbumEntity();
        album.setName(albumBean.getName());
        album.setCoverImage(albumBean.getCoverImage());
        album.setStatus(albumBean.isStatus());
        album.setUser(userOpt.get());

        return albumRepository.save(album);
    }

    // Phương thức sửa album
    public AlbumEntity updateAlbum(int id, AlbumBean albumBean) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {return null;}

        AlbumEntity album = albumRepository.findById(id).get();
        if (album == null) {return null;}

        album.setName(albumBean.getName());
        album.setCoverImage(albumBean.getCoverImage());
        album.setStatus(albumBean.isStatus());
        album.setUser(userOpt.get());

        return albumRepository.save(album);
    }

    // Phương thức lấy tất cả album của user
    public Page<AlbumEntity> getAllAlbumsByUserId(int status, String search, Pageable pageable) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {return null;}
        return albumRepository.findByUserId(status, userOpt.get().getId(), "%"+search+"%", pageable);
    }

    // phương thức lấy tất cả bài hát của user
    public List<SongsAlbumResponse> getSongsByUserId(Long albumId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return null;
        }

        List<SongEntity> list = songRepository.findSongsByUserAndAlbumMatchOrNull(0, userOpt.get().getId(), albumId);
        if (list.isEmpty()) {
            return null;
        }

        List<SongsAlbumResponse> responseList = new ArrayList<>();
        for (SongEntity songEntity : list) {
            SongsAlbumResponse response = new SongsAlbumResponse();
            response.setId(songEntity.getId());
            response.setName(songEntity.getName());
            responseList.add(response);
        }

        return responseList;
    }

    // cập nhật bài hát của album
    public boolean updateSongsInAlbum(SongsAlbumBean albumBean) {
        Optional<AlbumEntity> albumOpt = albumRepository.findById(albumBean.getAlbumId());
        if (albumOpt.isEmpty()) return false;

        AlbumEntity album = albumOpt.get();

        //  Xóa album_id khỏi tất cả các bài hát đang thuộc album này
        List<SongEntity> currentSongs = songRepository.findByAlbumId(album.getId());
        for (SongEntity song : currentSongs) {
            song.setAlbum(null);
        }
        songRepository.saveAll(currentSongs);

        //  Gán lại album cho danh sách mới
        List<SongEntity> newSongs = songRepository.findAllById(albumBean.getSongIds());
        for (SongEntity song : newSongs) {
            song.setAlbum(album);
        }
        songRepository.saveAll(newSongs);

        return true;
    }

    public List<SongEntity> getSongsInAlbum(Long albumId) {
        return songRepository.findAllByAlbumId(albumId);
    }
}
