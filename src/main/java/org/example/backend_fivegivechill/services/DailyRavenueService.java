package org.example.backend_fivegivechill.services;

import org.example.backend_fivegivechill.entity.DailyRavenueEntity;
import org.example.backend_fivegivechill.entity.SongEntity;
import org.example.backend_fivegivechill.repository.CountListenRepository;
import org.example.backend_fivegivechill.repository.DailyRavenueRepository;
import org.example.backend_fivegivechill.repository.SongRepository;
import org.example.backend_fivegivechill.response.DailyRavenueRespone;
import org.example.backend_fivegivechill.response.SongResponese;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;



@Service
public class DailyRavenueService {

    @Autowired
    private CountListenRepository countListenRepository;

    @Autowired
    private DailyRavenueRepository dailyRavenueRepository;

    @Autowired
    SongRepository songRepository;

    @Autowired
    CountListenService countListenService;

    //hàm này tính tền bài hát theo lượt nghe theo từng ngày
    @Scheduled(cron = "0 0 0 * * ?")  //0h 0p 0s (*moi ngay) (*moi thang)
    public void calculateDailyRevenue() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.with(LocalTime.MIDNIGHT).minusDays(1);
        LocalDateTime endTime = startTime.with(LocalTime.MAX).minusNanos(1);

        int pageSize = 100;
        int pageNumber = 0;
        Page<SongEntity> songPageListFindAll;
        do {
            songPageListFindAll = songRepository.findAll(PageRequest.of(pageNumber, pageSize));
            List<SongEntity> songs = songPageListFindAll.getContent();
            if (songs.isEmpty()) break;

            List<Integer> songIds = songs.stream().map(SongEntity::getId).collect(Collectors.toList());

            //lay doanh thu hien co trong ngay nghia la da tinh doanh thu roi
            List<DailyRavenueEntity> existingRevenues = dailyRavenueRepository
                    .findBySongEntityIdInAndCreateDateBetween(songIds, startTime, endTime);

            // convert ra id song và doanh thu là nguyen cái bảng
            Map<Integer, DailyRavenueEntity> existingRevenueMap = existingRevenues.stream()
                    .collect(Collectors.toMap(
                            r -> r.getSongEntity().getId(),
                            r -> r));

            List<Object[]> songIdAndCountListen = songRepository.countListBySongId(songIds, startTime, endTime); // dem so luot nghe cua bai hat ngay qua tai tinh ngay 12h
            Map<Integer, Integer> songIdAndCountListenMap = songIdAndCountListen.stream().collect(Collectors.toMap(
                    songId -> (Integer) songId[0],
                    countListen -> ((Long) countListen[1]).intValue()
            ));



            List<DailyRavenueEntity> toSave = new ArrayList<>();
            for (SongEntity songItem : songs) {
                int total_amount = songItem.isVipSong() ?
                        songIdAndCountListenMap.getOrDefault(songItem.getId(), 0) * 20 :
                        songIdAndCountListenMap.getOrDefault(songItem.getId(), 0) * 10;

                DailyRavenueEntity existing = existingRevenueMap.get(songItem.getId());

                if (existing != null) {
                    existing.setTotalAmount(total_amount);
                    toSave.add(existing);
                } else {
                    toSave.add(new DailyRavenueEntity(
                            0,
                            songItem,
                            total_amount,
                            Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant()) // dùng thời gian chính xác
                    ));
                }
            }
            dailyRavenueRepository.saveAll(toSave);
        } while (songPageListFindAll.hasNext());
    }

    // tinh lai nhung ngay chua co tinh
    public boolean recalculateMissingDailyRevenue() {
        try {
            LocalDate today = LocalDate.now();

            int pageSize = 100;
            int pageNumber = 0;
            Page<SongEntity> songPageListFindAll;

            do {
                songPageListFindAll = songRepository.findAll(PageRequest.of(pageNumber, pageSize));
                List<SongEntity> songList = songPageListFindAll.getContent();

                if (songList.isEmpty()) break;

                // Lấy tất cả ID bài hát trong lô
                List<Integer> songIds = songList.stream().map(SongEntity::getId).collect(Collectors.toList());

                // Lấy ngày tạo của các bài hát
                Map<Integer, LocalDate> songCreatedDateMap = songList.stream()
                        .collect(Collectors.toMap(
                                SongEntity::getId,
                                song -> song.getCreateDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                        ));

                // Lấy tất cả ngày đã có doanh thu cho lô bài hát
                List<Object[]> revenueDates = dailyRavenueRepository.findDistinctDatesBySongIds(songIds);
                Map<Integer, Set<LocalDate>> datesWithRevenueMap = revenueDates.stream()
                        .collect(Collectors.groupingBy(
                                arr -> (Integer) arr[0], // songId
                                Collectors.mapping(
                                        arr -> ((Date) arr[1]).toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                                        Collectors.toSet()
                                )
                        ));

                // Tìm ngày bị thiếu và tính toán
                List<DailyRavenueEntity> toSave = new ArrayList<>();
                for (SongEntity song : songList) {
                    int songId = song.getId();
                    LocalDate songCreatedDate = songCreatedDateMap.get(songId);
                    Set<LocalDate> datesWithRevenue = datesWithRevenueMap.getOrDefault(songId, Collections.emptySet());

                    // Tạo danh sách khoảng thời gian bị thiếu
                    List<LocalDate> missingDates = new ArrayList<>();
                    for (LocalDate date = songCreatedDate; date.isBefore(today); date = date.plusDays(1)) {
                        if (!datesWithRevenue.contains(date)) {
                            missingDates.add(date);
                        }
                    }

                    // Đếm lượt nghe cho tất cả ngày bị thiếu
                    if (!missingDates.isEmpty()) {

                        List<LocalDate> dates = new ArrayList<>(missingDates);
                        List<Object[]> listenCounts = countListenRepository.countBySongEntityIdAndCreateDatesIn(songId, dates);
                        for (Object[] row : listenCounts) {
                            System.out.println("Date: " + ((java.sql.Date) row[0]).toLocalDate() + ", Count: " + row[1]);
                        }
//                        System.out.println("nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn " + listenCounts[1]);
                        for (Object[] row : listenCounts) {

                            System.out.println("Row: ");
                            for (Object col : row) {
                                System.out.println("  " + col);
                            }
                        }

                        // Chuyển đổi java.sql.Date sang LocalDate
                        Map<LocalDate, Long> listenCountMap = listenCounts.stream()
                                .collect(Collectors.toMap(
                                        arr -> ((java.sql.Date) arr[0]).toLocalDate(),
                                        arr -> (Long) arr[1],
                                        (v1, v2) -> v1
                                ));

                        // Tạo bản ghi doanh thu cho ngày bị thiếu
                        for (LocalDate date : missingDates) {
                            long listenCount = listenCountMap.getOrDefault(date, 0L);
                            int totalAmount = song.isVipSong() ? (int) listenCount * 20 : (int) listenCount * 10;

                            DailyRavenueEntity dailyRevenue = new DailyRavenueEntity();
                            dailyRevenue.setSongEntity(song);
                            dailyRevenue.setCreateDate(Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
                            dailyRevenue.setTotalAmount(totalAmount);
                            toSave.add(dailyRevenue);
                        }
                    }
                }

                if (!toSave.isEmpty()) {

                    dailyRavenueRepository.saveAll(toSave);
                }

                pageNumber++;
            } while (songPageListFindAll.hasNext());

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<DailyRavenueRespone> findByUserIdEndFindAll(Integer userId, Pageable pageable) {
        List<DailyRavenueRespone> result;
        if (userId == null) {
            List<DailyRavenueEntity> dailyRavenueEntityList = dailyRavenueRepository.findAll(pageable).getContent();
            result = dailyRavenueEntityList.stream()
                    .map(dr -> {
                        SongEntity song = dr.getSongEntity();
                        SongResponese songResponse = new SongResponese(
                                song.getId(),
                                song.getName(),
                                song.getMp3File(),
                                song.isVipSong(),
                                song.getAvatar(),
                                song.getCreateDate(),
                                song.getStatus(),
                                song.getCountListens(),
                                song.getDuration(),
                                "kkkk",
                                song.getLyrics(),
                                new ArrayList<>(),
                                new ArrayList<>(),
                                new ArrayList<>()
                        );
                        long listenCount = countListenRepository.countBySongEntity_IdAndCreateDateBetween(
                                song.getId(),
                                dr.getCreateDate(),
                                new Date(dr.getCreateDate().getTime() + 24 * 60 * 60 * 1000 - 1)
                        );
                        return new DailyRavenueRespone(
                                dr.getId(),
                                Collections.singletonList(songResponse),
                                dr.getTotalAmount(),
                                (int) listenCount,
                                dr.getCreateDate()
                        );
                    })
                    .collect(Collectors.toList());
        } else {
            List<Object[]> rawResults = dailyRavenueRepository.findByUserIdWithListenCount(userId, pageable);

            // Lấy tất cả id trong rawResults để query entity cùng lúc (giảm số lần gọi DB)
            List<Integer> drIds = rawResults.stream()
                    .map(obj -> ((Number) obj[0]).intValue())  // dr.id là obj[0]
                    .collect(Collectors.toList());

            List<DailyRavenueEntity> drEntities = dailyRavenueRepository.findAllById(drIds);

            // Map id -> entity để dễ lấy
            Map<Integer, DailyRavenueEntity> drMap = drEntities.stream()
                    .collect(Collectors.toMap(DailyRavenueEntity::getId, Function.identity()));

            result = rawResults.stream()
                    .map(obj -> {
                        Integer drId = ((Number) obj[0]).intValue();
                        Long listenCount = ((Number) obj[4]).longValue(); // vì native query trả về dr.id, dr.create_date, dr.total_amount, dr.song_id, listenCount
                        DailyRavenueEntity dr = drMap.get(drId);
                        SongEntity song = dr.getSongEntity();

                        SongResponese songResponse = new SongResponese(
                                song.getId(),
                                song.getName(),
                                song.getMp3File(),
                                song.isVipSong(),
                                song.getAvatar(),
                                song.getCreateDate(),
                                song.getStatus(),
                                song.getCountListens(),
                                song.getDuration(),
                                "kkkk",
                                song.getLyrics(),
                                new ArrayList<>(),
                                new ArrayList<>(),
                                new ArrayList<>()
                        );

                        return new DailyRavenueRespone(
                                dr.getId(),
                                Collections.singletonList(songResponse),
                                dr.getTotalAmount(),
                                listenCount.intValue(),
                                dr.getCreateDate()
                        );
                    })
                    .collect(Collectors.toList());
        }
        return result;
    }

// tinh daily của hôm nay nghĩa là tính tiền cho bài hát từ 12h tơiws bây giờ la nmgay hom nay ne
    public List<DailyRavenueEntity> calculateTodayRevenue(int userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.with(LocalTime.MIDNIGHT); // 0h hôm nay
        LocalDateTime endTime = now; // đến thời điểm hiện tại

        int pageSize = 100;
        int pageNumber = 0;
        Page<SongEntity> songPageListFindAll;
        List<DailyRavenueEntity> allRevenuesToday = new ArrayList<>();

        do {
            songPageListFindAll = songRepository.findByUser(userId,PageRequest.of(pageNumber, pageSize));
            List<SongEntity> songs = songPageListFindAll.getContent();
            if (songs.isEmpty()) break;

            List<Integer> songIds = songs.stream().map(SongEntity::getId).collect(Collectors.toList());

            // Đếm số lượt nghe của bài hát từ 0h đến hiện tại
            List<Object[]> songIdAndCountListen = songRepository.countListBySongId(songIds, startTime, endTime);
            Map<Integer, Integer> songIdAndCountListenMap = songIdAndCountListen.stream().collect(Collectors.toMap(
                    songId -> (Integer) songId[0],
                    countListen -> ((Long) countListen[1]).intValue()
            ));

            for (SongEntity songItem : songs) {
                int listens = songIdAndCountListenMap.getOrDefault(songItem.getId(), 0);
                int totalAmount = songItem.isVipSong() ? listens * 20 : listens * 10;

                DailyRavenueEntity todayRevenue = new DailyRavenueEntity(
                        0,
                        songItem,
                        totalAmount,
                        Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant())
                );
                allRevenuesToday.add(todayRevenue);
            }

            pageNumber++;
        } while (songPageListFindAll.hasNext());

        return allRevenuesToday;
    }

    public Long calculateTodayRevenueForUser(Integer userId) {
        try {
            // Lấy thời gian bắt đầu và kết thúc của hôm nay
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startTime = now.with(LocalTime.MIDNIGHT);
            LocalDateTime endTime = now.with(LocalTime.MAX);

            Date startDate = Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant());

            // Truy vấn tất cả doanh thu của user trong ngày hôm nay
            List<DailyRavenueEntity> todayRevenues = dailyRavenueRepository
                    .findAllByCreateDateBetweenAndSongEntityUserId(startDate, endDate, userId);

            // Tính tổng doanh thu
            long totalAmount = 0L;
            for (DailyRavenueEntity revenue : todayRevenues) {
                totalAmount += revenue.getTotalAmount();
            }

            // In ra console cho dễ debug
            System.out.println("-------------");
            System.out.println("ID User: " + userId);
            System.out.println("Tổng doanh thu: " + String.format("%,d VND", totalAmount));

            return totalAmount;
        } catch (Exception e) {
            System.out.println("LỖI: Tính doanh thu hôm nay cho userId = " + userId + " thất bại: " + e.getMessage());
            return 0L;
        }
    }



    public int calculatorMoneyToday(int songId, boolean vipSong) {
        return countListenService.calculatorCountListenToDay(songId)  * (vipSong ? 10 : 20);
    }

}
