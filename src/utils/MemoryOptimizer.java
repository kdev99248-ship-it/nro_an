package utils;

import java.util.concurrent.TimeUnit;

public class MemoryOptimizer {
    
    private static MemoryOptimizer instance;
    private volatile boolean isRunning = false;
    
    public static MemoryOptimizer gI() {
        if (instance == null) {
            synchronized (MemoryOptimizer.class) {
                if (instance == null) {
                    instance = new MemoryOptimizer();
                }
            }
        }
        return instance;
    }
    
    public void startMemoryMonitoring() {
        if (isRunning) return;
        
        isRunning = true;
        ThreadManager.gI().scheduleRepeatingTask(() -> {
            try {
                Runtime runtime = Runtime.getRuntime();
                long totalMemory = runtime.totalMemory();
                long freeMemory = runtime.freeMemory();
                long usedMemory = totalMemory - freeMemory;
                long maxMemory = runtime.maxMemory();
                
                double usedPercent = (double) usedMemory / maxMemory * 100;
                
                if (usedPercent > 80) {
                    Logger.warning(String.format("High memory usage: %.1f%% (%.2f MB used)\n", 
                        usedPercent, usedMemory / 1024.0 / 1024.0));
                    
                    System.gc();
                    
                    Thread.sleep(1000);
                    
                    long newUsedMemory = runtime.totalMemory() - runtime.freeMemory();
                    double newUsedPercent = (double) newUsedMemory / maxMemory * 100;
                    
                    Logger.success(String.format("Memory after GC: %.1f%% (%.2f MB used)\n", 
                        newUsedPercent, newUsedMemory / 1024.0 / 1024.0));
                }
            } catch (Exception e) {
                Logger.error("Memory monitoring error: " + e.getMessage() + "\n");
            }
        }, 30, 30, TimeUnit.SECONDS);
        
        Logger.success("Memory monitoring started\n");
    }
    
    public void stopMemoryMonitoring() {
        isRunning = false;
        Logger.log(Logger.YELLOW, "Memory monitoring stopped\n");
    }
    
    public String getMemoryInfo() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory();
        
        return String.format(
            "Memory Info:\n" +
            "- Used: %.2f MB (%.1f%%)\n" +
            "- Free: %.2f MB\n" +
            "- Total: %.2f MB\n" +
            "- Max: %.2f MB\n",
            usedMemory / 1024.0 / 1024.0,
            (double) usedMemory / maxMemory * 100,
            freeMemory / 1024.0 / 1024.0,
            totalMemory / 1024.0 / 1024.0,
            maxMemory / 1024.0 / 1024.0
        );
    }
    
    public void optimizeMemory() {
        Logger.log(Logger.YELLOW, "Starting memory optimization...\n");
        
        Runtime runtime = Runtime.getRuntime();
        long beforeUsed = runtime.totalMemory() - runtime.freeMemory();
        
        System.gc();
        
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        long afterUsed = runtime.totalMemory() - runtime.freeMemory();
        long freed = beforeUsed - afterUsed;
        
        Logger.success(String.format("Memory optimization completed. Freed: %.2f MB\n", 
            freed / 1024.0 / 1024.0));
    }
}
