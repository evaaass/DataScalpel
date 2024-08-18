package cn.superhuang.data.scalpel.admin.app.service.impl.boot;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.thread.ExecutorBuilder;
import cn.superhuang.data.scalpel.admin.app.task.config.TaskBootConfig;
import cn.superhuang.data.scalpel.admin.app.task.domain.TaskInstance;
import cn.superhuang.data.scalpel.admin.app.task.model.TaskSubmitResult;
import cn.superhuang.data.scalpel.admin.model.enumeration.TaskBootType;
import cn.superhuang.data.scalpel.model.enumeration.TaskInstanceExecutionStatus;
import cn.superhuang.data.scalpel.model.task.TaskRunningStatus;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Service
public class TaskJarBoot implements BaseTaskBoot {
    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private TaskBootConfig taskBootConfig;
    private Map<String, Future<String>> runningInstanceMap = new HashMap<>();

    private TimedCache<String, TaskRunningStatus> runningInstanceStatusCache = CacheUtil.newTimedCache(24 * 60 * 60 * 1000);
    private ExecutorService executor = ExecutorBuilder.create()
            .setCorePoolSize(2)
            .setMaxPoolSize(5)
            .setWorkQueue(new LinkedBlockingQueue<>(100))
            .build();


    //后面要考虑取消了
    @Scheduled(fixedDelay = 10 * 1000)
    public void refreshRunningTaskStatus() {
        log.info("开始检查任务状态");

        Iterator<String> iter = runningInstanceMap.keySet().iterator();
        while (iter.hasNext()) {
            String channelId = iter.next();
            Future<String> future = runningInstanceMap.get(channelId);

            TaskRunningStatus taskRunningStatus = new TaskRunningStatus();
            taskRunningStatus.setChannelId(channelId);
            if (future.isCancelled()) {
                taskRunningStatus.setState(TaskInstanceExecutionStatus.FAILURE);
            } else if (future.isDone()) {
                taskRunningStatus.setState(TaskInstanceExecutionStatus.SUCCESS);
                try {
                    taskRunningStatus.setDetail(future.get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                iter.remove();
//                runningInstanceMap.remove(channelId);
            } else {
                taskRunningStatus.setState(TaskInstanceExecutionStatus.RUNNING);
            }
            TaskRunningStatus oldTaskRunningStatus = runningInstanceStatusCache.get(channelId);

            runningInstanceStatusCache.put(channelId, taskRunningStatus);

            if (oldTaskRunningStatus == null || taskRunningStatus.getState() != oldTaskRunningStatus.getState()) {
                applicationContext.publishEvent(new TaskRunningStateChangeEvent(this, taskRunningStatus));
            }
        }

//        runningInstanceMap.forEach((channelId, future) -> {
//
//        });
        log.info("检查任务状态结束");
    }

    @Override
    public Boolean canHandle(TaskBootType type) {
        return TaskBootType.JAR == type;
    }

    public TaskSubmitResult submitTask(TaskInstance instance) {
        Future<String> future = executor.submit(new TaskJarRunnable(instance, taskBootConfig.getLocalRunCommand()));
        runningInstanceMap.put(instance.getId(), future);

        TaskSubmitResult result = new TaskSubmitResult();
        result.setTaskInstanceId(instance.getId());
        result.setTaskInstanceChannelId(instance.getId());
        result.setSuccess(true);
        return result;
    }

    public TaskRunningStatus getTaskState(String channelId) {
        return runningInstanceStatusCache.get(channelId);
    }


}
