package org.csu.api.service;

import org.csu.api.common.CommonResponse;
import org.csu.api.domain.Category;
import org.csu.api.vo.CategoryVO;

import java.util.List;

public interface CategoryService {

    //获取单个分类信息的详情
    CommonResponse<CategoryVO> getCategory(Integer categoryId);

    //获取一个分类信息的一级子分类信息列表，不递归
    CommonResponse<List<CategoryVO>> getChildrenCategories(Integer categoryId);

    //获取一个分类及其所有子分类的ID，递归所有子节点
    CommonResponse<List<Integer>> getCategoryAndAllChildren(Integer categoryId);
}
