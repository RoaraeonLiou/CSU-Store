package org.csu.api.controller.admin;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.csu.api.common.CommonResponse;
import org.csu.api.domain.Category;
import org.csu.api.service.CategoryService;
import org.csu.api.vo.CategoryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/category/")
@Validated
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("getCategory")
    public CommonResponse<CategoryVO> getCategoryById(
            @RequestParam Integer categoryId,
            HttpSession session){
        //todo: 管理员接口需要权限校验，下同

        return categoryService.getCategory(categoryId);
    }

    @GetMapping("get_children_categories")
    public CommonResponse<List<CategoryVO>> getChildrenCategories(
            @RequestParam(defaultValue = "0") Integer categoryId,
            HttpSession session){
        return categoryService.getChildrenCategories(categoryId);
    }

    @GetMapping("get_all_children_categories")
    public CommonResponse<List<Integer>> getAllChildrenCategories(
            @RequestParam(defaultValue = "0") Integer categoryId){
        return categoryService.getCategoryAndAllChildren(categoryId);
    }
}
