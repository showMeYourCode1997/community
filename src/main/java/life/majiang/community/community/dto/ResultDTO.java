package life.majiang.community.community.dto;

import life.majiang.community.community.exception.CustomizeErrorCode;
import life.majiang.community.community.exception.CustomizeException;
import lombok.Data;

@Data
public class ResultDTO<T> {
    private Integer code;
    private String message;
    private T data;
    public static ResultDTO errorOf(CustomizeErrorCode customizeErrorCode){
            ResultDTO resultDTO = new ResultDTO();
            resultDTO.setCode(customizeErrorCode.getCode());
            resultDTO.setMessage(customizeErrorCode.getMessage());
            return resultDTO;
    }
    public static ResultDTO okOf(){
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setCode(200);
        resultDTO.setMessage("请求成功");
        return resultDTO;
    }
    public static <T> ResultDTO okOf(T t){
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setCode(200);
        resultDTO.setMessage("请求成功");
        resultDTO.setData(t);
        return resultDTO;
    }
    public static ResultDTO errorOf(CustomizeException e){
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setCode(e.getCode());
        resultDTO.setMessage(e.getMessage());
        return resultDTO;
    }
}
