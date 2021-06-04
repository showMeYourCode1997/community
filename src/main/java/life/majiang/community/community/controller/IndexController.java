package life.majiang.community.community.controller;

import life.majiang.community.community.mapper.UserMapper;
import life.majiang.community.community.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexController {

    @Autowired
    private UserMapper userMapper;

    //访问主页面时，会检测cookie
    @GetMapping("/")
    public String index(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie:cookies){
            //我们自己定义的cookie的name就是token
            if(cookie.getName().equals("token")){
                //获取对应的token值
                String token = cookie.getValue();
                //将这个token值拿去数据库里查找，并得到相应的User信息
                User user = userMapper.findByToken(token);
                if(user!=null){
                    //找到User信息后传给前端的request
                    request.getSession().setAttribute("user",user);
                }
                break;
            }
        }
        return "index";
    }
}
