package org.example.backend_fivegivechill.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlbumResponse {
    private Integer id;
    private String name;
    private String coverImage;
    private Boolean status;
    private String nameCreator;
    private List<TestURL> listSong;
    private List<Integer> ids;
}
