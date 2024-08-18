package cn.superhuang.data.scalpel.app.sys.model;

import cn.superhuang.data.scalpel.app.sys.model.emun.LogTargetType;
import cn.superhuang.data.scalpel.model.enumeration.LogLevel;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysLogCreateDTO {
    private LogTargetType logTargetType;
    private String logTargetId;
    private LogLevel level;
    private String message;
    private String detail;
    private LocalDateTime logTime;
}
