package org.example.backend_fivegivechill.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtistSongReponsneNew {
    private UserResponse creator;
    private List<TestURL> songs;
    private int numberOfSongs;
    private int numberFollows;
    private int numberAlbums;
    private List<AlbumResponse> albums;
}
