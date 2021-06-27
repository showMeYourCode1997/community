package life.majiang.community.community.dto;

import lombok.Data;

//上传文件时，markdown需要这些参数
@Data
public class FileDTO {
    private Integer success;
    private String message;
    private String url;
}
