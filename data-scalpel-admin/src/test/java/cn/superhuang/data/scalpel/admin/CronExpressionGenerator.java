package cn.superhuang.data.scalpel.admin;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

import java.time.ZonedDateTime;

public class CronExpressionGenerator {

    // 定义周期类型枚举
    public enum CycleType {
        YEARLY,    // 年
        MONTHLY,   // 月
        WEEKLY,    // 周
        DAILY,     // 天
        HOURLY     // 小时
    }

    /**
     * 根据 CycleType 和 cycleOffset 生成对应的 Cron 表达式
     *
     * @param cycleType   周期类型
     * @param cycleOffset 时间偏移量（分钟）
     * @return 对应的 Cron 表达式
     */
    public static String generateCronExpression(CycleType cycleType, Integer cycleOffset) {
        if (cycleOffset == null) {
            cycleOffset = 0;  // 默认偏移量为 0
        }

        // 生成 Cron 表达式
        String cronExpression = "";

        switch (cycleType) {
            case YEARLY:
                cronExpression = generateCronForLongOffset(cycleOffset, 525600, "0 0 0 1 1 ? *");
                break;
            case MONTHLY:
                // 一个月最大天数取31天，所以分钟数最大是 44640
                cronExpression = generateCronForLongOffset(cycleOffset, 44640, "0 0 0 1 * ? *");
                break;
            case WEEKLY:
                // 一周最大 7 天，分钟数为 10080
                cronExpression = generateCronForLongOffset(cycleOffset, 10080, "0 0 0 ? * MON *");
                break;
            case DAILY:
                // 一天最大 1440 分钟
                cronExpression = generateCronForLongOffset(cycleOffset, 1440, "0 0 0 * * ? *");
                break;
            case HOURLY:
                // 一小时最大 60 分钟，直接处理
                if (cycleOffset < 0 || cycleOffset >= 60) {
                    throw new IllegalArgumentException("For HOURLY cycle, cycleOffset must be between 0 and 59 minutes.");
                }
                cronExpression = String.format("0 %d * * * ? *", cycleOffset);
                break;
            default:
                throw new IllegalArgumentException("Unsupported CycleType: " + cycleType);
        }

        return cronExpression;
    }

    /**
     * 计算时间偏移量并返回相应的 Cron 表达式。
     *
     * @param cycleOffset 偏移量（分钟）
     * @param maxOffset   对应周期的最大分钟数（如一天是 1440 分钟）
     * @param cronBase    基础 Cron 表达式（将替换小时和分钟）
     * @return 最终的 Cron 表达式
     */
    private static String generateCronForLongOffset(int cycleOffset, int maxOffset, String cronBase) {
        // 校验偏移量是否在合理范围内
        if (cycleOffset < 0 || cycleOffset >= maxOffset) {
            throw new IllegalArgumentException("cycleOffset must be between 0 and " + (maxOffset - 1) + " minutes.");
        }

        int days = cycleOffset / 1440; // 跨过的天数
        int remainingMinutes = cycleOffset % 1440; // 剩余的分钟数
        int hours = remainingMinutes / 60; // 剩余的小时
        int minutes = remainingMinutes % 60; // 剩余的分钟

        // 动态调整 Cron 表达式，插入计算得到的小时和分钟
        if (days > 0) {
            return String.format("0 %d %d %d " + cronBase.substring(cronBase.indexOf(" ") + 1), minutes, hours, days);
        } else {
            return String.format("0 %d %d " + cronBase.substring(cronBase.indexOf(" ") + 1), minutes, hours);
        }
    }

    public static void main(String[] args) {
        String cronExpression = "0 0 0 1 1 ? *";

        CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);
        CronParser parser = new CronParser(cronDefinition);
        Cron cron = parser.parse(cronExpression);
        cron.validate();

        ExecutionTime executionTime = ExecutionTime.forCron(cron);
        executionTime.nextExecution(ZonedDateTime.now()).ifPresentOrElse(nextTime -> {
            executionTime.nextExecution(nextTime).ifPresentOrElse((nextNextTime -> {
                System.out.println(nextTime);
                System.out.println(nextNextTime);

                System.out.println(nextTime.plusYears(1).isEqual(nextNextTime));
            }),()->{
                throw new RuntimeException("周期运行不到两次，请使用定时运行。");
            });
        },()->{
            throw new RuntimeException("cron表达式不是周期运行");
        });
        System.out.println();
    }
}