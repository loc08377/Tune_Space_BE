package org.example.backend_fivegivechill.services;

import org.example.backend_fivegivechill.beans.ShareBean;
import org.example.backend_fivegivechill.entity.ShareEntity;
import org.example.backend_fivegivechill.entity.SongEntity;
import org.example.backend_fivegivechill.entity.UserEntity;
import org.example.backend_fivegivechill.repository.ShareRepository;
import org.example.backend_fivegivechill.repository.SongRepository;
import org.example.backend_fivegivechill.repository.UserRepository;
import org.example.backend_fivegivechill.response.ShareResponse;
import org.example.backend_fivegivechill.response.SongResponese;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ShareService {

    @Autowired
    private ShareRepository shareRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private UserRepository userRepository;

    // Lấy tất cả chia sẻ
    public List<ShareEntity> getAllShares() {
        return shareRepository.findAll();
    }

    // Lấy chia sẻ theo ID
    public ShareEntity getShareById(int id) {
        return shareRepository.findById(id).orElse(null);
    }

    public ShareEntity addShare(ShareBean shareBean) {
        ShareEntity shareEntity = new ShareEntity();
        shareEntity.setCreateDate(new Date());
        shareEntity.setSharingMethod(shareBean.getSharingMethod());
        shareEntity.setRecipient(shareBean.getRecipient());

        // Gán người dùng
        UserEntity user = userRepository.findById(shareBean.getUserId()).orElse(null);
        if (user == null) return null;
        shareEntity.setUser(user);

        // Gán bài hát
        if (shareBean.getSongId() == null) return null;
        SongEntity song = songRepository.findById(shareBean.getSongId() ).orElse(null);
        if (song == null) return null;
        shareEntity.setSong(song);

        return shareRepository.save(shareEntity);
    }



    // Xoá chia sẻ
//    public boolean deleteShare(int id) {
//        Optional<ShareEntity> optional = shareRepository.findById(id);
//        if (optional.isPresent()) {
//            shareRepository.deleteById(id);
//            return true;
//        }
//        return false;
//    }

    public ShareResponse mapToResponse(ShareEntity shareEntity) {
        ShareResponse response = new ShareResponse();
        response.setShareId(shareEntity.getId());
        response.setCreateDate(shareEntity.getCreateDate());
        response.setSharingMethod(shareEntity.getSharingMethod());
        response.setRecipient(shareEntity.getRecipient());
        response.setUserId(shareEntity.getUser().getId());

        //lop moi
        SongEntity song = shareEntity.getSong();
        if (song != null) {
            SongResponese songResponse = new SongResponese();
            songResponse.setId(song.getId());
            songResponse.setName(song.getName());
            songResponse.setMp3File(song.getMp3File());
            songResponse.setVipSong(song.isVipSong());
            songResponse.setAvatar(song.getAvatar());
            songResponse.setCreateDate(song.getCreateDate());
            songResponse.setStatus(song.getStatus());
            songResponse.setDuration(song.getDuration());

            response.setSong(songResponse);
        }

        return response;
    }
}