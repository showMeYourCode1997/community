package life.majiang.community.community.mapper;

import life.majiang.community.community.dto.QuestionQueryDTO;
import life.majiang.community.community.model.Question;
import life.majiang.community.community.model.QuestionExample;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface QuestionExtMapper {
    int inView(Question record);
    int inCommentCount(Question record);
    List<Question> selectRelated(Question question);

    Integer countBySearch(QuestionQueryDTO questionQueryDTO);

    List<Question> selectBySearch(QuestionQueryDTO questionQueryDTO);
}
