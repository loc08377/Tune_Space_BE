package org.example.backend_fivegivechill.controllers.Client;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.backend_fivegivechill.entity.SubscriptionPackageEntity;
import org.example.backend_fivegivechill.response.Response;
import org.example.backend_fivegivechill.response.SubscriptionPackageResponse;
import org.example.backend_fivegivechill.services.SubscriptionPackageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@CrossOrigin("*")
@RequestMapping("/")
public class SubscriptionPackageClientController {

    private final HttpServletRequest request;
    private final SubscriptionPackageService subscriptionPackageService;

    @GetMapping("/subscription-package")
    public ResponseEntity<Response> subscriptionPackage(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);

            Page<SubscriptionPackageEntity> packagePage;
            packagePage = subscriptionPackageService.getAllPackagesByStatusClient(true, pageable);

            List<SubscriptionPackageResponse> packageResponseList = packagePage.getContent().stream()
                    .map(pkg -> new SubscriptionPackageResponse(pkg.getId(), pkg.getName(), pkg.getDuration(), pkg.getPrice(), pkg.isStatus()))
                    .collect(Collectors.toList());

            Response response = new Response(0, "Success!", packageResponseList);
            response.setTotalPages(packagePage.getTotalPages());
            response.setTotalElements(packagePage.getTotalElements());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }

    @GetMapping("/subscription-package/detailt/{id}")
    public ResponseEntity<Response> subscriptionPackageDetail(@PathVariable int id) {
        try {
            SubscriptionPackageEntity packageEntity = subscriptionPackageService.getPackageById(id);

            if (packageEntity == null) {
                return ResponseEntity.badRequest().body(new Response(1, "Subscription Package not found", null));
            }

            SubscriptionPackageResponse packageResponse = new SubscriptionPackageResponse(
                    packageEntity.getId(), packageEntity.getName(),
                    packageEntity.getDuration(), packageEntity.getPrice(), packageEntity.isStatus());

            return ResponseEntity.ok(new Response(0, "Package found successfully", packageResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }


}
