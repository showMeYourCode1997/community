package life.majiang.community.community.service;

import life.majiang.community.community.dto.CommentDTO;
import life.majiang.community.community.enums.CommentTypeEnum;
import life.majiang.community.community.enums.NotificationStatusEnum;
import life.majiang.community.community.enums.NotificationTypeEnum;
import life.majiang.community.community.exception.CustomizeErrorCode;
import life.majiang.community.community.exception.CustomizeException;
import life.majiang.community.community.mapper.*;
import life.majiang.community.community.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentService {
    @Resource
    private CommentExtMapper commentExtMapper;
    @Resource
    private CommentMapper commentMapper;
    @Resource
    private QuestionMapper questionMapper;
    @Resource
    private QuestionExtMapper questionExtMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private NotificationMapper notificationMapper;

    //新建回复或评论，同时还要新建通知
    @Transactional
    public void insert(Comment comment,User commentator) {
        if (comment.getParentId() == null || comment.getParentId() == 0) {
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
        }
        if (comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())) {
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARA_WARNING);
        }
        if (comment.getType() == CommentTypeEnum.COMMENT.getType()){
            //找到母评论
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if(dbComment==null){
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }
            //把评论加入数据库
            commentMapper.insert(comment);

            //增加评论数
            dbComment.setCommentCount(1);
            commentExtMapper.inCommentCount(dbComment);

            Question question = questionMapper.selectByPrimaryKey(dbComment.getParentId());
            if(question==null){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }

            //新建一个回复评论的通知
            Long receiver = dbComment.getCommentator();
            creatNotification(comment,receiver,NotificationTypeEnum.REPLY_COMMENT.getType(),commentator.getName(),question);
        }else {
            //回复问题
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if(question==null){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            //把回复写进数据库
            commentMapper.insert(comment);

            //回复数+1，这里要设置CommentCount等于1，不然0+0=0
            //默认值是0，这里赋予值1，sql语句中是0+1=1
            question.setCommentCount(1);
            questionExtMapper.inCommentCount(question);

            //新建一个回复问题的通知
            Long receiver = question.getCreator();
            creatNotification(comment,receiver,NotificationTypeEnum.REPLY_QUESTION.getType(),commentator.getName(),question);
        }
    }
    public void creatNotification(Comment comment,Long receiver,int notificationType,String name,Question question){
        //不会给自己通知
        if(receiver==comment.getCommentator()) return;
        Notification notification = new Notification();
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setNotifier(comment.getCommentator());
        notification.setReceiver(receiver);
        notification.setType(notificationType);
        //这里的outerid并不是值这个评论或回复的父类id，
        //因为前端点击通知时，需要跳转到具体的question页面，所以这里的outerId一定要是questionId
        notification.setOuterId(question.getId());
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
        notification.setNotifierName(name);
        notification.setOuterTitle(question.getTitle());
        notificationMapper.insert(notification);
    }

    //点击某个问题，进入问题页面时调用的方法，功能时返回评论列表
    //返回评论的方法，如果type==question，返回是问题回复的CommentDTO,
    //type==comment,返回是评论回复的CommentDTO
    //这个太帅了！
    public List<CommentDTO> getByTargetId(Long id, CommentTypeEnum type) {
        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria()
                .andParentIdEqualTo(id)
                .andTypeEqualTo(type.getType());
        commentExample.setOrderByClause("gmt_creator desc");
        List<Comment> comments = commentMapper.selectByExample(commentExample);
        if(comments.size()==0){
            return new ArrayList<>();
        }
        //从每一个的comment里面取它的commentator时,如果多个评论的评论人都是一个人，就会有很多重复值
        //所以利用拉姆达表达式来去重
        Set<Long> commentator= comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());
        List<Long> userIds = new ArrayList<>();
        userIds.addAll(commentator);

        //获取评论人,并转化成map
        UserExample userExample = new UserExample();
        userExample.createCriteria().andIdIn(userIds);
        List<User> users = userMapper.selectByExample(userExample);
        Map<Long,User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(),user -> user));

        //把comment转化为commentDTO
        List<CommentDTO> commentDTOS = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment,commentDTO);
            commentDTO.setUser(userMap.get(commentDTO.getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());

        return commentDTOS;
    }
}
