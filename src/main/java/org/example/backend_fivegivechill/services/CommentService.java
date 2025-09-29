package org.example.backend_fivegivechill.services;

import org.example.backend_fivegivechill.beans.CommentBean;
import org.example.backend_fivegivechill.entity.CommentEntity;
import org.example.backend_fivegivechill.entity.SongEntity;
import org.example.backend_fivegivechill.entity.UserEntity;
import org.example.backend_fivegivechill.repository.CommentRepository;
import org.example.backend_fivegivechill.repository.SongRepository;
import org.example.backend_fivegivechill.repository.UserRepository;
import org.example.backend_fivegivechill.response.CommentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private UserRepository userRepository;

    // hiển thị cmt do chagpt làm nên tui chỉ hiểu chứ chưa biết đường giải thích
    public List<CommentResponse> getCommentsBySong(Long songId) {
        // Lấy toàn bộ comment thuộc bài hát, sắp xếp theo ngày tạo mới nhất
        List<CommentEntity> allComments = commentRepository.findBySongIdOrderByCreateDateDesc(songId);

        Map<Long, CommentResponse> map = new HashMap<>();
        // Danh sách lưu các bình luận gốc (không phải trả lời bình luận khác)
        List<CommentResponse> rootComments = new ArrayList<>();

        //chuyển từ entity sang reponse và đưa vào map theo id
        for (CommentEntity comment : allComments) {
            CommentResponse response = new CommentResponse(comment);
            map.put(comment.getId(), response); // lưu vào map theo id
        }

        // tổ chức lại bình luận dạng cây dựa theo quan hệ cha - con
        for (CommentEntity comment : allComments) {
            CommentResponse response = map.get(comment.getId()); // lấy comment hiện tại

            if (comment.getParentComment() != null) {
                // Nếu comment này là trả lời bình luận thì thêm nó vào replies của cha
                Long parentId = comment.getParentComment().getId(); // lấy id cha
                CommentResponse parentResponse = map.get(parentId); // lấy cha từ map

                if (parentResponse != null) {
                    parentResponse.getReplies().add(response); // thêm vào danh sách reply của cha
                }
            } else {
                // Nếu không có comment cha thì đây là bình luận gốc
                rootComments.add(response);
            }
        }

        // Trả về danh sách bình luận gốc, đã chứa replies bên trong
        return rootComments;
    }


    // thêm bình luận
    public CommentEntity addComment(CommentBean commentBean) {
        SongEntity song = songRepository.findById(commentBean.getSongId())
                .orElseThrow(() -> new RuntimeException("Song not found"));
        UserEntity user = userRepository.findById(commentBean.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        CommentEntity comment = new CommentEntity();
        comment.setSong(song);
        comment.setUser(user);
        comment.setContent(commentBean.getContent());
        comment.setCreateDate(new Date());

        // nếu có id cmt thì nó tìm và set vào là cmt trả lời
        if (commentBean.getParentCommentId() != null) {
            CommentEntity parent = commentRepository.findById(commentBean.getParentCommentId())
                    .orElseThrow(() -> new RuntimeException("Parent comment not found"));
            comment.setParentComment(parent);
        }

        CommentEntity savedComment = commentRepository.save(comment);
        return savedComment;
    }

    // sửa cmt
    public CommentEntity updateComment(Long commentId, String newContent) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);

        if(userOpt.get().getId() != comment.getUser().getId()) {
            return null;
        }
        comment.setContent(newContent);
        return commentRepository.save(comment);
    }

    // xóa cmt
    public void deleteComment(Long commentId) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);

        if(userOpt.get().getId() != comment.getUser().getId()) {
            return;
        }
        commentRepository.delete(comment);
//        commentRepository.deleteByIdCmt(comment.getId());
    }
}
