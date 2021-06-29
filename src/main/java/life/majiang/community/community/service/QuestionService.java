package life.majiang.community.community.service;

import life.majiang.community.community.dto.QuestionQueryDTO;
import org.apache.commons.lang.StringUtils;
import life.majiang.community.community.dto.PaginationDTO;
import life.majiang.community.community.dto.QuestionDto;
import life.majiang.community.community.exception.CustomizeErrorCode;
import life.majiang.community.community.exception.CustomizeException;
import life.majiang.community.community.mapper.QuestionExtMapper;
import life.majiang.community.community.mapper.QuestionMapper;
import life.majiang.community.community.mapper.UserMapper;
import life.majiang.community.community.model.Question;
import life.majiang.community.community.model.QuestionExample;
import life.majiang.community.community.model.User;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Resource
    private QuestionExtMapper questionExtMapper;

    @Resource
    private QuestionMapper questionMapper;

    @Resource
    private UserMapper userMapper;
    //主页面返回paginationDTO
    public PaginationDTO list(String search,Integer page, Integer size) {
        if(StringUtils.isNotBlank(search)){
            String[] tags = StringUtils.split(search," ");
            search = Arrays.stream(tags).collect(Collectors.joining("|"));
        }

        PaginationDTO paginationDTO = new PaginationDTO();
        //首先计算所有question的总数
        QuestionQueryDTO questionQueryDTO = new QuestionQueryDTO();
        questionQueryDTO.setSearch(search);
        Integer totalCount = questionExtMapper.countBySearch(questionQueryDTO);
        //计算总页面，定义布尔值
        paginationDTO.setPagination(totalCount,page,size);

        if(page<1) page = 1;
        if(page>paginationDTO.getTotalPage()) page = paginationDTO.getTotalPage();


        //偏移量
        Integer offset = (page-1)*size;
        //根据时间倒序拿到question列表
        QuestionExample questionExample = new QuestionExample();
        questionExample.setOrderByClause("gmt_create desc");
        questionQueryDTO.setSize(size);
        questionQueryDTO.setPage(offset);
        //mybaits的rowbounds进行分页，只需要传一个RowBounds对象，给他每一页的偏移量和每一页的页数，它会自动拼接到sql语句上
        List<Question> questions = questionExtMapper.selectBySearch(questionQueryDTO);

        //把question转变为questionDTO，并赋值给paginationDTO
        List<QuestionDto> questionDtoList = new ArrayList<>();
        for(Question question:questions){
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDto questionDto = new QuestionDto();
            BeanUtils.copyProperties(question,questionDto);
            questionDto.setUser(user);
            questionDtoList.add(questionDto);
        }
        paginationDTO.setData(questionDtoList);
        return paginationDTO;
    }

    //我的问题页面返回paginationDTO
    public PaginationDTO list(Long userId, Integer page, Integer size) {
        PaginationDTO paginationDTO = new PaginationDTO();
        //通过question.creator==User.id得到questionExample
        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria().andCreatorEqualTo(userId);
        //通过这个Example查到所有问题的数量
        Integer totalCount = (int)questionMapper.countByExample(questionExample);

        //这个方法中主要是通过当前页数，问题总数，每页显示问题数，计算出页面显示的页数、页面总数、是否显示上一页下一页等
        paginationDTO.setPagination(totalCount,page,size);

        //页数大于总页数 或 页数小于1时，强制赋予page值
        if(page<1) page = 1;
        else if(page>paginationDTO.getTotalPage()) page = paginationDTO.getTotalPage();

        //设置每一页的偏移量
        Integer offset = (page-1)*size;

        //mybatis 中，使用 RowBounds 分页，非常方便，不需要在 sql 语句中写 limit，mybatis 会自动拼接 sql ，
        //添加 limit最核心的是在 mapper 接口层，传参时传入 RowBounds(int offset, int limit) 对象，即可完成分页
        QuestionExample questionExample1 = new QuestionExample();
        questionExample1.createCriteria().andCreatorEqualTo(userId);
        questionExample1.setOrderByClause("gmt_create desc");
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(questionExample1,new RowBounds(offset,size));

        //把question转化成questionDTO
        List<QuestionDto> questionDtoList = new ArrayList<>();
        for(Question question:questions){
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDto questionDto = new QuestionDto();
            BeanUtils.copyProperties(question,questionDto);
            questionDto.setUser(user);
            questionDtoList.add(questionDto);
        }
        //再把questionDTO赋值给paginationDTO
        paginationDTO.setData(questionDtoList);
        return paginationDTO;
    }

    //点击问题详情页面时，通过id查询question
    public QuestionDto getById(Long id) {
        Question question = questionMapper.selectByPrimaryKey(id);
        if (question==null) throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);

        User user = userMapper.selectByPrimaryKey(question.getCreator());
        QuestionDto questionDto = new QuestionDto();
        BeanUtils.copyProperties(question,questionDto);
        questionDto.setUser(user);
        return questionDto;
    }

    public void creatOrUpdate(Question question) {
        if(question.getId()==null){
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(System.currentTimeMillis());
            question.setViewCount(0);
            question.setLikeCount(0);
            question.setCommentCount(0);
            questionMapper.insert(question);
        }
        else{
            question.setGmtModified(System.currentTimeMillis());
            Question updateQuestion = new Question();
            updateQuestion.setGmtModified(System.currentTimeMillis());
            updateQuestion.setTag(question.getTag());
            updateQuestion.setTitle(question.getTitle());
            updateQuestion.setDescription(question.getDescription());
            QuestionExample questionExample = new QuestionExample();
            questionExample.createCriteria().andIdEqualTo(question.getId());
            //数据库插入成功后返回1，插入失败返回0
            int updated = questionMapper.updateByExampleSelective(updateQuestion,questionExample);

            if(updated!=1){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
        }
    }

    /*
    这里不能这样写，在多线程并发的时候这个浏览数并不正确，
    如果多个线程同时访问可能获取的是同一个值

    public void inView(Integer id) {
        Question question = questionMapper.selectByPrimaryKey(id);
        question.setViewCount(question.getViewCount()+1);
        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria().andIdEqualTo(id);
        questionMapper.updateByExampleSelective(question,questionExample);
    }
    */
    //新建一个questionMapper，在里面定义我们自己需要的方法，直接使用就可以完成更新阅读数的问题了
    public void inView(Long id) {
        Question question = questionMapper.selectByPrimaryKey(id);
        question.setViewCount(1);
        questionExtMapper.inView(question);
    }
    //相关问题
    public List<QuestionDto> selectRelated(QuestionDto questionDto) {
        if(!StringUtils.isNotBlank(questionDto.getTag())){
            return new ArrayList<>();
        }

        String[] tags = StringUtils.split(questionDto.getTag(),",");
        String regexTag = Arrays.stream(tags).collect(Collectors.joining("|"));
        Question question = new Question();
        question.setId(questionDto.getId());
        question.setTag(regexTag);
        //从数据库中查出相关问题
        List<Question> questions = questionExtMapper.selectRelated(question);
        //把questions转换成questionDTO
        List<QuestionDto> questionDtos = questions.stream().map(q->{
            QuestionDto questionDto1 = new QuestionDto();
            BeanUtils.copyProperties(q,questionDto1);
            return questionDto1;
        }).collect(Collectors.toList());
        return questionDtos;
    }
}
