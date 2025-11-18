package utils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Supplier;
import java.time.Duration;

public class StructuredTaskManager {

    private static StructuredTaskManager instance;
    
    private StructuredTaskManager() {}
    
    public static StructuredTaskManager gI() {
        if (instance == null) {
            synchronized (StructuredTaskManager.class) {
                if (instance == null) {
                    instance = new StructuredTaskManager();
                }
            }
        }
        return instance;
    }
    
    public <T> List<T> executeAllTasks(List<Supplier<T>> tasks) {
        List<CompletableFuture<T>> futures = new ArrayList<>();
        
        for (Supplier<T> task : tasks) {
            CompletableFuture<T> future = CompletableFuture.supplyAsync(() -> {
                try {
                    return task.get();
                } catch (Exception e) {
                    Logger.error("Task failed: " + e.getMessage() + "\n");
                    throw new RuntimeException(e);
                }
            }, ThreadManager.gI().virtualThreadExecutor);
            futures.add(future);
        }
        
        CompletableFuture<Void> allOf = CompletableFuture.allOf(
            futures.toArray(new CompletableFuture[0]));
        
        try {
            allOf.get();
            
            List<T> results = new ArrayList<>();
            for (CompletableFuture<T> future : futures) {
                results.add(future.get());
            }
            
            return results;
        } catch (Exception e) {
            Logger.error("Task execution failed: " + e.getMessage() + "\n");
            return new ArrayList<>();
        }
    }
    
    @SuppressWarnings("unchecked")
    public <T> T executeFirstSuccess(List<Supplier<T>> tasks) {
        List<CompletableFuture<T>> futures = new ArrayList<>();
        
        for (Supplier<T> task : tasks) {
            CompletableFuture<T> future = CompletableFuture.supplyAsync(() -> {
                try {
                    return task.get();
                } catch (Exception e) {
                    Logger.error("Task failed: " + e.getMessage() + "\n");
                    throw new RuntimeException(e);
                }
            }, ThreadManager.gI().virtualThreadExecutor);
            futures.add(future);
        }
        
        try {
            CompletableFuture<Object> anyOf = CompletableFuture.anyOf(
                futures.toArray(new CompletableFuture[0]));
            return (T) anyOf.get();
        } catch (Exception e) {
            Logger.error("All tasks failed: " + e.getMessage() + "\n");
            return null;
        }
    }
    
    public <T> List<T> executeAllTasksWithTimeout(List<Supplier<T>> tasks, Duration timeout) {
        List<CompletableFuture<T>> futures = new ArrayList<>();
        
        for (Supplier<T> task : tasks) {
            CompletableFuture<T> future = CompletableFuture.supplyAsync(() -> {
                try {
                    return task.get();
                } catch (Exception e) {
                    Logger.error("Task failed: " + e.getMessage() + "\n");
                    throw new RuntimeException(e);
                }
            }, ThreadManager.gI().virtualThreadExecutor);
            futures.add(future);
        }
        
        CompletableFuture<Void> allOf = CompletableFuture.allOf(
            futures.toArray(new CompletableFuture[0]));
        
        try {
            allOf.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
            
            List<T> results = new ArrayList<>();
            for (CompletableFuture<T> future : futures) {
                if (future.isDone() && !future.isCompletedExceptionally()) {
                    results.add(future.get());
                } else {
                    Logger.warning("Task did not complete within timeout\n");
                }
            }
            
            return results;
        } catch (Exception e) {
            Logger.error("Task execution failed or timed out: " + e.getMessage() + "\n");
            return new ArrayList<>();
        }
    }
    
    public void initializeBossesInParallel(List<Runnable> bossInitTasks) {
        List<Supplier<Void>> suppliers = new ArrayList<>();
        for (Runnable task : bossInitTasks) {
            suppliers.add(() -> {
                task.run();
                return null;
            });
        }
        
        executeAllTasks(suppliers);
        Logger.success("All boss initialization tasks completed\n");
    }
    
    public void savePlayersInParallel(List<Runnable> saveTasks) {
        List<Supplier<Void>> suppliers = new ArrayList<>();
        for (Runnable task : saveTasks) {
            suppliers.add(() -> {
                try {
                    task.run();
                    return null;
                } catch (Exception e) {
                    Logger.error("Player save failed: " + e.getMessage() + "\n");
                    throw e;
                }
            });
        }
        
        executeAllTasksWithTimeout(suppliers, Duration.ofSeconds(30));
        Logger.success("Player save operations completed\n");
    }
}
