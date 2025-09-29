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
public class SubscriptionPackageBean {
    @NotBlank(message = "Vui lòng nhập tên gói đăng ký")
    @Length(min = 2, message = "Vui lòng nhập tên gói đăng ký ít nhất 2 ký tự")
    private String name;

    @Min(value = 1, message = "Thời hạn của gói đăng ký phải ít nhất là 1 ngày")
    private int duration;

    @Min(value = 0, message = "Giá phải lớn hơn hoặc bằng 0")
    private int price;

    private boolean status;
}
