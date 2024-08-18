package cn.superhuang.data.scalpel.admin.app.task.service.job;

import cn.hutool.core.date.DateTime;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.time.Instant;

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
@Service
public class NativeSQLJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("ScheduledFireTime:" + new DateTime(context.getScheduledFireTime()) + ",Now:" + Instant.now());
        JobDetail jobDetail = context.getJobDetail();
        Trigger trigger = context.getTrigger();
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();

        System.out.println("我的任务Id是" + jobDetail.getJobDataMap().getString("taskId"));
//        logger.info("jobGroup={},jobName={},jobDesc={},triggerGroup={},triggerName={},triggerDesc={}",
//                jobDetail.getKey().getGroup(),
//                jobDetail.getKey().getName(),
//                jobDetail.getDescription(),
//                trigger.getKey().getGroup(),
//                trigger.getKey().getName(),
//                trigger.getDescription());
        String val = mergedJobDataMap.getString("taskId");
        System.out.println("我的任务MERGED Id是" + jobDetail.getJobDataMap().getString("taskId"));
    }
}
