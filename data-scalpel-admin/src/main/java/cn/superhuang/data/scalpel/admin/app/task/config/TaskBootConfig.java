package cn.superhuang.data.scalpel.admin.app.task.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "task-boot")
@Data
public class TaskBootConfig {
    private String type;
    private List<String> localRunCommand;
}
