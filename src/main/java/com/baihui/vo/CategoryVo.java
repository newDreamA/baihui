package com.baihui.vo;

import com.baihui.pojo.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryVo extends Category{

    //子分类列表
    private List<Category> subClassifyList = new ArrayList<Category>();

    public List<Category> getSubClassifyList() {
        return subClassifyList;
    }

    public void setSubClassifyList(List<Category> subClassifyList) {
        this.subClassifyList = subClassifyList;
    }
}
