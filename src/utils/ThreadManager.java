package utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadFactory;

public class ThreadManager {

    private static ThreadManager instance;
    
    public final ExecutorService virtualThreadExecutor;
    public final ExecutorService platformThreadExecutor;
    public final ScheduledExecutorService scheduledExecutor;
    
    private ThreadManager() {
        this.virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();
        
        // Giới hạn platform threads để tiết kiệm RAM
        int cpuCores = Math.min(Runtime.getRuntime().availableProcessors(), 4);
        this.platformThreadExecutor = Executors.newFixedThreadPool(cpuCores, 
            Thread.ofPlatform().name("platform-worker-", 0).factory());
        
        // Chỉ cần 1 scheduler thread
        this.scheduledExecutor = Executors.newScheduledThreadPool(1,
            Thread.ofPlatform().name("scheduler-", 0).factory());
    }
    
    public static ThreadManager gI() {
        if (instance == null) {
            synchronized (ThreadManager.class) {
                if (instance == null) {
                    instance = new ThreadManager();
                }
            }
        }
        return instance;
    }
    
    public void submitIOTask(Runnable task) {
        Thread.ofVirtual().name("io-task-" + System.nanoTime()).start(task);
    }
    
    public void submitCPUTask(Runnable task) {
        platformThreadExecutor.execute(task);
    }
    
    public void scheduleTask(Runnable task, long delay, TimeUnit unit) {
        scheduledExecutor.schedule(() -> {
            Thread.ofVirtual().name("scheduled-task-" + System.nanoTime()).start(task);
        }, delay, unit);
    }
    
    public void scheduleRepeatingTask(Runnable task, long initialDelay, long period, TimeUnit unit) {
        scheduledExecutor.scheduleAtFixedRate(() -> {
            Thread.ofVirtual().name("repeating-task-" + System.nanoTime()).start(task);
        }, initialDelay, period, unit);
    }
    
    public Thread createVirtualThread(String name, Runnable task) {
        return Thread.ofVirtual().name(name).unstarted(task);
    }
    
    public Thread createPlatformThread(String name, Runnable task) {
        return Thread.ofPlatform().name(name).unstarted(task);
    }
    
    public void shutdown() {
        Logger.log(Logger.YELLOW, "ThreadManager: Shutting down thread pools...\n");
        
        virtualThreadExecutor.shutdown();
        platformThreadExecutor.shutdown();
        scheduledExecutor.shutdown();
        
        try {
            if (!virtualThreadExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                virtualThreadExecutor.shutdownNow();
            }
            if (!platformThreadExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                platformThreadExecutor.shutdownNow();
            }
            if (!scheduledExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduledExecutor.shutdownNow();
            }
            Logger.success("ThreadManager: All thread pools shut down successfully\n");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Logger.error("ThreadManager: Shutdown interrupted\n");
        }
    }
    
    public String getThreadStats() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        return String.format(
            "ThreadManager Stats:\n" +
            "- Virtual Thread Pool: %s\n" +
            "- Platform Thread Pool: %s\n" +
            "- Scheduled Thread Pool: %s\n" +
            "- Available Processors: %d\n" +
            "- Memory Used: %.2f MB\n" +
            "- Memory Free: %.2f MB\n" +
            "- Memory Total: %.2f MB\n",
            virtualThreadExecutor.toString(),
            platformThreadExecutor.toString(),
            scheduledExecutor.toString(),
            Runtime.getRuntime().availableProcessors(),
            usedMemory / 1024.0 / 1024.0,
            freeMemory / 1024.0 / 1024.0,
            totalMemory / 1024.0 / 1024.0
        );
    }
    
    public void forceGC() {
        System.gc();
        Logger.log(Logger.GREEN, "ThreadManager: Forced garbage collection\n");
    }
}
