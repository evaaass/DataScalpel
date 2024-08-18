package cn.superhuang.data.scalpel.admin.app.task.service.job;

import org.quartz.*;
import org.springframework.stereotype.Service;

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
@Service
public class DataIntegrationJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDetail jobDetail = context.getJobDetail();
        Trigger trigger = context.getTrigger();

        System.out.println("数据治理任务执行了！");
    }
}
