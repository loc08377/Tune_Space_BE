package org.example.backend_fivegivechill.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ArtistResponse {
    private int id;
    private String fullName;
    private String avatar;
    private String hometown;
    private String biography;
    private int status;
    private boolean following = false; // gán mặc định tại đây luôn

    //aaaaaaaaaaaaaaaaaaaaaaaaaaa
    // Constructor cũ 6 tham số để giữ tương thích
    public ArtistResponse(int id, String fullName, String avatar, String country, String biography, int status) {
        this.id = id;
        this.fullName = fullName;
        this.avatar = avatar;
        this.hometown = country;
        this.biography = biography;
        this.status = status;
    }
}
