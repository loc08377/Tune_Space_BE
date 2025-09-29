package org.example.backend_fivegivechill.beans;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryBean {

    @NotBlank(message = "Vui lòng nhập tên danh mục")
    @Length(min = 2, max = 250, message = "Vui lòng nhập tên danh mục từ 2-250 ký tự")
    private String name;

    @Min(value = 0, message = "Trạng thái không hợp lệ")
    private int status;
}
