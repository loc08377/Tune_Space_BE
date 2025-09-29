package org.example.backend_fivegivechill.services;

import jakarta.servlet.http.HttpServletRequest;
import org.example.backend_fivegivechill.entity.CountListenEntity;
import org.example.backend_fivegivechill.entity.SongEntity;
import org.example.backend_fivegivechill.entity.SubscriptionUserEntity;
import org.example.backend_fivegivechill.entity.UserEntity;
import org.example.backend_fivegivechill.repository.CountListenRepository;
import org.example.backend_fivegivechill.repository.SongRepository;
import org.example.backend_fivegivechill.repository.SubscriptionUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CountListenService {

    @Autowired
    SongRepository songRepository;

    @Autowired
    CountListenRepository countListenRepository;

    @Autowired
    SubscriptionUserRepository subscriptionUserRepository;

    public CountListenService(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    @Scheduled(cron = "0 0 0 * * ?") //aaaaaaaaaaaaaaaaaaaaaaaaaaa // Chạy lúc 0h mỗi ngày
    public void calculatorCountListenEveryday() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.with(LocalTime.MIDNIGHT).minusDays(1);
        LocalDateTime endTime = startTime.with(LocalTime.MAX).minusNanos(1);

        System.out.println(">>> Start tính lượt nghe trong khoảng:");
        System.out.println("StartTime: " + startTime);
        System.out.println("EndTime  : " + endTime);

        int pageSize = 100;
        int pageNumber = 0;
        Page<SongEntity> songPageListFindAll;

        do {
            songPageListFindAll = songRepository.findAll(PageRequest.of(pageNumber, pageSize));
            List<SongEntity> songList = songPageListFindAll.getContent();

            System.out.println(">>> Đang xử lý trang " + pageNumber + ", số bài hát: " + songList.size());

            if (songList.isEmpty()) break;

            List<Integer> songIdList = songList.stream().map(SongEntity::getId).collect(Collectors.toList());
            System.out.println("Danh sách songId trong trang này: " + songIdList);

            List<Object[]> songIdAndCountListen = songRepository.countListBySongId(songIdList, startTime, endTime);

            System.out.println(">>> Kết quả đếm lượt nghe:");
            for (Object[] obj : songIdAndCountListen) {
                System.out.println("SongID: " + obj[0] + ", Count: " + obj[1]);
            }

            Map<Integer, Integer> convertsongIdAndCountListen = songIdAndCountListen.stream()
                    .collect(Collectors.toMap(
                            songId -> (Integer) songId[0],
                            countlisten -> ((Long) countlisten[1]).intValue()
                    ));

            List<SongEntity> songsMap = new ArrayList<>();
            for (SongEntity songItem : songList) {
                int oldCount = songItem.getCountListens();
                int newCount = oldCount + convertsongIdAndCountListen.getOrDefault(songItem.getId(), 0);

                System.out.println("Cập nhật bài hát ID: " + songItem.getId() + ", lượt nghe cũ: " + oldCount + ", mới: " + newCount);

                songItem.setCountListens(newCount);
                songsMap.add(songItem);
            }

            songRepository.saveAll(songsMap);
            System.out.println(">>> Đã lưu cập nhật lượt nghe cho " + songsMap.size() + " bài hát.");

            pageNumber++;
        } while (songPageListFindAll.hasNext());

        System.out.println(">>> Hoàn tất tính lượt nghe hàng ngày.");
    }



    //    tính lại
    public void reCalculatorCountListen() {

    }

//    public String countListen(int songId, String fingerprint, UserEntity user) {
//        SongEntity song = songRepository.findById(songId).orElse(null);
//        if (song == null) return "Bài hát không tồn tại";
//
//        if (fingerprint == null || fingerprint.isEmpty()) {
//            return "Không xác định được thiết bị";
//        }
//
//        boolean hasVip = user != null && subscriptionUserRepository.isSubscribed(user.getId());
//
//        if (song.isVipSong() && (user == null || !hasVip)) {
//            return "Bài hát VIP - Không tăng lượt nghe";
//        }
//
//        int maxListen = 3;
//        if (user != null) {
//            maxListen = hasVip ? 10 : 5;
//        }
//
//        Date today = new Date();
//
//        // Tổng lượt đã nghe hôm nay theo fingerprint (dù có login hay chưa)
//        int totalByFingerprint = countListenRepository.countByDeviceFingerprintAndSongAndDate(fingerprint, songId, today);
//
//        if (totalByFingerprint >= maxListen) {
//            return "Bạn đã nghe tối đa bài hát này hôm nay";
//        }
//
//        // Tạo mới lượt nghe
//        CountListenEntity listen = new CountListenEntity();
//        listen.setSongEntity(song);
//        listen.setCreateDate(today);
//        listen.setDeviceFingerprint(fingerprint);
//        if (user != null) {
//            listen.setUserEntity(user);
//        }
//
//        countListenRepository.save(listen);
//        return "Đã tính lượt nghe";
//    }

    public String countListen(int songId, String fingerprint, UserEntity user) {
        SongEntity song = songRepository.findById(songId).orElse(null);
        if (song == null) return "Bài hát không tồn tại";


        if (user == null) {
            return "Bạn cần đăng nhập để được tính lượt nghe";
        }

        if (fingerprint == null || fingerprint.isEmpty()) {
            return "Không xác định được thiết bị";
        }

        boolean hasVip = subscriptionUserRepository.isSubscribed(user.getId());

        if (song.isVipSong() && !hasVip) {
            return "Bài hát VIP - Không tăng lượt nghe";
        }

        int maxListen = hasVip ? 10 : 3;

        Date today = new Date();

        // Tổng lượt đã nghe hôm nay theo fingerprint
        int totalByFingerprint = countListenRepository.countByDeviceFingerprintAndSongAndDate(fingerprint, songId, today);

        if (totalByFingerprint >= maxListen) {
            return "Bạn đã nghe tối đa bài hát này hôm nay";
        }

        // Tạo mới lượt nghe
        CountListenEntity listen = new CountListenEntity();
        listen.setSongEntity(song);
        listen.setCreateDate(today);
        listen.setDeviceFingerprint(fingerprint);
        listen.setUserEntity(user);

        countListenRepository.save(listen);
        return "Đã tính lượt nghe";
    }


    public int calculatorCountListenToDay(int songId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.with(LocalTime.MIDNIGHT);
        return countListenRepository.calculator(songId, startTime, now);

    }



}
