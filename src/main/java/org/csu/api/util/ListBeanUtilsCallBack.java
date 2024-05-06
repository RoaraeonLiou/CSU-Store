package org.csu.api.util;

@FunctionalInterface
public interface ListBeanUtilsCallBack<S, T> {
    void callback(S s, T t);
}
