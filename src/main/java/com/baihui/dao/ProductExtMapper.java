package com.baihui.dao;

import com.baihui.pojo.ProductExtInfo;

public interface ProductExtMapper {
    int deleteByPrimaryKey(ProductExtInfo key);

    int insert(ProductExtInfo record);

    int insertSelective(ProductExtInfo record);

    int updateByPrimaryKeySelective(ProductExtInfo record);

    int updateByPrimaryKeyWithBLOBs(ProductExtInfo record);


    ProductExtInfo selectByProductId(Integer productId);
}