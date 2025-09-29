package org.example.backend_fivegivechill.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserInfoRespose {
    private int id;
    private String email;
    private String fullName;
    private String phone;
    private String avatar;
    private boolean status;
    private int role;

}
