package com.myjo.ordercat.utils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by lee5hx on 17/5/7.
 */
public class OcListUtils {







    public static <T extends Object> List<List<T>> splitList(List<T> t1,int size) {

        int[] indexes =
                Stream.of(IntStream.range(-1, t1.size())
                        .filter(i -> i % size == 0), IntStream.of(t1.size()))
                        .flatMapToInt(s -> s).toArray();

        List<List<T>> subLists =
                IntStream.range(0, indexes.length - 1)
                        .mapToObj(i -> t1.subList(indexes[i], indexes[i + 1]))
                        .collect(Collectors.toList());
        return subLists;
    }
}
