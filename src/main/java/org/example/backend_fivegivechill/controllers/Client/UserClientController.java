package org.example.backend_fivegivechill.controllers.Client;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.backend_fivegivechill.beans.UserBean;
import org.example.backend_fivegivechill.beans.UserBeanClient;
import org.example.backend_fivegivechill.entity.UserEntity;
import org.example.backend_fivegivechill.response.Response;
import org.example.backend_fivegivechill.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@CrossOrigin("*")

@RequestMapping("/")
public class UserClientController {

    private final UserService userService;
    private final HttpServletRequest request;


        @GetMapping("/users/{id}")
    public ResponseEntity<Response> getUserById(@PathVariable int id) {
        return userService.GetUserById(id);
    }

    @PutMapping("/user/update/profilse")
    public ResponseEntity<Response> updateProfile(@RequestBody UserBeanClient userBean) {
        try {
            UserEntity updatedUser = userService.updateProfileUser(userBean);
            if (updatedUser == null) {
                return ResponseEntity.badRequest().body(new Response(0, "Thong tin chua duoc thay doi", false));
            }
            return ResponseEntity.ok().body(new Response(0, "User updated successfully", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }

    @PutMapping("/user/update/avatar")
    public ResponseEntity<Response> updateImage(@RequestBody UserBeanClient userBean) {
        try {
            UserEntity updatedUser = userService.updateAvatar(userBean);
            if (updatedUser == null) {
                return ResponseEntity.badRequest().body(new Response(0, "Thong tin chua duoc thay doi", false));
            }
            return ResponseEntity.ok().body(new Response(0, "User updated successfully", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }


}
