package com.baihui.controller.portal;

import com.baihui.common.ServerResponse;
import com.baihui.service.ICategoryService;
import com.baihui.vo.CategoryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/category/")
public class CategoryController {
    @Autowired
    private ICategoryService iCategoryService;
    @RequestMapping("queryAllClassify.do")
    @ResponseBody
    public ServerResponse<List<CategoryVo>> queryAllClassify(){
        return ServerResponse.createBySuccess(iCategoryService.queryAllClassify());
    }
}
