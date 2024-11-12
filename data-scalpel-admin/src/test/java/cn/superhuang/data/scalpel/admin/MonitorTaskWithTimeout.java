package cn.superhuang.data.scalpel.admin;

import java.util.concurrent.*;

public class MonitorTaskWithTimeout {
    public static void main(String[] args) {
        // 创建主线程池
        ExecutorService taskExecutor = Executors.newFixedThreadPool(5);

        // 创建监控任务的调度线程池
        ScheduledExecutorService monitorExecutor = Executors.newScheduledThreadPool(1);

        // 提交任务并获得 Future
        Callable<String> task = () -> {
            try {
                // 模拟任务，执行 3 小时
                System.out.println("Task started.");
                Thread.sleep(10 * 1000); // 3 小时
                return "Task completed.";
            } catch (InterruptedException e) {
                System.out.println("Task was interrupted.");
                return "Task interrupted.";
            }
        };

        Future<String> future = taskExecutor.submit(task);

        // 定时任务来监控任务的超时情况
        monitorExecutor.schedule(() -> {
            if (!future.isDone()) {
                System.out.println("Task timed out after %s sec.".formatted(5));
                future.cancel(true); // 尝试中断任务
            }else{
                System.out.println("已经结束啦");
            }
        }, 5, TimeUnit.SECONDS);

        // 关闭调度器和主线程池
        monitorExecutor.shutdown();
        taskExecutor.shutdown();
    }
}
