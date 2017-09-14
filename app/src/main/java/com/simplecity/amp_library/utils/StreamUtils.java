package com.simplecity.amp_library.utils;

import android.annotation.TargetApi;

import com.annimon.stream.function.Function;
import com.annimon.stream.function.Predicate;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tysovsky on 13/09/17.
 */

@TargetApi(24)
public class StreamUtils {

    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Set<Object> seen = Collections.newSetFromMap(new ConcurrentHashMap<>());
        return t -> seen.add(keyExtractor.apply(t));
    }

}
