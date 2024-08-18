package cn.superhuang.data.scalpel.model;

import cn.superhuang.data.scalpel.model.enumeration.LogLevel;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class LogRecord implements Serializable {
    @Serial
    private static final long serialVersionUID = -2429399113124139122L;

    private LocalDateTime logTime;
    private LogLevel level;
    private String message;
    private String detail;
}
