package life.majiang.community.community.cache;

import life.majiang.community.community.dto.TagDTO;
import org.apache.commons.lang.StringUtils;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TagCache {
    public static List<TagDTO> get(){
        List<TagDTO> tagDTOS = new ArrayList<>();
        //开发语言
        TagDTO program = new TagDTO();
        program.setTags(Arrays.asList("java","js","php","c++","html","css","python","node","c","golang"));
        program.setCategoryName("开发语言");
        tagDTOS.add(program);
        //开发框架
        TagDTO framework = new TagDTO();
        framework.setTags(Arrays.asList("spring","express","flask","struts","koa","tornado","laravel"));
        framework.setCategoryName("开发框架");
        tagDTOS.add(framework);
        //服务器
        TagDTO server = new TagDTO();
        server.setTags(Arrays.asList("linux","nginx","docker","tomcat","缓存","负载均衡","centos"));
        server.setCategoryName("服务器");
        tagDTOS.add(server);
        //数据库
        TagDTO db = new TagDTO();
        db.setTags(Arrays.asList("mysql","redis","h2","mongodb","sql","oracle"));
        db.setCategoryName("数据库");
        tagDTOS.add(db);
        //其他
        TagDTO others = new TagDTO();
        others.setTags(Arrays.asList("力扣","面经","生活","其他"));
        others.setCategoryName("其他");
        tagDTOS.add(others);
        return tagDTOS;
    }
    public static String filterInValid(String tags){
        //传过来的tags
        String[] split = StringUtils.split(tags,",");
        //定义的tagDTO
        List<TagDTO> tagDTOS = get();

        /*因为我们的tagDTO.getTags()，返回的是一个tags列表，如果使用map，同一个列表里的tags会被封装成一个list，
        **再加上tagDTOS也是个列表，所以会返回一个List<List<>>
        **但是flatMap并不会把每一个list的值再组装成list，他会做一个扁平化的处理
         */

        /*区别
        **map做的事情：把二箱鸡蛋分别加工成煎蛋，还是放成原来的两箱，分给2组学生；
        **flatMap做的事情：把二箱鸡蛋分别加工成煎蛋，然后放到一起【10个煎蛋】，分给10个学生；
         */

        //把传过来的tagDTO转化成tags
        List<String> tagList = tagDTOS.stream().flatMap(tagDTO->tagDTO.getTags().stream()).collect(Collectors.toList());
        //filter其实是把一个对象T变成boolea的函数
        //t是split，即传过来的tags；tagList是定义的tags.把不包含的tag组合成一个String，以"，"分割
        String invalid = Arrays.stream(split).filter(t->!tagList.contains(t)).collect(Collectors.joining(","));
        return invalid;
    }
}
