package org.example.backend_fivegivechill.services;

import org.example.backend_fivegivechill.beans.ReportBean;
import org.example.backend_fivegivechill.entity.ReportEntity;
import org.example.backend_fivegivechill.entity.SongEntity;
import org.example.backend_fivegivechill.entity.UserEntity;
import org.example.backend_fivegivechill.repository.ReportRepository;
import org.example.backend_fivegivechill.repository.SongRepository;
import org.example.backend_fivegivechill.repository.UserRepository;
import org.example.backend_fivegivechill.response.ReportMapRespone;
import org.example.backend_fivegivechill.response.ReportResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    // Lấy tất cả báo cáo
    public List<ReportEntity> getAllReports() {
        return reportRepository.findAll();
    }

    // Lấy báo cáo theo ID
    public ReportEntity getReportById(int id) {
        return reportRepository.findById(id).orElse(null);
    }

    // Thêm báo cáo từ client
    public ReportEntity addReport(ReportBean reportBean) {
        ReportEntity report = new ReportEntity();
        report.setCreateDate(new Date());
        report.setContent(reportBean.getContent());

        // Gán user
        UserEntity user = userRepository.findById(reportBean.getUserId()).orElse(null);
        if (user == null) return null;
        report.setUser(user);

        // Gán bài hát
        SongEntity song = songRepository.findById(reportBean.getSongId()).orElse(null);
        if (song == null) return null;

        //Kiểm ra xem người báo cáo có phải là người đăng bài hát không
        if (song.getUser().getId() == reportBean.getUserId()) {
            throw new RuntimeException("Bạn không thể báo cáo bài hát của chính mình");
        }
        report.setSong(song);

        return reportRepository.save(report);
    }

    // Lấy danh sách báo cáo cho admin
    public List<ReportResponse> getAllReportsForAdmin() {
       List<ReportEntity> report = reportRepository.findAll();

        if (report.size() < 0) return Collections.emptyList();

        List<ReportResponse> result = new ArrayList<>();

        for (ReportEntity reportEntity : report) {
            SongEntity song = songRepository.findByIdAndStatus3(reportEntity.getSong().getId());
            if (song == null) continue;

            ReportResponse response = new ReportResponse();
            response.setId(reportEntity.getId());
            response.setSongId(song.getId());
            response.setSongName(song.getName());
            response.setSongAvatar(song.getAvatar());
            response.setSongVip(song.isVipSong());
            response.setStatus(song.getStatus());
            response.setContentReport(reportEntity.getContent());

            result.add(response);
        }

        return result;
    }


    // Ẩn bài hát bị báo cáo
    public boolean hideSongOfReport(int reportId) {
        ReportEntity report = reportRepository.findById(reportId).orElse(null);
        if (report == null || report.getSong() == null) return false;

        SongEntity song = report.getSong();
        song.setStatus(0); // Đặt trạng thái là ẩn
        songRepository.save(song);
        return true;
    }

    // Xoá báo cáo không hợp lệ
    public boolean deleteReport(int reportId) {
        if (reportRepository.existsById(reportId)) {
            reportRepository.deleteById(reportId);
            return true;
        }
        return false;
    }

    // Xoá bài hát bị vi phạm và gửi mail nếu creator có role = 2
    public boolean deleteSongOfReport(int idReport) {
        reportRepository.deleteBySongId(idReport);
        return true;
    }

    // Ánh xạ từ ReportEntity → ReportResponse
    public ReportMapRespone mapToResponse(ReportEntity report) {
        ReportMapRespone response = new ReportMapRespone();
        response.setReportId(report.getId());
        response.setContent(report.getContent());
        response.setCreateDate(report.getCreateDate());

        if (report.getUser() != null) {
            response.setUserId(report.getUser().getId());
            response.setUserName(report.getUser().getFullName());
        }

        if (report.getSong() != null) {
            response.setSongId(report.getSong().getId());
            response.setSongName(report.getSong().getName());
        }

        return response;
    }


    // Gửi email khi bài hát bị xoá
    private void sendEmailToCreatorWhenDeleted(String toEmail, String songName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Bài hát bị xoá do vi phạm");
            message.setText("Bài hát \"" + songName + "\" của bạn đã bị xoá do vi phạm chính sách. Vui lòng kiểm tra và đảm bảo tuân thủ các quy định.");
            mailSender.send(message);
        } catch (Exception e) {
            System.out.println("Gửi email thất bại: " + e.getMessage());
        }
    }
}
