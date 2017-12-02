package com.baihui.pojo;

import java.util.Date;

public class ProductExtInfo {

    private Integer id;

    private Integer productId;



    private String subImages;

    private String detail;



    private Date createTime;

    private Date updateTime;

    public ProductExtInfo(Integer id, Integer productId,  Date createTime, Date updateTime,String subImages, String detail) {
        this.id = id;
        this.productId = productId;

        this.subImages = subImages;
        this.detail = detail;

        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public ProductExtInfo() {
        super();
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getSubImages() {
        return subImages;
    }

    public void setSubImages(String subImages) {
        this.subImages = subImages;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
