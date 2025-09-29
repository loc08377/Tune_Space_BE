package org.example.backend_fivegivechill.services;

import org.example.backend_fivegivechill.beans.SubscriptionUserBean;
import org.example.backend_fivegivechill.entity.SubscriptionPackageEntity;
import org.example.backend_fivegivechill.entity.SubscriptionUserEntity;
import org.example.backend_fivegivechill.entity.UserEntity;
import org.example.backend_fivegivechill.repository.SubscriptionPackageRepository;
import org.example.backend_fivegivechill.repository.SubscriptionUserRepository;
import org.example.backend_fivegivechill.repository.UserRepository;
import org.example.backend_fivegivechill.response.SubscriptionStatusResponse;
import org.example.backend_fivegivechill.response.SubscriptionUserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class SubscriptionPackage_userService {

    @Autowired
    private SubscriptionUserRepository subscriptionUserRepository;

    @Autowired
    private SubscriptionPackageRepository subscriptionPackageRepository;

    @Autowired
    private UserRepository userRepository;

    // Phương thức hiện tại: Lấy danh sách gói còn hiệu lực
    public List<SubscriptionUserResponse> findActiveByUserId(int id) {
        List<SubscriptionUserEntity> entities = subscriptionUserRepository.findActiveByUserId(id);
        return entities.stream()
                .map(entity -> new SubscriptionUserResponse(
                        entity.getId(),
                        entity.getPrice(),
                        entity.getSubscriptionPackageEntity().getName(),
                        entity.getCreateDate(),
                        entity.getFirstDay(),
                        entity.getLastDay(),
                        entity.isStatus(),
                        entity.getSubscriptionPackageEntity().getId(),
                        0
                ))
                .toList();
    }

    public List<SubscriptionUserResponse> getActiveSubscriptions(int userId) {
        List<SubscriptionUserEntity> entities = subscriptionUserRepository.findActiveByUserId(userId);
        Date today = new Date();
        return entities.stream()
                .filter(entity -> {
                    if (entity.getFirstDay() == null || entity.getLastDay() == null) {
                        return false;
                    }
                    return entity.getFirstDay().compareTo(entity.getLastDay()) <= 0
                            && !entity.getLastDay().before(today);
                })
                .map(entity -> {
                    SubscriptionUserResponse response = new SubscriptionUserResponse();
                    response.setId(entity.getId());
                    response.setPrice(entity.getPrice());
                    response.setCreateDate(entity.getCreateDate());
                    response.setFirstDay(entity.getFirstDay());
                    response.setLastDay(entity.getLastDay());
                    response.setStatus(entity.isStatus());
                    response.setSub_id(entity.getSubscriptionPackageEntity().getId());
                    response.setDuration(entity.getSubscriptionPackageEntity().getDuration());

                    return response;
                })
                .collect(Collectors.toList());
    }

    public List<SubscriptionUserResponse> getAllSubscriptionsByUserId(int userId) {
        List<SubscriptionUserEntity> entities = subscriptionUserRepository.findAllByUserId(userId);
        return entities.stream()
                .filter(entity -> entity.getFirstDay() != null && entity.getLastDay() != null)
                .map(entity -> {
                    SubscriptionUserResponse response = new SubscriptionUserResponse();
                    response.setId(entity.getId());
                    response.setPrice(entity.getPrice());
                    response.setTitle_subs(entity.getSubscriptionPackageEntity().getName());
                    response.setCreateDate(entity.getCreateDate());
                    response.setFirstDay(entity.getFirstDay());
                    response.setLastDay(entity.getLastDay());
                    response.setStatus(entity.isStatus());
                    response.setSub_id(entity.getSubscriptionPackageEntity().getId());
                    response.setDuration(entity.getSubscriptionPackageEntity().getDuration());
                    return response;
                })
                .collect(Collectors.toList());
    }


//    public SubscriptionStatusResponse getSubscriptionStatus(int userId) {
//        //này lấy danh sách còn ngày sử dụng
//        List<SubscriptionUserEntity> entities = subscriptionUserRepository.findActiveByUserId(userId);
//
//        Date today = new Date(); // Lấy ngày hiện tại
//        // Tìm last_day lớn nhất trongg nguên cái đóng mình truy ván ra đc
//        Date maxLastDay = entities.stream()
//                .map(SubscriptionUserEntity::getLastDay)
//                .max(Date::compareTo)
//                .orElse(today);
//
//        // Tính số ngày còn lại từ hiện tại tới cái này lát day lớn nhát mới kím được ỏ trên
//        long diffInMillies = maxLastDay.getTime() - today.getTime();
//        int daysRemaining = (int) TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
//        daysRemaining = Math.max(daysRemaining, 0); // Đảm bảo không âm
//
//        if (daysRemaining == 0) {
//            return new SubscriptionStatusResponse(false, daysRemaining);
//        }
//        return new SubscriptionStatusResponse(true, daysRemaining);
//    }

//
    public SubscriptionStatusResponse getSubscriptionStatus(int userId) {
        List<SubscriptionUserEntity> entities = subscriptionUserRepository.findActiveByUserId(userId);
        Date today = new Date();
        Date maxLastDay = entities.stream()
                .map(SubscriptionUserEntity::getLastDay)
                .max(Date::compareTo)
                .orElse(today);


        String detailedTime = calculateDetailedRemainingTime(maxLastDay);
        boolean isActive = !detailedTime.isEmpty();

        return new SubscriptionStatusResponse(isActive, detailedTime);
    }

    public String calculateDetailedRemainingTime(Date expiryDate) {
        Date now = new Date();

        long diffInMillis = expiryDate.getTime() - now.getTime();
        if (diffInMillis <= 0) {
            return ""; // Hết hạn
        }
    
        long days = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
        long remainingMillis = diffInMillis % TimeUnit.DAYS.toMillis(1);
        long hours = TimeUnit.HOURS.convert(remainingMillis, TimeUnit.MILLISECONDS);
        remainingMillis %= TimeUnit.HOURS.toMillis(1);
        long minutes = TimeUnit.MINUTES.convert(remainingMillis, TimeUnit.MILLISECONDS);

        StringBuilder result = new StringBuilder();

        if (days > 0) {
            result.append(days).append(" ngày ");
        }
        if (hours > 0 || days > 0) {
            result.append(hours).append("h ");
        }
        result.append(minutes).append("p");

        return result.toString().trim();
    }


    public SubscriptionUserResponse savaPayment(SubscriptionUserBean subscriptionUserBean) {
        // Tìm user có cái id trogn cái bean
        Optional<UserEntity> userOptional = userRepository.findById(subscriptionUserBean.getUser_id());
        UserEntity userEntity = userOptional.get();

        // Tìm gioongggs thằng trên nhưng mà tìm thằng khác thôi
        Optional<SubscriptionPackageEntity> packageOptional = subscriptionPackageRepository.findById(subscriptionUserBean.getSub_id());
        SubscriptionPackageEntity subscriptionPackageEntity = packageOptional.get();

//        cái này là lấy cái ngày hienj tại èn gán cho 2 ông cố này
        Date createDate = new Date();
        Date firstDay = new Date();

        // Lấy danh sách gói còn hiệu lực của thằn quần user bằng cái id trong cái bean á
        List<SubscriptionUserEntity> activeSubscriptions = subscriptionUserRepository.findActiveByUserId(subscriptionUserBean.getUser_id());

        //cái này là để kím cái ngày lát day lớn nhất trong cái database mà là của thằn quần user mới kím đó
        Date maxLastDay = null;
        boolean isFirstDayInActiveRange = false;

        for (SubscriptionUserEntity activeSubscription : activeSubscriptions) { // này là lấy mấy cía gói mới kím được đó chạy for chớ có qq dì đâu
            // Vòng lặp này duyệt qua từng gói đăng ký của người dùng.
            Date activeFirstDay = activeSubscription.getFirstDay(); // cáin này la cái ngày của thằng quần gói đăng kí lấy đươc ở trên bằn truy cấn nè
            Date activeLastDay = activeSubscription.getLastDay(); // k khác mẹ gì thằng ở trên ngoài cái tên


            // cái after là cái hàm trong date nó trả về true false nó so sánh thằn trước coi coi có sao cáu thằng trong ngoặc hay k
            // before ngc lại đó

            // Kiểm tra nếu firstDay nằm trong khoảng cái gói m truy vấn và dùng for để lấy ra nè hiểu chưa
            if (!firstDay.before(activeFirstDay) && !firstDay.after(activeLastDay)) {
                isFirstDayInActiveRange = true;
            }
            // Kiểm tra xem ngày bắt đầu mới là cái mà m tạo ra cái nằm ở trên hàng 96 á có nằm trong khoảng thời gian của một gói đăng ký hiện tại hay không.
            // Điều kiện trên kiểm tra nếu firstDay không sớm hơn activeFirstDay (firstDay >= activeFirstDay) và không muộn hơn activeLastDay (firstDay <= activeLastDay).
            // Nếu nằm trong khoảng này, đặt biến isFirstDayInActiveRange = true.

            // Tìm last_day lớn nhất trong cái đống last day của cái for này nè nói chung duyệt mảng kím last day lớn nhất đó
            if (maxLastDay == null || activeLastDay.after(maxLastDay)) {
                maxLastDay = activeLastDay;
            }
            // Tìm ngày kết thúc (lastDay) mới nhất của các gói đăng ký hiện tại.
            // Nếu maxLastDay chưa được gán giá trị (maxLastDay == null), hoặc activeLastDay mới hơn maxLastDay đã tìm được, thì cập nhật maxLastDay = activeLastDay.

        }

        // Nếu firstDay hay nói cách khác là cái này m chuẩn bị tạo á nó nằm trong cái gói có
        // sẳn r nên là lất cái last day lớn nhất nãy duyệt mảng kím được làm cái fist day mới
        if (isFirstDayInActiveRange && maxLastDay != null) {
            firstDay = maxLastDay;
        }

//        cái đoạn khốn nạn này là dùn để + thêm thời gian để có được cái last day tại vì nguyên
        //một đống ở trên chỉ để tìm cái fist day thôi còn lát day thì phải = fist day + thêm cái số ngày cảu cái gói á cụ thể cái lá duration á
        Calendar calendar = Calendar.getInstance(); // tạo biến lấy cái tgg hiện tại ngay cái lúc mà cái hanggf này chạy nè nói chung là tạo biến đó
        calendar.setTime(firstDay);  // gán cái ngày fist day vào nè
        calendar.add(Calendar.DAY_OF_MONTH, subscriptionPackageEntity.getDuration());  // cái này là nó lấy cái fist day nó + duration á
        Date lastDay = calendar.getTime();  // đó bây giờ sao khi cộng thì nó gán lại cho lát day

        // Tạo SubscriptionUserEntity mới
        // Tạo đối tượng SubscriptionUserEntity mới để lưu vào cơ sở dữ liệu
        SubscriptionUserEntity newSubscription = new SubscriptionUserEntity();
        newSubscription.setUserEntity(userEntity);
        newSubscription.setSubscriptionPackageEntity(subscriptionPackageEntity);
        newSubscription.setPrice(subscriptionUserBean.getPrice());
        newSubscription.setCreateDate(createDate);
        newSubscription.setFirstDay(firstDay);
        newSubscription.setLastDay(lastDay);
        newSubscription.setStatus(true);

        SubscriptionUserEntity savedSubscription = subscriptionUserRepository.save(newSubscription);

//        này là cái trả về cho người dung
        SubscriptionUserResponse response = new SubscriptionUserResponse();
        response.setId(savedSubscription.getId());
        response.setPrice(savedSubscription.getPrice());
        response.setCreateDate(savedSubscription.getCreateDate());
        response.setFirstDay(savedSubscription.getFirstDay());
        response.setLastDay(savedSubscription.getLastDay());
        response.setStatus(savedSubscription.isStatus());
        response.setSub_id(subscriptionUserBean.getSub_id());

        return response;
    }


}
