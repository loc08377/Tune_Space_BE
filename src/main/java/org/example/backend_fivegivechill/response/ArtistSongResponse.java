package org.example.backend_fivegivechill.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.backend_fivegivechill.entity.ArtistEntity;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArtistSongResponse {
    private int id;
    private ArtistEntity artistSong;
}
