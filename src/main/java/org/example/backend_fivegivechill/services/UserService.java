package org.example.backend_fivegivechill.services;

import org.apache.catalina.User;
import org.example.backend_fivegivechill.beans.UserBean;
import org.example.backend_fivegivechill.beans.UserBeanClient;
import org.example.backend_fivegivechill.entity.UserEntity;
import org.example.backend_fivegivechill.repository.UserRepository;
import org.example.backend_fivegivechill.response.Response;
import org.example.backend_fivegivechill.response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebSocketService webSocketService;

    public Page<UserEntity> getAllUsersByStatus(boolean status, String search, Pageable pageable) {
        return userRepository.findAllByStatusTrue(status, "%"+search+"%", pageable);
    }

    public UserEntity getUserById(int id) {
        return userRepository.findById(id).orElse(null);
    }

    public ResponseEntity<Response> GetUserById(int id) {
        try {
            UserEntity userEntity = userRepository.findById(id).orElse(null);
            if (userEntity == null) {
                return ResponseEntity.badRequest().body(new Response(1, "User not found", null));
            }

            UserResponse userResponse = new UserResponse(
                    userEntity.getId(),
                    userEntity.getEmail(),
                    userEntity.getFullName(),
                    userEntity.getPhone(),
                    userEntity.getAvatar(),
                    userEntity.isStatus(),
                    userEntity.getRole()
            );

            return ResponseEntity.ok(new Response(0, "User found successfully", userResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }


    public UserEntity updateUser(UserBean userBean) {
        UserEntity exist = userRepository.findById(userBean.getId()).orElse(null);
        if (exist != null) {
            if (exist.getRole() == 0) {
                return null;
            }
            UserEntity userEntity = new UserEntity();
            userEntity.setId(exist.getId());
            userEntity.setEmail(exist.getEmail());
            userEntity.setFullName(exist.getFullName());
            userEntity.setPassword(exist.getPassword());
            userEntity.setPhone(exist.getPhone());
            userEntity.setAvatar(exist.getAvatar());
            boolean oldStatus = exist.isStatus();
            userEntity.setStatus(userBean.isStatus());

            if (oldStatus && !userBean.isStatus()) {
                webSocketService.sendForceLogout(userEntity.getId(), "Tài khoản của bạn đã bị khóa.");
            }

            userEntity.setRole(exist.getRole());
            return userRepository.save(userEntity);
        }
        return null;
    }

//    public UserEntity updateUser(UserBeanClient userBean) {
//        UserEntity exist = userRepository.findById(userBean.getId()).orElse(null);
//        if (exist != null) {
//            if (exist.getRole() == 0) {
//                return null; // không update admin
//            }
//
//            boolean oldStatus = exist.isStatus(); // lấy trạng thái cũ từ DB
//
//            // cập nhật các field nếu muốn
//            exist.setFullName(userBean.getFullName());
//            exist.setPhone(userBean.getPhone());
//            exist.setAvatar(userBean.getAvatar());
//            exist.setStatus(userBean.isStatus());
//
//            if (oldStatus && !userBean.isStatus()) {
//                webSocketService.sendForceLogout(exist.getId(), "Tài khoản của bạn đã bị khóa.");
//            }
//
//            return userRepository.save(exist);
//        }
//        return null;
//    }


    public UserEntity updateProfileUser(UserBeanClient userBean) {
        UserEntity exist = userRepository.findById(userBean.getId()).orElse(null);
        if (userBean.getEmail().equals(exist.getEmail())
            && userBean.getFullName().equals(exist.getFullName())
            && userBean.getPhone().equals(exist.getPhone())
        ) {
            return null;
        }
        exist.setEmail(userBean.getEmail());
        exist.setFullName(userBean.getFullName());
        exist.setPhone(userBean.getPhone());
        return userRepository.save(exist);

    }

    public UserEntity updateAvatar(UserBeanClient userBean) {
        UserEntity exist = userRepository.findById(userBean.getId()).orElse(null);
        if (exist == null) {
            return null;
        }

        // Kiểm tra xem avatar có thay đổi không
        if (userBean.getAvatar() == null || userBean.getAvatar().equals(exist.getAvatar())) {
            return null;
        }

        exist.setAvatar(userBean.getAvatar());
        return userRepository.save(exist);
    }

    public void updateUserStatus(int userId, boolean status) {
        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user == null) return; // tránh null pointer

        user.setStatus(status);   // sửa ở đây
        userRepository.save(user);

        // nếu user bị khóa, gửi event force logout
        if (!status) {
            webSocketService.sendForceLogout(userId, "Tài khoản của bạn đã bị khóa.");
        }
    }

}
