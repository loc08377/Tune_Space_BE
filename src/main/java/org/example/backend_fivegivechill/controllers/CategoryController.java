package org.example.backend_fivegivechill.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend_fivegivechill.beans.CategoryBean;
import org.example.backend_fivegivechill.entity.CategoryEntity;
import org.example.backend_fivegivechill.response.CategoryResponse;
import org.example.backend_fivegivechill.response.Response;
import org.example.backend_fivegivechill.services.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@CrossOrigin("*")
@RequiredArgsConstructor // Tự động tạo constructor cho các biến final (thay thế @Autowired)
public class CategoryController {

    private final CategoryService categoryService; // Dependency được inject tự động qua constructor

    private final HttpServletRequest request;

    @GetMapping({"/categories", "/categories/trash", "/categories/review"})
    public ResponseEntity<Response> categories(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {
        try {
            // Tạo đối tượng Pageable để phân trang
            Pageable pageable = PageRequest.of(page, size);

            String url = request.getRequestURI();
            Page<CategoryEntity> categoryPage;
            if (url.equals("/admin/categories/trash")) {
                categoryPage = categoryService.getAllCategoryByStatus(1, search, pageable);
            } else if (url.equals("/admin/categories/review")) {
                categoryPage = categoryService.getAllCategoryByStatus(2, search, pageable);
            } else {
                categoryPage = categoryService.getAllCategoryByStatus(0, search, pageable);
            }

            // Chuyển đổi danh sách sang CategoryResponse
            List<CategoryResponse> categoryResponseList = categoryPage.getContent().stream()
                    .map(cate -> new CategoryResponse(cate.getId(), cate.getName(), cate.getStatus()))
                    .collect(Collectors.toList());

            // Tạo response
            Response response = new Response(0, "Success!", categoryResponseList);
            response.setTotalPages(categoryPage.getTotalPages());       // Tổng số trang
            response.setTotalElements(page);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }

    // Chức năng lấy thông tin chi tiết một danh mục theo ID
    @GetMapping("/categories/{id}")
    public ResponseEntity<Response> getCategoryById(@PathVariable int id) {
        try {
            CategoryEntity categoryEntity = categoryService.getCategoryById(id);
            if (categoryEntity == null) {
                return ResponseEntity.badRequest().body(new Response(1, "Category not found", null));
            }

            // Chuyển đổi CategoryEntity sang CategoryResponse
            CategoryResponse categoryResponse = new CategoryResponse(categoryEntity.getId(), categoryEntity.getName(), categoryEntity.getStatus());

            Response response = new Response(0, "Success!", categoryResponse);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }

    // Chức năng thêm mới danh mục
    @PostMapping("/categories")
    public ResponseEntity<Response> addCategory(@Valid @RequestBody CategoryBean categoryBean, Errors errors) {
        try {
            if (errors.hasErrors()) {
                Map<String, String> errorMap = new HashMap<>();
                errors.getFieldErrors().forEach(fieldError -> {
                    errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
                });
                return ResponseEntity.badRequest().body(new Response(1, "Validation failed", errorMap));
            }

            // kiểm tra trùng tên khi thêm
            CategoryEntity existCategoryAdd = categoryService.existCategoryAdd(categoryBean);
            if (existCategoryAdd != null) {
                return ResponseEntity.badRequest().body(new Response(1, "Đã tồn tại một danh mục giống vậy!", null));
            }

            CategoryEntity savedCategory = categoryService.addCategory(categoryBean);
            if (savedCategory == null) {
                return ResponseEntity.badRequest().body(new Response(1, "Category existed", null));
            }

            Response response = new Response(0, "Success!", savedCategory);
            return ResponseEntity.status(HttpStatus.CREATED).body(response); // 201
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null)); // 400
        }
    }

    // Chức năng sửa danh mục
    @PutMapping("/categories/{id}")
    public ResponseEntity<Response> updateCategory(@PathVariable int id, @Valid @RequestBody CategoryBean categoryBean, Errors errors) {
        try {
            if (errors.hasErrors()) {
                Map<String, String> errorMap = new HashMap<>();
                errors.getFieldErrors().forEach(fieldError -> {
                    errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
                });
                return ResponseEntity.badRequest().body(new Response(1, "Validation failed", errorMap));
            }

            // kiểm tra trùng tên khi cập nhật
            CategoryEntity existCategoryAdd = categoryService.existCategoryUpdate(id, categoryBean);
            if (existCategoryAdd != null) {
                return ResponseEntity.badRequest().body(new Response(1, "Đã tồn tại một danh mục giống vậy!", null));
            }

            CategoryEntity updatedCategory = categoryService.updateCategory(id, categoryBean);
            if (updatedCategory == null) {
                return ResponseEntity.badRequest().body(new Response(1, "Không tìm thấy danh mục", null));
            }

            Response response = new Response(0, "Success!", updatedCategory);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }
}