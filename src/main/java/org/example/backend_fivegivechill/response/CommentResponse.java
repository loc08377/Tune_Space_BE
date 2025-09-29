package org.example.backend_fivegivechill.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.backend_fivegivechill.entity.CommentEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    private Long id;
    private String content;
    private String userName;
    private Date createDate;
    private Long parentCommentId;
    private List<CommentResponse> replies = new ArrayList<>();
    private Long userId;

    public CommentResponse(CommentEntity commentEntity) {
        this.id = commentEntity.getId();
        this.content = commentEntity.getContent();
        this.userName = commentEntity.getUser().getFullName();
        this.createDate = commentEntity.getCreateDate();
        this.parentCommentId = commentEntity.getParentComment() != null ? commentEntity.getParentComment().getId() : null;
        this.userId = (long) commentEntity.getUser().getId();
    }



}
