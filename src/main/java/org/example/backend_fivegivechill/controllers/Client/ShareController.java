package org.example.backend_fivegivechill.controllers.Client;

import org.example.backend_fivegivechill.beans.ShareBean;
import org.example.backend_fivegivechill.entity.ShareEntity;
import org.example.backend_fivegivechill.response.Response;
import org.example.backend_fivegivechill.response.ShareResponse;
import org.example.backend_fivegivechill.services.ShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/shares")
@CrossOrigin("*")
public class ShareController {

    @Autowired
    private ShareService shareService;

    // Lấy tất cả chia sẻ
    @GetMapping
    public ResponseEntity<Response> getAllShares() {
        List<ShareEntity> shares = shareService.getAllShares();
        List<ShareResponse> responses = shares.stream()
                .map(shareService::mapToResponse)
                .collect(Collectors.toList());

        Response response = new Response(0, "Lấy danh sách chia sẻ thành công", responses);
        return ResponseEntity.ok(response);
    }

    // Lấy chia sẻ theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Response> getShareById(@PathVariable int id) {
        ShareEntity share = shareService.getShareById(id);
        if (share == null) {
            return ResponseEntity.ok(new Response(1, "Không tìm thấy chia sẻ với ID: " + id, null));
        }

        ShareResponse shareResponse = shareService.mapToResponse(share);
        return ResponseEntity.ok(new Response(0, "Lấy chia sẻ thành công", shareResponse));
    }

    // Tạo chia sẻ mới
    @PostMapping
    public ResponseEntity<Response> createShare(@RequestBody ShareBean shareBean) {
        ShareEntity newShare = shareService.addShare(shareBean);
        if (newShare == null) {
            return ResponseEntity.ok(new Response(1, "Tạo chia sẻ thất bại", null));
        }

        ShareResponse shareResponse = shareService.mapToResponse(newShare);
        return ResponseEntity.ok(new Response(0, "Tạo chia sẻ thành công", shareResponse));
    }
}
//oudfy8wnruywvbry