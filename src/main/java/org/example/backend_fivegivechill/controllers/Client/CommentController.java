package org.example.backend_fivegivechill.controllers.Client;

import org.example.backend_fivegivechill.beans.CommentBean;
import org.example.backend_fivegivechill.entity.CommentEntity;
import org.example.backend_fivegivechill.response.CommentResponse;
import org.example.backend_fivegivechill.response.Response;
import org.example.backend_fivegivechill.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comment")
@CrossOrigin(origins = "*")
public class CommentController {

    @Autowired
    private CommentService commentService;

    //  Lấy danh sách bình luận của bài hát (dạng cây)
    @GetMapping("/song/{songId}")
    public ResponseEntity<Response> getCommentsBySong(@PathVariable Long songId) {
        try{
            List<CommentResponse> comments = commentService.getCommentsBySong(songId);
            return ResponseEntity.ok(new Response(0, "Success!", comments));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new Response(0, "Lỗi khi lấy danh sách bình luận!", null));
        }
    }

    //  Thêm bình luận mới (gốc hoặc trả lời)
    @PostMapping("/add")
    public ResponseEntity<Response> addComment(@RequestBody CommentBean commentBean) {
        CommentEntity comment = commentService.addComment(commentBean);
        CommentResponse commentResponse = new CommentResponse(comment);
        return ResponseEntity.ok(new Response(0, "Success!", commentResponse));
    }

    //  Sửa nội dung bình luận
    @PutMapping("/update/{id}")
    public ResponseEntity<Response> updateComment(
            @PathVariable("id") Long commentId,
            @RequestBody CommentBean commentBean) {
        CommentEntity updatedComment = commentService.updateComment(commentId, commentBean.getContent());
        if (updatedComment == null) {
            return ResponseEntity.badRequest().body(new Response(1, "Lỗi khi cập nhật bình luận!", null));
        }
        CommentResponse commentResponse = new CommentResponse(updatedComment);
        return ResponseEntity.ok(new Response(0, "Success!", commentResponse));
    }

    //  Xoá bình luận
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response> deleteComment(@PathVariable("id") Long commentId) {
        try {
            commentService.deleteComment(commentId);
            return ResponseEntity.ok(new Response(0, "Success!", null));
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new Response(1, "Lỗi khi xóa bài hát!", null));
        }
    }
}

