package org.example.backend_fivegivechill.services;


import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.backend_fivegivechill.Ai.FptEkycClient;
import org.example.backend_fivegivechill.entity.IdCardEntity;
import org.example.backend_fivegivechill.entity.UserEntity;
import org.example.backend_fivegivechill.repository.IdCardRepository;
import org.example.backend_fivegivechill.repository.UserRepository;
import org.example.backend_fivegivechill.response.IdCardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class IdCardService {

    @Autowired
    private  IdCardRepository idCardRepo;

    @Autowired
    private  UserRepository userRepo;

    @Autowired
    private FptEkycClient ekyc;

    /** Ngưỡng similarity tối thiểu để chuyển sang PENDING */
    private static final float THRESHOLD = 80f;

    /* 1 gửi yêu cầu xác thực CCCD */
    public IdCardResponse submit(
            int userId,
            MultipartFile cccdImage,
            MultipartFile videoLiveness)
            throws IOException {


        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Nếu user đã có CCCD thì không được gửi thêm
        if (user.getIdCard() != null) {
            throw new IllegalStateException("User đã có CCCD – không thể gửi lại.");
        }

        //  API OCR lấy số CCCD từ ảnh
        String numberId = ekyc.trichXuatCccd(cccdImage);

        //  API so khớp khuôn mặt
        float similarity = ekyc.verifyVideoWithCccd(videoLiveness, cccdImage);


        IdCardEntity card = new IdCardEntity();
        card.setNumberId(numberId);
        card.setSimilarity(similarity);
        card.setStatus(similarity >= THRESHOLD
                ? IdCardEntity.Status.PENDING
                : IdCardEntity.Status.REJECTED);

        card.setUser(user);
        user.setIdCard(card);

        idCardRepo.save(card);

        return map(card);
    }

    /*  danh sách CCCD đang chờ duyệt */
    public List<IdCardResponse> listPending() {
        return idCardRepo.findAllByStatus(IdCardEntity.Status.PENDING)
                .stream()
                .map(this::map)
                .toList();
    }


    public void approve(Integer cardId, boolean accept) {
        IdCardEntity card = idCardRepo.findById(cardId)
                .orElseThrow(EntityNotFoundException::new);


        card.setStatus(accept
                ? IdCardEntity.Status.APPROVED
                : IdCardEntity.Status.REJECTED);

        if (accept) {

            UserEntity user = card.getUser();
            user.setRole(UserEntity.Role.CREATOR.getValue()); // 2 = CREATOR
        }
    }


    private IdCardResponse map(IdCardEntity c) {
        return new IdCardResponse(
                c.getId(),
                c.getNumberId(),
                c.getSimilarity(),
                c.getStatus(),
                c.getCreateDate()
        );
    }

    public Page<IdCardResponse> getAllByStatusAndSearch(String status, String search, Pageable pageable) {
        // Convert String sang enum
        IdCardEntity.Status enumStatus = IdCardEntity.Status.valueOf(status.toUpperCase());

        Page<IdCardEntity> pageResult = idCardRepo.findByStatusAndNumberIdContainingIgnoreCase(enumStatus, search, pageable);
        return pageResult.map(entity -> new IdCardResponse(
                entity.getId(),
                entity.getNumberId(),
                entity.getSimilarity(),
                entity.getStatus(),
                entity.getCreateDate()
        ));
    }

}
