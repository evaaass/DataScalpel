package cn.superhuang.data.scalpel.admin.app.task.service.job;

import cn.hutool.core.date.DateTime;
import cn.superhuang.data.scalpel.admin.app.task.service.TaskManagerService;
import cn.superhuang.data.scalpel.admin.app.task.service.TaskService;
import jakarta.annotation.Resource;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.time.Instant;

@PersistJobDataAfterExecution
@Service
public class SparkJob implements Job {
    @Resource
    private TaskManagerService taskManagerService;


    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            System.out.println("ScheduledFireTime:" + new DateTime(context.getScheduledFireTime()) + ",Now:" + Instant.now());
            JobDetail jobDetail = context.getJobDetail();
            String taskId = jobDetail.getJobDataMap().getString("taskId");
            System.out.println("提交任务:" + jobDetail.getJobDataMap().getString("taskInstanceId"));
            taskManagerService.submitTask(taskId, context.getScheduledFireTime());
            System.out.println("任务提交完毕:" + jobDetail.getJobDataMap().getString("taskInstanceId"));
        } catch (Exception e) {
            throw new JobExecutionException("提交任务失败:" + e.getMessage(), e);
        }

    }
}
