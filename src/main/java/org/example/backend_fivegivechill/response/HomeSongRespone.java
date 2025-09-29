package org.example.backend_fivegivechill.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HomeSongRespone {
    private int id;
    private String name;
    private String mp3File;
    private boolean vipSong;
    private String avatar;
    private Date createDate;
    private int duration;
    private int status;
    private String nameCateSong;
    private String nameArtistSong;
}
