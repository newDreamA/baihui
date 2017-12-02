package com.baihui.service;


import com.baihui.common.ServerResponse;
import com.baihui.pojo.Category;
import com.baihui.pojo.User;
import com.baihui.vo.CategoryVo;

import java.util.List;

public interface ICategoryService {

    List<CategoryVo> queryAllClassify();

    ServerResponse addCategory(String categoryName, Integer parentId,User user);
    ServerResponse updateCategoryName(Integer categoryId,String categoryName);
    ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);
    ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);

}
