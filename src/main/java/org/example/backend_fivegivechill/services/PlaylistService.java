package org.example.backend_fivegivechill.services;

import org.example.backend_fivegivechill.entity.ArtistEntity;
import org.example.backend_fivegivechill.entity.PlaylistEntity;
import org.example.backend_fivegivechill.entity.PlaylistItemEntity;
import org.example.backend_fivegivechill.entity.SongEntity;
import org.example.backend_fivegivechill.repository.PlaylistItemRepository;
import org.example.backend_fivegivechill.repository.PlaylistRepository;
import org.example.backend_fivegivechill.response.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PlaylistService {

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private PlaylistItemRepository playlistItemRepository;

    @Autowired
    private SongService songService;

    public Page<PlaylistResponse> getPlaylistByUserId(Pageable pageable, int userId) {
        Page<PlaylistEntity> playlistPage = playlistRepository.findByUserId(userId, pageable);

        return playlistPage.map(playlistEntity -> {
            PlaylistResponse playlistResponse = new PlaylistResponse();
            playlistResponse.setPlaylistId(playlistEntity.getId());
            playlistResponse.setPlaylistName(playlistEntity.getName());
            playlistResponse.setCoverImage(playlistEntity.getPlaylistItems() != null && !playlistEntity.getPlaylistItems().isEmpty()
                    ? playlistEntity.getPlaylistItems().get(0).getSongEntity().getAvatar()
                    : null);
            playlistResponse.setNameCreator(playlistEntity.getUserEntity().getFullName());
            playlistResponse.setIds(songService.getIdsSongByPlaylistId(playlistEntity.getId()));
            return playlistResponse;
        });
    }

    @Transactional
    public boolean deletePlaylist(int playlistId) {
        try {
            playlistItemRepository.deleteByPlaylistEntityId(playlistId);
            playlistRepository.deleteById(playlistId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }




    public PlaylistResponse getPlaylistById(int id) {
        PlaylistEntity playlistEntity = playlistRepository.findById(id).orElse(null);

        PlaylistResponse playlistResponse = new PlaylistResponse();
        playlistResponse.setPlaylistId(playlistEntity.getId());
        playlistResponse.setPlaylistName(playlistEntity.getName());
        playlistResponse.setUserId(playlistEntity.getUserEntity().getId());
        playlistResponse.setNameCreator(playlistEntity.getUserEntity().getFullName());


        List<SongResponese> songResponese = playlistEntity.getPlaylistItems().stream()
                .map(playlistItem -> {
                    SongResponese songDTO = new SongResponese();
                    songDTO.setId(playlistItem.getSongEntity().getId());
                    songDTO.setName(playlistItem.getSongEntity().getName());
                    songDTO.setMp3File(playlistItem.getSongEntity().getMp3File());
                    songDTO.setVipSong(playlistItem.getSongEntity().isVipSong());
                    songDTO.setAvatar(playlistItem.getSongEntity().getAvatar());
                    songDTO.setCreateDate(playlistItem.getSongEntity().getCreateDate());
                    songDTO.setStatus(playlistItem.getSongEntity().getStatus());
                    return songDTO;
                })
                .toList();

        playlistResponse.setSongs(songResponese);

        if (!songResponese.isEmpty()) {
            playlistResponse.setCoverImage(songResponese.get(0).getAvatar());
        }

        return playlistResponse;
    }

    public PlaylistResponseNew getPlaylistResponseNew(int id) {
        PlaylistEntity playlistEntity = playlistRepository.findById(id).orElse(null);

        PlaylistResponseNew playlistResponse = new PlaylistResponseNew();
        playlistResponse.setPlaylistId(playlistEntity.getId());
        playlistResponse.setPlaylistName(playlistEntity.getName());
        playlistResponse.setUserId(playlistEntity.getUserEntity().getId());
        playlistResponse.setNameCreator(playlistEntity.getUserEntity().getFullName());


        List<TestURL> songResponese = playlistEntity.getPlaylistItems().stream()
                .map(playlistItem -> {
                    TestURL songDTO = new TestURL();
                    songDTO.setId(playlistItem.getSongEntity().getId());
                    songDTO.setName(playlistItem.getSongEntity().getName());
                    songDTO.setVipSong(playlistItem.getSongEntity().isVipSong());
                    songDTO.setAvatar(playlistItem.getSongEntity().getAvatar());
                    songDTO.setCreateDate(playlistItem.getSongEntity().getCreateDate());
                    songDTO.setDuration(playlistItem.getSongEntity().getDuration());
                    songDTO.setStatus(playlistItem.getSongEntity().getStatus());
                    songDTO.setNameCateSong(songService.getCateNameBySongId(playlistItem.getSongEntity().getId()));
                    songDTO.setNameArtistSong(songService.getArtistNameBySongId(playlistItem.getSongEntity().getId()));
                    return songDTO;
                })
                .toList();
 playlistResponse.setIds(songService.getIdsSongByPlaylistId(playlistEntity.getId()));
        playlistResponse.setSongs(songResponese);

        if (!songResponese.isEmpty()) {
            playlistResponse.setCoverImage(songResponese.get(0).getAvatar());
        }

        return playlistResponse;
    }


    public List<PlaylistResponse> getAllPlaylistsByUserId(int userId) {
        // Lấy playlist theo userId
        List<PlaylistEntity> playlists = playlistRepository.findByUserEntityId(userId);

        return playlists.stream().map(playlistEntity -> {
            PlaylistResponse response = new PlaylistResponse();
            response.setPlaylistId(playlistEntity.getId());
            response.setPlaylistName(playlistEntity.getName());
            response.setUserId(playlistEntity.getUserEntity().getId());
            response.setNameCreator(playlistEntity.getUserEntity().getFullName());

            // nếu có bài hát thì lấy avatar bài đầu tiên làm cover
            if (playlistEntity.getPlaylistItems() != null && !playlistEntity.getPlaylistItems().isEmpty()) {
                response.setCoverImage(
                        playlistEntity.getPlaylistItems().get(0).getSongEntity().getAvatar()
                );
            }

            return response;
        }).toList();
    }



}
