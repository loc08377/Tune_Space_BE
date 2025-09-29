package org.example.backend_fivegivechill.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoryResponse {
    private int historyId;
    private Date historyCreateDate;
    private int songId;
    private String songName;
    private String mp3File;
    private boolean vipSong;
    private String avatar;
    private Date songCreateDate;
//    private int countListens;
    private int duration;
    private String nameCateSong;
    private String nameArtistSong;
}