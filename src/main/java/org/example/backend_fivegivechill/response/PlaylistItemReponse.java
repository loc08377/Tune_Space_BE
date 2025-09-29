package org.example.backend_fivegivechill.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistItemReponse {
    private int id;
    private int songId;
    private String name;
    private String avatar;
    private String artist;

}
