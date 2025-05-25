package com.vision_back.vision_back.component;

import java.util.List;
import java.util.function.Predicate;

import org.springframework.stereotype.Component;


@Component
public class SyncUtils {

    public static <T> void processIfAnyMissing(
        List<T> codes,
        Predicate<T> existsPredicate,
        Runnable processAllFunction
    ) {
        boolean hasMissing = codes.stream().anyMatch(code -> !existsPredicate.test(code));
        if (hasMissing) {
            processAllFunction.run();
        }
    }
}