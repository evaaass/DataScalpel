package cn.superhuang.data.scalpel.model.task;

import cn.superhuang.data.scalpel.model.enumeration.LogLevel;
import lombok.Data;

import java.util.Map;

@Data
public class SparkConfiguration {
    private String master;

    private LogLevel logLevel;

    private Integer cpu;

    private Integer memory;

    private Map<String, String> configs;
}
