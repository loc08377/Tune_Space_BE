package org.example.backend_fivegivechill.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SongResponese {
    private int id;
    private String name;
    private String mp3File;
    private boolean vipSong;
    private String avatar;
    private Date createDate;
    private int status;
    private int countListens;

    private int duration;
    private String user;
    private String lyrics;
    private List<CateSongResponse> cateSongs;
    private List<ArtistSongResponse> artistSongs;
    private List<ArtistSongResponse> artistSongss;

    private int totolAmountSong;

    //aaaaaaaaaaaaaaaaaaaaaaaaaaa
    public SongResponese(int id, String name, String mp3File, boolean vipSong, String avatar,
                         Date createDate, int status, int countListens, int duration,
                         String user, String lyrics, List<CateSongResponse> cateSongs,
                         List<ArtistSongResponse> artistSongs, List<ArtistSongResponse> artistSongss) {
        this.id = id;
        this.name = name;
        this.mp3File = mp3File;
        this.vipSong = vipSong;
        this.avatar = avatar;
        this.createDate = createDate;
        this.status = status;
        this.countListens = countListens;
        this.duration = duration;
        this.user = user;
        this.lyrics = lyrics;
        this.cateSongs = cateSongs;
        this.artistSongs = artistSongs;
        this.artistSongss = artistSongss;
    }



}
