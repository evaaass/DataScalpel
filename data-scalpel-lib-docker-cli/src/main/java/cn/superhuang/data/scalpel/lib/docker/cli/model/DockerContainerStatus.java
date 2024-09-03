package cn.superhuang.data.scalpel.lib.docker.cli.model;

import cn.hutool.core.annotation.Alias;
import lombok.Data;

@Data
public class DockerContainerStatus {
    @Alias(value = "ID")
    private String id;
    @Alias(value = "Names")
    private String names;
    @Alias(value = "Image")
    private String image;
    @Alias(value = "Networks")
    private String networks;
    @Alias(value = "Ports")
    private String ports;
    @Alias(value = "RunningFor")
    private String runningFor;

    //exited,running
    @Alias(value = "State")
    private String state;
    @Alias(value = "Status")
    private String status;
}