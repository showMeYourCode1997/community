package life.majiang.community.community.controller;

import life.majiang.community.community.dto.AccessTokenDTO;
import life.majiang.community.community.dto.GithubUser;
import life.majiang.community.community.mapper.UserMapper;
import life.majiang.community.community.model.User;
import life.majiang.community.community.provider.GithubProvider;
import life.majiang.community.community.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.filters.ExpiresFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
@Slf4j
public class AuthorizeController {

    @Autowired
    private GithubProvider githubProvider;

    @Value("${github.client.id}")
    private String clientId;
    @Value("${github.Client_secret}")
    private String clientSecret;
    @Value("${github.redirect.url}")
    private String redirectUri;


    @Autowired
    private UserService userService;

    //点击登录键会跳转到"https://github.com/login/oauth/authorize?
    //client_id=8bd1f4806d1d1c618755&redirect_uri=http://localhost:8887/callback&scope=user&state=1

    //然后重定向到http://localhost:8887/callback，这时候我们拿到code等参数封装成AccessTokenDTO
    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state,
                           HttpServletRequest request,
                           HttpServletResponse response){
        //点击登录后会跳转到github的一个url，会给我们返回一些信息
        //用AccessTokenDTO存储
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(redirectUri);
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setState(state);

        //githubProvider.getAccessToken是okhttp的post方法，将AccessTokenDTO传给github，https://github.com/login/oauth/access_token
        //github会返回一个response，将其转成string并拆分之后拿到token
        //将token通过githubProvider.getGithubUser方法（get方法），发送给github，进入https://api.github.com/user
        //github会验证token，成功会返回给我们githubUser信息
        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        GithubUser githubUser = githubProvider.getGithubUser(accessToken);

        if (githubUser!=null && githubUser.getId()!=null){
            //将githubUser信息，赋给我们model中的user
            User user = new User();
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setName(githubUser.getName());
            user.setAccountId(githubUser.getId());
            user.setAvatarUrl(githubUser.getAvatar_url());
            //把user存储到数据库中
            userService.creatOrUpdate(user);
            //给把这个token加到响应的cookie中
            response.addCookie(new Cookie("token",token));
            return "redirect:/";
        }
        else{
            log.error("callback get github error {}", githubUser);
            return "redirect:/";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request,
                         HttpServletResponse response){
        request.getSession().removeAttribute("user");
        Cookie cookie = new Cookie("token",null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return "redirect:/";
    }
}
