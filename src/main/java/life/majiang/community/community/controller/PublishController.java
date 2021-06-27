package life.majiang.community.community.controller;



import life.majiang.community.community.cache.TagCache;
import life.majiang.community.community.dto.QuestionDto;
import life.majiang.community.community.mapper.QuestionMapper;
import life.majiang.community.community.mapper.UserMapper;
import life.majiang.community.community.model.Question;
import life.majiang.community.community.model.User;
import life.majiang.community.community.service.QuestionService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


//annotation的方式，使用注解，不需要使用xml
@Controller
public class PublishController {

    @Autowired
    private QuestionService questionService;

    //点击编辑按钮，首先根绝这个id从数据库里找到对应的question，把question返回前端
    //这时候页面是在publish页面，修改过question之后，再点击发布，就会跳转到正常的发布方法
    @GetMapping("/publish/{id}")
    public String edit(@PathVariable(name = "id") Long id,
                       Model model){
        //这里用questionService.getById(id)返回的是QuestionDto
        //直接用QuestionDto就可以了，因为QuestionDto比Question多了一个user信息，但是这不重要
        //因为这里只需要通过得到信息传给前端就行了，无所谓是从QuestionDto获得的还是从Question获得的
        QuestionDto question = questionService.getById(id);
        model.addAttribute("title",question.getTitle());
        model.addAttribute("description",question.getDescription());
        model.addAttribute("tag",question.getTag());
        model.addAttribute("id",id);
        model.addAttribute("tags", TagCache.get());
        return "publish";
    }

    @GetMapping("/publish")
    public String publish(Model model){
        model.addAttribute("tags", TagCache.get());
        return "publish";
    }

    @PostMapping("/publish")
    public String doPublish(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description",required = false) String description,
            @RequestParam(value = "tag",required = false) String tag,
            //不加required=false，则说明这个变量是必须的，不能为空
            @RequestParam(value = "id",required = false) Long id,
            HttpServletRequest request,
            Model model){
        model.addAttribute("title",title);
        model.addAttribute("description",description);
        model.addAttribute("tag",tag);
        model.addAttribute("tags", TagCache.get());
        if(title ==null || title==""){
            model.addAttribute("error","标题不能为空");
            return "publish";
        }
        if(description ==null || description==""){
            model.addAttribute("error","问题描述不能为空");
            return "publish";
        }
        if(tag ==null || tag==""){
            model.addAttribute("error","标签不能为空");
            return "publish";
        }
        String invalid = TagCache.filterInValid(tag);
        if(StringUtils.isNotBlank(invalid)){
            model.addAttribute("error","标签不符合规定"+" "+"\""+invalid+"\"");
            return "publish";
        }

        User user = (User)request.getSession().getAttribute("user");
        if(user ==null){
                model.addAttribute("error","用户未登录");
                return "publish";
        }

        Question question = new Question();
        question.setDescription(description);
        question.setTitle(title);
        question.setTag(tag);
        question.setCreator(user.getId());
        //通过id判断这个问题是否已经存在了，所以需要把setId
        question.setId(id);

        //因为发布的问题可能是已经存在的或者不存在的
        questionService.creatOrUpdate(question);
        return "redirect:/";
    }
}
