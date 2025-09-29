package org.example.backend_fivegivechill.services;

import org.example.backend_fivegivechill.beans.ArtistBean;
import org.example.backend_fivegivechill.entity.ArtistEntity;
import org.example.backend_fivegivechill.repository.ArtistRepository;
import org.example.backend_fivegivechill.response.ArtistResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ArtistService {

    @Autowired
    private ArtistRepository artistRepository;

    // Lấy danh sách nghệ sĩ theo trạng thái
    public Page<ArtistEntity> getAllArtists(int status, String search, Pageable pageable) {
        return artistRepository.findAllByStatus(status, "%"+search+"%", pageable);
    }

    public List<ArtistEntity> getAllArtist() {
        return artistRepository.getAll();
    }

    // Lấy thông tin chi tiết một nghệ sĩ theo ID
    public ArtistEntity getArtistById(int id) {
        return artistRepository.findById(id).orElse(null);
    }

    // Thêm mới nghệ sĩ
    public ArtistEntity addArtist(ArtistBean artistBean) {
        ArtistEntity artistEntity = new ArtistEntity();
        artistEntity.setFullName(artistBean.getFullName());
        artistEntity.setAvatar(artistBean.getAvatar());
        artistEntity.setHometown(artistBean.getHometown());
        artistEntity.setBiography(artistBean.getBiography());
        artistEntity.setStatus(artistBean.getStatus());
        return artistRepository.save(artistEntity);
    }

    // Kiểm tra nghệ sĩ đã tồn tại khi thêm mới
    public ArtistEntity existArtistAdd(ArtistBean artistBean) {
        ArtistEntity existArtist = artistRepository.existByFullName(artistBean.getFullName(), artistBean.getHometown(), artistBean.getBiography());
        return existArtist != null ? existArtist : null;
    }

    // Cập nhật thông tin nghệ sĩ
    public ArtistEntity updateArtist(int id, ArtistBean artistBean) {
        ArtistEntity exist = artistRepository.findById(id).orElse(null);
        if (exist != null) {
            exist.setFullName(artistBean.getFullName());
            exist.setAvatar(artistBean.getAvatar());
            exist.setHometown(artistBean.getHometown());
            exist.setBiography(artistBean.getBiography());
            exist.setStatus(artistBean.getStatus());
            return artistRepository.save(exist);
        }
        return null;
    }

    // Kiểm tra nghệ sĩ đã tồn tại khi cập nhật
    public ArtistEntity existArtistUpdate(int id, ArtistBean artistBean) {
        ArtistEntity existArtist = artistRepository.existByFullNameAndId(artistBean.getFullName() , artistBean.getHometown(), artistBean.getBiography(), id);
        return existArtist != null ? existArtist : null;
    }

    public ArtistEntity art_song() {
        return artistRepository.getAllArtistTrue();
    }

    public List<ArtistResponse> getPersonalizedArtist(int userId) {
        List<Object[]> rawResults = artistRepository.getPersonalizedArtists(userId);
        List<ArtistResponse> artistResponses = new ArrayList<>();

        Set<Integer> addedArtistIds = new HashSet<>();

        for (Object[] row : rawResults) {
            Integer artistId = (Integer) row[0];

            if (!addedArtistIds.contains(artistId)) {
                ArtistResponse artistResponse = new ArtistResponse();
                artistResponse.setId(artistId);
                artistResponse.setFullName((String) row[1]);
                artistResponse.setAvatar((String) row[2]);
                artistResponse.setBiography((String) row[3]);
                artistResponse.setHometown((String) row[4]);
                artistResponse.setStatus((int) row[5]);
                artistResponse.setFollowing((Boolean) row[6]);

                artistResponses.add(artistResponse);
                addedArtistIds.add(artistId);
            }
        }

        return artistResponses;
    }







}
