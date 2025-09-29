package org.example.backend_fivegivechill.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FollowResponse {

    //lop moi
    private int id;
    private int followerId;
    private int followingId;
    private String followerName;
    private String followingName;
    private boolean isFollowing;
}
