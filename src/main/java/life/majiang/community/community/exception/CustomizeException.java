package life.majiang.community.community.exception;



//这里定义成RuntimeException，是为了不影响我们代码的正常运行
//只需要在handler里面去拦截就可以了
public class CustomizeException extends RuntimeException{
    private String message;
    private Integer code;
    public CustomizeException(ICustomizeErrorCode iCustomizeErrorCode){
        this.code = iCustomizeErrorCode.getCode();
        this.message = iCustomizeErrorCode.getMessage();
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }
}
