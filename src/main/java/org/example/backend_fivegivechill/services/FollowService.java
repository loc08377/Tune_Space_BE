package org.example.backend_fivegivechill.services;

import org.example.backend_fivegivechill.entity.FollowEntity;
import org.example.backend_fivegivechill.entity.UserEntity;
import org.example.backend_fivegivechill.repository.FollowRepository;
import org.example.backend_fivegivechill.repository.UserRepository;
import org.example.backend_fivegivechill.response.FollowResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.databind.type.LogicalType.DateTime;

@Service
public class FollowService {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;



    // Người dùng theo dõi người khác
    public boolean followUser(int followerId, int followingId) {
        if (followerId == followingId) return false; // Không tự follow chính mình

        UserEntity follower = userRepository.findById(followerId).orElse(null);
        UserEntity following = userRepository.findById(followingId).orElse(null);

        if (follower == null || following == null) return false;

        // Kiểm tra đã follow chưa
        boolean exists = followRepository.existsByFollowerIdAndUserId(followerId, followingId);
        if (exists) return false;

        FollowEntity follow = new FollowEntity();
        follow.setFollower(follower);
        follow.setFollowing(following);
        follow.setCreateDate(LocalDate.now());



        followRepository.save(follow);
        return true;
    }

    // Người dùng bỏ theo dõi
    public boolean unfollowUser(int followerId, int followingId) {
        followRepository.deleteByFollowerIdAndUserId(followerId, followingId);
        return false;
    }

    // Lấy danh sách người mà user đang theo dõi
    public List<FollowResponse> getFollowings(int followerId) {
        List<FollowEntity> list = followRepository.findByFollowerId(followerId);
        return list.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // Lấy danh sách người đang theo dõi user (fan của nghệ sĩ)
    public List<FollowResponse> getFollowers(int userId) {
        List<FollowEntity> list = followRepository.findByFollowingId(userId);
        return list.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // Lấy tất cả lượt theo dõi (admin)
    public List<FollowResponse> getAllFollows() {
        return followRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    //lop moi
    // Mapping FollowEntity sang FollowResponse
    public FollowResponse mapToResponse(FollowEntity follow) {
        FollowResponse response = new FollowResponse();
        response.setId(follow.getId());
        response.setFollowerId(follow.getFollower().getId());
        response.setFollowerName(follow.getFollower().getFullName());
        response.setFollowingId(follow.getFollowing().getId());
        response.setFollowingName(follow.getFollowing().getFullName());
        response.setFollowing(true);

        return response;
    }

}
