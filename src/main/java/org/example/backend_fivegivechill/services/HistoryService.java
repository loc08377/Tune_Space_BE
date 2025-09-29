package org.example.backend_fivegivechill.services;

import org.example.backend_fivegivechill.beans.HistoryBean;
import org.example.backend_fivegivechill.entity.HistoryEntity;
import org.example.backend_fivegivechill.entity.SongEntity;
import org.example.backend_fivegivechill.entity.UserEntity;
import org.example.backend_fivegivechill.repository.HistoryRepository;
import org.example.backend_fivegivechill.repository.SongRepository;
import org.example.backend_fivegivechill.repository.UserRepository;
import org.example.backend_fivegivechill.response.HistoryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HistoryService {

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private SongService songService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SongRepository songRepository;

    public List<HistoryResponse> getHistoryByUserId(int userId) {
        List<HistoryEntity> historyEntities = historyRepository.findByUserEntityId(userId);
        return historyEntities.stream().map(this::mapToHistoryResponse).collect(Collectors.toList());
    }

    private HistoryResponse mapToHistoryResponse(HistoryEntity historyEntity) {
        return new HistoryResponse(
                historyEntity.getId(),
                historyEntity.getCreateDate(),
                historyEntity.getSongEntity().getId(),
                historyEntity.getSongEntity().getName(),
                historyEntity.getSongEntity().getMp3File(),
                historyEntity.getSongEntity().isVipSong(),
                historyEntity.getSongEntity().getAvatar(),
                historyEntity.getSongEntity().getCreateDate(),
//                historyEntity.getSongEntity().getCountListens(),
                historyEntity.getSongEntity().getDuration(),
                songService.getCateNameBySongId(historyEntity.getSongEntity().getId()),
                songService.getArtistNameBySongId(historyEntity.getSongEntity().getId())
        );
    }

//    @Transactional
//    public boolean deleteHistory(int idUser, List<Integer> historyIds) {
//        if (historyIds == null || historyIds.isEmpty()) {
//            return false;
//        }
//        try {
//            int affectedRows = historyRepository.deleteByHistoryIdsAndUserId(historyIds, idUser);
//            return affectedRows > 0;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }

    @Transactional
    public boolean deleteHistory(int idUser, List<Integer> songIds) {
        if (songIds == null || songIds.isEmpty()) {
            return false;
        }
        try {
            int affectedRows = historyRepository.deleteByUserIdAndSongIds(idUser, songIds);
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int saveHistory(HistoryBean historyBean) {
        try {
            int userId = historyBean.getUserId();
            int songId = historyBean.getSongId();

            Optional<HistoryEntity> historyOpt = historyRepository.findByUserEntityIdAndSongEntityId(userId, songId);

            if (historyOpt.isPresent()) {
                // Nếu đã tồn tại → cập nhật thời gian
                HistoryEntity history = historyOpt.get();
                history.setCreateDate(new Date());
                historyRepository.save(history);
                return 2; // cập nhật
            } else {
                // Nếu chưa có → tạo mới
                UserEntity userEntity = userRepository.findById(userId).orElse(null);
                SongEntity songEntity = songRepository.findById(songId).orElse(null);
                if (userEntity == null || songEntity == null) return 0; // koong tim thay
                HistoryEntity history = new HistoryEntity();
                history.setUserEntity(userEntity);
                history.setSongEntity(songEntity);
                history.setCreateDate(new Date());
                historyRepository.save(history);
                return 1; // thêm mới
            }
        } catch (Exception e) {
            return -1; // loi trong qua trinh xu ly
        }
    }

}
