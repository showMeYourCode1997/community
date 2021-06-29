package life.majiang.community.community.controller;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import life.majiang.community.community.dto.FileDTO;
import life.majiang.community.community.provider.UCloudProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
/*
* File类（文件类）是java.io包中的类，是以抽象的方式代表文件名和目录路径名。
* 该类主要用于文件和目录的创建、文件的查找和文件的删除等。
* MultipartFile是多部份请求中收到的上传文件的表示形式
* java打开一个文件，需要新建一个Runtime对象，使用Runtime对象的exec方法
* 如：process = runtime.exec("C:\\Program Files\\Notepad++\\notepad++.exe");
* InputStream的作用是用来表示那些从不同数据源产生输入的类
* OutputStream该类别的类决定了输出所要去往的目标
* */

@Controller
@Slf4j
public class FileController {
    @Autowired
    private UCloudProvider uCloudProvider;

    @ResponseBody
    @RequestMapping("/file/upload")
    public FileDTO upload(HttpServletRequest request) throws IOException {
        //把request转换成multipartRequest
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        //通过前端id拿到multipartFile的文件
        MultipartFile file = multipartRequest.getFile("editormd-image-file");

        int length = file.getInputStream().read();
        //拿到multipartFile的类型，文件名，输入流
        try {
            String fileName = uCloudProvider.upload(file.getInputStream(), file.getContentType(), file.getOriginalFilename());
            FileDTO fileDTO = new FileDTO();
            fileDTO.setSuccess(1);
            fileDTO.setUrl(fileName);
            return fileDTO;
        } catch (IOException e){
            log.error("upload error", e);
            FileDTO fileDTO = new FileDTO();
            fileDTO.setSuccess(0);
            fileDTO.setMessage("上传失败");
            return fileDTO;
        }

    }
}
