package org.example.backend_fivegivechill.beans;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistItemBean {
    private int playlistId;
    private int songId;
}
