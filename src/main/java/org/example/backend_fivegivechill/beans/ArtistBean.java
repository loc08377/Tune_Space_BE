package org.example.backend_fivegivechill.beans;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArtistBean {
    @NotBlank(message = "Vui lòng nhập tên nghệ sĩ")
    @Length(min = 2, max = 250, message = "Vui lòng nhập tên nghệ sĩ từ 2-250 ký tự")
    private String fullName;

    @NotBlank(message = "Vui lòng chọn hình ảnh của nghệ sĩ")
    private String avatar;

    @NotBlank(message = "Vui lòng nhập quê quán")
    @Length(min = 2, max = 250, message = "Vui lòng nhập quê quán từ 2-250 ký tự")
    private String hometown;

    @NotBlank(message = "Vui lòng nhập tiểu sử của nghệ sĩ")
    @Length(min = 2, message = "Vui lòng nhập tiểu sử của nghệ sĩ ít nhất 2 ký tự")
    private String biography;

    @Min(value = 0, message = "Trạng thái không hợp lệ")
    private int status;
}
