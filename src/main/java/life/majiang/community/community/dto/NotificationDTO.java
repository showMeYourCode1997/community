package life.majiang.community.community.dto;

import life.majiang.community.community.model.Comment;
import life.majiang.community.community.model.Question;
import life.majiang.community.community.model.User;
import lombok.Data;

//这个通知传递给前端显示XXX评论了您的问题/您的回复，所以只需要这些属性
@Data
public class NotificationDTO {
    private Long id;
    private Long gmtCreate;
    private Integer status;
    private User notifier;
    private String notifierName;
    private String outerTitle;
    private Long outerId;
    private String typeName;
    private Integer type;
}
