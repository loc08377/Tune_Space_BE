package org.example.backend_fivegivechill.controllers;


import lombok.RequiredArgsConstructor;
import org.example.backend_fivegivechill.Ai.FptEkycClient;
import org.example.backend_fivegivechill.response.IdCardResponse;
import org.example.backend_fivegivechill.response.Response;
import org.example.backend_fivegivechill.services.IdCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/idcard")
@RequiredArgsConstructor
public class IdCardController {

    @Autowired
    private IdCardService idCardService;

    @Autowired
    private FptEkycClient fptEkycClient;

    /* -------------------------------------------------------------
     1. Người dùng đăng ký xác thực CCCD bằng ảnh + video
     ------------------------------------------------------------- */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public IdCardResponse submit(
            @RequestParam int userId,
            @RequestPart("idImage") MultipartFile idImage,
            @RequestPart("video")   MultipartFile video
    ) throws IOException {
        return idCardService.submit(userId, idImage, video);
    }

    /* -------------------------------------------------------------
     2. Lấy danh sách yêu cầu đang chờ duyệt
     ------------------------------------------------------------- */
    @GetMapping("/pending")
    public List<IdCardResponse> getPendingList() {
        return idCardService.listPending();
    }

    /* -------------------------------------------------------------
     3. Admin duyệt hoặc từ chối yêu cầu CCCD
     ------------------------------------------------------------- */
    @PatchMapping("/{id}/approve")
    public void approveRequest(
            @PathVariable("id") Integer id,
            @RequestParam("chapNhan") boolean chapNhan
    ) {
        idCardService.approve(id, chapNhan);
    }

    /* -------------------------------------------------------------
     4. OCR – FE upload ảnh CCCD, back-end trích số CCCD
     ------------------------------------------------------------- */
    @PostMapping("/ocr-cccd")
    public Map<String, String> ocrCccd(@RequestPart("idImage") MultipartFile file)
            throws IOException {
        String cccd = fptEkycClient.trichXuatCccd(file);
        return Map.of("cccd", cccd);
    }

    @GetMapping
    public ResponseEntity<Response> getIdCards(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "PENDING") String status
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size);

            // Gọi service lấy danh sách phân trang + lọc trạng thái + search
            Page<IdCardResponse> idCardPage = idCardService.getAllByStatusAndSearch(status, search, pageable);

            Response response = new Response(0, "Success!", idCardPage.getContent());
            response.setTotalPages(idCardPage.getTotalPages());
            response.setTotalElements(idCardPage.getTotalElements());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }

}
