package org.example.backend_fivegivechill.controllers.Client;

import org.example.backend_fivegivechill.beans.FollowBean;
import org.example.backend_fivegivechill.entity.UserEntity;
import org.example.backend_fivegivechill.repository.FollowRepository;
import org.example.backend_fivegivechill.repository.UserRepository;
import org.example.backend_fivegivechill.response.FollowResponse;
import org.example.backend_fivegivechill.response.Response;
import org.example.backend_fivegivechill.services.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class FollowController {

    @Autowired
    private FollowService followService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    FollowRepository followRepository;

    // Lấy danh sách người dùng mà user đang theo dõi
    @GetMapping("/following/{userId}")
    public ResponseEntity<Response> getFollowings(@PathVariable int userId) {
        List<FollowResponse> list = followService.getFollowings(userId);
        return ResponseEntity.ok(new Response(0, "Fetched followings", list));
    }

    // Lấy danh sách người dùng đang theo dõi user
    @GetMapping("/followers/{userId}")
    public ResponseEntity<Response> getFollowers(@PathVariable int userId) {
        List<FollowResponse> list = followService.getFollowers(userId);
        return ResponseEntity.ok(new Response(0, "Fetched followers", list));
    }

    @GetMapping("/checkFollow/{id}")
    public ResponseEntity<Response> checkFollow(@PathVariable int id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(new Response(1, "Người dùng không tồn tại", null));
        }

        boolean exists = followRepository.existsByFollowerIdAndUserId(userOpt.get().getId(), id);

        return ResponseEntity.ok(new Response(0, "Fetched followers", exists));
    }


    // Theo dõi một người - lấy followerId từ người đăng nhập
    @PostMapping("/follow/{id}")
    public ResponseEntity<Response> followUser(@PathVariable int id) {
       try {
           String email = SecurityContextHolder.getContext().getAuthentication().getName();
           Optional<UserEntity> userOpt = userRepository.findByEmail(email);
           if (userOpt.isEmpty()) {
               return ResponseEntity.badRequest().body(new Response(1, "Người dùng không tồn tại", null));
           }

           int followerId = userOpt.get().getId(); // Id người theo dõi
           int followingId = id;  // ID của người được theo dõi

           UserEntity follower = userOpt.get();
           Optional<UserEntity> followingOpt = userRepository.findById(followingId);

           if (followingOpt.isEmpty()) {
               return ResponseEntity.badRequest().body(new Response(1, "Người được theo dõi không tồn tại", null));
           }

           UserEntity following = followingOpt.get();

           // 👉 Kiểm tra chỉ người dùng (role = 1) mới được follow nghệ sĩ (role = 2)
           if (follower.getRole() != 1) {
               return ResponseEntity.badRequest().body(new Response(1, "Chỉ người dùng mới có quyền theo dõi", null));
           }

           if (following.getRole() != 2) {
               return ResponseEntity.badRequest().body(new Response(1, "Chỉ được theo dõi nghệ sĩ", null));
           }

           boolean exists = followRepository.existsByFollowerIdAndUserId(followerId, followingId);

           boolean success;

           if(exists){
               success = followService.unfollowUser(followerId, followingId);
           } else {
               success = followService.followUser(followerId, followingId);
           }

           if (success) {
               return ResponseEntity.ok(new Response(0, "Follow thành công", null));
           } else {
               return ResponseEntity.ok(new Response(0, "UnFollow thành công", null));
           }
       } catch (Exception e) {
           return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
       }
    }
}

