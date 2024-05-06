package org.csu.api.util;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ListBeanUtils extends BeanUtils {

    public static <S, T> List<T> copyListProperties(List<S> sources, Supplier<T> target){
        return copyListProperties(sources, target, (ListBeanUtilsCallBack<S, T>) null);
    }

    public static <S, T> List<T> copyListProperties(List<S> sources, Supplier<T> target, ListBeanUtilsCallBack<S, T> callBack) {
        List<T> list = new ArrayList<>(sources.size());
        for (S source : sources) {
            T t = target.get();
            copyProperties(source, t);
            if(callBack != null){
                callBack.callback(source, t);
            }
            list.add(t);
        }
        return list;
    }

    public static <S, T> List<T> copyListProperties(List<S> sources, Supplier<T> target, ListBeanUtilsCallBackWithReturnValue<S, T> callBack) {
        List<T> list = new ArrayList<>(sources.size());
        for (S source : sources) {
            T t = target.get();
            copyProperties(source, t);
            if(callBack != null){
                t = callBack.callback(source, t);
            }
            list.add(t);
        }
        return list;
    }
}
