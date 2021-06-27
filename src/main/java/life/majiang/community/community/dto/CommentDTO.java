package life.majiang.community.community.dto;

import life.majiang.community.community.model.User;
import lombok.Data;

@Data
public class CommentDTO {
    private Long id;

    private Long parentId;

    private Integer type;

    private Long gmtCreator;

    private Long gmtModified;

    private Long commentator;

    private Long likeCount;

    private String content;

    private User user;

    private Integer commentCount;

}
