package org.csu.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.csu.api.common.CONSTANT;
import org.csu.api.common.CommonResponse;
import org.csu.api.domain.Category;
import org.csu.api.persistence.CategoryMapper;
import org.csu.api.service.CategoryService;
import org.csu.api.util.ListBeanUtils;
import org.csu.api.vo.CategoryVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service("categoryService")
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public CommonResponse<CategoryVO> getCategory(Integer categoryId) {
        if(categoryId == null){
            return CommonResponse.createForError("查询分类信息时，ID不能为空");
        }
        if(categoryId == CONSTANT.CATEGORY_ROOT){
            return CommonResponse.createForError("根分类无分类信息");
        }
        Category category = categoryMapper.selectById(categoryId);
        if(category == null){
            return CommonResponse.createForError("无该分类信息");
        }
        CategoryVO categoryVO = new CategoryVO();
        BeanUtils.copyProperties(category,categoryVO);
        return CommonResponse.createForSuccess(categoryVO);
    }

    @Override
    public CommonResponse<List<CategoryVO>> getChildrenCategories(Integer categoryId) {
        if(categoryId == null){
            return CommonResponse.createForError("查询分类信息时，ID不能为空");
        }
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", categoryId);
        List<Category> categoryList = categoryMapper.selectList(queryWrapper);


        if(CollectionUtils.isEmpty(categoryList)){
            log.info("非递归查询分类信息的一级子分类时，没有查询到对应子分类信息");
        }
        List<CategoryVO> categoryVOList = ListBeanUtils.copyListProperties(categoryList, CategoryVO::new);
        return CommonResponse.createForSuccess(categoryVOList);
    }

    @Override
    public CommonResponse<List<Integer>> getCategoryAndAllChildren(Integer categoryId) {
        Set<Category> categorySet = Sets.newHashSet();
        List<Integer> categoryIdList = Lists.newArrayList();

        if(categoryId == null){
            return CommonResponse.createForSuccess(categoryIdList);
        }

        findChildCategory(categoryId, categorySet);

        for(Category category : categorySet){
            categoryIdList.add(category.getId());
        }

        return CommonResponse.createForSuccess(categoryIdList);
    }

    private Set<Category> findChildCategory(Integer categoryId, Set<Category> categorySet){
        Category category = categoryMapper.selectById(categoryId);
        if(category != null){
            categorySet.add(category);
        }
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", categoryId);
        List<Category> categoryList = categoryMapper.selectList(queryWrapper);
        for(Category categoryItem : categoryList){
            findChildCategory(categoryItem.getId(),categorySet);
        }
        return categorySet;
    }
}
