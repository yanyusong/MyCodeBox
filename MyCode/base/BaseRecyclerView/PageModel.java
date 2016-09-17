package net.boyazhidao.cgb.model;

/**
 * Created by mac on 16/3/3.
 */
public class PageModel extends ComRespInfo{
    public String totalPage;
    public String page;
    public Boolean has_next;
    public String list;

    public String getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(String totalPage) {
        this.totalPage = totalPage;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public Boolean getHas_next() {
        return has_next;
    }

    public void setHas_next(Boolean has_next) {
        this.has_next = has_next;
    }

    public String getList() {
        return list;
    }

    public void setList(String list) {
        this.list = list;
    }
}
