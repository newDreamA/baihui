package com.baihui.service.impl;

import com.baihui.common.Const;
import com.baihui.common.ResponseCode;
import com.baihui.common.ServerResponse;
import com.baihui.dao.CategoryMapper;
import com.baihui.dao.ProductExtMapper;
import com.baihui.dao.ProductMapper;
import com.baihui.pojo.Category;
import com.baihui.pojo.Product;
import com.baihui.pojo.ProductExtInfo;
import com.baihui.service.ICategoryService;
import com.baihui.service.IProductService;
import com.baihui.util.DateTimeUtil;
import com.baihui.util.PropertiesUtil;
import com.baihui.vo.ProductDetailVo;
import com.baihui.vo.ProductListVo;
import com.baihui.vo.ProductVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

/**
 * Created by geely
 */
@Service("iProductService")
public class ProductServiceImpl implements IProductService {


    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductExtMapper productExtMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ICategoryService iCategoryService;

    @Transactional
    public ServerResponse saveOrUpdateProduct(ProductVo product){
        if(product != null) {
            ProductExtInfo  productExtWithBLOBs = new ProductExtInfo();
            if(StringUtils.isNotBlank(product.getSubImages())){
                String[] subImageArray = product.getSubImages().split(",");
                if(subImageArray.length > 0){
                    product.setMainImage(subImageArray[0]);
                }
                productExtWithBLOBs.setSubImages(product.getSubImages());
            }

            productExtWithBLOBs.setDetail(product.getDetail());

            Product dbProduct = new Product();
            BeanUtils.copyProperties(product,dbProduct);
            if(product.getId() != null){
                productExtWithBLOBs.setProductId(product.getId());

                int rowCount = productMapper.updateByPrimaryKey(dbProduct);
                int rowDetailCount = productExtMapper.updateByPrimaryKeySelective(productExtWithBLOBs);

                if(rowCount > 0&&rowDetailCount>0){
                    return ServerResponse.createBySuccess("更新产品成功");
                }
                return ServerResponse.createBySuccess("更新产品失败");
            }else{
                int rownum = productMapper.insert(dbProduct);
                productExtWithBLOBs.setProductId(dbProduct.getId());
                int rowDetailCount = productExtMapper.insert(productExtWithBLOBs);
                if(rownum > 0 && rowDetailCount>0){
                    return ServerResponse.createBySuccess("新增产品成功");
                }
                return ServerResponse.createBySuccess("新增产品失败");
            }
        }
        return ServerResponse.createByErrorMessage("新增或更新产品参数不正确");
    }


    public ServerResponse<PageInfo> getProductList(int pageNum, int pageSize){
        //startPage--start
        //填充自己的sql查询逻辑
        //pageHelper-收尾
        PageHelper.startPage(pageNum,pageSize);
        List<Product> productList = productMapper.selectList();

        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product productItem : productList){
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    private ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.ycbaihui.cn/"));
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());
        return productListVo;
    }


        public ServerResponse<ProductDetailVo> getProductDetail(Integer productId){
        if(productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        ProductVo productVo = new ProductVo();
        Product product = productMapper.selectByPrimaryKey(productId);
        BeanUtils.copyProperties(product,productVo);

        ProductExtInfo productDetail = productExtMapper.selectByProductId(productId);
        productVo.setDetail(productDetail.getDetail());
        productVo.setSubImages(productDetail.getSubImages());
        if(product == null){
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
        if(product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()){
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(productVo);
        return ServerResponse.createBySuccess(productDetailVo);
    }


        private ProductDetailVo assembleProductDetailVo(ProductVo product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());

        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.ycbaihui.cn/"));

        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category == null){
            productDetailVo.setParentCategoryId(0);//默认根节点
        }else{
            productDetailVo.setParentCategoryId(category.getParentId());
        }

        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return productDetailVo;
    }


    public ServerResponse<String> setSaleStatus(Integer productId,Integer status){
        if(productId == null || status == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if(rowCount > 0){
            return ServerResponse.createBySuccess("修改产品销售状态成功");
        }
        return ServerResponse.createByErrorMessage("修改产品销售状态失败");
    }


    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId){
        if(productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        ProductExtInfo productExtInfo = productExtMapper.selectByProductId(productId);
        Product product = productMapper.selectByPrimaryKey(productId);
        ProductVo productVo = new ProductVo();
        BeanUtils.copyProperties(product,productVo);
        productVo.setSubImages(productExtInfo.getSubImages());
        productVo.setDetail(productExtInfo.getDetail());
        if(product == null){
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(productVo);
        return ServerResponse.createBySuccess(productDetailVo);
    }








//
//    public ServerResponse<PageInfo> searchProduct(String productName,Integer productId,int pageNum,int pageSize){
//        PageHelper.startPage(pageNum,pageSize);
//        if(StringUtils.isNotBlank(productName)){
//            productName = new StringBuilder().append("%").append(productName).append("%").toString();
//        }
//        List<Product> productList = productMapper.selectByNameAndProductId(productName,productId);
//        List<ProductListVo> productListVoList = Lists.newArrayList();
//        for(Product productItem : productList){
//            ProductListVo productListVo = assembleProductListVo(productItem);
//            productListVoList.add(productListVo);
//        }
//        PageInfo pageResult = new PageInfo(productList);
//        pageResult.setList(productListVoList);
//        return ServerResponse.createBySuccess(pageResult);
//    }





    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword,Integer categoryId,int pageNum,int pageSize,String orderBy){
        if(StringUtils.isBlank(keyword) && categoryId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        List<Integer> categoryIdList = new ArrayList<Integer>();

        if(categoryId != null){
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if(category == null && StringUtils.isBlank(keyword)){
                //没有该分类,并且还没有关键字,这个时候返回一个空的结果集,不报错
                PageHelper.startPage(pageNum,pageSize);
                List<ProductListVo> productListVoList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVoList);
                return ServerResponse.createBySuccess(pageInfo);
            }
            categoryIdList = iCategoryService.selectCategoryAndChildrenById(category.getId()).getData();
        }
        if(StringUtils.isNotBlank(keyword)){
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        }

        PageHelper.startPage(pageNum,pageSize);
        //排序处理
        if(StringUtils.isNotBlank(orderBy)){
            if(Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                String[] orderByArray = orderBy.split("_");
                PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);
            }
        }
        List<Product> productList = productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword)?null:keyword,categoryIdList.size()==0?null:categoryIdList);

        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product product : productList){
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }

        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }


























}

