package com.vision_back.vision_back.component;

import java.util.function.Supplier;

import org.springframework.stereotype.Component;

@Component
public class EntityRetryUtils {

    public static <T> T retryUntilFound(Supplier<T> supplier, int maxRetries, long sleepMillis, String entityName) {
        T result = supplier.get();
        int retries = 0;
        while (result == null && retries < maxRetries) {
            try {
                Thread.sleep(sleepMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread interrompida ao esperar por " + entityName);
            }
            result = supplier.get();
            retries++;
        }

        if (result == null) {
            throw new IllegalStateException(entityName + " ainda não disponível após " + maxRetries + " tentativas.");
        }

        return result;
    }
}