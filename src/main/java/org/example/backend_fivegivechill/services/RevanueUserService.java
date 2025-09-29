package org.example.backend_fivegivechill.services;

import org.example.backend_fivegivechill.entity.*;
import org.example.backend_fivegivechill.repository.*;
import org.example.backend_fivegivechill.response.BankRespone;
import org.example.backend_fivegivechill.response.SongAndfollowQuantityRespone;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;



@Service
public class RevanueUserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    DailyRavenueRepository dailyRevenueRepository;

    @Autowired
    RavenueUserRepository ravenueUserRepository;

    @Autowired
    DailyRavenueService dailyRavenueService;
    @Autowired
    AdminService adminService;

    @Autowired
    RevenueUserBankRepository revenueUserBankRepository;

    @Autowired
    FollowRepository followRepository;


    LocalDateTime now = LocalDateTime.now(); // lấy thoi gian hiện tịa
    LocalDateTime targetDayStart = now.with(LocalTime.MIDNIGHT).minusDays(1); // 0h00 hôm nay nè
    LocalDateTime targetDayEnd = targetDayStart.with(LocalTime.MAX).minusNanos(1); // 23:59:59 ngày của hômnay

    Date startDate = Date.from(targetDayStart.atZone(ZoneId.systemDefault()).toInstant());
    Date endDate = Date.from(targetDayEnd.atZone(ZoneId.systemDefault()).toInstant());
    @Autowired
    private SongRepository songRepository;

    @Scheduled(cron = "0 0 0 * * ?")  //0h 0p 0s (*moi ngay) (*moi thang)
    public void calculatetRevenueUser() {
        try {
            int pageSize = 100;
            int pageNumber = 0;
            Page<UserEntity> userPageListFindAll;

            do {
                userPageListFindAll = userRepository.findByRole(2, PageRequest.of(pageNumber, pageSize));
                List<UserEntity> users = userPageListFindAll.getContent();
                if (users.isEmpty()) break;

                // 🔹 Lọc user thỏa điều kiện checkEarnMoney
                List<UserEntity> eligibleUsers = users.stream()
                        .filter(user -> isEligibleToEarn(user.getId()))
                        .toList();

                if (eligibleUsers.isEmpty()) {
                    pageNumber++;
                    continue; // bỏ qua page này
                }

                List<Integer> userIds = eligibleUsers.stream()
                        .map(UserEntity::getId)
                        .toList();

                // Lấy tổng doanh thu từ DailyRavenueEntity cho lô user
                List<Object[]> dailyRevenueSums = dailyRevenueRepository
                        .sumTotalAmountByUserIdInAndCreateDateBetween(userIds, startDate, endDate);

                Map<Integer, Integer> userRevenueMap = dailyRevenueSums.stream()
                        .collect(Collectors.toMap(
                                arr -> ((Number) arr[0]).intValue(),
                                arr -> ((Number) arr[1]).intValue(),
                                (v1, v2) -> v1
                        ));

                // Lấy bản ghi RavenueUserEntity đã tồn tại trong ngày
                List<RavenueUserEntity> existingRevenues = ravenueUserRepository
                        .findByUserEntityIdInAndCreateDateBetween(userIds, startDate, endDate);

                Map<Integer, RavenueUserEntity> existingRevenueMap = existingRevenues.stream()
                        .collect(Collectors.toMap(
                                revenue -> revenue.getUserEntity().getId(),
                                revenue -> revenue
                        ));

                // Tạo/cập nhật bản ghi RavenueUserEntity
                List<RavenueUserEntity> toSave = new ArrayList<>();
                for (UserEntity user : eligibleUsers) {
                    Integer userId = user.getId();
                    int totalAmount = userRevenueMap.getOrDefault(userId, 0);

                    RavenueUserEntity existingRevenue = existingRevenueMap.get(userId);
                    if (existingRevenue != null) {
                        existingRevenue.setAmount(totalAmount);
                        existingRevenue.setType(true);
                        toSave.add(existingRevenue);
                    } else {
                        RavenueUserEntity ravenue = new RavenueUserEntity();
                        ravenue.setUserEntity(user);
                        ravenue.setAmount(totalAmount);
                        ravenue.setType(true);
                        ravenue.setCreateDate(startDate);
                        toSave.add(ravenue);
                    }
                }

                if (!toSave.isEmpty()) {
                    ravenueUserRepository.saveAll(toSave);
                }

                pageNumber++;
            } while (userPageListFindAll.hasNext());
        } catch (Exception e) {
            System.out.println("❌ Lỗi khi tính revenue user: " + e.getMessage());
        }
    }


    public SongAndfollowQuantityRespone checkEarnMoney(int userId) {
        int followQuantity = 0;
        int recentSongCount = 0;
        int topListenCount = 0;
        // query trả về duy nhất 1 record với 2 cột
        Object result = songRepository.findSongStatsByUser(userId);

       if(followRepository.countFollowersByUserId(userId) != 0){
           followQuantity = followRepository.countFollowersByUserId(userId);
       }

        if (result != null) {
            Object[] row = (Object[]) result;
            recentSongCount = row[0] != null ? ((Number) row[0]).intValue() : 0;
            topListenCount  = row[1] != null ? ((Number) row[1]).intValue() : 0;
        }

        boolean eligible = followQuantity >= 0
                && recentSongCount >= 3
                && topListenCount >= 1000;

        return new SongAndfollowQuantityRespone(followQuantity, recentSongCount, topListenCount, eligible);
    }



    public boolean isEligibleToEarn(int userId) {
        SongAndfollowQuantityRespone stats = checkEarnMoney(userId);
        return stats.getFollowQuantity() >= 500
                && stats.getRecentSongCount() >= 3
                && stats.getTopListenCount() >= 1000;
    }


    public boolean recalculateMissingRevenuUser() {
        try {
            LocalDate today = LocalDate.now();
            int pageSize = 100;
            int pageNumber = 0;
            Page<UserEntity> userPageListFindAll;

            do {
                userPageListFindAll = userRepository.findByRole(2, PageRequest.of(pageNumber, pageSize));
                List<UserEntity> users = userPageListFindAll.getContent();
                if (users.isEmpty()) break;

                List<Integer> userIds = users.stream().map(UserEntity::getId).collect(Collectors.toList());

                List<Object[]> earliestRevenueDates = dailyRevenueRepository.findEarliestDatesByUserIds(userIds);
                Map<Integer, LocalDate> earliestRevenueDateMap = earliestRevenueDates.stream()
                        .collect(Collectors.toMap(
                                arr -> (Integer) arr[0],
                                arr -> ((Date) arr[1]).toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                                (d1, d2) -> d1.isBefore(d2) ? d1 : d2
                        ));

                List<Object[]> revenueDates = ravenueUserRepository.findDistinctDatesByUserIds(userIds);
                Map<Integer, Set<LocalDate>> datesWithRevenueMap = revenueDates.stream()
                        .collect(Collectors.groupingBy(
                                arr -> (Integer) arr[0],
                                Collectors.mapping(
                                        arr -> ((Date) arr[1]).toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                                        Collectors.toSet()
                                )
                        ));

                List<RavenueUserEntity> toSave = new ArrayList<>();
                for (UserEntity user : users) {
                    Integer userId = user.getId();
                    LocalDate startDate = earliestRevenueDateMap.get(userId);
                    if (startDate == null) {
                        continue;
                    }

                    Set<LocalDate> datesWithRevenue = datesWithRevenueMap.getOrDefault(userId, Collections.emptySet());

                    List<LocalDate> missingDates = new ArrayList<>();
                    for (LocalDate date = startDate; date.isBefore(today); date = date.plusDays(1)) {
                        if (!datesWithRevenue.contains(date)) {
                            missingDates.add(date);
                        }
                    }

                    if (!missingDates.isEmpty()) {
                        for (LocalDate date : missingDates) {
                            LocalDateTime startOfDay = date.atStartOfDay();
                            LocalDateTime endOfDay = date.atTime(23, 59, 59);
                            Date start = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
                            Date end = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());

                            List<Object[]> dailyRevenueSums = dailyRevenueRepository.sumTotalAmountByUserIdInAndCreateDateBetween(
                                    Collections.singletonList(userId), start, end
                            );

                            // Sửa đoạn stream để xử lý kiểu Long
                            int totalAmount = dailyRevenueSums.stream()
                                    .filter(arr -> {
                                        Number userIdFromQuery = (Number) arr[0]; // userId có thể là Long
                                        return userIdFromQuery.intValue() == userId;
                                    })
                                    .map(arr -> {
                                        Number sum = (Number) arr[1]; // totalAmount có thể là Long
                                        return sum.intValue();
                                    })
                                    .findFirst()
                                    .orElse(0);

                            RavenueUserEntity ravenue = new RavenueUserEntity();
                            ravenue.setUserEntity(user);
                            ravenue.setAmount(totalAmount);
                            ravenue.setType(true);
                            ravenue.setCreateDate(start);
                            ravenue.setStatus(1);
                            toSave.add(ravenue);
                        }
                    }
                }

                if (!toSave.isEmpty()) {
                    ravenueUserRepository.saveAll(toSave);
                }

                pageNumber++;
            } while (userPageListFindAll.hasNext());

            return true;
        } catch (Exception e) {
            return false;
        }
    }
    // rut tien
//    public String withdrawMoney(int drawMoney, int userId, BankRespone bankRespone) {
//        try {
//
//            System.out.println("DEBUG - BankRespone: ");
//            System.out.println("Số tài khoản: " + bankRespone.getNumberAccount());
//            System.out.println("Tên tài khoản: " + bankRespone.getNameAccount());
////            Object result = ravenueUserRepository.sumAmountsByType(userId);
////
////            int totalRecharge = 0;
////            int totalWithdraw = 0;
////
////            if (result != null) {
////                Object[] data = (Object[]) result;
////                totalRecharge = data[0] != null ? ((Number) data[0]).intValue() : 0;
////                totalWithdraw = data[1] != null ? ((Number) data[1]).intValue() : 0;
////            }
//
//            Long totalMoney = ravenueUserRepository.getTotalAmountDifferenceByUser(userId);
//            System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
//            System.out.println(totalMoney);
//            System.out.println(drawMoney);
//            if (drawMoney > totalMoney) {
//                return "Số tiền không đủ để rút";
//            }
//
//            Optional<UserEntity> optionalUser = userRepository.findById(userId);
//
//            RavenueUserEntity ravenue = new RavenueUserEntity();
//            ravenue.setUserEntity(optionalUser.get());
//            ravenue.setAmount(drawMoney);
//            ravenue.setType(false);
//            ravenue.setCreateDate(new Date());
//            ravenue.setStatus(0);
//
//            RevenueUserBankEntity revenueUserBank = new RevenueUserBankEntity();
//            revenueUserBank.setRevenueUser(ravenue);
//            revenueUserBank.setBankAccountNumber(bankRespone.getNumberAccount());
//            revenueUserBank.setBankName(bankRespone.getNameAccount());
//
//            ravenueUserRepository.save(ravenue);
//
//            revenueUserBankRepository.save(revenueUserBank);
//
//            return "Đã rút số tiền " + drawMoney;
//        } catch (Exception e) {
//            return "Lỗi khi rút tiền";
//        }
//    }

    public String withdrawMoney(int drawMoney, int userId, BankRespone bankRespone) {
        try {
            Long totalMoney = ravenueUserRepository.getTotalAmountDifferenceByUser(userId);
            if (drawMoney > totalMoney) {
                return "Số tiền không đủ để rút";
            }

            Optional<UserEntity> optionalUser = userRepository.findById(userId);

            // ✅ Tính phí mới
            int fee = 0;
            if (drawMoney > 100000) {
                fee = 5000;
            }
            int finalAmount = drawMoney - fee;

            RavenueUserEntity ravenue = new RavenueUserEntity();
            ravenue.setUserEntity(optionalUser.get());
            ravenue.setAmount(finalAmount); // ✅ số tiền thực nhận sau khi trừ phí
            ravenue.setType(false);
            ravenue.setCreateDate(new Date());
            ravenue.setStatus(0);

            RevenueUserBankEntity revenueUserBank = new RevenueUserBankEntity();
            revenueUserBank.setRevenueUser(ravenue);
            revenueUserBank.setBankAccountNumber(bankRespone.getNumberAccount());
            revenueUserBank.setBankName(bankRespone.getNameAccount());

            ravenueUserRepository.save(ravenue);
            revenueUserBankRepository.save(revenueUserBank);

            return "Yêu cầu rút tiền thành công";


        } catch (Exception e) {
            return "Lỗi khi rút tiền";
        }
    }



    public RavenueUserEntity calculateTodayUserRevenueFromMemory(int userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.with(LocalTime.MIDNIGHT);
        Date startDate = Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant());

        List<DailyRavenueEntity> todayRevenues = dailyRavenueService.calculateTodayRevenue(userId);

        try {
            Optional<UserEntity> optionalUser = userRepository.findById(userId);
            if (optionalUser.isPresent()) {
                UserEntity user = optionalUser.get();

                long totalAmount = todayRevenues.stream()
                        .filter(rev -> rev.getSongEntity().getUser().getId() == userId)
                        .mapToLong(DailyRavenueEntity::getTotalAmount)
                        .sum();
                RavenueUserEntity userRevenue = new RavenueUserEntity();
                userRevenue.setUserEntity(user);
                userRevenue.setAmount(totalAmount);
                userRevenue.setType(true);
                userRevenue.setCreateDate(startDate);

                return userRevenue;
            } else {
                System.out.println("Không tìm thấy user với ID = " + userId);
            }
        } catch (Exception e) {
            System.out.println("LỖI: Tính doanh thu user trong ngày từ bộ nhớ thất bại: " + e.getMessage());
        }
        return null;
    }




    public Long calculatorRavenueUser(int userId, Date startDate, Date endDate) {
        Long result = ravenueUserRepository.getTotalAmountByDateRangeAndTypeTrue(userId, startDate, endDate);
        return result != null ? result : 0L;
    }

}
