package life.majiang.community.community.controller;

import life.majiang.community.community.dto.PaginationDTO;
import life.majiang.community.community.model.User;
import life.majiang.community.community.service.NotificationService;
import life.majiang.community.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ProfileController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/profile/{section}")
    public String profile(@PathVariable(name = "section")String section,
                          @RequestParam(name="page",defaultValue = "1") Integer page,
                          @RequestParam(name="size",defaultValue = "5") Integer size,
                          HttpServletRequest request,
                          Model model){
        //未登录状态进入这个页面直接跳回主页面
        User user = (User)request.getSession().getAttribute("user");
        if(user==null) return "redirect:/";

        if("question".equals(section)){
            model.addAttribute("section","question");
            model.addAttribute("sectionName","我的提问");
            PaginationDTO pagination = questionService.list(user.getId(),page,size);
            model.addAttribute("pagination",pagination);
        }
        else if("replies".equals(section)){
            model.addAttribute("section","replies");
            model.addAttribute("sectionName","最新回复");
            PaginationDTO pagination = notificationService.list(user.getId(),page,size);
            model.addAttribute("pagination",pagination);
        }


        return "profile";
    }
}
