package org.example.backend_fivegivechill.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.backend_fivegivechill.entity.CategoryEntity;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CateSongResponse {
    private int id;
    private CategoryEntity cateSong;
}
