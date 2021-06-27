package life.majiang.community.community.dto;

import life.majiang.community.community.model.User;
import lombok.Data;

@Data
public class QuestionDto {
    private User user;

    private Long id;
    private String title;
    private String description;
    private String tag;
    private long gmtCreate;
    private long gmtModified;
    private Long Creator;
    private Integer viewCount;
    private Integer commentCount;
    private Integer likeCount;

}
