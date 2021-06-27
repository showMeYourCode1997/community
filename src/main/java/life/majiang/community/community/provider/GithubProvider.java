package life.majiang.community.community.provider;

import com.alibaba.fastjson.JSON;
import life.majiang.community.community.dto.AccessTokenDTO;
import life.majiang.community.community.dto.GithubUser;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GithubProvider {
    //拿到token
    public String getAccessToken(AccessTokenDTO accessTokenDTO){
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");//定义媒体类型信息
        OkHttpClient client = new OkHttpClient();//创建client对象
        RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(accessTokenDTO));
        Request request = new Request.Builder()//构建request
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();
        //把这个request执行newCall方法会拿到一个response
        try (Response response = client.newCall(request).execute()) {//执行同步方法，会阻塞（异步方法会放入队列中），拿到response
            String string = response.body().string();
            String[] split = string.split("&");
            String tokenString = split[0];
            String token = tokenString.split("=")[1];
            return token;
        } catch (IOException e) {
        }
        return null;
    }
    //通过token拿到github里的user信息
    public GithubUser getGithubUser(String accessToken){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                    .url("https://api.github.com/user")
                    .header("Authorization","token "+accessToken)
                    .build();
        try (Response response = client.newCall(request).execute()) {
            String string = response.body().string();
            GithubUser githubUser = JSON.parseObject(string,GithubUser.class);
            return githubUser;
        } catch (IOException e) {
        }
        return null;
    }
}
