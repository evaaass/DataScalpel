package cn.superhuang.data.scalpel.admin.web.resource.impl;

import cn.superhuang.data.scalpel.admin.app.task.service.job.CreateTestJobDTO;
import cn.superhuang.data.scalpel.admin.app.task.service.job.SparkJob;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Tag(name = "quartz")
@RestController
@RequestMapping("/api/v1/quartz-test")
public class QuartzResource  {
    @Resource
    private Scheduler scheduler;

    @Transactional
    @PostMapping("/tasks")
    public void create(@RequestBody CreateTestJobDTO createTestJobDTO) throws SchedulerException {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("taskId", createTestJobDTO.getValue());
        JobDetail jobDetail = JobBuilder.newJob(SparkJob.class)
                .withIdentity(createTestJobDTO.getJobName(), createTestJobDTO.getJobGroup())
                .withDescription(createTestJobDTO.getJobDesc())
                .usingJobData(jobDataMap)
                .storeDurably(true)
                .requestRecovery(true)
                .build();

        JobDataMap triggerDataMap = new JobDataMap();
        TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
        triggerBuilder.withIdentity(createTestJobDTO.getTriggerName(), createTestJobDTO.getTriggerGroup());
        triggerBuilder.withDescription(createTestJobDTO.getTriggerDesc());
        triggerBuilder.usingJobData(triggerDataMap);
        triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(createTestJobDTO.getCron()).withMisfireHandlingInstructionIgnoreMisfires());
        Trigger trigger = triggerBuilder.build();

        //scheduleJob(JobDetail jobDetail, Trigger trigger):作业注册并启动。注意同一个组下面的任务详情或者触发器名称必须唯一，否则重复注册时会报错，已经存在.
        //scheduleJobs(Map<JobDetail, Set<? extends Trigger>> triggersAndJobs, boolean replace)
        // replace=true，表示如果存储相同的 Job 或者 Trigger ，则替换它们
        //因为全局配置文件中配置了 spring.quartz.uto-startup=true，所以不再需要手动启动：scheduler.start()
        Set<Trigger> triggerSet = new HashSet<>();
        triggerSet.add(trigger);
        scheduler.scheduleJob(jobDetail, triggerSet, true);
    }


    @GetMapping("/jobs")
    public List<String> jobs() throws SchedulerException {
        return scheduler.getTriggerGroupNames().stream().map(groupName -> {
            try {
                return groupName + "|" + scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName)).stream().map(jobKey -> jobKey.getName()).collect(Collectors.joining(","));
            } catch (SchedulerException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }

    @GetMapping("/triggers")
    public List<String> triggers() throws SchedulerException {
        return scheduler.getTriggerGroupNames().stream().map(groupName -> {
            try {
                return groupName + "|" + scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(groupName)).stream().map(jobKey -> jobKey.getName()).collect(Collectors.joining(","));
            } catch (SchedulerException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }

    @Transactional
    @DeleteMapping("/jobs/{jobName}")
    public Boolean deleteJob(@PathVariable String jobName) throws SchedulerException {
        return scheduler.deleteJob(new JobKey(jobName, "superhuang"));
    }

    @Transactional
    @DeleteMapping("/triggers/{triggerName}")
    public Boolean deleteTrigger(@PathVariable String triggerName) throws SchedulerException {
        return scheduler.unscheduleJob(new TriggerKey(triggerName, "superhuang"));
    }

    @Transactional
    @PostMapping("/jobs/{jobName}/actions/pause")
    public Boolean pauseJob(@PathVariable String jobName) throws SchedulerException {
        scheduler.pauseJob(new JobKey(jobName, "superhuang"));
        return true;
    }
    @Transactional
    @PostMapping("/jobs/{jobName}/actions/resume")
    public Boolean resumeJob(@PathVariable String jobName) throws SchedulerException {
        scheduler.resumeJob(new JobKey(jobName, "superhuang"));
        return true;
    }


}
