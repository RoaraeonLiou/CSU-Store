package org.csu.api.util;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.function.Supplier;

public class ListBeanUtilsForPage extends BeanUtils {

    public static <S, T> Page<T> copyPageList(Page<S> sourcePage, Supplier<T> target){
        return copyPageList(sourcePage, target, null);
    }

    public static <S, T> Page<T> copyPageList (Page<S> sourcePage, Supplier<T> target, ListBeanUtilsCallBack<S, T> callBack ){
        Page<T> page = new Page<>();
        page.setTotal(sourcePage.getTotal());
        page.setSize(sourcePage.getSize());
        page.setCurrent(sourcePage.getCurrent());
        List<S> sourceList = sourcePage.getRecords();
        List<T> targetList = ListBeanUtils.copyListProperties(sourceList, target, callBack);

        page.setRecords(targetList);
        return page;
    }
}
