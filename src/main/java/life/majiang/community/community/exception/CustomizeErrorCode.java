package life.majiang.community.community.exception;

public enum CustomizeErrorCode implements ICustomizeErrorCode{
    QUESTION_NOT_FOUND(2001,"你找的问题不存在！！！"),
    TARGET_PARAM_NOT_FOUND(2002,"未选中任何评论或问题进行回复！！！"),
    NO_LOGIN(2003,"请先登录"),
    SYS_ERROR(2004,"服务器冒烟了。。。"),
    TYPE_PARA_WARNING(2005,"评论类型不存在"),
    COMMENT_NOT_FOUND(2006,"回复的评论不在了"),
    CONTENT_IS_EMPTY(2007,"输入内容不能为空"),
    READ_NOTIFICATION_FAILED(2008,"用户不匹配，请重新登录"),
    NOTIFICATION_NOT_FOUND(2008,"这个通知过期了或被删除了"),
    ;

    @Override
    public String getMessage(){
        return message;
    }
    public Integer getCode(){ return code;}
    private String message;
    private Integer code;

    CustomizeErrorCode(Integer code, String message) {
        this.message = message;
        this.code = code;
    }
}
