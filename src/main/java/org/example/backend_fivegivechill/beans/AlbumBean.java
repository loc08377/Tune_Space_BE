package org.example.backend_fivegivechill.beans;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlbumBean {
    @NotBlank(message = "Vui lòng nhập tên bài hát")
    @Length(min = 2, max = 250, message = "Vui lòng nhập tên album từ 2-250 ký tự")
    private String name;
    private String coverImage;
    private boolean status;
}
