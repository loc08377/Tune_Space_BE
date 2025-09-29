package org.example.backend_fivegivechill.services;

import org.example.backend_fivegivechill.entity.ArtistEntity;
import org.example.backend_fivegivechill.entity.SongFavoriteEntity;
import org.example.backend_fivegivechill.entity.SongEntity;
import org.example.backend_fivegivechill.entity.UserEntity;
import org.example.backend_fivegivechill.repository.SongFavoriteRepository;
import org.example.backend_fivegivechill.response.ArtistSongResponse;
import org.example.backend_fivegivechill.response.SongResponese;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FavoriteService {

    @Autowired
    private SongFavoriteRepository songFavoriteRepository;

    public List<SongEntity> findByUser(UserEntity user) {
        return songFavoriteRepository.findByUserId(user.getId());
    }
    //aaaaaaaaaaaaaaaaaaaaaaaaaaa
    public List<SongResponese> favoriteOfSongEndArtists(UserEntity user) {
        List<Object[]> favoriteOfSongEndArtists = songFavoriteRepository.favoriteOfSongEndArtists(user.getId());
        Map<Integer, SongResponese> songMap = new LinkedHashMap<>();
        for (Object[] row : favoriteOfSongEndArtists) {
            SongEntity songEntity = (SongEntity) row[0];
            ArtistEntity artistEntity = (ArtistEntity) row[1];

            if(!songMap.containsKey(songEntity.getId())){
                SongResponese songResponese = new SongResponese();
                songResponese.setId(songEntity.getId());
                songResponese.setName(songEntity.getName());
                songResponese.setMp3File(songEntity.getMp3File());
                songResponese.setVipSong(songEntity.isVipSong());
                songResponese.setAvatar(songEntity.getAvatar());
                songResponese.setCreateDate(songEntity.getCreateDate());
                songResponese.setStatus(songEntity.getStatus());
                songResponese.setCountListens(songEntity.getCountListens());
                songResponese.setDuration(songEntity.getDuration());

                songResponese.setCateSongs(null);
                songResponese.setArtistSongs(new ArrayList<>()); // khởi tạo list nghệ sĩ

                songMap.put(songEntity.getId(), songResponese);
            }
            ArtistSongResponse asr = new ArtistSongResponse();
            asr.setId(artistEntity.getId());
            asr.setArtistSong(artistEntity);

            songMap.get(songEntity.getId()).getArtistSongs().add(asr);
        }
        return new ArrayList<>(songMap.values());
    }

    public SongFavoriteEntity save(SongFavoriteEntity songFavorite) {
        return songFavoriteRepository.save(songFavorite);
    }

    public Optional<SongFavoriteEntity> findByUserIdAndSongId(int userId, int songId) {
        return songFavoriteRepository.findByUserIdAndSongId(userId, songId);
    }

    public void delete(int userId, int songId) {
        Optional<SongFavoriteEntity> favoriteOpt = findByUserIdAndSongId(userId, songId);
        favoriteOpt.ifPresentOrElse(
                songFavoriteRepository::delete,
                () -> { throw new RuntimeException("Yêu thích không tồn tại"); }
        );
    }
}
