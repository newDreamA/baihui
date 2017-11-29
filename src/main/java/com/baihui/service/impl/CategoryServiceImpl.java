package com.baihui.service.impl;

import com.baihui.dao.CategoryMapper;
import com.baihui.pojo.Category;
import com.baihui.service.ICategoryService;
import com.baihui.vo.CategoryVo;
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


}
