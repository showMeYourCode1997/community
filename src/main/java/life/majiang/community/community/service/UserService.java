package life.majiang.community.community.service;

import life.majiang.community.community.mapper.UserMapper;
import life.majiang.community.community.model.User;
import life.majiang.community.community.model.UserExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

    public void creatOrUpdate(User user){
        UserExample userExample = new UserExample();
        //通过AccountId()在数据库中找user信息
        userExample.createCriteria().andAccountIdEqualTo(user.getAccountId());
        //通过这个方法会从数据库返回一个user列表
        List<User> userList = userMapper.selectByExample(userExample);
        //返回列表为空，说明不存在这个user信息，存入这个数据
        if(userList.size() == 0){
            user.setGmtCreat(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreat());
            userMapper.insert(user);
        }
        //不为空，dbUser就是数据库中返回的user，user就是前端传来需要更新的user
        else{
            User dbUser = userList.get(0);
            //这部分是将user的更新信息封装到一个新的updateUser里面
            User updateUser = new User();
            updateUser.setGmtModified(System.currentTimeMillis());
            updateUser.setToken(user.getToken());
            updateUser.setName(user.getName());
            updateUser.setAvatarUrl(user.getAvatarUrl());
            //在数据库中通过dbUser的id，返回一个userExample1
            UserExample userExample1 = new UserExample();
            userExample1.createCriteria().andAccountIdEqualTo(dbUser.getAccountId());
            //将更新过后的updateUser更新至数据库中
            userMapper.updateByExampleSelective(updateUser,userExample1);
        }
    }
}
