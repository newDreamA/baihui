package com.baihui.service.impl;

import com.baihui.common.ServerResponse;
import com.baihui.dao.CategoryMapper;
import com.baihui.pojo.Category;
import com.baihui.pojo.User;
import com.baihui.service.ICategoryService;
import com.baihui.vo.CategoryVo;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CategoryServiceImpl implements ICategoryService {

    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    CategoryMapper categoryMapper;

    @Override
    public List<CategoryVo> queryAllClassify() {
        List<CategoryVo> resultList = new ArrayList<CategoryVo>();
        for(CategoryVo vo : this.queryAllClassifyMap().values()){
            resultList.add(vo);
        }
        return resultList;
    }


    /**
     * 获取所有分类
     */
    public Map<Integer,CategoryVo> queryAllClassifyMap(){
        Map<Integer,CategoryVo> resultMap = new LinkedHashMap<Integer,CategoryVo>();
        Iterator<Category> it = categoryMapper.queryAll().iterator();
        while(it.hasNext()){
            Category c = it.next();
            if(0==c.getParentId()){//一级分类
                CategoryVo vo = new CategoryVo();
                BeanUtils.copyProperties(c, vo);
                resultMap.put(vo.getId(), vo);
            }else{
                if(null != resultMap.get(c.getParentId())){
                    resultMap.get(c.getParentId()).getSubClassifyList().add(c);//添加到子分类中
                }
            }
        }
        return resultMap;
    }




    public ServerResponse addCategory(String categoryName, Integer parentId, User user){
        if(parentId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("添加品类参数错误");
        }

        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);//这个分类是可用的
        category.setCreateUser(user.getUsername());
        category.setUpdateUser(user.getUsername());

        int rowCount = categoryMapper.insert(category);
        if(rowCount > 0){
            return ServerResponse.createBySuccess("添加品类成功");
        }
        return ServerResponse.createByErrorMessage("添加品类失败");
    }

    public ServerResponse updateCategoryName(Integer categoryId,String categoryName){
        if(categoryId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("更新品类参数错误");
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if(rowCount > 0){
            return ServerResponse.createBySuccess("更新品类名字成功");
        }
        return ServerResponse.createByErrorMessage("更新品类名字失败");
    }

    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId){
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if(CollectionUtils.isEmpty(categoryList)){
            logger.info("未找到当前分类的子分类");
        }
        return ServerResponse.createBySuccess(categoryList);
    }


    /**
     * 递归查询本节点的id及孩子节点的id
     * @param categoryId
     * @return
     */
    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId){
        Set<Category> categorySet = Sets.newHashSet();
        findChildCategory(categorySet,categoryId);


        List<Integer> categoryIdList = Lists.newArrayList();
        if(categoryId != null){
            for(Category categoryItem : categorySet){
                categoryIdList.add(categoryItem.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }


    //递归算法,算出子节点
    private Set<Category> findChildCategory(Set<Category> categorySet ,Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category != null){
            categorySet.add(category);
        }
        //查找子节点,递归算法一定要有一个退出的条件
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        for(Category categoryItem : categoryList){
            findChildCategory(categorySet,categoryItem.getId());
        }
        return categorySet;
    }


}
