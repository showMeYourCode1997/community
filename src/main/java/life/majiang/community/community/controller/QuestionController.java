package life.majiang.community.community.controller;

import life.majiang.community.community.dto.CommentDTO;
import life.majiang.community.community.dto.QuestionDto;
import life.majiang.community.community.enums.CommentTypeEnum;
import life.majiang.community.community.service.CommentService;
import life.majiang.community.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;

    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id") Long id,
                           Model model){
        //浏览数无法在questionService.getById(id)中去定义，因为这个方法在查询和更新都会有调用
        //而我们只需在查询时增加浏览数
        QuestionDto questionDto = questionService.getById(id);
        //相关问题
        List<QuestionDto> relatedQuestions = questionService.selectRelated(questionDto);
        //实现更新浏览数的方法
        questionService.inView(id);

        List<CommentDTO> comments = commentService.getByTargetId(id, CommentTypeEnum.QUESTION);
        model.addAttribute("question",questionDto);
        model.addAttribute("comments",comments);
        model.addAttribute("relatedQuestions",relatedQuestions);
        return "question";
    }
}
