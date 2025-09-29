package org.example.backend_fivegivechill.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend_fivegivechill.beans.SubscriptionPackageBean;
import org.example.backend_fivegivechill.entity.SubscriptionPackageEntity;
import org.example.backend_fivegivechill.response.Response;
import org.example.backend_fivegivechill.response.SubscriptionPackageResponse;
import org.example.backend_fivegivechill.services.SubscriptionPackageService;
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
@RequiredArgsConstructor // Tự động tạo constructor cho các biến final
public class SubscriptionPackageController {

    private final SubscriptionPackageService subscriptionPackageService;
    private final HttpServletRequest request;

    @GetMapping({"/subscription-packages", "/subscription-packages/trash"})
    public ResponseEntity<Response> getSubscriptionPackages(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            String url = request.getRequestURI();

            Page<SubscriptionPackageEntity> packagePage;
            if (url.equals("/admin/subscription-packages/trash")) {
                packagePage = subscriptionPackageService.getAllPackagesByStatus(false, search, pageable);
            } else {
                packagePage = subscriptionPackageService.getAllPackagesByStatus(true, search, pageable);
            }

            List<SubscriptionPackageResponse> packageResponseList = packagePage.getContent().stream()
                    .map(pkg -> new SubscriptionPackageResponse(pkg.getId(), pkg.getName(), pkg.getDuration(), pkg.getPrice(), pkg.isStatus()))
                    .collect(Collectors.toList());

            Response response = new Response(0, "Success!", packageResponseList);
            response.setTotalPages(packagePage.getTotalPages());
            response.setTotalElements(page);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }

    //aaa
    @GetMapping("/subscription-packages/{id}")
    public ResponseEntity<Response> getPackageById(@PathVariable int id) {
        try {
            SubscriptionPackageEntity packageEntity = subscriptionPackageService.getPackageById(id);
            if (packageEntity == null) {
                return ResponseEntity.badRequest().body(new Response(1, "Subscription Package not found", null));
            }

            SubscriptionPackageResponse packageResponse = new SubscriptionPackageResponse(
                    packageEntity.getId(), packageEntity.getName(),
                    packageEntity.getDuration(), packageEntity.getPrice(), packageEntity.isStatus());

            return ResponseEntity.ok(new Response(0, "Success!", packageResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }

    @PostMapping("/subscription-packages")
    public ResponseEntity<Response> addPackage(@Valid @RequestBody SubscriptionPackageBean packageBean, Errors errors) {
        try {
            if (errors.hasErrors()) {
                Map<String, String> errorMap = new HashMap<>();
                errors.getFieldErrors().forEach(fieldError -> {
                    errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
                });
                return ResponseEntity.badRequest().body(new Response(1, "Validation failed", errorMap));
            }

            SubscriptionPackageEntity existPackage = subscriptionPackageService.existPackageAdd(packageBean);
            if (existPackage != null) {
                return ResponseEntity.badRequest().body(new Response(1, "Đã tồn tại một gói đăng ký giống vậy!", null));
            }

            SubscriptionPackageEntity savedPackage = subscriptionPackageService.addPackage(packageBean);
            return ResponseEntity.status(HttpStatus.CREATED).body(new Response(0, "Success!", savedPackage));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }

    @PutMapping("/subscription-packages/{id}")
    public ResponseEntity<Response> updatePackage(@PathVariable int id, @Valid @RequestBody SubscriptionPackageBean packageBean, Errors errors) {
        try {
            if (errors.hasErrors()) {
                Map<String, String> errorMap = new HashMap<>();
                errors.getFieldErrors().forEach(fieldError -> {
                    errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
                });
                return ResponseEntity.badRequest().body(new Response(1, "Validation failed", errorMap));
            }

            SubscriptionPackageEntity existPackage = subscriptionPackageService.existPackageUpdate(id, packageBean);
            if (existPackage != null) {
                return ResponseEntity.badRequest().body(new Response(1, "Đã tồn tại một gói đăng ký giống vậy!", null));
            }

            SubscriptionPackageEntity updatedPackage = subscriptionPackageService.updatePackage(id, packageBean);
            if (updatedPackage == null) {
                return ResponseEntity.badRequest().body(new Response(1, "Không tìm thấy gói đăng ký", null));
            }

            return ResponseEntity.ok(new Response(0, "Success!", updatedPackage));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }
}
