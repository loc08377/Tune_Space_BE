package org.example.backend_fivegivechill.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistResponse {
    private int playlistId;
    private String playlistName;
    private int userId;
    private String coverImage;
    private String nameCreator;
    private List<SongResponese> songs;
    private List<Integer> ids;
}
