package cn.superhuang.data.scalpel.app.task.model;

import lombok.Data;

import java.util.Date;

@Data
public class TimeRangeStrategy {
    private TimeRangeStrategyType type;
    private Date startTime;
    private Date endTime;

    public static TimeRangeStrategy All() {
        TimeRangeStrategy timeRangeStrategy = new TimeRangeStrategy();
        timeRangeStrategy.setType(TimeRangeStrategyType.ALL);
        return timeRangeStrategy;
    }
}
