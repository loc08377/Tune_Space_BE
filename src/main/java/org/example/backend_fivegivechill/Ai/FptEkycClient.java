package org.example.backend_fivegivechill.Ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import org.example.backend_fivegivechill.Config.MultipartInputStreamFileResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FptEkycClient {

    @Value("${fpt.api-key}")
    private String apiKey;

    @Autowired
    private RestTemplate rest;
    @Value("${fpt.liveness-url}")
    private String livenessUrl;

    @Value("${fpt.ocr-id-url}")
    private URI ocrIdUrl;

    @Autowired
    private  ObjectMapper mapper;

    public float verifyVideoWithCccd(MultipartFile video,
                                     MultipartFile idImg) throws IOException {

        /* 1️ chuẩn bị multipart */
        HttpHeaders h = new HttpHeaders();
        h.set("api_key", apiKey);
        h.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String,Object> body = new LinkedMultiValueMap<>();
        body.add("video", new MultipartInputStreamFileResource(
                video.getInputStream(), Objects.requireNonNull(video.getOriginalFilename())));
        body.add("cmnd", new MultipartInputStreamFileResource(
                idImg.getInputStream(), Objects.requireNonNull(idImg.getOriginalFilename())));

        /* 2️ gọi API */
        String json = rest.postForObject(
                livenessUrl, new HttpEntity<>(body, h), String.class);

        JsonNode root = mapper.readTree(json);
        JsonNode liveness = root.path("liveness");
        JsonNode faceMatch = root.path("face_match");

        /* 3️ kiểm tra liveness */
        if (!"200".equals(liveness.path("code").asText())
                || !"true".equalsIgnoreCase(liveness.path("is_live").asText())) {
            throw new IllegalStateException("Liveness FAIL video không hợp lệ (is_live=false)");
        }

        /* 4️ kiểm tra so khớp khuôn mặt */
        if (!"200".equals(faceMatch.path("code").asText())
                || !"true".equalsIgnoreCase(faceMatch.path("isMatch").asText())) {
            throw new IllegalStateException("Khuôn mặt KHÔNG khớp với ảnh CCCD");
        }

        float similarity = (float) faceMatch.path("similarity").asDouble();
        return similarity;
    }

    public String trichXuatCccd(MultipartFile idImg) throws IOException {

        /* 1Chuẩn bị request */
        HttpHeaders headers = new HttpHeaders();
        headers.set("api_key", apiKey);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String,Object> body = new LinkedMultiValueMap<>();
        body.add("image", new MultipartInputStreamFileResource(
                idImg.getInputStream(), Objects.requireNonNull(idImg.getOriginalFilename())));

        ResponseEntity<String> resp = rest.postForEntity(
                ocrIdUrl, new HttpEntity<>(body, headers), String.class);

        if (!resp.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("OCR CMND/CCCD HTTP " + resp.getStatusCode());
        }

        /*   Phân tích JSON trả về */
        JsonNode root = mapper.readTree(resp.getBody());

        // Kiểm tra errorCode
        if (root.path("errorCode").asInt() != 0) {
            throw new IllegalStateException("FPT OCR lỗi: "
                    + root.path("errorMessage").asText());
        }

        JsonNode dataArr = root.path("data");
        if (!dataArr.isArray() || dataArr.isEmpty()) {
            throw new IllegalStateException("FPT OCR không trả về dữ liệu.");
        }

        JsonNode first = dataArr.get(0);


        String cccd = first.path("id").asText(null);
        if (cccd == null || cccd.isBlank())
            cccd = first.path("idCard").path("id").asText(null);
        if (cccd == null || cccd.isBlank())
            cccd = first.path("so_cmnd").asText(null);

        if (cccd == null || cccd.isBlank()) {
            throw new IllegalStateException("Không trích xuất được số CCCD từ ảnh!");
        }

        return cccd;
    }

}
