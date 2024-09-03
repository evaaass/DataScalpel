package cn.superhuang.data.scalpel.lib.docker.cli.model;

import cn.hutool.core.annotation.Alias;
import lombok.Data;

@Data
public class DockerContainerStat {
    @Alias(value = "ID")
    private String id;
    @Alias(value = "Name")
    private String name;
    @Alias(value = "BlockIO")
    private String blockIo;
    @Alias(value = "CPUPerc")
    private String cpuPerc;
    @Alias(value = "Container")
    private String container;
    @Alias(value = "MemPerc")
    private String memPerc;
    @Alias(value = "MemUsage")
    private String memUsage;
    @Alias(value = "NetIO")
    private String netIO;
    @Alias(value = "PIDs")
    private String pids;

}