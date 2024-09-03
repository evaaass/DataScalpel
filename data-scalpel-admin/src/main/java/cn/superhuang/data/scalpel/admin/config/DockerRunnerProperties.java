package cn.superhuang.data.scalpel.admin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@ConfigurationProperties(
        prefix = "data-scalpel.dispatcher.runner.docker"
)
@Configuration
@Data
public class DockerRunnerProperties {
    private List<String> commands;
}
