package org.example.backend_fivegivechill.services;

import org.example.backend_fivegivechill.beans.PlaylistItemBean;
import org.example.backend_fivegivechill.entity.PlaylistEntity;
import org.example.backend_fivegivechill.entity.PlaylistItemEntity;
import org.example.backend_fivegivechill.entity.SongEntity;
import org.example.backend_fivegivechill.repository.PlaylistItemRepository;
import org.example.backend_fivegivechill.repository.PlaylistRepository;
import org.example.backend_fivegivechill.repository.SongRepository;
import org.example.backend_fivegivechill.response.HomeSongRespone;
import org.example.backend_fivegivechill.response.PlaylistItemReponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlaylistItemService {
    @Autowired
    private PlaylistItemRepository playlistItemRepository;

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private SongService songService;

    public String addSongToPlaylist(PlaylistItemBean playlistItemBean) {

        PlaylistEntity playlistEntity = playlistRepository.findById(playlistItemBean.getPlaylistId()).orElse(null);
        SongEntity songEntity = songRepository.findById(playlistItemBean.getSongId()).orElse(null);

        if (playlistItemRepository.existsByPlaylistEntityAndSongEntity(playlistEntity, songEntity)) {
            return "Bài hát đã tồn tại";
        }

        PlaylistItemEntity playlistItemEntity = new PlaylistItemEntity();
        playlistItemEntity.setPlaylistEntity(playlistEntity);
        playlistItemEntity.setSongEntity(songEntity);
        playlistItemRepository.save(playlistItemEntity);

        return null;
    }
    @Transactional
    public boolean deletePlaylistItems(List<Integer> idPlaylistItems) {
        try {
            int a = playlistItemRepository.deletePlaylistItems(idPlaylistItems);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<PlaylistItemReponse> getPlayListItem(int id) {

        List<PlaylistItemEntity> playlistItemEntities = playlistItemRepository.findPlayListItemByIdPlaylist(id);

        List<PlaylistItemReponse> playlistItemReponse = playlistItemEntities.stream().map(a -> new PlaylistItemReponse(
                a.getId(),
                a.getSongEntity().getId(),
                a.getSongEntity().getName(),
                a.getSongEntity().getAvatar(),
                songService.getArtistNameBySongId(a.getSongEntity().getId())
        )).collect(Collectors.toList());

        return playlistItemReponse;
    }


    @Transactional
    public int deletePlaylistItem(int id) {
        int delete = playlistItemRepository.deleteByIdCustom(id);
        return delete;
    }

    public List<PlaylistItemReponse> getPlaylistItemBySongAndPlaylist(int songId, int playlistId) {

        List<PlaylistItemEntity> playlistItemEntities = playlistItemRepository
                .findPlaylistItemBySongAndPlaylist(songId, playlistId);

        List<PlaylistItemReponse> playlistItemResponse = playlistItemEntities.stream()
                .map(a -> new PlaylistItemReponse(
                        a.getId(),
                        a.getSongEntity().getId(),
                        a.getSongEntity().getName(),
                        a.getSongEntity().getAvatar(),
                        songService.getArtistNameBySongId(a.getSongEntity().getId())
                )).collect(Collectors.toList());

        return playlistItemResponse;
    }

}
