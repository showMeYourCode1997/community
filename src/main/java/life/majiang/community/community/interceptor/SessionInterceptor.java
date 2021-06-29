package life.majiang.community.community.interceptor;

import life.majiang.community.community.mapper.UserMapper;
import life.majiang.community.community.model.User;
import life.majiang.community.community.model.UserExample;
import life.majiang.community.community.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service
public class SessionInterceptor implements HandlerInterceptor {
    @Resource
    private NotificationService notificationService;

    @Resource
    private UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();
        if(cookies!=null && cookies.length!=0) {
            for (Cookie cookie : cookies) {
                //我们自己定义的cookie的name就是token
                if (cookie.getName().equals("token")) {
                    //获取对应的token值
                    String token = cookie.getValue();
                    //将这个token值拿去数据库里查找，并得到相应的User信息
                    UserExample userExample = new UserExample();
                    userExample.createCriteria().andTokenEqualTo(token);
                    List<User> userList = userMapper.selectByExample(userExample);
                    if (userList.size() != 0) {
                        //找到User信息后传给前端的request
                        request.getSession().setAttribute("user", userList.get(0));
                        Long unreadCount = notificationService.unreadCount(userList.get(0).getId());
                        request.getSession().setAttribute("unreadMessage",unreadCount);
                    }
                    break;
                }
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
