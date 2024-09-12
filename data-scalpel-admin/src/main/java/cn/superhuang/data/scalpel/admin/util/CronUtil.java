package cn.superhuang.data.scalpel.admin.util;

import cn.superhuang.data.scalpel.model.enumeration.TaskCycleType;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

import java.time.ZonedDateTime;

public class CronUtil {
    public static CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);
    public static CronParser parser = new CronParser(cronDefinition);

    public static void validateCycleCron(TaskCycleType cycleType, String cronExpression) {
        Cron cron = parser.parse(cronExpression);
        cron.validate();

        ExecutionTime executionTime = ExecutionTime.forCron(cron);
        executionTime.nextExecution(ZonedDateTime.now()).ifPresentOrElse(nextTime -> {
            executionTime.nextExecution(nextTime).ifPresentOrElse((nextNextTime -> {
                if (cycleType == TaskCycleType.HOUR && !nextTime.plusHours(1).isEqual(nextNextTime)) {
                    throw new RuntimeException("Cron表达式的周期间隔不是小时");
                }
                if (cycleType == TaskCycleType.DAY && !nextTime.plusDays(1).isEqual(nextNextTime)) {
                    throw new RuntimeException("Cron表达式的周期间隔不是天");
                }
                if (cycleType == TaskCycleType.WEEK && !nextTime.plusWeeks(1).isEqual(nextNextTime)) {
                    throw new RuntimeException("Cron表达式的周期间隔不是周");
                }
                if (cycleType == TaskCycleType.MONTH && !nextTime.plusMonths(1).isEqual(nextNextTime)) {
                    throw new RuntimeException("Cron表达式的周期间隔不是月");
                }
                if (cycleType == TaskCycleType.YEAR && !nextTime.plusYears(1).isEqual(nextNextTime)) {
                    throw new RuntimeException("Cron表达式的周期间隔不是年");
                }
            }), () -> {
                throw new RuntimeException("周期运行不到两次，请使用定时运行。");
            });
        }, () -> {
            throw new RuntimeException("cron表达式不是周期运行");
        });
    }
}
