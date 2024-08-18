package cn.superhuang.data.scalpel.admin.app.task.service.job;

import cn.hutool.core.date.DateTime;
import cn.superhuang.data.scalpel.admin.app.task.service.TaskService;
import jakarta.annotation.Resource;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.time.Instant;

@PersistJobDataAfterExecution
@Service
public class SparkJob implements Job {
    @Resource
    private TaskService taskService;


    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("ScheduledFireTime:" + new DateTime(context.getScheduledFireTime()) + ",Now:" + Instant.now());
        JobDetail jobDetail = context.getJobDetail();
        String taskId = jobDetail.getJobDataMap().getString("taskId");
        System.out.println("提交任务:" + jobDetail.getJobDataMap().getString("taskInstanceId"));
        taskService.runTask(taskId, context.getScheduledFireTime());
        System.out.println("任务提交完毕:" + jobDetail.getJobDataMap().getString("taskInstanceId"));
    }
}
