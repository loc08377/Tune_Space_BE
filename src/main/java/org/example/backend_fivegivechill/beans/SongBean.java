package org.example.backend_fivegivechill.beans;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SongBean {

    @NotBlank(message = "Vui lòng nhập tên bài hát")
    @Length(min = 2, max = 250, message = "Vui lòng nhập tên bài hát từ 2-250 ký tự")
    private String name;

    @NotBlank(message = "Vui lòng chọn file nhạc")
    private String mp3File;

    private boolean vipSong;

    @NotBlank(message = "Vui lòng chọn hình ảnh cho bài hát")
    private String avatar;

    @Min(value = 0, message = "Trạng thái không hợp lệ")
    private int status;

    @Min(value = 1, message = "Thời lượng phải là số nguyên dương")
    private int duration;

    @NotBlank(message = "Lời bài hát không được trống")
    private String lyrics;

    @NotNull(message = "Vui lòng chọn thể loại")
    @Size(min = 1, message = "Vui lòng chọn ít nhất một thể loại")
    private List<@NotNull(message = "ID thể loại không được null") Integer> categoryIds;

    @NotNull(message = "Vui lòng chọn ca sĩ")
    @Size(min = 1, message = "Vui lòng chọn ít nhất một ca sĩ.")
    private List<@NotNull(message = "ID ca sĩ không được null") Integer> artistIds;

    @NotNull(message = "Vui lòng chọn tác giả")
    @Size(min = 1, message = "Vui lòng chọn ít nhất một tác giả.")
    private List<@NotNull(message = "ID tác giả không được null") Integer> artistIdss;
}
