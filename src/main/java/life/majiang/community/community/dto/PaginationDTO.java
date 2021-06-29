package life.majiang.community.community.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PaginationDTO<T> {
    private List<T> data;
    private boolean showPrevious;
    private boolean showNext;
    private boolean showFirstPage;
    private boolean showLastPage;

    private Integer page;
    private List<Integer> pages ;
    private Integer totalPage;

    public void setPagination(Integer totalCount, Integer page, Integer size) {
        //计算总页数
        if (totalCount % size == 0) {
            totalPage = totalCount / size;
        } else {
            totalPage = totalCount / size + 1;
        }

        if(page<1) page = 1;
        else if(page>totalPage) page = totalPage;
        this.page = page;

        //通过当前页数，把左右三页都加入可以显示的页数中
        pages = new ArrayList<>();
        pages.add(page);
        for(int i=1;i<=3;i++){
            if(page-i>0) pages.add(0,page-i);
            if(page+i<=totalPage) pages.add(page+i);
        }

        if(page==1){
            showPrevious = false;
        }
        else showPrevious = true;

        if(page==totalPage){
            showNext = false;
        }
        else showNext = true;

        if(pages.contains(1)){
            showFirstPage = false;
        }
        else showFirstPage = true;

        if(pages.contains(totalPage)){
            showLastPage = false;
        }
        else showLastPage = true;
    }
}
