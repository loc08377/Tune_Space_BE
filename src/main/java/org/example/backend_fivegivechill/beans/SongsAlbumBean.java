package org.example.backend_fivegivechill.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SongsAlbumBean {
    private int albumId;
    private List<Integer> songIds;
}
