package cn.superhuang.data.scalpel.admin.app.task.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.date.DateTime;
import cn.superhuang.data.scalpel.admin.app.task.repository.TaskScriptRepository;
import cn.superhuang.data.scalpel.admin.app.task.service.job.NativeSQLJob;
import cn.superhuang.data.scalpel.admin.app.task.service.job.SparkJob;
import cn.superhuang.data.scalpel.model.enumeration.TaskType;
import com.fasterxml.jackson.databind.ObjectMapper;
import cn.superhuang.data.scalpel.admin.BaseException;
import cn.superhuang.data.scalpel.admin.app.datasource.repository.DatasourceRepository;
import cn.superhuang.data.scalpel.admin.app.model.repository.ModelRepository;
import cn.superhuang.data.scalpel.admin.app.task.domain.TaskInstance;
import cn.superhuang.data.scalpel.admin.app.task.model.TaskSubmitResult;
import cn.superhuang.data.scalpel.admin.model.enumeration.TaskBootType;
import cn.superhuang.data.scalpel.admin.model.enumeration.TaskScheduleType;
import cn.superhuang.data.scalpel.admin.app.service.impl.boot.BaseTaskBoot;
import cn.superhuang.data.scalpel.admin.app.service.impl.boot.TaskRunningStateChangeEvent;
import cn.superhuang.data.scalpel.model.enumeration.TaskInstanceExecutionStatus;
import cn.superhuang.data.scalpel.admin.model.dto.TaskUpdateDTO;
import cn.superhuang.data.scalpel.admin.app.task.repository.TaskInstanceRepository;
import cn.superhuang.data.scalpel.admin.app.task.repository.TaskRepository;
import cn.superhuang.data.scalpel.admin.app.task.domain.Task;
import cn.superhuang.data.scalpel.admin.model.enumeration.TaskStatus;
import cn.superhuang.data.scalpel.admin.model.dto.TaskDTO;
import cn.superhuang.data.scalpel.model.task.TaskRunningStatus;
import jakarta.annotation.Resource;
import org.quartz.*;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class TaskService {

    @Resource
    private TaskRepository taskRepository;
    @Resource
    private ModelRepository modelRepository;
    @Resource
    private DatasourceRepository datasourceRepository;
    @Resource
    private TaskInstanceRepository instanceRepository;

    private TaskBootType taskBootType = TaskBootType.DOCKER;//K8S,YARN,JAR

    @Resource
    private List<BaseTaskBoot> taskBoots;

    @Resource
    private Scheduler scheduler;
    @Resource
    private ObjectMapper objectMapper;

    public static final String QUARTZ_JOB_GROUP = "DataScalpel";
    public static final String QUARTZ_TRIGGER_GROUP = "DataScalpel";

    private BaseTaskBoot getBoot() {
        for (BaseTaskBoot taskBoot : taskBoots) {
            if (taskBoot.canHandle(taskBootType)) {
                return taskBoot;
            }
        }
        throw new RuntimeException("不支持的任务BOOT类型：" + taskBootType);
    }

    public Task save(TaskDTO taskDTO) {
        Task task = BeanUtil.copyProperties(taskDTO, Task.class);
        task.setStatus(TaskStatus.DISABLE);
        task.setSuccessCount(0l);
        task.setFailureCount(0l);
        return taskRepository.save(task);
    }

    public void update(TaskUpdateDTO taskUpdateDTO) {
        taskRepository.findById(taskUpdateDTO.getId()).ifPresent(existingTask -> {
            BeanUtil.copyProperties(taskUpdateDTO, existingTask, CopyOptions.create().setIgnoreNullValue(true));
            taskRepository.save(existingTask);
            if (existingTask.getStatus() == TaskStatus.ENABLE) {
                scheduleTask(existingTask);
            }
        });
    }

    public void updateTaskDefinition(String id, String definition) {
        taskRepository.findById(id).ifPresent(existingTask -> {
            if (existingTask.getStatus() == TaskStatus.ENABLE) {
                throw new RuntimeException("启用状态不能修改");
            }
            existingTask.setDefinition(definition);
            taskRepository.save(existingTask);
        });
    }


    public void delete(String id) {
        taskRepository.findById(id).ifPresent(task -> {
            try {
                taskRepository.deleteById(id);
                unscheduleTask(task);
            } catch (Exception e) {
                throw new BaseException("删除quartz任务失败:" + e.getMessage(), e);
            }
        });
    }


    public void enableTask(String id) {
        taskRepository.findById(id).ifPresent(task -> {
            task.setStatus(TaskStatus.ENABLE);
            taskRepository.save(task);
            scheduleTask(task);
        });
    }

    public void disableTask(String id) {
        taskRepository.findById(id).ifPresent(task -> {
            task.setStatus(TaskStatus.DISABLE);
            taskRepository.save(task);
            scheduleTask(task);
        });
    }


    private void scheduleTask(Task task) {
        try {
            if (task.getScheduleType() == TaskScheduleType.NONE) {
                unscheduleTask(task);
            } else {
                JobDataMap jobDataMap = new JobDataMap();
                jobDataMap.put("taskId", task.getId());
                Class<? extends Job> jobClass = null;
                if (task.getTaskType() == TaskType.BATCH_CANVAS) {
                    jobClass = NativeSQLJob.class;
                } else {
                    jobClass = SparkJob.class;
                }
                JobDetail jobDetail = JobBuilder.newJob(jobClass)
                        .withIdentity(task.getId(), QUARTZ_JOB_GROUP)
                        .withDescription(task.getName())
                        .usingJobData(jobDataMap)
                        .storeDurably(true)
                        .requestRecovery(true)
                        .build();

                Set<Trigger> triggerSet = new HashSet<>();
                TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
                triggerBuilder.withIdentity(task.getId(), QUARTZ_TRIGGER_GROUP);
                triggerBuilder.withDescription(task.getName());
                if (task.getStartTime() != null) {
                    triggerBuilder.startAt(new DateTime(task.getStartTime()));
                }
                if (task.getEndTime() != null) {
                    triggerBuilder.startAt(new DateTime(task.getEndTime()));
                }
                triggerSet.add(triggerBuilder.build());
                if (task.getScheduleType() == TaskScheduleType.CRON) {
                    triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(task.getCron()).withMisfireHandlingInstructionIgnoreMisfires());
                } else if (task.getScheduleType() == TaskScheduleType.TIMER) {
                    triggerBuilder.withSchedule(SimpleScheduleBuilder.simpleSchedule().withRepeatCount(0).withMisfireHandlingInstructionIgnoreMisfires());
                }
                scheduler.scheduleJob(jobDetail, triggerSet, true);
            }
        } catch (Exception e) {
            throw new BaseException("启用quartz任务调取失败:" + e.getMessage(), e);
        }
    }

    private void unscheduleTask(Task task) {
        try {
            JobKey jobKey = new JobKey(task.getId(), QUARTZ_JOB_GROUP);
            if (scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
            }
        } catch (Exception e) {
            throw new BaseException("取消quartz任务调取失败:" + e.getMessage(), e);
        }
    }

    public void runTask(String id, Date scheduledFireTime) {
        taskRepository.findById(id).ifPresentOrElse(task -> {
            TaskInstance instance = new TaskInstance();
            instance.setTaskId(task.getId());
            instance.setStartTime(new Date());
            instance.setScheduledTriggerTime(scheduledFireTime);
            instance.setStatus(TaskInstanceExecutionStatus.QUEUING);
            instance = instanceRepository.save(instance);
            TaskSubmitResult taskSubmitResult = getBoot().submitTask(instance);
            instance.setChannelId(taskSubmitResult.getTaskInstanceChannelId());
        }, () -> {
            throw new RuntimeException("任务不存在");
        });
    }

//    public TaskInstancePlan getTaskPlan(String taskId, String taskInstanceId) {
//        SparkConfiguration sparkConfiguration = new SparkConfiguration();
//        sparkConfiguration.setMaster("local");
//        sparkConfiguration.setLogLevel(LogLevel.INFO);
//        sparkConfiguration.setConfigs(Maps.newHashMap());
//
//        CanvasTaskConfiguration taskConfiguration = new CanvasTaskConfiguration();
//
//        taskConfiguration.setSparkConfiguration(sparkConfiguration);
//
//        taskConfiguration.setParams(Maps.newHashMap());
//        taskConfiguration.setInputItems(new ArrayList<String>());
//        taskConfiguration.setOutputSinks(new ArrayList<String>());
//
//
//        TaskInstancePlan taskInstancePlan = new TaskInstancePlan();
//        taskInstancePlan.setTaskConfiguration(taskConfiguration);
//
//        instanceRepository.findById(taskInstanceId).ifPresentOrElse(taskInstance -> {
//            taskRepository.findById(taskInstance.getTaskId()).ifPresentOrElse(task -> {
//                taskConfiguration.setTaskId(task.getId());
//                taskConfiguration.setTaskName(task.getName());
//                taskConfiguration.setTaskInstanceId(taskInstance.getId());
//                taskConfiguration.setType(task.getTaskType());
//                taskConfiguration.setCanvas(task.getContent());
//            }, () -> {
//                throw new BaseException("任务%s不存在".formatted(taskInstance.getTaskId()));
//            });
//        }, () -> {
//            throw new BaseException("任务实例%s不存在".formatted(taskInstanceId));
//        });
//        try {
//            if (StrUtil.isNotBlank(taskConfiguration.getCanvas())) {
//                Canvas canvas = objectMapper.readValue(taskConfiguration.getCanvas(), Canvas.class);
//                Set<String> dsIds_01 = CanvasUtil.getCanvasDatasourceIds(canvas);
//                Set<String> modelIds = CanvasUtil.getCanvasModelIds(canvas);
//                Map<String, ModelDTO> modelMap = modelRepository.findAllByIdIn(modelIds).stream().map(m -> BeanUtil.copyProperties(m, ModelDTO.class)).collect(Collectors.toMap(ModelDTO::getId, m -> m));
//                Set<String> dsIds_02 = modelMap.values().stream().map(m -> m.getDatasourceId()).collect(Collectors.toSet());
//                Set<String> dsIds = new HashSet<>();
//                dsIds.addAll(dsIds_01);
//                dsIds.addAll(dsIds_02);
//
//                Map<String, DsConnInfo> datasourceConfigMap = new HashMap<>();
//                List<Datasource> datasourceList = datasourceRepository.findAllByIdIn(dsIds);
//                for (Datasource datasource : datasourceList) {
//                    DatasourceConfig datasourceConfig = DatasourceConfig.getConfig(datasource.getType(), datasource.getProps());
//                    datasourceConfigMap.put(datasource.getId(), datasourceConfig);
//                }
//
//                taskConfiguration.setModelMap(modelMap);
//                taskConfiguration.setDatasourceMap(datasourceConfigMap);
//            }
//        } catch (Exception e) {
//            throw new BaseException("解析任务内容失败:" + e.getMessage(), e);
//        }
//        return taskInstancePlan;
//    }

    public synchronized void updateInstanceState(String instanceId, TaskRunningStatus taskRunningStatus) {
        instanceRepository.findById(instanceId).ifPresentOrElse(instance -> {
            //如果状态代表任务已完成，则更新结束时间的属性
            if (taskRunningStatus.getState() == TaskInstanceExecutionStatus.SUCCESS || taskRunningStatus.getState() == TaskInstanceExecutionStatus.FAILURE) {
                instance.setEndTime(new Date());
            }
            instance.setStatus(taskRunningStatus.getState());
            instanceRepository.save(instance);
            taskRepository.findById(instance.getTaskId()).ifPresent(task -> {
                task.setTaskLastRunStatus(taskRunningStatus.getState());
                //这里稳定之后可以直接+1处理，不用这么麻烦
                if (taskRunningStatus.getState() == TaskInstanceExecutionStatus.SUCCESS) {
                    Long successCount = instanceRepository.countAllByTaskIdAndStatus(task.getId(), TaskInstanceExecutionStatus.SUCCESS);
                    task.setSuccessCount(successCount);
                }
                if (taskRunningStatus.getState() == TaskInstanceExecutionStatus.FAILURE) {
                    Long failureCount = instanceRepository.countAllByTaskIdAndStatus(task.getId(), TaskInstanceExecutionStatus.FAILURE);
                    task.setFailureCount(failureCount);
                }
                taskRepository.save(task);
            });
        }, () -> {
            throw new RuntimeException("实例(instanceId:%s)不存在".formatted(instanceId));
        });

    }

    @EventListener({TaskRunningStateChangeEvent.class})
    public void taskRunningStateChangeListener(TaskRunningStateChangeEvent event) {
        TaskRunningStatus status = event.getStatus();

        instanceRepository.findTaskInstanceByChannelId(status.getChannelId()).ifPresentOrElse(instance -> {
            if (instance.getStatus() == TaskInstanceExecutionStatus.RUNNING) {
                TaskRunningStatus taskRunningStatus = new TaskRunningStatus();
                taskRunningStatus.setState(TaskInstanceExecutionStatus.FAILURE);
                taskRunningStatus.setMessage("任务异常结束");
                taskRunningStatus.setDetail("任务实例已运行完成，但是没有收到任务结束的消息。");
            } else {
                updateInstanceState(instance.getId(), status);
            }
            updateInstanceState(instance.getId(), status);
        }, () -> {
            throw new RuntimeException("实例(channelId:%s)不存在".formatted(status.getChannelId()));
        });
    }
}
