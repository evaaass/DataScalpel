package cn.superhuang.data.scalpel.app.task.model;

import lombok.Data;

@Data
public class TimeRangeStrategy {
    private TimeRangeStrategyType type;
    private Long startTime;
    private Long endTime;
}
