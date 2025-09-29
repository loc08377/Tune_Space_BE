package org.example.backend_fivegivechill.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend_fivegivechill.beans.UserBean;
import org.example.backend_fivegivechill.beans.UserBeanClient;
import org.example.backend_fivegivechill.entity.UserEntity;
import org.example.backend_fivegivechill.response.Response;
import org.example.backend_fivegivechill.response.UserResponse;
import org.example.backend_fivegivechill.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@CrossOrigin("*")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final HttpServletRequest request;

    @GetMapping({"/users", "/users/trash"})
    public ResponseEntity<Response> getUsers(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            String url = request.getRequestURI();

            Page<UserEntity> userPage;
            if (url.equals("/admin/users/trash")) {
                userPage = userService.getAllUsersByStatus(false, search, pageable);
            } else {
                userPage = userService.getAllUsersByStatus(true, search, pageable);
            }

            List<UserResponse> userResponseList = userPage.getContent().stream()
                    .map(user -> new UserResponse(user.getId(), user.getEmail(), user.getFullName(), user.getPhone(), user.getAvatar(), user.isStatus(), user.getRole()))
                    .collect(Collectors.toList());

            Response response = new Response(0, "Success!", userResponseList);
            response.setTotalPages(userPage.getTotalPages());
            response.setTotalElements(page);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<Response> getUserById(@PathVariable int id) {
        try {

            UserEntity userEntity = userService.getUserById(id);
            if (userEntity == null) {
                return ResponseEntity.badRequest().body(new Response(1, "User not found", null));
            }
            UserResponse userResponse = new UserResponse(
                    userEntity.getId(), userEntity.getEmail(), userEntity.getFullName(),
                    userEntity.getPhone(), userEntity.getAvatar(), userEntity.isStatus(), userEntity.getRole());

            return ResponseEntity.ok(new Response(0, "Success!", userResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }


    @PutMapping("/users")
    public ResponseEntity<Response> updateUser(@Valid @RequestBody UserBean userBean) {
        try {
            UserEntity updatedUser = userService.updateUser(userBean);
            if (updatedUser == null) {
                return ResponseEntity.badRequest().body(new Response(1, "User not found", null));
            }

            return ResponseEntity.ok(new Response(0, "Success!", updatedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }
}
