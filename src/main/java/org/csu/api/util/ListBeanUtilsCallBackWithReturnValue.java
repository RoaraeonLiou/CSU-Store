package org.csu.api.util;

@FunctionalInterface
public interface ListBeanUtilsCallBackWithReturnValue<S, T> {
    T callback(S s, T t);
}
