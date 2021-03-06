package life.majiang.community.community.controller;

import life.majiang.community.community.dto.NotificationDTO;
import life.majiang.community.community.enums.NotificationTypeEnum;
import life.majiang.community.community.model.User;
import life.majiang.community.community.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;

@Controller
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @GetMapping("/notification/{id}")
    public String profile(@PathVariable(name = "id")Long id,
                          HttpServletRequest request){
        //未登录状态进入这个页面直接跳回主页面
        User user = (User)request.getSession().getAttribute("user");
        if(user==null) return "redirect:/";

        NotificationDTO notificationDTO = notificationService.read(id,user);

        if(NotificationTypeEnum.REPLY_COMMENT.getType()==notificationDTO.getType()
                ||NotificationTypeEnum.REPLY_QUESTION.getType()==notificationDTO.getType()){
            return "redirect:/question/"+notificationDTO.getOuterId();
        }
        else return "redirect:/";
    }
}
